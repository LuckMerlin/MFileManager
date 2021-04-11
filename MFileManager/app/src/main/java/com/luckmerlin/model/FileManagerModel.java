package com.luckmerlin.model;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.databinding.dialog.PopupWindow;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.databinding.touch.OnViewLongClick;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalClient;
import com.luckmerlin.file.LocalFolder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.NasClient;
import com.luckmerlin.file.NasFolder;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.TaskListActivity;
import com.luckmerlin.file.adapter.ClientAdapter;
import com.luckmerlin.file.adapter.FileBrowserAdapter;
import com.luckmerlin.file.api.Label;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import com.luckmerlin.file.databinding.FileBrowserMenuBinding;
import com.luckmerlin.file.nas.Nas;
import com.luckmerlin.file.task.DownloadTask;
import com.luckmerlin.file.task.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManagerModel extends FileBrowserModel implements OnViewClick, OnViewLongClick {
    private final PopupWindow mClientNamePopupWindow=new PopupWindow(true,null);

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        NasClient client=new NasClient("http://192.168.0.6",2018,"NAS");
//        NasClient client=new NasClient("http://192.168.0.4",2019,"NAS");
        add(new LocalClient("/sdcard",getString(R.string.local,null)).setSyncHost(client.getHostUri()),"");
        add(client,"");
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object tag) {
        switch (i){
            case R.drawable.selector_menu:
                return showBrowserMenu(view,"While menu view click.");
            case R.id.fileBrowser_clientNameTV:
                return null!=tag&&tag instanceof Client&&(i1<=1?switchSelectClient(null,"While view click."):
                        showClientSelectOption(view,"While view click."));
            case R.string.exit:
                return finishActivity("While exit view click.");
            case R.string.transportManager:
                return startActivity(TaskListActivity.class,null,"After transport view click.");
            case R.string.createFolder:
                return createFile(true,"While view click.");
            case R.string.createFile:
                return createFile(false,"While view click.");
            case R.string.rename:
                return renameFile(null!=tag&&tag instanceof Path?((Path)tag):null,true,"While view click.");
            case R.string.setAsHome:
                Client client=getCurrentClient();
                return null!=client&&client.setAsHome(getCurrentFolder(), (OnApiFinish<Reply<Path>>) (int what, String note, Reply<Path> data, Object arg)-> {
                    toast(getString(what== What.WHAT_SUCCEED&&null!=data&&data.isSuccess()? R.string.whichSucceed:R.string.whichFailed,"",getString(R.string.setAsHome,"")));
                });
            case R.string.multiChoose:
                return selectMode(new Mode(Mode.MODE_MULTI_CHOOSE),"While multi choose view click.");
            case R.drawable.selector_back:
                return onBackKeyPressed("While back view click.");
            case R.string.upload:
                Mode modeUpload=getMode();
                if (null!=tag&&tag instanceof LocalPath){
                    modeUpload=null!=(modeUpload=(null!=modeUpload?modeUpload.cleanArgs():null))&&
                            modeUpload.getMode()==Mode.MODE_UPLOAD?modeUpload:new Mode(Mode.MODE_UPLOAD);
                    return selectMode(modeUpload.add((LocalPath)tag),"While upload view click.");
                }
                Folder uploadFolder=getCurrentFolder();
                if (null==uploadFolder||!(uploadFolder instanceof NasFolder)){
                    return toast(R.string.notActionHere)||true;
                }else if (null!=modeUpload&&modeUpload.getMode()==Mode.MODE_UPLOAD){
                    ArrayList<Path> paths=modeUpload.getArgs();
                    if (null==paths||paths.size()<=0){
                        toast(R.string.emptyContent);
                    }else{
                        boolean deleteSucceed=modeUpload.isExistExtra(Label.LABEL_DELETE,Label.LABEL_DELETE);
                        for (Path child:paths){
                            if (null!=child){
                                startTask(new UploadTask(child.getName(),child,uploadFolder).
                                        deleteSucceed(deleteSucceed),"While upload view click.");
                            }
                        }
                    }
                    return selectMode(null,"While upload view click.");
                }
                return true;
            case R.string.download:
                Mode modeDownload=getMode();
                if (null!=tag&&tag instanceof NasPath){
                    modeDownload=null!=(modeDownload=(null!=modeDownload?modeDownload.cleanArgs():null))
                            &&modeDownload.getMode()==Mode.MODE_DOWNLOAD?modeDownload:new Mode(Mode.MODE_DOWNLOAD);
                    return selectMode(modeDownload.add((NasPath)tag),"While download view click.");
                }
                Folder downloadFolder=getCurrentFolder();
                if (null==downloadFolder||!(downloadFolder instanceof LocalFolder)){
                    return toast(R.string.notActionHere)||true;
                }
                return null!=modeDownload&&modeDownload.getMode()==Mode.MODE_DOWNLOAD&& startTask(new DownloadTask(getString(R.string.download,null),modeDownload.getArgs(),
                        downloadFolder),"While download view click.")&&
                        (selectMode(null,"While download view click.")||true);
            case R.string.cancel:
                return selectMode(null,"While cancel view click.");
            case R.string.attr:
                Client attrClient=getCurrentClient();
                return showPathAttr(attrClient,null!=tag&&tag instanceof Path?(Path)tag:null,null)||true;
            default:
                if (null!=tag&&tag instanceof Path){
                    return openPath(((Path)tag),"While path view click.");
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onViewLongClick(View view, int i, Object tag) {
        if (null!=tag&&tag instanceof Path){
            return showPathContextMenu(((Path)tag),"While path view long click.");
        }
        return false;
    }

    private boolean showClientSelectOption(View view,String debug){
        PopupWindow popupWindow=null!=view?mClientNamePopupWindow:null;
        if (null!=popupWindow){
            if (popupWindow.isShowing()){
                popupWindow.dismiss();
                return true;
            }
            final ClientAdapter clientAdapter=new ClientAdapter(getClients());
            RecyclerView recyclerView=new RecyclerView(view.getContext());
            recyclerView.setPadding(20,20,20,20);
            recyclerView.setBackgroundResource(R.drawable.dialog_round_corner_gray);
            recyclerView.setAdapter(clientAdapter);
            recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.
                    LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(recyclerView);
            return null!=popupWindow.showAsDropDown(view,0,0,
                    PopupWindow.DISMISS_INNER_MASK|PopupWindow.DISMISS_OUT_MASK);
        }
        return false;
    }

    private boolean showBrowserMenu(View view, String debug) {
        Context context=null!=view?view.getContext():null;
        context=null!=context?context:getContext();
        final Dialog dialog=new Dialog(context);
        BrowserContextModel model=new BrowserContextModel(getCurrentClient(),getCurrentFolder()){
            @Override
            public boolean onViewClick(View view, int i, int i1, Object o) {
                dialog.dismiss();
                return FileManagerModel.this.onViewClick(view, i, i1, o);
            }
        };
        return dialog.setContentView(model).setCancelable(true).setCanceledOnTouchOutside(true).show();
    }

    private boolean showPathContextMenu(Path path,String debug){
        final Dialog dialog=new Dialog(getContext());
        return null!=path&&dialog.setCanceledOnTouchOutside(true).setContentView(new FileContextMenuModel(path){
                    @Override
                    public boolean onViewClick(View view, int i, int i1, Object o) {
                        dialog.dismiss();
                        return super.onViewClick(view, i, i1, o)||FileManagerModel.this.onViewClick(view,i,i1,o);
                    }},new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)).show();
    }

}
