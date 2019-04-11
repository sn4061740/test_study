package com.xcore.base;

import android.com.baselibrary.BaseCommonPopupActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public abstract class BasePopActivity extends BaseCommonPopupActivity {

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getLayoutId() != 0) {
                setContentView(getLayoutId());
            }
            initViews(savedInstanceState);
        }catch (Exception ex){}
    }

}
