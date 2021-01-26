package com.luckmerlin.file.api;

public final class Reply<T> {
    private String note;
    private boolean success;
    private int what;
    private T data;

    public Reply(){
        this(false,0,null,null);
    }

    public Reply(boolean success, int what, String note, T data){
        this.success=success;
        this.what=what;
        this.note=note;
        this.data=data;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "note='" + note + '\'' +
                ", success=" + success +
                ", what=" + what +
                ", data=" + data +
                '}';
    }
}
