package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.util.Closer;
import com.luckmerlin.file.LocalPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.What;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

final class FileOutput implements Output{
    private final File mFile;
    private FileOutputStream mOutputStream;

    public FileOutput(File file) {
        mFile=file;
    }

    @Override
    public long getLength() {
        File file=mFile;
        return null!=file?file.length():-1;
    }

    @Override
    public CodeResult open(long seek) throws Exception {
        final File file=mFile;
        if (null==file){
            Debug.W("Can't create outputStream while file null.");
            return new CodeResult<>(What.WHAT_ARGS_INVALID);
        }else if (!file.exists()){
            File parent=file.getParentFile();
            if (null!=parent&&!parent.exists()){
                parent.mkdirs();
            }
            file.createNewFile();
            if (!file.exists()){
                Debug.W("Can't create outputStream while create file fail.");
                return new CodeResult<>(What.WHAT_CREATE_FAILED);
            }
            Debug.D("Create file while task open uri stream."+file);
        }
        final long length=file.length();
        if (seek>0&&seek!=length){
            Debug.W("Can't create outputStream while seek not match.");
            return new CodeResult<>(What.WHAT_FAIL);
        }
        mOutputStream=new FileOutputStream(file,seek>0);
        return new CodeResult(What.WHAT_SUCCEED);
    }

    @Override
    public Path close() {
        OutputStream outputStream=mOutputStream;
        mOutputStream=null;
        if (null!=outputStream){
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                new Closer().close(outputStream);
            }
        }
        File file=mFile;
        return null!=file?LocalPath.create(file):null;
    }

    @Override
    public boolean write(byte[] buffer, int offset, int length) throws Exception {
        FileOutputStream outputStream=mOutputStream;
        if (null!=outputStream&&null!=buffer){
            outputStream.write(buffer,offset,length);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete() {
        File file=mFile;
        return null!=file&&file.exists()&&file.delete()&&!file.exists();
    }
}
