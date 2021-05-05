package com.luckmerlin.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.file.Folder;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.R;
import com.luckmerlin.file.adapter.UploadPrepareListAdapter;
import com.luckmerlin.file.task.StreamTask;
import com.luckmerlin.file.ui.UriPath;
import com.luckmerlin.lib.StringBuffer;
import com.luckmerlin.task.Status;
import com.luckmerlin.task.Task;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

public class UploadDialogModel extends Model implements OnModelResolve, OnViewClick {
    private final ObservableField<Object> mStatus=new ObservableField<>(R.string.upload);
    private final ObservableField<String> mMessage=new ObservableField<>();
    private final boolean mDeleteSucceed;
    private final UploadPrepareListAdapter mPrepareListAdapter=new UploadPrepareListAdapter();
    private final Folder mFolder;
    private final Object mFiles;
    private boolean mCanceled=false;

    public UploadDialogModel(Object files,Folder folder,boolean deleteSucceed){
        mDeleteSucceed=deleteSucceed;
        mFolder=folder;
        mFiles=files;
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        mCanceled=false;
        mStatus.set(R.string.preparing);
        Observable.create((ObservableEmitter<Collection> emitter)->{
            prepare(mFiles,mFolder,null);
            post(()->mStatus.set(getString(R.string.sureWhich,null,getString(R.string.upload,null))));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean setStatus(Object status){
        mStatus.set(status);
        return false;
    }

    private boolean prepare(Object files,Folder folder,List<String> layers){
        Object currentStatus=mStatus.get();
        if (null==currentStatus||!(currentStatus instanceof Integer)||((Integer)currentStatus)!=R.string.preparing){
            return false;
        }else if (mCanceled){
            return setStatus(getString(R.string.whichFailed,null,getString(R.string.cancel,null)))&&false;
        }else if (null==folder){
            return false;
        }
        final UploadPrepareListAdapter listAdapter=mPrepareListAdapter;
        final Object status=mStatus.get();
        if (null==files||null==listAdapter||null==folder||folder.isLocal()){
            return setStatus(getString(R.string.whichFailed,null,getString(R.string.prepare,null)))&&false;
        }else if (null!=status&&status instanceof Integer&&(Integer)status==Status.PREPARING){
            return setStatus(getString(R.string.alreadyWhich,null,getString(R.string.prepare,null)))&&false;
        }else if (files instanceof String){
            return prepare(new File((String)files),folder,layers);
        }else if (files instanceof File){
            return prepare(Uri.fromFile((File)files),folder,layers);
        }else if (files instanceof Path){
            return prepare(((Path)files).toUri(),folder,layers);
        }else if (files instanceof Collection){
            Collection collection=(Collection)files;
            for (Object child:collection) {
                if (mCanceled){
                    break;
                }
                prepare(child,folder,layers);
            }
            return true;
        }else if (files instanceof Uri){
            Uri uri=(Uri)files;
            String fileName=null;
            String scheme=uri.getScheme();
            if (null!=scheme&&scheme.equals(ContentResolver.SCHEME_FILE)){
                String localFilePath=uri.getPath();
                File localFile=null!=localFilePath&&localFilePath.length()>0?new File(localFilePath):null;
                fileName=null!=localFile?localFile.getName():null;
                if (null==fileName||fileName.length()<=0){
                    Debug.W("Fail scan directory files while file name invalid.");
                    return false;
                }
                (layers=null!=layers?layers:new LinkedList<>()).add(fileName);
                if (null!=localFile&&localFile.isDirectory()){//Browser all files
                    String folderSep=folder.getSep();
                    if (null==folderSep||folderSep.length()<=0){
                        Debug.W("Fail scan directory files while folder sep invalid.");
                        return false;
                    }
                    File[] listFiles=localFile.listFiles();
                    if (null==listFiles||listFiles.length<=0){//Empty
                        return true;
                    }
                    for (File child:listFiles) {
                        prepare(Uri.fromFile(child),folder,layers);
                    }
                    return true;
                }
            }else if (null!=scheme&&scheme.equals(ContentResolver.SCHEME_CONTENT)){
                ContentResolver contentResolver=getContentResolver();
                Cursor cursor = null!=contentResolver?contentResolver.query(uri, null, null,
                        null, null, null):null;
                File contentFile=null;
                if (null!=cursor){
                    while (cursor.moveToFirst()){
                        String filePath=cursor.getString(cursor.getColumnIndex("_data"));
                        contentFile=null!=filePath&&filePath.length()>0?new File(filePath):null;
                        break;
                    }
                    cursor.close();
                }
                return null!=contentFile&&prepare(contentFile,folder,layers);
            }
//            Debug.D("WWWWWWWWWW "+scheme+" "+taskName+" "+uri.getPath()+" "+uri.toString());
//            StreamTask task=new StreamTask(uri,null!=layers&&layers.size()>0?folder.getChildUri(layers):null);
//            Debug.D("上传 "+task.getName()+" from="+task.getFrom()+" to="+task.getTo());
            return addFileTask(new StreamTask(fileName,uri,folder.getChildUri(layers)));
        }
        return false;
    }

    private boolean addFileTask(StreamTask task){
        UploadPrepareListAdapter adapter=mPrepareListAdapter;
        if (null!=task&&null!=adapter){
            final String fileName=task.getName();
            return post(()->{
                StringBuffer buffer=new StringBuffer();
                buffer.append(null!=fileName&&fileName.length()>15?fileName.substring(0,14):fileName);
                adapter.add(task);
                buffer.append("\n"+getString(R.string.summeryItemWhich,null,adapter.getDataCount()));
                mMessage.set(buffer.toString());
            });
        }
        return false;
    }

    @Override
    public boolean onViewClick(View view, int i, int i1, Object o) {
        if (i==R.string.cancel){
            mCanceled=true;
            return true;
        }
        return false;
    }

    public final boolean isCanceled(){
        return mCanceled;
    }

    public final List<Task> getTasks(){
        return mPrepareListAdapter.getData();
    }

    public final ObservableField<Object> getStatus() {
        return mStatus;
    }

    public final UploadPrepareListAdapter getPrepareListAdapter() {
        return mPrepareListAdapter;
    }

    public final ObservableField<String> getMessage() {
        return mMessage;
    }

    @Override
    public final Object onResolveModel() {
        return R.layout.upload_dialog_model;
    }
}
