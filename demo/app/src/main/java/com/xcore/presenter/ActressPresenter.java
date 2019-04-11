package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.CategoriesBean;
import com.xcore.data.bean.TypeListBean;
import com.xcore.data.bean.TypeTabBean;
import com.xcore.data.utils.TCallback;
import com.xcore.data.utils.DataUtils;
import com.xcore.presenter.view.ActressView;
import com.xcore.services.ApiFactory;

public class ActressPresenter extends BasePresent<ActressView> {
    /**
     * 得到女星电影列表
     */
    public void getActress(final String shortId,final int pageIndex,final String type,final boolean showall){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("shortId",shortId);
        params.put("PageIndex",pageIndex);
        params.put("sorttype",type);
        params.put("showall",showall);
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getActress(params, new TCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                view.onActressResult(typeListBean);
                if(dialog!=null) {
                    dialog.cancel();
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
    public void getTag(final String key,final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("key",key);
        params.put("PageIndex",pageIndex);

        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getTag(params, new TCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
                dialog.cancel();
                view.onTagResult(typeListBean);
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
    public void getTag(final String key,final String sortType,final int pageIndex){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("key",key);
        params.put("PageIndex",pageIndex);
        params.put("sorttype",sortType);
        if(dialog!=null) {
            dialog.show();
        }
        ApiFactory.getInstance().<TypeListBean>getMovieBytagsList(params, new TCallback<TypeListBean>() {
            @Override
            public void onNext(TypeListBean typeListBean) {
            dialog.cancel();
            view.onTagResult(typeListBean);
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

    public void getTags(){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TypeTabBean>getTags(new TCallback<TypeTabBean>() {
            @Override
            public void onNext(TypeTabBean typeTabBean) {
                typeTabBean.getData().getCategories().add(0,new CategoriesBean("","全部"));
                typeTabBean.getData().getSpecies().add(0,new CategoriesBean("","全部"));
//                        typeTabBean.getData().getSorttype().add(0,new CategoriesBean("","全部"));
                DataUtils.typeTabBean=typeTabBean;
                view.onTags(typeTabBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
