package com.luckmerlin.file;

import java.io.File;

public final class Thumb {
    private final Path mPath;
    private final File mThumb;

    public Thumb(){
        this(null,null);
    }

    public Thumb(Path path,File file){
        mPath=path;
        mThumb=file;
    }

    public File getThumb() {
        return mThumb;
    }

    public Path getPath() {
        return mPath;
    }
}
