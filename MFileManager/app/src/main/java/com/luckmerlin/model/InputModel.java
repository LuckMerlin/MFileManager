package com.luckmerlin.model;

import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.file.R;

public class InputModel extends Model implements OnModelResolve {

    @Override
    public Object onResolveModel() {
        return R.layout.input_model;
    }
}
