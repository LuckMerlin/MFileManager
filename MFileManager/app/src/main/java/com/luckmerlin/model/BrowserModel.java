package com.luckmerlin.model;

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

public class BrowserModel extends Model implements OnViewClick, OnPathSpanClick {
    private final ObservableField<Client> mCurrentClient=new ObservableField<Client>();
    private final ObservableField<Integer> mClientCount=new ObservableField<Integer>();
    private final ObservableField<Integer> mCurrentSelectSize=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mBrowserMode=new ObservableField<>(Mode.NONE);
    private final FileBrowserAdapter mBrowserAdapter=new FileBrowserAdapter(){
        @Override
        protected void onReset(boolean succeed, Section<Query, Path> section) {
            super.onReset(succeed, section);
            mCurrentFolder.set(null!=section&&section instanceof Folder?(Folder)section:null);
        }
    };

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        selectClient(new NasClient("http://192.168.0.6",2018),"While root attached.");
    }

    private boolean selectClient(Client client,String debug){
        FileBrowserAdapter browserAdapter=mBrowserAdapter;
        return null!=browserAdapter&&browserAdapter.setClient(client,debug);
    }

    @Override
    public void onPathSpanClick(Path path, int start, int end, String value) {

    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        Debug.D("QQQQQQQQ "+i+" "+i1+" "+o);
        switch (i1){
            case R.drawable.selector_back:
                return onBackKeyPressed("While back view click.");
        }
        return false;
    }

    private boolean onBackKeyPressed(String debug){
        toast("dddddddddddd");
        return false;
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

    public ObservableField<Folder> getCurrentFolder() {
        return mCurrentFolder;
    }

    public FileBrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }

    public ObservableField<Integer> getBrowserMode() {
        return mBrowserMode;
    }
}
