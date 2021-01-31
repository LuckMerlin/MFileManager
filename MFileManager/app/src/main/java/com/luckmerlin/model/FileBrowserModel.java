package com.luckmerlin.model;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
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
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.NasClient;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.Query;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.FileBrowserAdapter;
import com.luckmerlin.file.databinding.AlertDialogBinding;
import com.luckmerlin.file.ui.OnPathSpanClick;
import com.luckmerlin.mvvm.activity.OnActivityBackPress;

public class FileBrowserModel extends Model implements OnViewClick, OnPathSpanClick, OnActivityBackPress {
    private final ObservableField<Client> mCurrentClient=new ObservableField<Client>();
    private final ObservableField<Integer> mClientCount=new ObservableField<Integer>();
    private final ObservableField<Integer> mCurrentSelectSize=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mBrowserMode=new ObservableField<>(Mode.NONE);
    private final ObservableField<String> mSearchInput=new ObservableField<>();
    private final FileBrowserAdapter mBrowserAdapter=new FileBrowserAdapter(){
        @Override
        protected void onReset(boolean succeed, Section<Query, Path> section) {
            super.onReset(succeed, section);
            mCurrentFolder.set(null!=section&&section instanceof Folder?(Folder)section:null);
        }

        @Override
        protected void onClientChanged(Client client) {
            super.onClientChanged(client);
            mCurrentClient.set(client);
        }
    };

    public final boolean selectMode(int mode,String debug){
        Integer current=mBrowserMode.get();
        if (null==current||(current!=mode)){
            mBrowserMode.set(mode);
            return true;
        }
        return false;
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
//        selectClient(new NasClient("http://192.168.0.6",2018,"NAS"),"While root attached.");
        selectClient(new LocalClient("/sdcard/android",getString(R.string.local,null)),"While root attached.");
        showAlertDialog("是的发送到发",null);
    }

    private boolean selectClient(Client client,String debug){
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
    public boolean onViewClick(View view, int i, int i1, Object tag) {
        switch (i){
            case R.drawable.selector_back:
                return onBackKeyPressed("While back view click.");
        }
        if (null!=tag&&tag instanceof Path){
            return openPath(((Path)tag),"While path view click.");
        }
        return false;
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return onBackKeyPressed("While activity back pressed.");
    }

    private boolean onBackKeyPressed(String debug){
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

    private boolean openPath(Path path,String debug){
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

    public final Client getCurrentClientObject() {
        ObservableField<Client> client=getCurrentClient();
        return null!=client?client.get():null;
    }

    public final Folder getCurrentFolderObject() {
        ObservableField<Folder> folder=getCurrentFolder();
        return null!=folder?folder.get():null;
    }

    public final boolean showAlertDialog(Object title, TouchListener callback){
        Context context=getContext();
        Dialog dialog=new Dialog(context);
        return showAtLocation();
    }

    public ObservableField<Client> getCurrentClient() {
        return mCurrentClient;
    }

    public ObservableField<Integer> getClientCount() {
        return mClientCount;
    }

    public ObservableField<Integer> getCurrentSelectSize() {
        return mCurrentSelectSize;
    }

    public final ObservableField<Folder> getCurrentFolder() {
        return mCurrentFolder;
    }

    public FileBrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }

    public ObservableField<Integer> getBrowserMode() {
        return mBrowserMode;
    }

}
