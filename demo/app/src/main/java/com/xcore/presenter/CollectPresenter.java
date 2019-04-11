package com.xcore.presenter;

import android.util.Log;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.RCBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.CollectView;
import com.xcore.services.ApiFactory;

import java.util.List;

public class CollectPresenter extends BasePresent<CollectView> {

    //删除收藏
    public void deleteCollect(List<String> ids,int type){
        if(!checkNetwork()){
            return;
        }
        ApiFactory.getInstance().<String>deleteRC(type, ids, new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG","删除："+s);
            }
        });
//        HttpParams params=new HttpParams();
//        params.put("shortId",shortId);
//        ApiFactory.getInstance().<String>removeCollect(params, new TCallback<String>() {
//            @Override
//            public void onNext(String s) {
//                Log.e("TAG","取消收藏：："+s);
//            }
//
//            @Override
//            public void onError(Response<String> response) {
//                super.onError(response);
//            }
//        });
    }

    /**
     * 获取记录
     * @param status  1：当天，2.本周  3,其他或者不传则是更早
     */
    public void getRecode(int status,int pageIndex){
        final int type=status;
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<RCBean>getRecode(type, pageIndex, 20, new TCallback<RCBean>() {
            @Override
            public void onNext(RCBean s) {
                if(view!=null){
                    view.onRecode(s,type);
                }
                if(dialog!=null){
                    dialog.cancel();
                }
            }
            @Override
            public void onError(Response<RCBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    /**
     * 获取收藏
     */
    public void getCollect(int pageIndex){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<RCBean>getCollect(pageIndex, 20, new TCallback<RCBean>() {
            @Override
            public void onNext(RCBean s) {
                if(view!=null){
                    view.onCollect(s);
                }
                if(dialog!=null){
                    dialog.cancel();
                }
            }
            @Override
            public void onError(Response<RCBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    //删除记录 OR 删除收藏
    public void batchCollect(int type){

    }

}
