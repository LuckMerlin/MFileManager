package com.luckmerlin.model;

import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.file.R;

public class UploadDialogModel extends Model implements OnModelResolve {

    @Override
    public Object onResolveModel() {
        return R.layout.upload_dialog_model;
    }
}
