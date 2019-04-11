package com.xcore.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.common.BaseCommon;
import com.shuyu.common.CommonRecyclerAdapter;
import com.shuyu.common.CommonRecyclerManager;
import com.shuyu.common.model.RecyclerBaseModel;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.MvpActivity;
import com.xcore.cache.CacheManager;
import com.xcore.cache.CacheModel;
import com.xcore.cache.DownModel;
import com.xcore.cache.LocalDownLoader;
import com.xcore.cache.beans.CacheBean;
import com.xcore.cache.beans.CacheType;
import com.xcore.data.bean.DetailBean;
import com.xcore.data.bean.DownloadBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.bean.LineModel;
import com.xcore.data.bean.MovieBean;
import com.xcore.data.bean.PathBean;
import com.xcore.data.bean.PathDataBean;
import com.xcore.data.bean.QualtyBean;
import com.xcore.data.model.SelectModel;
import com.xcore.picgen.GeneratePictureManager;
import com.xcore.picgen.SharePicModel;
import com.xcore.presenter.DetailPresenter;
import com.xcore.presenter.view.DetailView;
import com.xcore.ui.Config;
import com.xcore.ui.enums.DetailTouchType;
import com.xcore.ui.enums.PlayTypeEnum;
import com.xcore.ui.holder.PlayDetailHolder;
import com.xcore.ui.holder.PlayRelatedHolder;
import com.xcore.ui.holder.model.PlayHolderBaseModel;
import com.xcore.ui.other.PlayErrorDialogView;
import com.xcore.ui.other.TipsEnum;
import com.xcore.ui.other.XRecyclerView;
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
import com.xcore.utils.LogUtils;
import com.xcore.utils.NetWorkUtils;
import com.xcore.utils.SystemUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.utils.common.Utils;

