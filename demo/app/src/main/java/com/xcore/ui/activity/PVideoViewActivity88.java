package com.xcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.jay.HttpServerManager;
import com.jay.config.Config;
import com.jay.config.DownConfig;
import com.jay.play.IPlayListener;
import com.shuyu.common.CommonRecyclerAdapter;
import com.shuyu.common.CommonRecyclerManager;
import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.DownModel;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.AdvBean;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.DownloadBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.LineModel;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.PathDataBean;
import com.xcore.data.bean.QualtyBean;
import com.xcore.data.model.SelectModel;
import com.xcore.ext.ImageViewExt;
import com.xcore.ext.wheelview.BottomDialog;
import com.xcore.ext.wheelview.WheelView;
import com.xcore.picgen.GeneratePictureManager;
import com.xcore.picgen.SharePicModel;
import com.xcore.presenter.DetailPresenter;
import com.xcore.presenter.view.DetailView;
import com.xcore.ui.IADVListener;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.ui.enums.PlayTypeEnum;
import com.xcore.ui.holder.PlayDetailHolder;
import com.xcore.ui.holder.PlayRelatedHolder;
import com.xcore.ui.holder.model.PlayHolderBaseModel;
import com.xcore.ui.other.PlayErrorDialogView;
import com.xcore.ui.other.TipsEnum;
import com.xcore.ui.test.CommentView;
import com.xcore.ui.test.HttpLineController;
import com.xcore.ui.test.LineChangeListener;
import com.xcore.ui.test.LocalLinneController;
import com.xcore.ui.test.M3u8LineController;
import com.xcore.ui.test.PlaySpeedModel;
import com.xcore.ui.test.PlayerListener;
import com.xcore.ui.test.PlayerView;
import com.xcore.ui.test.TorLineController;
import com.xcore.ui.touch.ChangeTotalLister;
import com.xcore.ui.touch.IPlayClickListenner;
import com.xcore.utils.AdvUtils;
import com.xcore.utils.JumpUtils;
import com.xcore.utils.LogUtils;
import com.xcore.utils.NetWorkUtils;
import com.xcore.utils.SystemUtils;
import com.xcore.utils.TrackerCheck;
import com.xcore.utils.VideoCheckUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.p2ptrans.P2PTrans;
import cn.dolit.utils.common.Utils;

