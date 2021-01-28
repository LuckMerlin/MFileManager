package com.luckmerlin.file.ui;

public final class ContextMenu {
    private final Object mText;
    private final Object mArg;

    public ContextMenu(Object text, Object arg){
        mText=text;
        mArg=arg;
    }

    public Object getText() {
        return mText;
    }

    public Object getArg() {
        return mArg;
    }

    public static ContextMenu create(Object text){
        return create(text,null);
    }

    public static ContextMenu create(Object text,Object arg){
        return new ContextMenu(text,arg);
    }

}
