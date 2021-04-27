package com.luckmerlin.file.task;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.file.MD5;
import com.luckmerlin.file.api.Label;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileReadableStream  implements ReadableStream{
    private final File mFile;
    private List<String> mFiles;
    private byte[] mResidueBytes;
    private InputStream mInputStream=null;

    public FileReadableStream(File file){
        mFile=file;
    }

    private void prepare(File file,List<String> files){
        if (null!=file&&null!=files){
            final String path=file.getAbsolutePath();
            if (file.isDirectory()){
                File[] directoryFiles=file.listFiles();
                int length=null!=directoryFiles?directoryFiles.length:-1;
                if (length>0){
                    for (int i = 0; i < length; i++) {
                        prepare(directoryFiles[i],files);
                    }
                    return;
                }
            }
            if(null!=path&&path.length()>0){
                files.add(path);
            }
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        if (null==mFiles){//If prepare files
            List<String> files=new ArrayList<>();
            prepare(mFile, files);
            mFiles=files.size()>0?files:null;
        }
        byte[] residueBytes=mResidueBytes;
        if (null!=residueBytes&&residueBytes.length>0){
            return -1;
        }
        mResidueBytes=null;
        InputStream inputStream=mInputStream;
        if (null!=inputStream){
            int read=inputStream.read(b);
            if (read>=0){
                return read;
            }
            inputStream.close();
        }
        mInputStream=null;//Next file
        final List<String> files=mFiles;
        if (null==files||files.size()<=0){
            Debug.W("Prepare file read fail.");
            return -1;
        }
        final String nextPath=files.remove(0);
        File file=null!=nextPath&&nextPath.length()>0?new File(nextPath):null;
        if (null==file){
            return 0;
        }
        final boolean isDirectory=file.isDirectory();
        JSONObject json=new JSONObject();
        try {
            long length=file.length();
            String md5="";
            if (!isDirectory&&length>0){
                if (null==(md5=new MD5().getFileMD5(file))||md5.length()<=0){
                    return -1;
                }
            }
            json.put(Label.LABEL_LENGTH,length);
            json.put(Label.LABEL_NAME,file.getName());
            json.put(Label.LABEL_FOLDER, isDirectory?Label.LABEL_FOLDER:"");
            json.put(Label.LABEL_MD5,md5);
            String jsonString=json.toString();
            byte[] bytes=null!=jsonString?jsonString.getBytes():null;
            if (null==bytes||bytes.length<=0){
                return -1;
            }
            mResidueBytes=bytes;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        mInputStream=new FileInputStream(file);
        return 0;
    }

    @Override
    public void close() throws IOException {
        InputStream inputStream=mInputStream;
        mInputStream=null;
        if (null!=inputStream){
            inputStream.close();
        }
    }
}
