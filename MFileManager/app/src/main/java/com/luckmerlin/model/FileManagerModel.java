package com.luckmerlin.model;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.OnItemSlideRemove;
import com.luckmerlin.adapter.recycleview.Remover;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.databinding.dialog.PopupWindow;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.databinding.touch.OnViewLongClick;
import com.luckmerlin.file.Client;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalClient;
import com.luckmerlin.file.LocalFolder;
import com.luckmerlin.file.Mode;
import com.luckmerlin.file.NasClient;
import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.TaskListActivity;
import com.luckmerlin.file.adapter.ClientAdapter;
import com.luckmerlin.file.api.OnApiFinish;
import com.luckmerlin.file.api.Reply;
import com.luckmerlin.file.api.What;
import java.io.File;

public class FileManagerModel extends FileBrowserModel implements OnViewClick, OnViewLongClick {
    private final PopupWindow mClientNamePopupWindow=new PopupWindow(true,null);

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
//        NasClient client=new NasClient("http://192.168.0.6",2018,"NAS");
//        NasClient client=new NasClient("http://192.168.0.4",2019,"NAS");
        NasClient client=new NasClient("http://192.168.1.6",2019,"NAS");
//        add(new LocalClient("/sdcard",getString(R.string.local,null)).setSyncHost(client.getHostUri()),"");
        add(client,"");
        new File("/sdcard/linqiang2021.mp4").delete();
//        sudo rm /usr/local/mysql
//        sudo rm -rf /usr/local/mysql*
//                sudo rm -rf /Library/StartupItems/MySQLCOM
//        sudo rm -rf /Library/PreferencePanes/My*
//                rm -rf ~/Library/PreferencePanes/My*
//                sudo rm -rf /Library/Receipts/mysql*
//                sudo rm -rf /Library/Receipts/MySQL*
//                sudo rm -rf /var/db/receipts/com.mysql.*
        post(new Runnable() {
            @Override
            public void run() {
                ////            File file=new File("/storage/emulated/0/Android/data/com.luckmerlin.file/cache/1914wx_camera_1618929165018.mp4");
//            File file=new File("/storage/emulated/0/Android/data/com.luckmerlin.file");
//            startTask(new StreamTask(getApplicationContext(),
////                    Uri.fromFile(file),
////                    Uri.fromFile(new File("/sdcard/linqiang2021.mp4")),
//                    Uri.fromFile(new File("/sdcard/DCIM/Camera/IMG_20201112_232734_1_145355615081.jpg")),
//                   Uri.parse(client.getHostUri()+"?"+Label.LABEL_PATH+"="+"/Volumes/Others/linqiang.jpg")
//            ).enableRecheckMd5(true).enableDeleteFail(true),null);
//            startUploadFiles(Uri.fromFile(new File("/sdcard/360/sdk/persistence/data")),true,"");
//            startUploadFiles(Uri.fromFile(new File("/sdcard/360/sdk/persistence/data")),true,"");
//                startUploadFiles(Uri.fromFile(new File("/sdcard/DCIM/Camera/IMG_20201112_232734_1_145355615081.jpg")),
//                        true,"");
            }
        }, 3000);
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
                return selectMode(new Mode(Mode.MODE_MULTI_CHOOSE).add(null!=tag&&tag
                        instanceof Path?(Path)tag:null),"While multi choose view click.");
            case R.drawable.selector_back:
                return onBackKeyPressed("While back view click.");
            case R.string.upload:
                return startUploadFiles(tag,false,"While upload view click.")||true;
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
//                return null!=modeDownload&&modeDownload.getMode()==Mode.MODE_DOWNLOAD&& startTask(new DownloadTask(getString(R.string.download,null),modeDownload.getArgs(),
//                        downloadFolder),"While download view click.")&&
//                        (selectMode(null,"While download view click.")||true);
                return false;
            case R.string.cancel://Get through
            case R.drawable.selector_cancel:
                return selectMode(null,"While cancel view click.");
            case R.string.scanCurrent:
                Client scanClient=getCurrentClient();
                return scanCurrentFolder(scanClient,getCurrentFolder(),"While scan view click.");
            case R.string.attr:
                Client attrClient=getCurrentClient();
                return showPathAttr(attrClient,null!=tag&&tag instanceof Path?(Path)tag:null,null)||true;
            case R.id.itemListFile_icon:
                return toast("打开预览图")||true;
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

    private boolean showClientSelectOption(View view, String debug){
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
