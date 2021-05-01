package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public final class FileInput implements Input  {
    private final File mFile;
    private InputStream mInputStream;
    private LocalPath mLocalPath;
    private String mMd5=null;

    public FileInput(File file) {
        mFile=file;
    }

    @Override
    public CodeResult open(long seek) throws Exception {
        File file=mFile;
        if (null==file||!file.exists()) {
            Debug.W("Can't open input while from invalid.");
            return new CodeResult(What.WHAT_NOT_EXIST);
        }else if (file.isDirectory()?!file.canExecute():!file.canRead()){
            Debug.W("Can't open input while from NONE permission.");
            return new CodeResult(What.WHAT_NONE_PERMISSION);
        }else if (seek<0){
            Debug.W("Can't open Uri inputStream while seek invalid."+seek);
            return new CodeResult(What.WHAT_FAIL);
        }else if (file.isDirectory()){
            Debug.W("Can't open Uri inputStream while file is directory.");
            return new CodeResult<>(What.WHAT_FAIL);
        }
        new Closer().close(mInputStream);
        InputStream inputStream=mInputStream=new FileInputStream(file);
        if (seek>0&&inputStream.skip(seek)!=seek){
            Debug.W("Can't open Uri inputStream while seek fail."+seek);
            new Closer().close(inputStream);
            return new CodeResult<>(What.WHAT_FAIL);
        }
        return new CodeResult<>(What.WHAT_SUCCEED);
    }

    @Override
    public int read(byte[] buffer) throws Exception {
        InputStream inputStream=mInputStream;
        return null!=inputStream&&null!=buffer?inputStream.read(buffer):-1;
    }

    @Override
    public Path close() {
        InputStream inputStream=mInputStream;
        if (null!=inputStream){
            mInputStream=null;
            new Closer().close(inputStream);
            mLocalPath=null;//Clean to reload path
        }
        return getLocalPath();
    }

    @Override
    public String getName() {
        File file=mFile;
        return null!=file?file.getName():null;
    }

    @Override
    public long getLength() {
        File file=mFile;
        return null!=file?file.length():0;
    }

    final LocalPath getLocalPath(){
        if (null==mLocalPath){
            File file=mFile;
            return null!=file?mLocalPath=LocalPath.create(file,true,null):null;
        }
        return mLocalPath;
    }
}
