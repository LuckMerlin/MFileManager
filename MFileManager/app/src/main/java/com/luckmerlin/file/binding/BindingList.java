package com.luckmerlin.file.binding;

import android.view.View;

import com.luckmerlin.databinding.BindingObject;
import com.luckmerlin.databinding.CustomBinding;

import java.util.ArrayList;
import java.util.List;

public class BindingList implements CustomBinding {
    private List<BindingObject> mBinds;

    public final BindingList append(boolean skipEquals, BindingObject... objects) {
        List<BindingObject> binds=mBinds;
        int length=null!=objects?objects.length:-1;
        if (length> 0) {
            binds=null!=binds?binds:(mBinds=new ArrayList<>(length));
            for (BindingObject child:objects) {
                if (child != null &&(!skipEquals|| !binds.contains(child))) {
                    binds.add(child);
                }
            }
        }
        return this;
    }

    public static BindingList list(BindingObject object){
        return new BindingList().append(true,object);
    }

    @Override
    public boolean onBind(View view) {
        List<BindingObject> binds=mBinds;
        if (null!=binds){
            for (BindingObject child:binds) {
                if (null!=child&&child instanceof CustomBinding){
                    ((CustomBinding)child).onBind(view);
                }
            }
        }
        return true;
    }
}
