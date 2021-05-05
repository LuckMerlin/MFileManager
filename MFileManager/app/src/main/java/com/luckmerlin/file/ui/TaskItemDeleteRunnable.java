package com.luckmerlin.file.ui;

import androidx.databinding.ObservableField;

public interface TaskItemDeleteRunnable extends Runnable {
    ObservableField<Integer> getCounter();
}