public class PVideoViewActivity88 extends MvpActivity<DetailView,DetailPresenter> implements DetailView,
        ChangeTotalLister,IPlayClickListenner {

    private SelectModel lineModel;//播放的当前线路
    private HttpLineController httpLineController;
    private M3u8LineController m3u8LineController;
    private TorLineController torLineController;
    private LocalLinneController localLinneController;

    private String playerUrl="";
    private List<PathDataBean> pathDataBeans=null;//当前播放线路的全部路径,清晰度
    PlayerView player;

    PlayerListener playerListener=new PlayerListener() {
        @Override
        public void onStartPlay(SelectModel selectModel) {
            if(movieBean==null||advTime>0){//没有返回详情
                return;
            }
            lineModel=selectModel;
            if(lineModel==null){
                Toast.makeText(PVideoViewActivity88.this,"没有播放线路哟~~",Toast.LENGTH_SHORT).show();
                return;
            }
            String line= (String) lineModel.getValue();
            if(line.isEmpty()||line.length()<=0){
                Toast.makeText(PVideoViewActivity88.this,"没有播放线路哟~~",Toast.LENGTH_SHORT).show();
                return;
            }
            //请求线路播放地址
            presenter.getPlayPath(shortId,line);
        }
        @Override
        public void onStartPlay(SelectModel selectModel, int pos,int qualty1) {
            playSeconds=pos;
            qualty=qualty1;
            try {
                lineModel = selectModel;
                if (lineModel == null) {
                    toast("没有播放的线路哟~~");
                    return;
                }
                String line = (String) lineModel.getValue();
                if (line.isEmpty() || line.length() <= 0) {
                    toast("没有播放的线路哟~~");
                    return;
                }
                if (lineModel.getType() == PlayTypeEnum.TORRENT) {
                    if (!MainApplicationContext.serverSuccess) {
                        MainApplicationContext.startP2PServer();
                        if (player != null) {
                            player.changeLine();
                        }
                        return;
                    }
                }
                toast("线路切换中...");
                PathBean pathBean = lineMaps.get(lineModel.getValue());
                if (pathBean == null) {
                    //请求线路播放地址
                    presenter.getPlayPath(shortId, line);
                } else {
                    onGetPathResult(pathBean);
                }
            }catch (Exception ex){}
        }
        @Override
        public void onStartPlayChangeQualty(SelectModel selectModel, int qualty1,int pos) {//切换清晰度
            playSeconds=pos;
            qualty=qualty1;
            try {
                PathDataBean dataBean = pathDataBeans.get(qualty1);
//            qualty=dataBean.getQuality();
                String qStr = dataBean.getQualityName();
//            toast("切换清晰度");
                Toast toast = Toast.makeText(PVideoViewActivity88.this, "切换清晰度为" + qStr, Toast.LENGTH_LONG);
                toast.show();
                changeQualty();
            }catch (Exception ex){}
        }
        @Override
        public void onBackPressed() {
            if(!player.onBackPressed()){
                finish();
            }
        }

        @Override
        public void onError(String model, String msg, int code) {//播放出错 切换
            try {
                model=model.replace("&v1=","ewR7V9lxTQ2Vx00F");
                LogUtils.videoErrorUp(model, msg, code);
                toChange();
                toMobclickAgent("play_error");
                if(lineModel.getType()==PlayTypeEnum.TORRENT) {
                    new TrackerCheck().start(model, playerUrl, msg);
                }
            }catch (Exception ex){}
        }
        @Override
        public void onPlaySuccess(int pos) {//播放成功
            //更新观看记录
            try {
                if (movieBean != null&&pos>0) {
                    if(pos==playSeconds){
                        return;
                    }
                    presenter.updateCollect(movieBean, DetailTouchType.TOUCH_RECODE, pos);
                }
            }catch (Exception ex){}
            toMobclickAgent("play_success");
        }
        @Override
        public void playerError() {
            toChange();
        }
        @Override
        public void onExit(long pos) {
            //更新观看记录
            if(movieBean!=null&&pos>0) {
                if(playSeconds==pos){//如果传进来的和当前的一样就不用存了
                    return;
                }
                presenter.updateCollect(movieBean, DetailTouchType.TOUCH_RECODE, (int) pos);
            }
        }
        @Override
        public void updatePlaySpeed(PlaySpeedModel playSpeedModel) {
            try {
                if(playSpeedModel==null){
                    return;
                }
                if(playSpeedModel.getTotal()<=0){
                    return;
                }
                playSpeedModel.setLine((String) lineModel.getValue());
                LogUtils.playSpeedUp(playSpeedModel, shortId);
            }catch (Exception ex){}
        }
    };
    private void toChange(){
        try {
            PathDataBean pathDataBean = pathDataBeans.get(qualty);
            //播放出错,看本条线路还有其他地址没有
            if (lineModel.getType() == PlayTypeEnum.HTTP) {
                if (httpLineController != null) {
                    httpLineController.change(pathDataBean.getPath());
                }
            } else if (lineModel.getType() == PlayTypeEnum.M3U8) {
                if (m3u8LineController != null) {
                    m3u8LineController.onDestroy();
                    m3u8LineController.change(pathDataBean.getPath(), pathDataBean.getMd5());
                }
            } else if (lineModel.getType() == PlayTypeEnum.TORRENT) {
                if (torLineController != null) {
                    torLineController.onDestroy();
                    torLineController.change(pathDataBean.getPath());
                }
            } else {//都不是的话就重新切换
                changeQualty();
            }
        }catch (Exception ex){}
    }
    //切换线路回调
    LineChangeListener lineChangeListener=new LineChangeListener() {
        @Override
        public void changeSuccess(String path) {
            playerUrl=path;
            if(player!=null){
                player.setStreamId("");
//                String[] res=new String[]{
//                        "http://38.27.96.36:1080/m/index?sid=xsp1543899390&qua=2"
//                };
//                int n=NumberUtils.getRandom(res.length);
//                path=res[n];//"http://38.27.96.36:1080/m/index?sid=xsp1544014125&qua=3&r=1";
//                path=P2PTrans.getM3u8PlayPath(path,"b24w9AwfAspg3QfpWUGuTPZCi8E48v8P");
//                path="http://38.27.96.36:1080/m/index2?sid=xsp1544030211&qua=3&r=1";//";
                try {
                    player.startPlay(path, playSeconds);
                    if (pathDataBeans == null || path.length() <= 0) {
                        return;
                    }
                    PathDataBean dataBean = pathDataBeans.get(qualty);
                    long fileSize = dataBean.getFileSize();
                    if (fileSize <= 0) {
                        return;
                    }
                    //判断当前是否是流量方式播放
                    boolean boo = NetWorkUtils.isWifiConnected(PVideoViewActivity88.this);
                    if (boo == true) {
                        return;
                    }
                    String vStr = Utils.getDisplayFileSize(fileSize);
                    toastLong("预计消耗流量 " + vStr, R.color.color_black);
                }catch (Exception ex){}
            }
        }
        @Override
        public void changeError() {
            if(player!=null){
                player.changeLine();
            }
        }
        @Override
        public void onProgress(long speed) {//种子用
            String sp=Utils.getDisplayFileSize(speed);
            Log.e("TAG","种子下载速度:"+sp);
            if(player!=null){
                player.setTorSpeed("加载中,请稍后...\n"+sp+"/S");
            }
        }

        @Override
        public void setStreamId(String sId) {
            if(player!=null){
                player.setStreamId(sId);
            }
        }
    };

    //切换线路
    private void changeLine(){
        if(player!=null){
            player.changeLine();
        }
    }
    //////////////////////////////////////// 定义属性 ///////////////////////////////////////////////

    private String shortId="";
    private boolean isCollect=false;
    private MovieBean movieBean;
    private int playSeconds=0;
    private int qualty=0;
    private PVideoViewActivity88 __this;

    private RelativeLayout advLayout;
    private TextView txtTime;
    private ImageViewExt advConver;

    private RecyclerView xRecyclerView;
    private List<RecyclerBaseModel> baseModels=new ArrayList<>();
    CommonRecyclerAdapter adapter;

    Map<String,PathBean> lineMaps=new HashMap<>();

    DolitVideoView dolitVideoView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pvideo_view88;
    }
    @Override
    public String getParamsStr() {
        return "电影ID:"+shortId;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MainApplicationContext.onWindowFocusChanged(hasFocus,this);
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
//        HttpUtils.getInstance().setHttpCall(PVideoViewActivity100.this);
        __this=this;
        MainApplicationContext.SHORT_ID="";

        //获取传入的参数
        Intent intent = getIntent();
        shortId=intent.getStringExtra("shortId");
        playSeconds=intent.getIntExtra("position",0);
        String pUrl=intent.getStringExtra("playUrl");
        qualty=Integer.valueOf(MainApplicationContext.CURRENT_QUALITY);
        //
        player=new PlayerView(this,shortId);
        player.setPlayerListener(playerListener);

        LogUtils.shortId=shortId;
        LogUtils.sId="";
        LogUtils.isUpSpeed=false;

        //findViewById(R.id.commentLayout).setVisibility(View.GONE);

        ////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////  广告  /////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////
        advLayout=findViewById(R.id.advLayout);
        advLayout.setOnClickListener(onClickListener);
        advLayout.setVisibility(View.GONE);

        advConver=findViewById(R.id.adv_conver);
        advConver.setVisibility(View.GONE);

        txtTime=findViewById(R.id.txt_time);
        txtTime.setVisibility(View.GONE);

        volumeImage=findViewById(R.id.volume_image);
        volumeImage.setVisibility(View.GONE);
        volumeImage.setOnClickListener(onClickListener);

        findViewById(R.id.back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dolitVideoView= findViewById(R.id.playerVideo);
        boolean isMediaEnable = VideoCheckUtil.getMediaCodecEnabled();
        //LogUtils.showLog("是否开启硬件解码:" + isMediaEnable);
        dolitVideoView.setIsHardDecode(false);
        dolitVideoView.setMediaCodecEnabled(isMediaEnable);
        dolitVideoView.requestFocus();
        dolitVideoView.setVisibility(View.GONE);

        ////////////////////////////////////////////////////////////////////////////////////////////

        CommonRecyclerManager manager=new CommonRecyclerManager();
        manager.addType(PlayDetailHolder.ID,PlayDetailHolder.class.getName());
        manager.addType(PlayRelatedHolder.ID,PlayRelatedHolder.class.getName());
        adapter=new CommonRecyclerAdapter(this,manager,baseModels);

        xRecyclerView=findViewById(R.id.xRecyclerView);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        xRecyclerView.setAdapter(adapter);

        //判断是否停止下载
        //LocalDownLoader localDownLoader=CacheManager.getInstance().getLocalDownLoader();
        if(MainApplicationContext.IS_PLAYING_TO_CACHE==true) {//停止所有运行的任务
//            localDownLoader.stopRunAll();
//            M3u8DownTaskManager.getInstance().stopTaskAll();
            HttpServerManager.getInstance().stopAll();
        }

//        if(MainApplicationContext.CACHE_PROMPT==true) {//有提示才进来
//            //每次进入时检测空间是否足够 剩余1G 就提示   本地启动不再提示
//            Long size = Long.valueOf(1024 * 1L * 1024 * 1024);
//            boolean boo = SystemUtils.availableSpaceVerification(size);//小于3G的时候给提示
//            if (!boo) {//空间不足,请及时清理,以免影响看片。
//                MainApplicationContext.showips("观看缓存过多,建议及时清理以免影响看影片", this, TipsEnum.TO_CLEAR);
//            }
//        }
        HttpServerManager.getInstance().setPlayListener(playListener);
//        M3u8Utils.getInstance().setPlayListener(playListener);
        checkLocalPlay();
    }

    IPlayListener playListener=new IPlayListener() {
        @Override
        public void onError(String model, String errMsg, int code) {
            LogUtils.videoErrorUp(model,errMsg,code);
        }
    };

    @Override
    protected void initData() {
        presenter.getDetail(shortId);
    }
    @Override
    public DetailPresenter initPresenter() {
        return new DetailPresenter();
    }

    private Timer advTimer;
    private int advTime=0;

    AudioManager audioManager;
    ImageView volumeImage;
    boolean volumeBoo=false;
    private int currentVolume=0;

    private void startAdvTimer(){
        if(advTimer==null){
            advTimer=new Timer();
            advTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                try {
                    advTime--;
                    if (advTime <=0) {
                        stopAdvTimer();
                    }
                    if (txtTime == null) {
                        return;
                    }
                    txtTime.post(new Runnable() {
                        @Override
                        public void run() {
                            txtTime.setText(advTime + "");
                        }
                    });
                }catch (Exception ex){}
                }
            },1000,1000);
        }
    }
    private void stopAdvTimer(){
        if(advTimer!=null){
            advTimer.cancel();
        }
        advTimer=null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            player.show();
            if(advLayout!=null) {
                advLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        advLayout.setVisibility(View.GONE);
                    }
                });
            }
            if(dolitVideoView!=null){
                dolitVideoView.pause();
                dolitVideoView.stopPlayback();
                dolitVideoView.setVisibility(View.GONE);
            }
            }
        });


    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.volume_image:
                    try {
                        volumeBoo = !volumeBoo;
                        if (volumeBoo) {//静音
                            volumeImage.setImageResource(R.drawable.ic_volume_off_white_36dp);
                            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        } else {
                            volumeImage.setImageResource(R.drawable.ic_volume_up_white_36dp);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                        }
                    }catch (Exception ex){}
                    break;
                case R.id.advLayout:
                    if(movieBean==null||movieBean.getPlayerAdv()==null){
                        return;
                    }
                    JumpUtils.to(PVideoViewActivity88.this,movieBean.getPlayerAdv());
                    break;
            }
        }
    };
    //广告监听
    IADVListener iadvListener=new IADVListener() {
        @Override
        public void onLoadVideoSuccess(String path) {
//            if(advConver!=null) {
//                advConver.setVisibility(View.GONE);
//            }
            if(advTime>0&&dolitVideoView!=null){
                volumeImage.setVisibility(View.VISIBLE);
                dolitVideoView.setVisibility(View.VISIBLE);
                dolitVideoView.setVideoPath(path);
                dolitVideoView.start();
            }
        }
        @Override
        public void onLoadImageSuccess(String path) {
            advConver.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onDetailResult(DetailBean detailBean) {
        movieBean=detailBean.getData();
        movieBean.setCollect(isCollect);
        try {
//            String[] vRes=new String[]{
//                    "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200fca0000bggajhk0gfkn1khrfpa0&line=0",
//                    "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0300fe80000bgfg18v82ijq8qsqgpug&line=0",
//                    "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f660000bgfo90gm4cil69k01km0&line=0",
//                    "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f0e0000bfrb4nl8n75osammj4sg&line=0"
//            };
//            int n=NumberUtils.getRandom(vRes.length);
//            AdvBean newAdv1=new AdvBean();
////            newAdv1.setImagePath("http://bestwanbang.com/UploadFiles/logo/bilibili-big.png");
//            newAdv1.setJump(8);
//            newAdv1.setTime(14);
//            newAdv1.setShortId("xx");
//            newAdv1.setToUrl("http://www.qxtv2.com");
//            newAdv1.setVideoPath(vRes[n]);
//            newAdv1.setContent("广告测试测试");
            //movieBean.setPlayerAdv(newAdv1);
            PlayHolderBaseModel relatedModel = new PlayHolderBaseModel();
            relatedModel.setiPlayClickListenner(this);
            relatedModel.setValue(movieBean.getRelated());
            relatedModel.setResLayoutId(PlayRelatedHolder.ID);

            PlayHolderBaseModel relatedModel1 = new PlayHolderBaseModel();
            relatedModel1.setiPlayClickListenner(this);
            relatedModel1.setValue(movieBean);
            relatedModel1.setResLayoutId(PlayDetailHolder.ID);

            baseModels.add(relatedModel1);
            baseModels.add(relatedModel);

            adapter.notifychange();

            List<LineModel> lineModels = movieBean.getLineList();
            List<SelectModel> lines=new ArrayList<>();
            for (LineModel model : lineModels) {
                SelectModel model1 = new SelectModel(model.getType(), model.getName(), model.getType(), 0, model.getLineID(),2);
                lines.add(model1);
            }
            player.setLines(lines);

            player.setTitle(movieBean.getTitle());
            player.setBackground(movieBean.getConverUrl());
            long duration=movieBean.getDuration()*1L*1000;
            player.setDuration(duration);

            playAdv();
        }catch (Exception ex){}
        //缓存好了的视频就自动播放了
//        checkLocalPlay();
        //检查
//       new CheckLineModel(lineSelectModels);
    }
    private boolean isLocalPlay=false;
    //检查本地下载播放
    private void checkLocalPlay(){
        Config config=HttpServerManager.getInstance().getM3u8DownManager().getConfig(shortId);
        if(config==null||config.getProgress()<1){
            return;
        }
        if(config.getProgress()>=100){
            try {
                String url=P2PTrans.getM3u8PlayUrlV1(config.getmUrl(),config.getKey());
                isLocalPlay=true;
                lineChangeListener.changeSuccess(url);
                Toast toas = Toast.makeText(activity, "本影片已缓存成功,不消耗流量!", Toast.LENGTH_SHORT);
                toas.setGravity(Gravity.CENTER, 0, 0);
                toas.show();
            } catch (Exception ex) {
            }
        }


//        M3U8TaskModel model= M3u8DownTaskManager.getInstance().getM3u8TaskModelByShortId(shortId);
//        if(model==null){
//            return;
//        }
//        if(model.getPrent()>=100||model.getStatus()==M3U8TaskModel.TaskStatus.COMPLETE){
//            String url="";
//            if(model.getUrl().contains(".xv")){
//                url=model.getUrl();
//            }else{
//                url=P2PTrans.getM3u8PlayUrlV1(model.getUrl(),model.getKey());
//            }
//            if(url.length()>0) {
//                try {
//                    isLocalPlay=true;
//                    lineChangeListener.changeSuccess(url);
//                    Toast toas = Toast.makeText(activity, "本影片已缓存成功,不消耗流量!", Toast.LENGTH_SHORT);
//                    toas.setGravity(Gravity.CENTER, 0, 0);
//                    toas.show();
//                } catch (Exception ex) {
//                }
//            }
//        }

//        CacheModel cacheModel = CacheManager.getInstance().getLocalDownLoader().getOverByShortId(shortId);
//        if (cacheModel != null && cacheModel.getComplete() == 1) {
//            String sourceUrl = cacheModel.getUrl();
//            final String sId = cacheModel.getStreamId();
//            String path = "";
//            try {
//                //得到的路径格式：http://xx.com/xvcvccvcx.Torrent
//                int lastIndex = sourceUrl.lastIndexOf("/");
//                String nameStr = sourceUrl.substring(lastIndex + 1);
//                nameStr = nameStr.replace(".Torrent", "");
//                nameStr = nameStr.replace(".torrent", "");
//                path = MainApplicationContext.SD_PATH + sId + "/" + nameStr + ".xv";
//                File file = new File(path);
//                if (file.exists()) {
//                    if (lineChangeListener != null) {
//                        stopAdvTimer();
//                        isLocalPlay=true;
//                        lineChangeListener.changeSuccess(path);
//                        try {
//                            Toast toas = Toast.makeText(activity, "本影片已缓存成功,不消耗流量!", Toast.LENGTH_SHORT);
//                            toas.setGravity(Gravity.CENTER, 0, 0);
//                            toas.show();
//                        } catch (Exception ex) {
//                        }
//                    }
//                }
//            } catch (Exception ex) {
//                //ex.printStackTrace();
//            }
//        }
    }
    //播放广告
    private void playAdv(){
        if(isLocalPlay){
            advLayout.setVisibility(View.GONE);
            return;
        }
        if(movieBean!=null&&movieBean.getPlayerAdv()!=null){
            AdvBean advBean= movieBean.getPlayerAdv();
            advLayout.setVisibility(View.VISIBLE);
            advTime=advBean.getTime();
            AdvUtils.loadImage(advBean.getImagePath(),advConver,iadvListener);
            AdvUtils.loadVideo(advBean.getVideoPath(),iadvListener);
            audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            txtTime.setText(advTime+"");

            txtTime.setVisibility(View.VISIBLE);
            if(advBean.getImagePath()!=null&&advBean.getImagePath().length()<=0){
                volumeImage.setVisibility(View.GONE);
            }
            startAdvTimer();
            if(player!=null){
                player.hide();
            }
        }else{
            advLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onGetPathResult(PathBean pathBean) {
        if(pathBean==null||(pathBean!=null&&pathBean.getData()==null)||(pathBean!=null&&pathBean.getData()!=null&&pathBean.getData().size()<=0)){
            String modelStr=shortId+"|";
            if(lineModel!=null) {
                modelStr+=lineModel.getName()+" 没有获取到播放地址";
            }
            LogUtils.videoErrorUp(modelStr, "获取播放地址失败,得到的数据是null", 100011);
            //TODO...切换线路
            changeLine();
            return;
        }
        qualty=Integer.valueOf(MainApplicationContext.CURRENT_QUALITY);
        String key= (String) lineModel.getValue();
        lineMaps.put(key,pathBean);
        pathDataBeans= pathBean.getData();
        try {
            int cIndex = 0;
            int currentQualityPosition=-1;
            List<SelectModel> qualtys = new ArrayList<>();

            int maxNum =pathDataBeans.get(0).getQuality();
            int minNum=maxNum;
            int minIndex=0;
            int maxIndex=0;
            for (PathDataBean mod : pathDataBeans) {
                SelectModel selectModel = new SelectModel();
                selectModel.setIcon(0);
                selectModel.setId(0);
                selectModel.setName(mod.getQualityName());
                selectModel.setValue(mod);
                selectModel.setvType(1);
                if (qualty==mod.getQuality()) {//如果返回的里面有相同的
                    currentQualityPosition = cIndex;
                }
                if (mod.getQuality() >= maxNum) {
                    maxIndex = cIndex;
                    maxNum=mod.getQuality();
                }
                if (mod.getQuality() <= minNum) {
                    minIndex = cIndex;
                    minNum=mod.getQuality();
                }
                qualtys.add(selectModel);
                cIndex++;
            }
            if(currentQualityPosition==-1){
                if(qualty<maxNum){//大于最小的 取最大的
                    currentQualityPosition=minIndex;
                }else{//取最小的
                    currentQualityPosition=maxIndex;
                }
            }
            //设置清晰度
            player.setQualtys(qualtys);
            qualty=currentQualityPosition;
            changeQualty();

            toMobclickAgent("play_total");
        }catch (Exception ex){}
    }

    private void changeQualty(){
        player.setQualty(qualty);
        PathDataBean pathDataBean= pathDataBeans.get(qualty);
        if(pathDataBean!=null){
            player.eggTime=pathDataBean.getEggTime();
        }
        if(lineModel.getType()==PlayTypeEnum.HTTP){
            httpLineController=new HttpLineController(lineChangeListener);
            httpLineController.change(pathDataBean.getPath());
        }else  if(lineModel.getType()==PlayTypeEnum.M3U8){
            if(m3u8LineController!=null){
                m3u8LineController.onDestroy();
            }
            m3u8LineController=new M3u8LineController(lineChangeListener);//   xfdfd.m3u8
            m3u8LineController.change(pathDataBean.getPath(),pathDataBean.getMd5());
        }else if(lineModel.getType()==PlayTypeEnum.TORRENT){
            if(torLineController!=null){//先关闭上一个播放
                torLineController.onDestroy();
            }
            torLineController=new TorLineController(lineChangeListener,PVideoViewActivity88.this,shortId);
            torLineController.change(pathDataBean.getPath());
        }
    }
    @Override
    public void onUpdateCollect() {
    }
    @Override
    public void onCacheResult(LikeBean likeBean) {
        if(likeBean==null){
            toast("该片暂不支持缓存!");
            return;
        }
        if(TextUtils.isEmpty(likeBean.getData())){
            toast("该片暂不支持缓存!");
            return;
        }
        String xplayUrl=BaseCommon.VIDEO_URL+likeBean.getData();// "http://demo.flvurl.cn/dianyunMovieBT/4667496-1hd.mp4.torrent";//likeBean.getData();

        DownModel downModel=new DownModel();
        downModel.setShortId(shortId);
        downModel.setUrl(xplayUrl);
        downModel.setName(movieBean.getTitle());
        downModel.setConver(movieBean.getCover());

        CacheManager.getInstance().downByUrl(downModel);
    }
    @Override
    public void onLikeResult(LikeBean likeBean, int type) {
    }
    @Override
    public void onError(String msg) {
    }
    @Override
    public void onDetailError() {
        try {
            //删除观看记录,收藏中的电影信息
            CacheBean c = new CacheBean();
            c.setShortId(shortId);
            c.settType(CacheType.CACHE_RECODE);
            CacheManager.getInstance().getDbHandler().delete(c);

            CacheBean c1 = new CacheBean();
            c1.setShortId(shortId);
            c1.settType(CacheType.CACHE_COLLECT);
            CacheManager.getInstance().getDbHandler().delete(c1);
        }catch (Exception ex){}
        finally {
            finish();
        }
    }

    @Override
    public void onPause() {
        if(player!=null){
            player.onPause();
        }
        super.onPause();
        /**demo的内容，恢复系统其它媒体的状态*/
        //MediaUtils.muteAudioFocus(mContext, true);
    }
    @Override
    public void onResume() {
        if(player!=null){
            player.onResume();
        }
        super.onResume();
    }
    @Override
    public void onDestroy() {
        stopAdvTimer();
        if(commentView!=null){
            commentView.onDestroy();
        }
        if(player!=null){
            player.onDestroy();
        }
        if(m3u8LineController!=null){
            m3u8LineController.onDestroy();
        }
        if(torLineController!=null){
            torLineController.onDestroy();
        }
//        if(dolitVideoView!=null){
//            dolitVideoView.stopPlayback();
//        }
        super.onDestroy();
    }
    /**
     * 发送事件到 MobclickAgent
     * @param eventKey
     */
    private void toMobclickAgent(String eventKey){
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("shortId", shortId);
            toMobclickAgentEvent(PVideoViewActivity88.this, eventKey, map);
//            MobclickAgent.onEvent();
        }catch (Exception ex){}
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// 布局事件回调 /////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onFinish() {
        finish();
    }
    private int collectNumber=0;
    private Timer collectTimer;
    //点击收藏  3 秒才能点击一次
    @Override
    public boolean onCollect(boolean isCollect) {
        if(movieBean==null||collectNumber>0){
            return false;
        }
        try {
            collectTimer=new Timer();
            collectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                collectNumber++;
                if(collectNumber>3){
                    stopCollectTimer();
                }
                }
            },100,1000);
            movieBean.setCollect(isCollect);
            presenter.updateCollect(movieBean, DetailTouchType.TOUCH_COLLECT, 0);
            if (isCollect == true) {
                toast("取消收藏成功");
            } else {
                toast("添加收藏成功");
            }
            return true;
        }catch (Exception ex){}
        return false;
    }
    private void stopCollectTimer(){
        if(collectTimer!=null){
            collectTimer.cancel();
        }
        collectTimer=null;
        collectNumber=0;
    }
    @Override
    public boolean onLike() {
        if(movieBean==null){
            return false;
        }
        //判断如果已经点、踩过了就不请求了
        if(movieBean.getPraiseState()!=0){
            return false;
        }
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity88.this);
        if(boo){
            return false;
        }
        presenter.getLike(shortId, 1);
        return true;
    }
    @Override
    public boolean onNolike() {
        if(movieBean==null){
            return false;
        }
        if(movieBean.getPraiseState()!=0){
            return false;
        }
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity88.this);
        if(boo){
            return false;
        }
        presenter.getLike(shortId, 2);
        return true;
    }
    @Override
    public boolean onDown() {
        if(this.movieBean==null){
            return false;
        }
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity88.this);
        if(boo){
            return false;
        }
        Config config= HttpServerManager.getInstance().getM3u8DownManager().getConfig(shortId);
        if(config!=null){
            toast("该影片已缓存过了");
            return false;
        }
