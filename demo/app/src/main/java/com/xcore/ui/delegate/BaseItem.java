package com.xcore.ui.delegate;

import android.support.annotation.NonNull;

import com.xcore.commonAdapter.Savable;
import com.xcore.ui.touch.IHomeOnClick;

public class BaseItem implements Savable {


    @Override
    public void init(@NonNull byte[] data) {

    }

    @NonNull
    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @NonNull
    @Override
    public String describe() {
        return null;
    }
}
