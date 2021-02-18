package com.csdk.api.struct;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import com.csdk.api.core.Label;
import com.csdk.api.data.Json;

public final class Struct implements Parcelable {
    public static final String TYPE_TEXT="text";
    public static final String TYPE_AT="at";
    public static final String TYPE_LINK_TEXT="linkText";
    public static final String TYPE_LINK="link";
    private final static String PROTOCOL="herotalk://";
    private final static String SEP="/";
    private final CharSequence mTitle;
    private final CharSequence mType;
    private Json mData;

    protected Struct(Parcel in) {
        this(in.readString());
    }

    public Struct(String json) {
        this(null!=json? Json.create(json):null);
    }

    public Struct(Json json) {
        this(null!=json?json.optString(Label.LABEL_TYPE,null):null,
                null!=json?json.optString(Label.LABEL_TITLE,null):null,
                null!=json?Json.create(json.optString(Label.LABEL_DATA,null)):null);
    }

    public Struct(CharSequence type,CharSequence title){
        this(type,title,null);
    }

    public Struct(CharSequence type,CharSequence title,Json data){
        mTitle=title;mType=type;mData=data;
    }

    public static final Creator<Struct> CREATOR = new Creator<Struct>() {
        @Override
        public Struct createFromParcel(Parcel in) {
            return new Struct(in);
        }

        @Override
        public Struct[] newArray(int size) {
            return new Struct[size];
        }
    };

    public final boolean isAnyType(String ...types){
        if (null!=types&&types.length>0){
            CharSequence type=getType();
            for (String child:types) {
                if (null!=type&&null!=child&&type.equals(child)){
                    return true;
                }
            }
        }
        return false;
    }

    public Struct setTitleColor(String colorHex){
        Json data=mData;
        data=null!=data?data:(mData=new Json());
        if (null==colorHex||colorHex.length()<=0){
            data.remove(Label.LABEL_COLOR);
        }else{
            data.putSafe(Label.LABEL_COLOR,colorHex);
        }
        return this;
    }

    public Json getData() {
        return mData;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public CharSequence getType() {
        return mType;
    }

    public Integer getTitleColor(){
        Json data=mData;
        String colorText=data.optString(Label.LABEL_COLOR,null);
        try {
            colorText=null!=colorText&&!colorText.startsWith("#")?"#"+colorText:colorText;
            colorText=null!=colorText?colorText.trim().toLowerCase():null;
            return null!=colorText&&colorText.length()>0&&colorText.startsWith("#")&&
                    colorText.length()%2==1? Color.parseColor(colorText):null;
        }catch (Exception e){
            //Do nothing
        }
        return null;
    }

    public String toJson(){
        return null;
    }

    public final int length(){
        CharSequence charSequence=toText();
        return null!=charSequence?charSequence.length():-1;
    }

    public CharSequence toText(){
        CharSequence type=mType;
        if (null==type){
            return null;
        }else if (type.equals(TYPE_TEXT)){
            return mTitle;
        }else if(type.equals(TYPE_LINK_TEXT)){
            final CharSequence title=mTitle;
            return null!=title?"【"+title+"】":null;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(toJson());
    }
}
