package com.xcore.presenter;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xcore.base.BasePresent;
import com.xcore.data.bean.TagBean;
import com.xcore.data.utils.TCallback;
import com.xcore.presenter.view.TagSelectView;
import com.xcore.services.ApiFactory;

public class TagSelectPreseneter extends BasePresent<TagSelectView> {

    //得到所有标签
    public void getTags(){
        if(!checkNetwork()){
            return;
        }
        if(dialog!=null){
            dialog.show();
        }
        ApiFactory.getInstance().<TagBean>getAllTags(new TCallback<TagBean>() {
            @Override
            public void onNext(TagBean tagBean) {
                view.onTagResult(tagBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<TagBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

    public void getTagList(final int pageIndex, final String key){
        if(!checkNetwork()){
            return;
        }
        HttpParams params=new HttpParams();
        params.put("PageIndex",pageIndex);
        params.put("shortId",key);

        if(dialog!=null){
            dialog.show();
        }

        ApiFactory.getInstance().<TagBean>getAllTagsList(params, new TCallback<TagBean>() {
            @Override
            public void onNext(TagBean tagBean) {
                view.onResult(tagBean);
                if(dialog!=null){
                    dialog.cancel();
                }
            }

            @Override
            public void onError(Response<TagBean> response) {
                super.onError(response);
                if(dialog!=null){
                    dialog.cancel();
                }
            }
        });
    }

}
