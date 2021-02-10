package com.csdk.api.struct;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Create LuckMerlin
 * Date 16:01 2021/1/25
 * TODO
 */
public class StructArray extends Struct {
    private List<SimpleStruct> mStructs;

    public StructArray() {
        this(null);
    }

    public StructArray(JSONArray jsonArray) {
        int length=null!=jsonArray?jsonArray.length():-1;
        List<SimpleStruct> structs=mStructs=new ArrayList<>();
        JSONObject json=null;
        try {
            for (int i = 0; i < length; i++) {
                if (null!=(json=jsonArray.getJSONObject(i))){
                    structs.add(new SimpleStruct(json));
                }
            }
        }catch (Exception e){
            //Do nothing
        }
    }

    public final SimpleStruct getFirst(){
        List<SimpleStruct> structs=mStructs;
        if (null!=structs){
            synchronized (structs){
                return structs.size()>0?structs.get(0):null;
            }
        }
        return null;
    }

    public final StructArray add(SimpleStruct struct){
        if (null!=struct){
            List<SimpleStruct> structs=mStructs;
            structs=null!=structs?structs:(mStructs=new ArrayList<>());
            synchronized (structs){
                structs.add(struct);
            }
        }
        return this;
    }

    public final String getContentText(){
        CharSequence text= getStructSpannableStringBuilder(true, null);
        return null!=text?text.toString():null;
    }

    public final SpannableStringBuilder getStructSpannableStringBuilder(OnStructClick callback){
            return getStructSpannableStringBuilder(false, callback);
    }

    public final SpannableStringBuilder getStructSpannableStringBuilder(boolean text,OnStructClick callback){
        List<SimpleStruct> structList=mStructs;
        if (null!=structList){
            synchronized (structList){
                SpannableStringBuilder builder = new SpannableStringBuilder("");
                for (SimpleStruct child:structList) {
                    if (null==child){
                        continue;
                    }
                    final String title=child.getTitle();
                    if (null==title||title.length()<=0){
                        continue;
                    }
                    final boolean isLinkType=child.isAnyType(Struct.TYPE_LINK);
                    final String titleContent=isLinkType&&!text?"【"+title+"】":title;
                    final int length=null!=titleContent?titleContent.length():0;
                    if (length<=0){
                        continue;
                    }
                    int start=builder.length();
                    builder.append(titleContent);
                    if (isLinkType){
                        final int end=start+length;
                        final Integer color=child.getDataColor();
                        builder.setSpan(new StructClickableSpan(start,end) {
                            @Override
                            public void onClick(View widget) {
                                if (null!=callback){
                                    callback.onStructClicked(widget,title,StructArray.this,start,end);
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                if (null!=color&&null!=ds){
                                    ds.setColor(color);
                                }
                            }
                        }, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
                return builder;
            }
        }
        return null;
    }

    @Override
    public String getType() {
        Struct struct=getFirst();
        return null!=struct?struct.getType():null;
    }

    public final List<SimpleStruct> getArray(){
        List<SimpleStruct> structs=mStructs;
        if (null!=structs){
            synchronized (structs){
                return new ArrayList<>(structs);
            }
        }
        return null;
    }

    @Override
    public Object getData() {
        Struct struct=getFirst();
        return null!=struct?struct.getData():null;
    }

    @Override
    public String getTitle() {
        Struct struct=getFirst();
        return null!=struct?struct.getTitle():null;
    }

    @Override
    public Object json() {
        List<SimpleStruct> structs=mStructs;
        if (null!=structs){
            synchronized (structs){
                JSONArray array=new JSONArray();
                Struct struct=null;Object structJson=null;
                int size=structs.size();
                for (int i = 0; i < size; i++) {
                    if (null!=(struct=structs.get(i))&&null!=(structJson=struct.json())){
                        array.put(structJson);
                    }
                }
                return array;
            }
        }
        return null;
    }

    public final int size(){
        List<SimpleStruct> structs=mStructs;
        if (null!=structs){
            synchronized (structs){
                return structs.size();
            }
        }
        return 0;
    }
}