public class PVideoViewActivity1202 extends MvpActivity<DetailView,DetailPresenter> implements DetailView,
        ChangeTotalLister,IPlayClickListenner {

    private SelectModel lineModel;//播放的当前线路
    private HttpLineController httpLineController;
    private M3u8LineController m3u8LineController;
    private TorLineController torLineController;
    private LocalLinneController localLinneController;

    private Config config;

    private String playerUrl="";
    private List<PathDataBean> pathDataBeans=null;//当前播放线路的全部路径,清晰度
    PlayerView player;

    PlayerListener playerListener=new PlayerListener() {
        @Override
        public void onStartPlay(SelectModel selectModel) {
            try {
                if (movieBean == null) {//没有返回详情
                    return;
                }
                lineModel = selectModel;
                if (lineModel == null) {
                    Toast.makeText(PVideoViewActivity1202.this, "没有播放线路哟~~", Toast.LENGTH_SHORT).show();
                    return;
                }
                String line = (String) lineModel.getValue();
                if (line.isEmpty() || line.length() <= 0) {
                    Toast.makeText(PVideoViewActivity1202.this, "没有播放线路哟~~", Toast.LENGTH_SHORT).show();
                    return;
                }
                //请求线路播放地址
                presenter.getPlayPath(shortId, line);
            }catch (Exception ex){}
        }
        @Override
        public void onStartPlay(SelectModel selectModel, int pos,int qualty1) {
            playSeconds=pos;
            qualty=qualty1;

            lineModel=selectModel;
            if(lineModel==null){
                toast("没有播放的线路哟~~");
                return;
            }
            String line= (String) lineModel.getValue();
            if(line.isEmpty()||line.length()<=0){
                toast("没有播放的线路哟~~");
                return;
            }
            if(lineModel.getType()==PlayTypeEnum.TORRENT){
                if(!MainApplicationContext.serverSuccess){
                    MainApplicationContext.startP2PServer();
                    if(player!=null){
                        player.changeLine();
                    }
                    return;
                }
            }
            toast("线路切换中...");
            PathBean pathBean= lineMaps.get(lineModel.getValue());
            if(pathBean==null) {
                //请求线路播放地址
                presenter.getPlayPath(shortId, line);
            }else{
                onGetPathResult(pathBean);
            }
        }
        @Override
        public void onStartPlayChangeQualty(SelectModel selectModel, int qualty1,int pos) {//切换清晰度
            playSeconds=pos;
            qualty=qualty1;
            PathDataBean dataBean= pathDataBeans.get(qualty1);
//            qualty=dataBean.getQuality();
            String qStr=dataBean.getQualityName();
//            toast("切换清晰度");
            Toast toast=Toast.makeText(PVideoViewActivity1202.this,"切换清晰度为"+qStr,Toast.LENGTH_LONG);
            toast.show();
            changeQualty();
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
                player.startPlay(path,playSeconds);

                PathDataBean dataBean= pathDataBeans.get(qualty);
                long fileSize=dataBean.getFileSize();
                if(fileSize<=0){
                    return;
                }
                //判断当前是否是流量方式播放
                boolean boo=NetWorkUtils.isWifiConnected(PVideoViewActivity1202.this);
                if(boo==true){
                    return;
                }
                String vStr=Utils.getDisplayFileSize(fileSize);
                toastLong("预计消耗流量 "+vStr,R.color.color_black);
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
    private PVideoViewActivity1202 __this;

    private XRecyclerView xRecyclerView;
    private List<RecyclerBaseModel> baseModels=new ArrayList<>();
    CommonRecyclerAdapter adapter;

    Map<String,PathBean> lineMaps=new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pvideo_view100;
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
        config=MainApplicationContext.getConfig();

        //获取传入的参数
        Intent intent = getIntent();
        shortId=intent.getStringExtra("shortId");
        playSeconds=intent.getIntExtra("position",0);
        qualty=Integer.valueOf(MainApplicationContext.CURRENT_QUALITY);
        //
        player=new PlayerView(this,shortId);
        player.setPlayerListener(playerListener);


        LogUtils.shortId=shortId;
        LogUtils.sId="";
        LogUtils.isUpSpeed=false;

        CommonRecyclerManager manager=new CommonRecyclerManager();
        manager.addType(PlayDetailHolder.ID,PlayDetailHolder.class.getName());
        manager.addType(PlayRelatedHolder.ID,PlayRelatedHolder.class.getName());
        adapter=new CommonRecyclerAdapter(this,manager,baseModels);

        xRecyclerView=findViewById(R.id.xRecyclerView);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        xRecyclerView.setAdapter(adapter);


        //判断是否停止下载
        LocalDownLoader localDownLoader=CacheManager.getInstance().getLocalDownLoader();
        if(MainApplicationContext.IS_PLAYING_TO_CACHE==true) {//停止所有运行的任务
            localDownLoader.stopRunAll();
        }

        if(MainApplicationContext.CACHE_PROMPT==true) {//有提示才进来
            //每次进入时检测空间是否足够 剩余1G 就提示   本地启动不再提示
            Long size = Long.valueOf(1024 * 1L * 1024 * 1024);
            boolean boo = SystemUtils.availableSpaceVerification(size);//小于3G的时候给提示
            if (!boo) {//空间不足,请及时清理,以免影响看片。
                MainApplicationContext.showips("观看缓存过多,建议及时清理以免影响看影片", this, TipsEnum.TO_CLEAR);
            }
        }
    }
    @Override
    protected void initData() {
        presenter.getDetail(shortId);
    }
    @Override
    public DetailPresenter initPresenter() {
        return new DetailPresenter();
    }

    @Override
    public void onDetailResult(DetailBean detailBean) {
        movieBean=detailBean.getData();
        movieBean.setCollect(isCollect);
        try {
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
        }catch (Exception ex){}
        //缓存好了的视频就自动播放了
        checkLocalPlay();
        //检查
//       new CheckLineModel(lineSelectModels);
    }
    //检查本地下载播放
    private void checkLocalPlay(){
        localLinneController=new LocalLinneController(lineChangeListener,PVideoViewActivity1202.this);
        localLinneController.change(shortId);
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
            torLineController=new TorLineController(lineChangeListener,PVideoViewActivity1202.this,shortId);
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
        if(player!=null){
            player.onDestroy();
        }
        if(m3u8LineController!=null){
            m3u8LineController.onDestroy();
        }
        if(torLineController!=null){
            torLineController.onDestroy();
        }
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
            toMobclickAgentEvent(PVideoViewActivity1202.this, eventKey, map);
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
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity1202.this);
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
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity1202.this);
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
        try {
            boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity1202.this);
            if(boo){
                return false;
            }
            boolean serverSuccess1 = MainApplicationContext.serverSuccess;
            if (!serverSuccess1) {//启动服务失败,重新启动
                MainApplicationContext.startP2PServer();
                return false;
            }
            CacheModel cDownModel = CacheManager.getInstance().getLocalDownLoader().getDataByShortId(shortId);//.getDownHandler().getByShortId(shortId);
            if (cDownModel != null) {
                showMsg("本影片已在缓存列表!!!");
                return true;
            } else {
                //判断当前是否开启仅wifi 下载
                boolean wifiBoo=MainApplicationContext.onlyWifiDownBoo;
                if(wifiBoo){//开启了
                    //判断当前是否是wifi
                    boolean boo1=NetWorkUtils.isWifiConnected(PVideoViewActivity1202.this);
                    if(!boo1){//不让下载,给出提示
                        toast("已设置仅wifi下载,可到设置界面重置!");
                        return false;
                    }
                }
                presenter.getCachePath(shortId);
            }
            return true;
        }catch (Exception ex){}
        return true;
    }

    @Override
    public void onGetQualityResult(QualtyBean qualtyBean) {
    }
    @Override
    public void onCacheResultV1(DownloadBean downloadBean) {
    }

    LoadingDailog loadingDailog=null;
    @Override
    public boolean onShare() {
        if(movieBean==null){
            return false;
        }
        boolean boo=MainApplicationContext.toGuestLogin(PVideoViewActivity1202.this);
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
            }, PVideoViewActivity1202.this);
            return true;
        }catch (Exception ex){}
        return false;
    }
    @Override
    public void onClickError() {
//        isOpenErr=true;
//        Intent intent1=new Intent(PVideoViewActivity1202.this,VideoSubmitErrorActivity.class);
//        intent1.putExtra("shortId",movieBean.getSourceID());
//        intent1.putExtra("playUrl",playerUrl);
//        startActivity(intent1);

        new PlayErrorDialogView(PVideoViewActivity1202.this,playerUrl,shortId)
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
        if(commentView!=null) {
            commentView= new CommentView(this, shortId);
        }
        commentView.show();
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
    private void showMsg(String msg){
        toast(msg);
    }

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
