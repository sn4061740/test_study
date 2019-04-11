package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.ThemeCoverBean;
import com.xcore.data.bean.ThemeRecommendBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.ThemeView;
import com.xcore.services.ApiFactory;

public class ThemePresenter extends BasePresent<ThemeView> {
    //获取信息
    public void getResult(final String shortId, final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("shortId",shortId);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getThemeById(params, new TCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                if(dialog!=null){
                    dialog.cancel();
                }
                if(view!=null) {
                    view.onResoult(typeListBean);
                }
            }
            @Override
            public void onError(Response<TypeListBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    public void getThemeCover(String shortId){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<ThemeCoverBean>getThemeByIdCover(params, new TCallback<ThemeCoverBean>() {
            @Override
            public void onNext(ThemeCoverBean themeCoverBean) {
                view.onRecommendCoverResult(themeCoverBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
            @Override
            public void onError(Response<ThemeCoverBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    /**
     * 得到专题列表
     * @param pageIndex
     */
    public void getThemes(int pageIndex){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<ThemeRecommendBean>getThems(new TCallback<ThemeRecommendBean>() {
            @Override
            public void onNext(ThemeRecommendBean recommendBean) {
                try {
                    view.onRecommendTheme(recommendBean);
                    if(dialog!=null) {
                        dialog.cancel();
                    }
                }catch (Exception ex){}
            }
        },params);
    }

}
