package com.xcore.ui.otheractivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.xcore.R;
import com.xcore.base.BaseActivity;

public class AActivity extends BaseActivity {
    public static void toActivity(Context context) {
        Intent intent = new Intent(context, AActivity.class);
        context.startActivity(intent);
    }
    public static void toActivity(Context context,boolean isRun,String sId) {
        Intent intent = new Intent(context, AActivity.class);
        intent.putExtra("isRun",isRun);
        intent.putExtra("shortId",sId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {
    }

}