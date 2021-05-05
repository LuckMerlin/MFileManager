package com.luckmerlin.model;

import androidx.databinding.ObservableField;

import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.file.R;

public class InputModel extends Model implements OnModelResolve {
    private final ObservableField<String> mInput=new ObservableField<>();
    private final ObservableField<String> mHint=new ObservableField<>();
    
    public InputModel(String hint){
        mHint.set(hint);
    }

    @Override
    public Object onResolveModel() {
        return R.layout.input_model;
    }

    public final String getInputText() {
        return mInput.get();
    }

    public final ObservableField<String> getInput() {
        return mInput;
    }

    public final ObservableField<String> getHint() {
        return mHint;
    }
}