//        M3U8TaskModel mod= M3u8DownTaskManager.getInstance().getM3u8TaskModelByShortId(shortId);
//        if(mod!=null) {
//            toast("该影片已缓存过了");
//            return false;
//        }
        //判断当前是否台允许下载
        //判断当前是否开启仅wifi 下载
        boolean wifiBoo=MainApplicationContext.onlyWifiDownBoo;
        if(wifiBoo){//开启了
            //判断当前是否是wifi
            boolean boo1=NetWorkUtils.isWifiConnected(PVideoViewActivity88.this);
            if(!boo1){//不让下载,给出提示
                toast("已设置仅wifi下载,可到设置界面重置!");
                return false;
            }
        }
        presenter.getQualtys(shortId);
        return true;
    }

    @Override
    public void onCacheResultV1(DownloadBean downloadBean) {
        if(downloadBean==null||downloadBean.getData()==null){
            toast("该片暂不支持下载");
            return;
        }
        DownloadBean.DownloadData data= downloadBean.getData();

        DownConfig config=new DownConfig();
        config.setDownUrl(BaseCommon.M3U8_URL+data.getPath());
        config.setmId(shortId);
        config.setmName(movieBean.getTitle());
        config.setmConver(movieBean.getCover());
        config.setmSize(data.getFileSize());
        config.setV1(data.getMd5());
        config.setThreadNum(3);

        HttpServerManager.getInstance().startDown(config);

        //获取当前可用空间 和当前影片大小进行判断
        toast("即将开始下载");
        boolean boo = SystemUtils.availableSpaceVerification(data.getFileSize());
        if (!boo) {//空间不足,请及时清理,以免影响看片。
            MainApplicationContext.showips("储存空间不足建议清理手机缓存。", PVideoViewActivity88.this, TipsEnum.TO_TIPS);
        }

//        M3U8TaskModel model=new M3U8TaskModel();
//        model.setTitle(movieBean.getTitle());
//        model.setKey(data.getMd5());
//        model.setShortId(shortId);
//        model.setOldTask(false);
//        model.setUrl(BaseCommon.M3U8_URL+data.getPath());
//        model.setConver(movieBean.getCover());
//        model.setFileSize(data.getFileSize());

        //添加到数据库中
//        TaskModel taskModel=new TaskModel();
//        taskModel.setTaskId(model.getTaskId());
//        taskModel.setPercent("0");
//        taskModel.setTitle(movieBean.getTitle());
//        taskModel.setUrl(model.getUrl());
//        taskModel.setConver(movieBean.getCover());
//        taskModel.setvKey(data.getMd5());
//        taskModel.setShortId(shortId);
//        taskModel.setFileSize(data.getFileSize()+"");

        //添加到下载任务
        //M3u8DownTaskManager.getInstance().addTask(model);
        //添加到数据库
        //CacheManager.getInstance().getM3U8DownLoader().update(taskModel);
    }

    @Override
    public void onGetQualityResult(QualtyBean qualtyBean) {
        if(qualtyBean==null||qualtyBean.getData()==null||qualtyBean.getData().size()<=0){
            toast("该片暂不支持下载");
            return;
        }
        showSelectQualty(qualtyBean.getData());
    }

    private int downQPos=0;
    private BottomDialog bottomDialog;
    //显示选择清晰度弹窗
    private void showSelectQualty(final List<QualtyBean.QualtyData> qualtyData){
        View outerView1 = LayoutInflater.from(this).inflate(R.layout.dialog_select_quality, null);
        //日期滚轮
        final WheelView wv1 = (WheelView) outerView1.findViewById(R.id.wv1);
        final List<String> qualityList=new ArrayList<String>();
        for(QualtyBean.QualtyData data:qualtyData){
            qualityList.add(data.getQualityName());
        }
        wv1.setItems(qualityList,0);

        //联动逻辑效果
        wv1.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index,String item) {
                Log.e("TAG",item+" position:"+index);
            }
        });

        TextView tv_ok = (TextView) outerView1.findViewById(R.id.tv_ok);
        TextView tv_cancel = (TextView) outerView1.findViewById(R.id.tv_cancel);
        //点击确定
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bottomDialog.dismiss();
                String mSelectDate = wv1.getSelectedItem();
                downQPos=wv1.getSelectedPosition();
                Log.e("TAG",downQPos+"");

                QualtyBean.QualtyData qData= qualtyData.get(downQPos);
                presenter.getCacheV1(shortId,qData.getQuality());
                //请求对应清晰度
                //presenter.getCacheV1(qualityList)

                //String posStr=(downQPos+1)+"";
                //qualityText.setText(mSelectDate);
