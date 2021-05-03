package com.luckmerlin.file.task;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;

import java.io.InputStream;

public class ContentInput implements Input {
    private final ContentResolver mContentResolver;
    private InputStream mInputStream;
    private final Uri mUri;
    private Path mPath;

    public ContentInput(Uri uri,ContentResolver contentResolver){
        mContentResolver=contentResolver;
        mUri=uri;
    }

    @Override
    public CodeResult open(long seek) throws Exception {
        ContentResolver resolver=mContentResolver;
        Uri uri=mUri;
        if (null==resolver){
            Debug.W("Can't create Uri input while resolver is NULL.");
            return new CodeResult(What.WHAT_FAIL);
        }else if (seek<0){
            Debug.W("Can't open Uri inputStream while seek invalid."+seek);
            return new CodeResult<>(What.WHAT_FAIL);
        }else if (null==uri){
            Debug.W("Can't open Uri inputStream while uri NULL.");
            return new CodeResult<>(What.WHAT_FAIL);
        }
        InputStream inputStream = resolver.openInputStream(uri);
        if (null!=inputStream&&seek>0&&inputStream.skip(seek)!=seek){
            Debug.W("Can't open Uri inputStream while seek fail."+seek);
            return new CodeResult<>(What.WHAT_FAIL);
        }
        return new CodeResult(null==inputStream?What.WHAT_FAIL:What.WHAT_SUCCEED);
    }

    @Override
    public int read(byte[] buffer) throws Exception {
        InputStream inputStream=mInputStream;
        if (null==inputStream){
            return -1;
        }
        return inputStream.read(buffer);
    }

    @Override
    public Path close() {
        InputStream inputStream=mInputStream;
        if (null!=inputStream){
            new Closer().close(inputStream);
            mInputStream=null;
        }
        return getPath();
    }

    @Override
    public String getName() {
        Path path=getPath();
        return null!=path?path.getNameWithExtension():null;
    }

    @Override
    public long getLength() {
        Path path=getPath();
        return null!=path?path.getLength():-1;
    }

    private Path getPath(){
        Path path=mPath;
        if (null==path){
            ContentResolver resolver=mContentResolver;
            Uri uri=mUri;
            Cursor cursor = null!=resolver&&null!=uri?resolver.query(uri, null,
                    null, null, null, null):null;
            long fileSize=0;
            if (null!=cursor){
                while (cursor.moveToFirst()){
                    String[] columns=cursor.getColumnNames();
                    if (null!=columns&&columns.length>0){
                        for (String child:columns) {
                            Debug.D("QQQQQQQQQQQQQ  "+child+" "+cursor.getString(cursor.getColumnIndex(child)));
                        }
                    }
//                taskName=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//                fileSize=cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                    break;
                }
                cursor.close();
            }
        }
        return mPath;
    }
}
