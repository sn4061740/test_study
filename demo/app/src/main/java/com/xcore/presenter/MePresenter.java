package com.xcore.presenter;

import android.util.Log;

import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.PlayerBean;
import com.xcore.data.bean.RCBean;
import com.xcore.data.utils.TCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.MeView;
import com.xcore.services.ApiFactory;

public class MePresenter extends BasePresent<MeView> {

    //获取用户信息
    public void getUserInfo(){
        if(!checkNetwork()){
            return;
        }
//        if(dialog!=null){
//            dialog.show();
//        }
        ApiFactory.getInstance().<PlayerBean>getUserInfo(new TCallback<PlayerBean>() {
            @Override
            public void onNext(PlayerBean playerBean) {
                DataUtils.playerBean=playerBean;
                view.onResult(playerBean);
//                if(dialog!=null){
//                    dialog.cancel();
//                }
            }

            @Override
            public void onError(Response<PlayerBean> response) {
                super.onError(response);
//                if(dialog!=null){
//                    dialog.cancel();
//                }
            }
        });
    }

    /**
     * 获取记录
     * @param status  1：当天，2.本周  3,其他或者不传则是更早
     */
    public void getRecode(final int status){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory.getInstance().<RCBean>getRecode(status, 1, 20, new TCallback<RCBean>() {
                    @Override
                    public void onNext(RCBean s) {
                        if(view!=null){
                            view.onRecode(s);
                        }
                    }
                    @Override
                    public void onError(Response<RCBean> response) {
                        super.onError(response);
                    }
                });
            }
        }).start();
    }

    /**
     * 获取收藏
     */
    public void getCollect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiFactory.getInstance().<RCBean>getCollect(1, 20, new TCallback<RCBean>() {
                    @Override
                    public void onNext(RCBean s) {
                        if(view!=null){
                            view.onCollect(s);
                        }
                    }
                    @Override
                    public void onError(Response<RCBean> response) {
                        super.onError(response);
                    }
                });
            }
        }).start();
    }

    public void show1(){
        if(dialog!=null){
            dialog.show();
        }
    }
    public void hide(){
        if(dialog!=null){
            dialog.cancel();
        }
    }
}
