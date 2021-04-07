package com.luckmerlin.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalClient;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.Query;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.FileBrowserAdapter;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.service.TaskBinder;
import com.luckmerlin.file.service.TaskService;
import com.luckmerlin.file.task.ActionFolderTask;
import com.luckmerlin.file.task.GroupTask;
import com.luckmerlin.file.task.Progress;
import com.luckmerlin.file.ui.OnPathSpanClick;
import com.luckmerlin.file.ui.UriPath;
import com.luckmerlin.lib.ArraysList;
import com.luckmerlin.mvvm.activity.OnActivityBackPress;
import com.luckmerlin.mvvm.activity.OnActivityStart;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;
import com.luckmerlin.task.OnTaskUpdate;
import com.luckmerlin.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileBrowserModel extends Model implements OnPathSpanClick, OnActivityBackPress,
        OnModelServiceResolve, OnServiceBindChange, OnTaskUpdate, OnActivityStart {
    private final ObservableField<Integer> mClientCount=new ObservableField<Integer>();
    private final ObservableField<Integer> mCurrentSelectSize=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Mode> mBrowserMode=new ObservableField<>(null);
    private final ObservableField<String> mSearchInput=new ObservableField<>();
    private final ObservableField<CharSequence> mNotifyText=new ObservableField<>();
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
            mClientCount.set(clients.size());
            FileBrowserAdapter adapter=mBrowserAdapter;
            return (null!=adapter&&adapter.getCurrentClient()==null&&setClientSelect(client,debug))||true;
        }
        return false;
    }

    public final boolean selectMode(Mode mode,String debug){
        mBrowserMode.set(mode);
        return true;
    }

    protected final boolean switchSelectClient(Boolean local,String debug){
        Client nextClient=nextClient(local);
        if (null!=nextClient){
            mCurrentFolder.set(null);//Clean
            return setClientSelect(nextClient,debug);
        }
        return false;
    }

    public final Client nextClient(Boolean local){
        List<Client> clients=getClients();
        int size=null!=clients?clients.size():-1;
        Client current = getCurrentClient();
        int index = null != current ? clients.indexOf(current) : -1;
        for (int i = 0; i < size; i++) {
            index=(index < 0 ? -1 : index) + 1;
            index=index>=size?index-size:index;
            Client client=clients.get(index);
            if(null!=client){
                if (null==local){
                    return client;
                }
                return local?client instanceof LocalClient?client:null:client instanceof LocalClient?null:client;
            }
        }
        return null;
    }

    protected final boolean createFile(boolean directory,String debug){
        Client client=getCurrentClient();
        final String title=getString(directory?R.string.createFolder:R.string.createFile,null);
        if (null==client){
            return toast(getString(R.string.whichFailed, "",title))&&false;
        }
        Context context=getContext();
        InputModel inputModel=new InputModel();
        Dialog dialog=null!=context?new Dialog(context).setContentView(new AlertDialogModel(title,
                null,R.string.sure,R.string.cancel).setContentLayout(inputModel),new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)).
                setCancelable(true).setCanceledOnTouchOutside(true):null;
        return null!=dialog&&dialog.show(this);
//        return null!=createFolder&&createFolder.createPath(getCurrentFolder(), true, (OnApiFinish<Reply<Path>>) (int what, String note, Reply<Path> data, Object arg)-> {
//            toast(getString(what== What.WHAT_SUCCEED&&null!=data&&data.isSuccess()? R.string.whichSucceed:R.string.whichFailed,"",getString(R.string.createFolder,"")));
//        });
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
            final Context context=getContext();
            if (null==context){
                return false;
            }
            Client currentClient=getCurrentClient();
            Client cloudClient=nextClient(false);
            if (null!=cloudClient&&(null==currentClient||currentClient!=cloudClient)){
                setClientSelect(cloudClient,"Before start upload files.");
            }
            //
            Mode mode=new Mode(Mode.MODE_UPLOAD);
            Collection collection=(Collection)files;
            UriPath uriPath=new UriPath();
            for (Object child:collection){
                child=null!=child&&child instanceof Uri?uriPath.getUriPath(context,(Uri)child):child;
                child=null!=child&&child instanceof String?new File((String)child):child;
                child=null!=child&&child instanceof File?LocalPath.create((File)child):child;
                if (null!=child&&child instanceof Path){
                    mode.add((Path)child);
                }
            }
            //
            Dialog dialog=new Dialog(context);
            return dialog.setContentView(new UploadDialogModel()).show();
//            Dialog dialog=new Dialog(getContext());
//            AlertDialogModel model=new AlertDialogModel(R.string.upload).setLeftText(R.string.upload).
//                    setCenterText(R.string.uploadWithDel).setRightText(R.string.cancel);
//            return dialog.setContentView(model).show((OnViewClick)(View view, int i, int i1, Object o)-> {
//                   switch (i){
//                       case R.string.upload:selectMode(mode.setExtra(Label.LABEL_DELETE,null),debug);break;
//                       case R.string.uploadWithDel:selectMode(mode.setExtra(Label.LABEL_DELETE,Label.LABEL_DELETE),debug);break;
//                   }
//                   dialog.dismiss();
//                  return true;
//            });
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

    protected final boolean showPathAttr(Client client,Path path,String debug){

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
        Progress progress=null!=task?task.getProgress():null;
        CharSequence charSequence=null;
        if (null!=progress){
            Object titleObject=progress.getProgress(Progress.TYPE_TITLE);
            String title=null!=titleObject?titleObject.toString():null;
            SpannableStringBuilder builder=new SpannableStringBuilder("");
            int length=null!=title?title.length():-1;
            if (length>0){
                Object perObject=progress.getProgress(Progress.TYPE_PERCENT);
                int start=builder.length();
                String preText=null!=perObject?perObject.toString():null;
                if (null!=preText){
                    builder.append(preText+"% ");
                }
                builder.append(title);
                int end=builder.length();
                builder.setSpan(new ForegroundColorSpan(Color.parseColor("#88ffffff")),start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
            charSequence=builder;
        }
        showNotify(charSequence,null);
    }

    protected boolean showNotify(CharSequence text,String debug){
        mNotifyText.set(text);
        return true;
    }

    protected final boolean startTask(Task task,String debug){
        if (null==task){
            return false;
        }
        if (task instanceof GroupTask &&((GroupTask)task).isEmpty()){
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

    public final ObservableField<CharSequence> getNotifyText() {
        return mNotifyText;
    }
}
