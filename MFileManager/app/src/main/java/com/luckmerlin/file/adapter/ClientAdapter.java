package com.luckmerlin.file.adapter;

import android.view.ViewGroup;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.R;
import com.luckmerlin.file.databinding.ItemClientNameBinding;
import java.util.List;

public final class ClientAdapter extends ListAdapter<Client> {

    public ClientAdapter(List<Client> clients){
        set(clients,null);
    }

    @Override
    protected Object onResolveDataViewHolder(ViewGroup viewGroup) {
        return R.layout.item_client_name;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ViewDataBinding binding, int i1, Client client, List<Object> list) {
        super.onBindViewHolder(viewHolder, i, binding, i1, client, list);
        if (null!=binding&&binding instanceof ItemClientNameBinding){
            ItemClientNameBinding clientNameBinding=(ItemClientNameBinding)binding;
            clientNameBinding.setClient(client);
        }
    }
}
