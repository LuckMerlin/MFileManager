package com.luckmerlin.model;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import androidx.databinding.ViewDataBinding;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.R;
import com.luckmerlin.file.TransportActivity;
import com.luckmerlin.file.databinding.FileBrowserMenuBinding;

public class FileManagerModel extends FileBrowserModel {

    @Override
    public boolean onViewClick(View view, int i, int i1, Object tag) {
        if (!super.onViewClick(view,i,i1,tag)){
            switch (i){
                case R.drawable.selector_menu:
                    return showBrowserMenu(view,"While menu view click.");
                case R.string.exit:
                    return finishActivity("While exit view click.");
                case R.string.transportManager:
                    return startActivity(TransportActivity.class,null,"After transport view click.");
                case R.string.multiChoose:
                    return selectMode(Mode.MODE_MULTI_CHOOSE,"While multi choose view click.");
            }
        }
        return false;
    }

    private boolean showBrowserMenu(View view, String debug){
        Context context=null!=view?view.getContext():null;
        context=null!=context?context:getContext();
        ViewDataBinding binding=null!=context?DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.file_browser_menu,null,false):null;
        if (null!=binding&&binding instanceof FileBrowserMenuBinding){
            FileBrowserMenuBinding browserBinding=(FileBrowserMenuBinding)binding;
            browserBinding.setClient(getCurrentClientObject());
            browserBinding.setFolder(getCurrentFolderObject());
            return showAtLocationAsContext(view,browserBinding,this);
        }
        return false;
    }
}
