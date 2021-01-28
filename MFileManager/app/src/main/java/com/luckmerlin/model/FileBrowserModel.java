package com.luckmerlin.model;

import android.app.Activity;
import android.view.View;
import androidx.databinding.ObservableField;

import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.NasClient;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.Query;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.FileBrowserAdapter;
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

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        selectClient(new NasClient("http://192.168.0.6",2018,"NAS"),"While root attached.");
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
            return browserPath(((Path)tag).getPath(),"While path view click.");
        }
        return false;
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return onBackKeyPressed("While activity back presssed.");
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
