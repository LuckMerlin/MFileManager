package com.luckmerlin.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.databinding.touch.OnViewLongClick;
import com.luckmerlin.databinding.touch.TouchListener;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalClient;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.NasClient;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.Query;
import com.luckmerlin.file.R;
import com.luckmerlin.file.TaskListActivity;
import com.luckmerlin.file.adapter.FileBrowserAdapter;
import com.luckmerlin.file.service.TaskBinder;
import com.luckmerlin.file.service.TaskService;
import com.luckmerlin.file.task.ActionFolderTask;
import com.luckmerlin.file.task.ActionTask;
import com.luckmerlin.file.task.UploadTask;
import com.luckmerlin.file.ui.OnPathSpanClick;
import com.luckmerlin.file.ui.UriPath;
import com.luckmerlin.lib.ArraysList;
import com.luckmerlin.mvvm.activity.OnActivityBackPress;
import com.luckmerlin.mvvm.activity.OnActivityIntentChange;
import com.luckmerlin.mvvm.activity.OnActivityStart;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileBrowserModel extends Model implements OnViewClick, OnPathSpanClick, OnActivityBackPress,
        OnModelServiceResolve, OnServiceBindChange, OnTaskUpdate,OnViewLongClick, OnActivityStart {
    private final ObservableField<Integer> mClientCount=new ObservableField<Integer>();
    private final ObservableField<Integer> mCurrentSelectSize=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Mode> mBrowserMode=new ObservableField<>(null);
    private final ObservableField<String> mSearchInput=new ObservableField<>();
    private final ObservableField<String> mNotifyText=new ObservableField<>("正在上传 dsddddd.mp3");
    private final List<Client> mClients=new ArrayList<>();
    private TaskBinder mTaskBinder;
    private final FileBrowserAdapter mBrowserAdapter=new FileBrowserAdapter(){
        @Override
        protected void onReset(boolean succeed, Section<Query, Path> section) {
            super.onReset(succeed, section);
            mCurrentFolder.set(null!=section&&section instanceof Folder?(Folder)section:null);
        }
    };

    protected final boolean add(Client client, String debug){
        List<Client> clients=mClients;
        if (null!=client&&null!=clients&&!clients.contains(client)&&clients.add(client)){
            FileBrowserAdapter adapter=mBrowserAdapter;
            return (null!=adapter&&adapter.getCurrentClient()==null&&setClientSelect(client,debug))||true;
        }
        return false;
    }

    public final boolean selectMode(Mode mode,String debug){
        mBrowserMode.set(mode);
        return true;
    }

    protected final boolean switchSelectClient(String debug){
        List<Client> clients=getClients();
        int size=null!=clients?clients.size():-1;
        if (size>0){
            Client current=getCurrentClient();
            int index=null!=current?clients.indexOf(current):-1;
            index=(index<0?-1:index)+1;
            return setClientSelect(clients.get(index>=0&&index<size?index:0),debug);
        }
        return false;
    }

    public final List<Client> getClients() {
        return mClients;
    }

    protected final boolean setClientSelect(Client client, String debug){
        FileBrowserAdapter browserAdapter=mBrowserAdapter;
        return null!=browserAdapter&&browserAdapter.setClient(client,debug);
    }

    @Override
    public void onPathSpanClick(Path path, int start, int end, String value) {
        if (null!=value&&value.length()>0){
            browserPath(value,"After path span click.");
        }
    }

    @Override
    public List<Intent> onServiceResolved(List<Intent> list) {
        list=null!=list?list:new ArrayList<>(1);
        list.add(new Intent(getContext(), TaskService.class));
        return list;
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object tag) {
        return false;
    }

    @Override
    public boolean onViewLongClick(View view, int i, Object tag) {
        return false;
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return onBackKeyPressed("While activity back pressed.");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Intent intent=null!=activity?activity.getIntent():null;
        String action=null!=intent?intent.getAction():null;
        if (null!=action&&action.equals(Intent.ACTION_SEND)){
            startUploadFiles(intent.getParcelableExtra(Intent.EXTRA_STREAM),"While activity send action start.");
        }else if (null!=action&&action.equals(Intent.ACTION_SEND_MULTIPLE)){
            startUploadFiles(intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM),"While activity send action start.");
        }
    }

    final boolean startUploadFiles(Object files,String debug){
        if (null==files){
            return false;
        }else if ((files instanceof String)||(files instanceof Uri)||(files instanceof File)){
            return startUploadFiles(new ArraysList<>().addData(files),debug);
        }else if (files instanceof Collection){
            Mode mode=new Mode(Mode.MODE_UPLOAD);
            Collection collection=(Collection)files;
            UriPath uriPath=new UriPath();
            Context context=getContext();
            for (Object child:collection){
                child=null!=child&&child instanceof Uri?uriPath.getUriPath(context,(Uri)child):child;
                child=null!=child&&child instanceof String?new File((String)child):child;
                child=null!=child&&child instanceof File?LocalPath.create((File)child):child;
                if (null!=child&&child instanceof Path){
                    mode.add((Path)child);
                }
            }
            return selectMode(mode,debug);
        }
        return false;
    }

    protected final boolean onBackKeyPressed(String debug){
        Folder folder=mCurrentFolder.get();
        String parent=null!=folder?folder.getParent():null;
        return null!=parent&&parent.length()>0&&browserPath(parent,debug);
    }

    private boolean search(String searchInput,String debug){
        FileBrowserAdapter adapter=mBrowserAdapter;
        Query query=null!=adapter?adapter.getNextLastSectionArg():null;
        String path=null!=query?query.getPath():null;
        return null!=adapter&&adapter.browser(new Query(path,searchInput),debug);
    }

    protected final boolean openPath(Path path,String debug){
        String pathValue=null!=path?path.getPath():null;
        if (null==pathValue||pathValue.length()<=0){
            return false;
        }else if (path.isDirectory()){
            return browserPath(pathValue,debug);
        }
        Debug.D("打开 "+debug);
        return false;
    }

    private boolean browserPath(String path,String debug){
        if (null==path||path.length()<=0){
            return false;
        }
        FileBrowserAdapter adapter=mBrowserAdapter;
        String searchInput=mSearchInput.get();
        return null!=adapter&&adapter.browser(new Query(path,searchInput),debug);
    }

    public final Folder getCurrentFolder() {
        ObservableField<Folder> folder=getBrowserFolder();
        return null!=folder?folder.get():null;
    }

    @Override
    public void onServiceBindChanged(IBinder iBinder, ComponentName componentName) {
        TaskBinder binder=mTaskBinder;
        if (null!=binder){
            binder.unregister(this);
        }
        TaskBinder taskBinder=mTaskBinder=null!=iBinder&&iBinder instanceof TaskBinder?((TaskBinder)iBinder):null;
        if (null!=taskBinder){
            taskBinder.register(this,null);
        }
    }

    @Override
    public void onTaskUpdated(Task task, int status) {
        //Do nothing
    }

    protected final boolean startTask(Task task,String debug){
        if (null==task){
            return false;
        }
        if (task instanceof ActionTask&&((ActionTask)task).isEmpty()){
            return toast(R.string.emptyContent)&&false;
        }
        if (task instanceof ActionFolderTask){
            if (((ActionFolderTask)task).getFolder()==null){
                return toast(R.string.noneTargetFolder)&&false;
            }else if (((ActionFolderTask)task).isAllInSameFolder()){
                return toast(R.string.notActionHere)&&false;
            }
        }
        TaskBinder binder=mTaskBinder;
        if (null==binder){
            return toast(R.string.transporterNotBind)&&false;
        }
        return binder.startTask(task)||true;
    }

    protected final Mode getMode() {
        return mBrowserMode.get();
    }

    protected final Client getCurrentClient() {
        return mBrowserAdapter.getCurrentClient();
    }

    public final ObservableField<Client> getBrowserClient() {
        return mBrowserAdapter.getBrowserClient();
    }

    public final ObservableField<Integer> getClientCount() {
        return mClientCount;
    }

    public final ObservableField<Integer> getCurrentSelectSize() {
        return mCurrentSelectSize;
    }

    public final ObservableField<Folder> getBrowserFolder() {
        return mCurrentFolder;
    }

    public final FileBrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }

    public final ObservableField<Mode> getBrowserMode() {
        return mBrowserMode;
    }

    public final ObservableField<String> getNotifyText() {
        return mNotifyText;
    }
}
