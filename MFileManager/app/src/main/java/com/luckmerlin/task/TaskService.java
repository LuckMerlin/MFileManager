package com.luckmerlin.task;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.core.match.Matcher;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.service.DefaultTaskExecutor;
import com.luckmerlin.file.service.OnTaskSyncUpdate;

import java.io.Externalizable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public final class TaskService extends Service implements Tasker{
    private final List<Task> mTasks=new ArrayList<>();
    private final Map<OnTaskUpdate,Matchable> mUpdateMaps=new WeakHashMap<>();
    private final DefaultTaskExecutor mExecutor=new DefaultTaskExecutor();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final OnTaskUpdate mInnerUpdate=(Task task, int status)-> notifyTaskUpdate(task, status);

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        File folder=getTaskSaveFolder();
        final TaskExecutor executor=mExecutor;
        if (null!=folder&&null!=executor){
            executor.submit(()->
                folder.listFiles((File child)-> {
                    if (null!=child){
                        ObjectInputStream objectStream=null;
                        try {
                            objectStream= child.exists()&&child.isFile()?
                                    new ObjectInputStream(new FileInputStream(child)):null;
                            Object object=objectStream.readObject();
                            if (null!=object&&object instanceof Task){
                                addTask(Status.PREPARE|Status.DELETE,((Task)object));
                            }
                        } catch (Exception e) {
                            Debug.E("Exception load saved task.e="+e,e);
                            e.printStackTrace();
                            child.delete();
                        }finally {
                            new Closer().close(objectStream);
                        }
                    }
                    return false;
                }));
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder(TaskService.this);
    }

    @Override
    public boolean action(int action, Object... tasks) {
        if (null!=tasks){
            for (Object object:tasks){
                actionTask(action,object);
            }
            return true;
        }
        return null!=getTasks((Object o)-> actionTask(action,o)?Matchable.CONTINUE:Matchable.CONTINUE, -1);
    }

    private boolean actionTask(int action,Object task){
        if (null!=task){
            action=(action&Status.START)>0?(action|Status.ADD):action;
            action=(action&Status.REMOVE)>0?(action|Status.CANCEL):action;
            action=(action&Status.DELETE)>0?(action|Status.CANCEL|Status.REMOVE):action;
            if ((action&Status.ADD)>0){
                return addTask(action,task);
            }else if ((action&Status.REMOVE)>0){
                return remove(task,(action&Status.CANCEL)>0, (action&Status.DELETE)>0);
            }
        }
        return false;
    }

    private boolean addTask(int action,Object taskObj) {
        if (null==taskObj){
            Debug.W("Can't add task while task NULL.");
            return false;
        }
        Task child=null;
        List<Task> list=mTasks;
        if (null!=list){
            if (taskObj instanceof Collection){
                for (Object object:(Collection)taskObj){
                    addTask(action,object);
                }
                return true;
            }else if (taskObj instanceof Task){
                Task task=(Task)taskObj;
                if (!list.contains(task)&&list.add(task)){
                    if ((action&Status.DELETE)<=0&&task instanceof Externalizable){//Save task
                        task=null!=task.getId()?task:task.setId(UUID.randomUUID().toString());
                        File saveFile=getTaskSaveFile(task);
                        if (null!=saveFile){//Save task
                            ObjectOutputStream outputStream=null;
                            try {
                                outputStream=new ObjectOutputStream(new FileOutputStream(saveFile));
                                outputStream.writeObject(task);
                                Debug.E("Save task."+saveFile.length()+" "+saveFile);
                            }catch (Exception e){
                                new Closer().close(outputStream);
                                saveFile.delete();
                                Debug.E("Exception save task.e="+e,e);
                                e.printStackTrace();
                            }finally {
                                new Closer().close(outputStream);
                            }
                        }
                    }
                    child= task;
                    notifyTaskUpdate(task, Status.ADD);
                }
            }else{
                child=indexTask(taskObj);
            }
        }
        final Boolean startTaskFlag=(action&Status.DOING)>0?Boolean.valueOf(true):
                (action&Status.PREPARE)>0?Boolean.valueOf(false):null;
        if (null==startTaskFlag){
            return null!=child&&child instanceof Task;
        }
        final TaskExecutor executor=mExecutor;
        if (null==executor){
            Debug.W("Can't start task while executor NULL.");
            return false;
        }
        final Task finalTask=child;
        if (null==finalTask){
            Debug.W("Can't start task while NONE task matched");
            return false;
        }else if (finalTask.isStarted()){
            Debug.W("Can't start task while task already started.");
            return false;
        }
        Debug.D("To start task."+this);
        return null!=executor.submit(()->finalTask.execute(this,startTaskFlag,mInnerUpdate));
    }

    public boolean remove(Object taskObj,boolean cancel,boolean deleteFail){
        Task task=null!=taskObj?indexTask(taskObj):null;
        if (null!=task){
            List<Task> list=mTasks;
            if (null!=list&&list.remove(task)){
                if (deleteFail&&!task.isFinished()){
                    task.deleteFailed(true);
                }
                if (cancel&&task.cancel(true)){
                    //Do nothing
                }
                Debug.D("QQQQQQQQQq  "+cancel);
                //Delete task save
                File saveFile=getTaskSaveFile(task);
                if (null!=saveFile&&saveFile.exists()){
                    saveFile.delete();
                }
                notifyTaskUpdate(task, Status.REMOVE);
                return true;
            }
        }
        return false;
    }

    public final Task indexTask(Object task){
        List<Task> list=mTasks;
        if (null!=task&&null!=list){
            synchronized (list){
                int index=list.indexOf(task);
                return index>=0?list.get(index):null;
            }
        }
        return null;
    }

    @Override
    public boolean register(OnTaskUpdate callback,Matchable matchable) {
        final Map<OnTaskUpdate,Matchable> maps=null!=callback?mUpdateMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.put(callback,matchable);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean unregister(OnTaskUpdate callback) {
        final Map<OnTaskUpdate,Matchable> maps=null!=callback?mUpdateMaps:null;
        if (null!=maps){
            synchronized (maps){
                maps.remove(callback);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getTasks(Matchable matchable, int max) {
        List<Task> list=mTasks;
        if (null!=list){
            List<Task> result=null;
            synchronized (list){
                int size=list.size();
                (result=new ArrayList<>(size)).addAll(list);
            }
            if (null!=result){
                return new Matcher().match(result,matchable,max<=0?Integer.MAX_VALUE:max);
            }
        }
        return null;
    }

    private File getTaskSaveFile(Task task){
        String taskId=null!=task?task.getId():null;
        if (null!=taskId&&taskId.length()>0){
            File folder=getTaskSaveFolder();
            return null!=folder?new File(folder,taskId):null;
        }
        return null;
    }

    private File getTaskSaveFolder(){
        File file= getFilesDir();
        file= null!=file?new File(file,"tasks"):null;
        if (null!=file&&!file.exists()&&file.mkdirs()){
            Debug.D("Create task save folder."+file);
        }
        return null!=file&&file.exists()&&file.isDirectory()?file:null;
    }

    private final boolean notifyTaskUpdate(Task task, int status){
        Map<OnTaskUpdate,Matchable> updateMaps=mUpdateMaps;
        if (null!=updateMaps&&null!=task){
            final Handler handler=mHandler;
            synchronized (updateMaps){
                Set<OnTaskUpdate> set=updateMaps.keySet();
                if (null!=set){
                    Matchable matchable=null;Integer matched=null;
                    for (OnTaskUpdate child:set) {
                        if (null!=child&&(null==(matchable=updateMaps.get(child))||
                                null!=(matched=matchable.onMatch(task))&&matched==Matchable.MATCHED)){
                            if (child instanceof OnTaskSyncUpdate){
                                notifyTaskUpdate(task,status,child);
                            }else if (null!=handler){
                                handler.post(()->notifyTaskUpdate(task,status,child));
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    protected final boolean notifyTaskUpdate(Task task, int status,OnTaskUpdate callback){
        if (null!=callback){
            callback.onTaskUpdated(task,status);
            return true;
        }
        return false;
    }
}
