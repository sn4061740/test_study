package com.xcore.picgen;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwsd.libzxing.QRCodeManager;
import com.xcore.R;
import com.xcore.data.bean.MovieBean;
import com.xcore.ui.touch.ShareMovieListenner;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.DateUtils;
import com.xcore.utils.QRCodeUtils;

import java.io.File;


/**
 * Created by HomgWu on 2017/11/29.
 */

public class SharePicModel extends GenerateModel {
    private ImageView mTitleAvatarIv;
    private View mSharePicView;
    private MovieBean movieBean;
    private Activity activity;

    public SharePicModel(ViewGroup rootView,MovieBean movieBean1,Activity activity1) {
        super(rootView);
        this.movieBean=movieBean1;
        this.activity=activity1;

        String rootPath=activity.getExternalCacheDir()+"/share/";
        File f=new File(rootPath);
        if(!f.exists()) {
            f.mkdirs();
        }
        rootPath+=movieBean.getShortId()+ "_"+(DateUtils.getDate(DateUtils.M_D_H_M)) + ".jpg";
        setSavePath(rootPath);
    }

    @Override
    protected void startPrepare(final GeneratePictureManager.OnGenerateListener listener) throws Exception {
        try {
            mSharePicView = LayoutInflater.from(mContext).inflate(R.layout.movie_share_view, mRootView, false);
            mTitleAvatarIv = mSharePicView.findViewById(R.id.icon);
            ImageView qImage = mSharePicView.findViewById(R.id.qcodeImage);
            TextView infoTxt = mSharePicView.findViewById(R.id.infoTxt);
            infoTxt.setText(Html.fromHtml(movieBean.getMovieShareTextV1()));

            TextView txt = mSharePicView.findViewById(R.id.titleTxt);
            txt.setText(movieBean.getTitle());
            // QRCodeManager.getInstance().createQRCode(movieBean.getShareUrl(), 300, 300);
            Bitmap bitmap = QRCodeUtils.createQRCodeBitmap(movieBean.getShareUrl(), 300, "2");
            qImage.setImageBitmap(bitmap);

            if(activity.isDestroyed()||activity.isFinishing()){
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CacheFactory.getInstance().getImage(mTitleAvatarIv, movieBean.getConverUrl(), new ShareMovieListenner() {
                        @Override
                        public void onSuccess() {
                            prepared(listener);
                        }

                        @Override
                        public void onError() {
                            if (listener != null) {
                                listener.onGenerateFinished(null, null);
                            }
                        }
                    });
                }
            });
        }catch (Exception ex){}
    }

    @Override
    public View getView() {
        return mSharePicView;
    }
}
