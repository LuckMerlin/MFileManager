package com.luckmerlin.file.task;

import android.net.Uri;

import com.luckmerlin.file.NasPath;
import com.luckmerlin.file.Path;
import com.luckmerlin.file.api.Label;

public class NasInput implements Input {
    private final String mHostUri;
    private final String mPath;
    private NasPath mNasPath;

    public NasInput(Uri uri) {
        String host=null!=uri?uri.getHost():null;
        String scheme=null!=uri?uri.getScheme():null;
        host=null!=host&&null!=scheme?scheme+"://"+host:null;
        mHostUri=null!=host?host+":"+uri.getPort():null;
        mPath=null!=uri?uri.getQueryParameter(Label.LABEL_PATH):null;
    }

    @Override
    public CodeResult open(long seek) throws Exception {
        return null;
    }

    @Override
    public int read(byte[] buffer) throws Exception {
        return 0;
    }

    @Override
    public Path close() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public long getLength() {
        return 0;
    }
}