//                MainApplicationContext.CURRENT_QUALITY=posStr;
//                CacheManager.getInstance().getLocalHandler().put("quality_info",posStr);
            }
        });
        //点击取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bottomDialog.dismiss();
            }
        });
        //防止弹出两个窗口
        if (bottomDialog !=null && bottomDialog.isShowing()) {
            return;
        }

        bottomDialog = new BottomDialog(this, R.style.ActionSheetDialogStyle);
        //将布局设置给Dialog
        bottomDialog.setContentView(outerView1);
        bottomDialog.show();//显示对话框
    }



    LoadingDailog loadingDailog=null;
    @Override
    public boolean onShare() {
        if(movieBean==null){
            return false;
        }
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity88.this);
        if(boo){
            return false;
        }
        try {
            loadingDailog = presenter.getDialog();
            if(loadingDailog!=null) {
                loadingDailog.show();
            }
        }catch (Exception ex){}
        try {
            final boolean b=SystemUtils.copy(movieBean.getMovieShareText(), this);
            SharePicModel sharePicModel = new SharePicModel((ViewGroup) getWindow().getDecorView(), movieBean, this);
            GeneratePictureManager.getInstance().generate(sharePicModel, new GeneratePictureManager.OnGenerateListener() {
                @Override
                public void onGenerateFinished(Throwable throwable, Bitmap bitmap) {
                if (throwable != null || bitmap == null) {
                    if(b==true){
                        //改变颜色
                        toast("推广文字已复制,快去分享吧!!!");
                    }
                } else {
                    toast("推广文字已复制,图片保存到相册,快去分享吧!!!");
                }
                try {
                    if(loadingDailog!=null) {
                        loadingDailog.cancel();
                    }
                } catch (Exception ex) {
                }
//                    try {
//                        PlayHolderBaseModel playHolderBaseModel = (PlayHolderBaseModel) baseModels.get(0);
//                        playHolderBaseModel.isShare = true;
//
//                        adapter.notifyItemChanged(0);
//                    }catch (Exception ex){}
                }
            }, PVideoViewActivity88.this);
            return true;
        }catch (Exception ex){}
        return false;
    }
    @Override
    public void onClickError() {
        new PlayErrorDialogView(PVideoViewActivity88.this,playerUrl,shortId)
                .setPlayErrorDialogListener(new PlayErrorDialogView.IPlayErrorDialogListener() {
                    @Override
                    public void onSuccess() {
                        toast("上报成功,感谢您的反馈!!!");
                    }
                    @Override
                    public void onError() {
                        toast("上报失败,请稍后重试!");
                    }
                })
                .show();
    }

    private CommentView commentView;

    @Override
    public void onClickComment() {
        try {
            LogUtils.showLog("点击评论了...");
            if (commentView == null) {
                commentView = new CommentView(this, shortId);
            }
            commentView.show();
        }catch (Exception ex){}
    }

    @Override
    public void onChangeTotal(int total) {
        try {
            if (movieBean != null && baseModels != null && baseModels.size() > 0) {
                movieBean.setFilmReviewCount(total);
                PlayHolderBaseModel mod = (PlayHolderBaseModel) baseModels.get(0);
                mod.setValue(movieBean);
                adapter.notifyItemChanged(0);
            }
        }catch (Exception ex){}
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////// 缓存提示 ///////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (player != null) {
            player.onConfigurationChanged(newConfig);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            boolean boo = player != null ? player.onBackPressed() : super.onKeyDown(keyCode, event);
            if (!boo) {
                return super.onKeyDown(keyCode, event);
            }
            return boo;
        }
        return super.onKeyDown(keyCode, event);
    }
}
