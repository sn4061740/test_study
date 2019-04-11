package com.xcore.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.xcore.MainApplicationContext;
import com.xcore.R;

import java.util.Timer;
import java.util.TimerTask;

public abstract class MvpActivity<V,P extends BasePresent<V>> extends BaseActivity{

    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        presenter = initPresenter();
        super.onCreate(savedInstanceState);
        presenter.attach((V) this);
        initData();
        presenter.joinView(className,getParamsStr());
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(presenter!=null) {
//            presenter.attach((V) this);
//        }
    }

    @Override
    public void onDestroy() {
        try {
            if(presenter!=null) {
                presenter.outView(className,getParamsStr());
                presenter.detach();
            }
            OkGo.getInstance().cancelTag(this);
        }catch (Exception ex){}
        super.onDestroy();
    }

    public abstract P initPresenter();
    public String getParamsStr(){
        return "这个是"+className+"页面哦!!!";
    }

}