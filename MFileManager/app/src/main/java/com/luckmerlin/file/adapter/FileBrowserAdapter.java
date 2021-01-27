package com.luckmerlin.file.adapter;

import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.ItemSlideRemover;
import com.luckmerlin.adapter.recycleview.ItemTouchInterrupt;
import com.luckmerlin.adapter.recycleview.OnItemTouchResolver;
import com.luckmerlin.adapter.recycleview.SectionListAdapter;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.Thumbs;
import com.luckmerlin.file.databinding.ItemListFileBinding;

import java.util.List;

public final class FileBrowserAdapter<T extends Path> extends SectionListAdapter<String, T> implements OnItemTouchResolver {
    private Client mClient;

    @Override
    protected final Canceler onNextSectionLoad(SectionRequest<String> request, OnSectionLoadFinish<String, T> callback, String s) {
        Client client=mClient;
        return null!=client?client.onNextSectionLoad(request,callback,s):null;
    }

    public final boolean setClient(Client client, String debug) {
        Client current=mClient;
        if (null==client&&null!=current){
            mClient=null;
            return reset(debug);
        }else if (null!=client&&!client.equals(current)){
            mClient=client;
            return reset(debug);
        }
        return false;
    }

    public final boolean reset(String debug){
        Client client=mClient;
        clean("While client reset to NULL.");
        return null!=client&&resetSection(debug);
    }

    @Override
    protected final Object onResolveDataViewHolder(ViewGroup viewGroup) {
        return R.layout.item_list_file;
    }

    @Override
    public final RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ViewDataBinding binding, int i1, T data, List<Object> list) {
        super.onBindViewHolder(viewHolder, i, binding, i1, data, list);
        if (null!=binding&&binding instanceof ItemListFileBinding){
            ItemListFileBinding fileBinding=(ItemListFileBinding)binding;
            fileBinding.setPath(data);
            fileBinding.setPosition(i+1);
        }
    }

    @Override
    public final Object onResolveItemTouch(RecyclerView recyclerView) {
        return new ItemTouchInterrupt(){
            @Override
            protected Integer onResolveSlide(RecyclerView.ViewHolder holder, RecyclerView.LayoutManager manager) {
                return new ItemSlideRemover().onResolveSlide(holder,manager);
            }
        };
    }

}
