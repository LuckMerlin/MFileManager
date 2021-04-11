package com.luckmerlin.model;

import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.R;

public class BrowserContextModel extends Model implements OnModelResolve, OnViewClick {
    private final ObservableField<Client> mClient=new ObservableField<>();
    private final ObservableField<Folder> mFolder=new ObservableField<>();

    public BrowserContextModel(Client client, Folder folder){
        mClient.set(client);
        mFolder.set(folder);
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        return false;
    }

    @Override
    public Object onResolveModel() {
        return R.layout.file_browser_menu;
    }

    public ObservableField<Client> getClient() {
        return mClient;
    }

    public ObservableField<Folder> getFolder() {
        return mFolder;
    }
}
