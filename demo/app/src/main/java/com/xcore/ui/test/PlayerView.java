package com.xcore.ui.test;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.common.BaseCommon;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseRecyclerAdapter;
import com.xcore.data.bean.EggBean;
import com.xcore.data.bean.LikeBean;
import com.xcore.data.model.SelectModel;
import com.xcore.data.utils.TCallback;
import com.xcore.ext.ImageViewExt;
import com.xcore.fadai.particlesmasher.ParticleSmasher;
import com.xcore.fadai.particlesmasher.SmashAnimator;
import com.xcore.services.ApiFactory;
import com.xcore.ui.adapter.SelectAdapter;
import com.xcore.ui.other.TipsEnum;
import com.xcore.utils.CacheFactory;
import com.xcore.utils.LogUtils;
import com.xcore.utils.VideoCheckUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.dolit.p2ptrans.P2PTrans;
import cn.dolit.utils.common.Utils;

public class PlayerView implements DolitBaseMediaPlayer.OnRotationChangeListener {
    @Override
    public void onRotationChangeListener() {
        if(!IS_FULL_SCREEN){
            tryFullScreen(false);
        }else{
            onlyFullScreen();
        }
    }
    public interface OnControlPanelVisibilityChangeListener{
        void change(boolean isShowing);
    }
    public void clickPlayButton(){
        //得到当前的线路
        playSpeedModel=new PlaySpeedModel();
        currentLinePos=0;
        SelectModel lineMod=getLine();
        playerListener.onStartPlay(lineMod);
    }
    //点击
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.playButton:
                    if(playerListener!=null){
                        clickPlayButton();
                    }
                    break;
                case R.id.puaseButton:
                case R.id.center_puase:
                    if(videoView!=null&&videoView.getCurrentPosition()>0) {
                        boolean boo = videoView.isPlaying();
                        if(boo){
                            currentPos=videoView.getCurrentPosition();
                            videoView.pause();//切换成暂停
                        }else{
                            videoView.start();
                        }
                        updatePuaseButton(!boo);
                    }
                    break;
                case R.id.full_icon:
                    toggleFullScreen();
                    break;
                case R.id.backLeft:
                    if(playerListener!=null){
                        playerListener.onBackPressed();
                    }
                    break;
                case R.id.txt_doubleSpeed://点击倍速
                    type=0;
                    touchSetting();
                    break;
                case R.id.txt_qualty://点击清晰度
                    type=1;
                    touchSetting();
                    break;
                case R.id.txt_line://点击线路
                    type=2;
                    touchSetting();
                    break;
            }
        }
    };

    private PlayerListener playerListener;
    public PlayerView setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
        return this;
    }
    private boolean IS_FULL_SCREEN=false;//当前是否是全屏
    private boolean IS_OPTION=false;

    public int eggTime=10;//默认时间

    //设置按钮显示,隐藏
    public void setFull(boolean full) {
        IS_FULL_SCREEN = full;
//        Log.e("TAG","设置是否全屏:"+IS_FULL_SCREEN);
//        $.id(R.id.txt_doubleSpeed).visibility(IS_FULL_SCREEN?View.VISIBLE:View.GONE);
//        $.id(R.id.txt_total_time).visibility(IS_FULL_SCREEN?View.VISIBLE:View.GONE);
//        $.id(R.id.txt_qualty).visibility(IS_FULL_SCREEN?View.VISIBLE:View.GONE);

        $.id(R.id.full_icon).image(IS_FULL_SCREEN?R.drawable.full_no:R.drawable.quanping);
    }

    private View setLayout;
    private AudioManager audioManager;
    private int mMaxVolume;
    private static final int MESSAGE_SHOW_PROGRESS = 1;
    private static final int MESSAGE_FADE_OUT = 2;
    private static final int MESSAGE_SEEK_NEW_POSITION = 3;
    private static final int MESSAGE_HIDE_CENTER_BOX = 4;
    private static final int MESSAGE_RESTART_PLAY = 5;
    private static final int MESSAGE_SPEED_SHOW=6;
    private OrientationEventListener orientationEventListener;
    private Query $;
    private SeekBar seekBar;

    private Activity activity;
    private DolitVideoView videoView;
    private int initHeight;
    private int defaultTimeout=3000;
    private int screenWidthPixels;

    private int currentPosition;
    private boolean fullScreenOnly;
    private boolean portrait;
    private boolean isLive = false;//是否为直播
    private boolean isShowing=false;
    private boolean isStartPlay=false;//是否点击播放了,点击了才可以显示或隐藏控制条

    private float brightness=-1;
    private int volume=-1;
    private long newPosition = -1;
    private long defaultRetryTime=5000;
    private boolean isDragging;
    private boolean instantSeeking;
    //当前点击的是哪个(倍速,清晰度,线路)
    private int type=0;

    private int currentPos=0;
    private SelectAdapter adapter;
    private long joinTime=0;

//    private RelativeLayout eggLinnearLayout;

    ImageViewExt eggImg;

    @SuppressWarnings("HandlerLeak")
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_FADE_OUT:
                    hide(false);
                    break;
                case MESSAGE_HIDE_CENTER_BOX:
                    $.id(R.id.app_video_volume_box).gone();
                    $.id(R.id.app_video_brightness_box).gone();
                    $.id(R.id.app_video_fastForward_box).gone();
                    break;
                case MESSAGE_SEEK_NEW_POSITION:
                    if (!isLive && newPosition >= 0&&videoView!=null) {
                        videoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging && isShowing) {
                        msg = obtainMessage(MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
//                        updatePausePlay();
                    }
                    break;
                case MESSAGE_RESTART_PLAY:
//                    play(url);
                    break;
                case MESSAGE_SPEED_SHOW:
                    String spStr= (String) msg.obj;
                    $.id(R.id.txt_speed).text("加载中,请稍后...\n"+spStr+"/s");
                    break;
            }
        }
    };

    private Timer speedTiimer;
    private PlaySpeedModel playSpeedModel;

    //开始获取速度
    private void startGetSpeed(){
        Log.e("TAG","获取速度。。。");
        if(speedTiimer==null){
            speedTiimer=new Timer();
            speedTiimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                            if (speedTiimer != null) {
                                speedTiimer.cancel();
                                speedTiimer = null;
                                return;
                            }
                        }
                        long speed = videoView.getTcpSpeed();
                        if (streamId == null || streamId.length() <= 0) {
                            if (videoView == null) {
                                return;
                            }
                            if (playSpeedModel != null && speed > 0) {
                                playSpeedModel.setHttpUrl(modelUrl);
                                playSpeedModel.setTime(1);
                                long total = playSpeedModel.getTotal();
                                total += speed;
                                playSpeedModel.setTotal(total);
                                long htotal = playSpeedModel.getHttpTotal();
                                playSpeedModel.setHttpTotal(speed + htotal);
                                playSpeedModel.setHttpAvg(speed);
                                playSpeedModel.setAvgSpeed(speed);
                            }
                            final String vStr = Utils.getDisplayFileSize(speed);
                            Log.e("TAG", "TCP速度:" + vStr);

                            Message msg = handler.obtainMessage();
                            msg.obj = vStr;
                            msg.what = MESSAGE_SPEED_SHOW;
                            handler.sendMessage(msg);
                        } else {//种子播放获取速度
                            String urlStr = P2PTrans.getStreamInfoUrl(BaseCommon.P2P_SERVER_PORT, streamId);
                            OkGo.<String>get(urlStr).execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    try {
                                        String v = response.body();
                                        P2PTrans.StartStreamResult result = new Gson().fromJson(v, P2PTrans.StartStreamResult.class);
                                        setSpeed(result);
                                    } catch (Exception ex) {
                                    }
                                }

                                @Override
                                public void onError(Response<String> response) {
                                }
                            });
                        }
                    }catch (Exception ex){}
                }
            },0,1000);
        }
    }
    private String streamId="";
    public void setStreamId(String sId){
        this.streamId=sId;
    }
    private void setSpeed(P2PTrans.StartStreamResult result1){
        final P2PTrans.StartStreamResult result=result1;
        int code=result.getCode();
        if(code!=0){
            return;
        }
        P2PTrans.StreamInfo info= result.getStream();
        if(info==null){
            return;
        }
        try {
            long speed= info.getDownloadSpeed();
            LogUtils.showLog("TOR播放速度:"+speed);
            String vStr=Utils.getDisplayFileSize(info.getDownloadSpeed());
            Message msg = handler.obtainMessage();
            msg.obj = vStr;
            msg.what=MESSAGE_SPEED_SHOW;
            handler.sendMessage(msg);

            if(playSpeedModel!=null){
                playSpeedModel.setHttpUrl(modelUrl);
                playSpeedModel.setTime(1);
                playSpeedModel.setTotal(info.getTotalBytesDown());
                playSpeedModel.setHttpTotal(info.getTotalServerBytes());
                playSpeedModel.setHttpAvg(info.getServerTotalSpeed());
                playSpeedModel.setAvgSpeed(info.getDownloadSpeed());
                int pos=0;
                if(videoView!=null){
                    videoView.getCurrentPosition();
                }
                playSpeedModel.setCurrentPos(pos);
            }
        }catch (Exception ex){}
    }

    //停止获取速度
    private void stopGetSpeed(){
        if(speedTiimer==null){
            return;
        }
        try {
            speedTiimer.cancel();
            speedTiimer = null;
            if (playerListener != null) {
                if (playSpeedModel != null) {
                    long endTime = System.currentTimeMillis();
                    long endT = endTime - joinTime;
                    playSpeedModel.setStopTime((int) endT);
                    int pos = 0;
                    if (videoView != null) {
                        pos = videoView.getCurrentPosition();
                    }
                    playSpeedModel.setCurrentPos(pos);
                    playSpeedModel.setHttpUrl(modelUrl);
                    playerListener.updatePlaySpeed(playSpeedModel);
                }
            }
        }catch (Exception ex){}
    }

    //更新进度
    private long setProgress() {
        if (isDragging){
            return 0;
        }
        updatePuaseButton(videoView.isPlaying());
        try {
            long position = videoView.getCurrentPosition();
            long duration = videoView.getDuration();
            if (seekBar != null) {
                if (duration > 0) {
                    double xx = position * 1.0 / duration;
                    int xPos = (int) (xx * 1000);
                    seekBar.setProgress(xPos);
                }
                //只有网络资源有缓冲  不设置缓冲
//                if (!modelUrl.contains(".xv")||modelUrl.contains("127.0.0.1")) {
//                    int bufferPercentage = videoView.getBufferPercentage();  //0~100
//                    int totalbuffer = bufferPercentage * seekBar.getMax();
//                    int secordProgress = totalbuffer / 100;
//                    seekBar.setSecondaryProgress(secordProgress);
//                } else {//本地视频播放 没有缓冲
//                    seekBar.setSecondaryProgress(0);
//                }
                seekBar.setSecondaryProgress(0);
            }
            $.id(R.id.txt_current_time).text(generateTime(position));
            if (duration > 0) {
                $.id(R.id.txt_total_time).text("/"+generateTime(duration));
            }
            return position;
        }catch (Exception ex){}
        return 0;
    }

    String modelUrl="";
    int timeOutNext=1;
    int pos=0;//传进来的播放seekTo...

    IPlayerViewListener iPlayerViewListener=new IPlayerViewListener(){
        @Override
        public void onError(String msg, int code) {
            IS_OPTION=false;
            if(code==900005&&timeOutNext%2==1){//一般为超时,再请求一次
                timeOutNext++;
                startPlay(modelUrl,pos);
            }else {
                if (playerListener != null) {
                    playerListener.onError(modelUrl, msg, code);
                }
            }
        }
        @Override
        public void startBuffer() {
            Log.e("TAG","开始缓冲...");
            $.id(R.id.loadingLayout).visible();
            //获取速度
            startGetSpeed();
            $.id(R.id.center_puase).gone();
            isLoading=true;
        }
        @Override
        public void endBuffer() {
            $.id(R.id.loadingLayout).gone();
            if(playSpeedModel!=null){
                if(playSpeedModel.getTotal()<=0){
                    playSpeedModel.setTotal(1000);
                }
            }
            IS_OPTION = true;
            stopGetSpeed();
            videoView.setVisibility(View.VISIBLE);
            isLoading=false;
        }
        @Override
        public void startBufferPlay() {//开始播放了
            try {
                isLoading=false;
                videoView.setVisibility(View.VISIBLE);
                $.id(R.id.loadingLayout).gone();
                $.id(R.id.txt_speed).text("");
                if(playSpeedModel!=null){
                    if(playSpeedModel.getTotal()<=0){
                        playSpeedModel.setTotal(1000);
                    }
                }
                stopGetSpeed();
                updatePuaseButton(true);//更新按钮显示
                //更新时间
                String currentTime = generateTime(videoView.getCurrentPosition());
                $.id(R.id.txt_current_time).text(currentTime);

                String totalTime = generateTime(videoView.getDuration());
                $.id(R.id.txt_total_time).text("/"+totalTime);
                seekBar.setOnTouchListener(null);
                IS_OPTION = true;
                $.id(R.id.full_icon).visibility(View.VISIBLE);

                if(videoView!=null){
                    currentPos= videoView.getCurrentPosition();
                }

                //TODO 显示清晰度,线路,倍速
                if (speeds != null && speeds.size() > currentSpeedPos) {
                    SelectModel speedMod = speeds.get(currentSpeedPos);
                    $.id(R.id.txt_doubleSpeed).text(speedMod.getName());
                }

                if (lines != null && lines.size() > currentLinePos && currentLinePos >= 0) {
                    String lineName = lines.get(currentLinePos).getName();
                    $.id(R.id.txt_line).text(lineName);
                }
                if (qualtys != null && qualtys.size() > currentQualtyPos) {
                    String qualtyName = qualtys.get(currentQualtyPos).getName();
                    $.id(R.id.txt_qualty).text(qualtyName);
                }

                //TODO 观看记录,发送播放成功事件,以接口的方式返回到主界面去操作...
                if (playerListener != null) {
                    playerListener.onPlaySuccess(currentPos);
                }
            }catch (Exception ex){}
        }
    };

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
//            $.id(R.id.app_video_status).gone();//移动时隐藏掉状态image
            int duration=videoView.getDuration();
//            int newPosition = (int) ((duration * progress*1.0) / 1000);
            int pro=progress;
            double xPro=pro*1.0/1000;//得到当前的进度
            double value=duration*1.0*xPro;//得到要跳的值
            if(value>duration){
                value=duration;
            }else if(value<=0){
                value=0;
            }
            String time = generateTime((long) value);
            if (instantSeeking&&videoView!=null){
                videoView.seekTo((long) value);
            }
            $.id(R.id.txt_current_time).text(time);
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            show(3600000);
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            if (instantSeeking){
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking&&videoView!=null){
                int duration=videoView.getDuration();
                if(duration<=0){
                    return;
                }
                int pro=seekBar.getProgress();
                double xPro=pro*1.0/1000;//得到当前的进度
                double value=duration*1.0*xPro;//得到要跳的值
                if(value>duration){
                    value=duration;
                }else if(value<=0){
                    value=0;
                }
                Log.e("TAG","滑动:"+value+" Progress："+pro+"  总:"+duration+"xPRO:"+xPro);
                videoView.seekTo((long) value);
            }
            show(defaultTimeout);
//            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
//            handler.sendEmptyMessageDelayed(MESSAGE_SHOW_PROGRESS, 1000);
        }
    };
    public void hide(boolean force) {
        if (force || isShowing) {
            handler.removeMessages(MESSAGE_SHOW_PROGRESS);
            showBottomControl(false);
            $.id(R.id.app_video_top_box).gone();
//            $.id(R.id.app_video_fullscreen).invisible();
            $.id(R.id.center_puase).gone();
            isShowing = false;
            onControlPanelVisibilityChangeListener.change(false);
        }
    }
    /**
     * @param timeout
     */
    public void show(int timeout) {
        if (!isShowing) {
            $.id(R.id.app_video_top_box).visible();

            //判断loading 是否正在显示
            int v=$.id(R.id.loadingLayout).isVisible();
            if(v==View.GONE&&IS_FULL_SCREEN) {//是全屏状态才显示
                $.id(R.id.center_puase).visible();
            }else{
                $.id(R.id.center_puase).gone();
            }
            if (!isLive) {
                showBottomControl(true);
            }
            if (!fullScreenOnly) {
//                $.id(R.id.app_video_fullscreen).visible();
            }
            isShowing = true;
            onControlPanelVisibilityChangeListener.change(true);
        }
//        updatePausePlay();
        handler.sendEmptyMessage(MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(MESSAGE_FADE_OUT);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(MESSAGE_FADE_OUT), timeout);
        }
    }
    boolean isLoading=true;
    //初始化界面
    public PlayerView(Activity activity1,String shortId){
        this.activity=activity1;
        //查询类
        $=new Query(this.activity);
        joinTime=System.currentTimeMillis();
        MainApplicationContext.SHOWING=false;

//        HttpUtils.getInstance().setHttpCall(new PlayerViewErrorListener(iPlayerViewListener));
        try {
            setFull(false);
            $.id(R.id.full_icon).visibility(View.GONE);

            eggImg=activity.findViewById(R.id.eggImg);
            smasher = new ParticleSmasher(activity);
            eggImg.setVisibility(View.GONE);
            eggImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 默认为爆炸动画
                    smasher.with(eggImg).addAnimatorListener(new SmashAnimator.OnAnimatorListener() {
                        @Override
                        public void onAnimatorStart() {
                            super.onAnimatorStart();
                        }
                        @Override
                        public void onAnimatorEnd() {
                            super.onAnimatorEnd();
                            //smasher.reShowView(img);
                            //发送获取请求
                            eggImg.setVisibility(View.GONE);
                            eggImg.setFocusableInTouchMode(false);
                            sendGetEgg();
                        }
                    }).start();
                }
            });

            //初始化播放器信息
            videoView = (DolitVideoView) activity.findViewById(R.id.video_view);
            videoView.setOnRotationListener(PlayerView.this);
            videoView.setOnInfoListener(new PlayerViewInfoListener(iPlayerViewListener));
            videoView.setOnErrorListener(new PlayerViewErrorListener(iPlayerViewListener));
            videoView.setOnBufferingUpdateListener(new DolitBaseMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(Object mp, int percent) {
                    if(MainApplicationContext.isRequest==false){
                        Log.e("TAG","当天没有蛋了。。");
                        //移除监听
                        videoView.setOnBufferingUpdateListener(null);
                        eggImg.setVisibility(View.GONE);
                        return;
                    }
                    if(isLoading||videoView.isPlaying()==false){//当前加载中,不计数
                        return;
                    }
                    //当前滑没有显示且是播放状态才计时,如果是已经显示了就算了
                    if(MainApplicationContext.EGG_KEY!=null&&!MainApplicationContext.SHOWING){
                        MainApplicationContext.SHOWING=true;
                        showEggResult();
                    }else if(!MainApplicationContext.SHOWING) {
                        MainApplicationContext.EGG_TIME++;
                        Log.e("TAG", "P=" + percent+" T="+MainApplicationContext.EGG_TIME);
                        if(MainApplicationContext.EGG_TIME%eggTime==0){
                            Log.e("TAG","发送请求");
                            showEgg();
                        }
                    }
                }
            });

            seekBar = activity.findViewById(R.id.seekBar);
            seekBar.setMax(1000);
            seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            isShowing = true;
            hide(false);

            $.id(R.id.app_video_top_box).visible();//Egg1553344798972
            setLayout = activity.findViewById(R.id.setLayout);

            adapter = new SelectAdapter(activity);// CommonRecyclerAdapter(activity,manager,baseModels);
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<SelectModel>() {
                @Override
                public void onItemClick(SelectModel item, int position) {
                    onSelectSetting(item, position);
                }
            });
            //更新显示
            updateSetting();

            boolean isMediaEnable = VideoCheckUtil.getMediaCodecEnabled();
            LogUtils.showLog("是否开启硬件解码:" + isMediaEnable);
            videoView.setIsHardDecode(false);
            videoView.setMediaCodecEnabled(isMediaEnable);
            videoView.requestFocus();

            screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;

            audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
            mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

            final GestureDetector gestureDetector = new GestureDetector(activity, new PlayerGestureListener());

//        videoView.setClickable(true);
//        videoView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (gestureDetector.onTouchEvent(motionEvent))
//                    return true;
//                // 处理手势结束
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_UP:
//                        endGesture();
//                        break;
//                }
//                return false;
//            }
//        });
            View liveBox = activity.findViewById(R.id.app_video_box);
            liveBox.setClickable(true);
            liveBox.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (gestureDetector.onTouchEvent(motionEvent))
                        return true;
                    // 处理手势结束
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            endGesture();
                            break;
                    }
                    return false;
                }
            });
            //重力感应旋转监听
            orientationEventListener = new OrientationEventListener(activity) {
                @Override
                public void onOrientationChanged(int orientation) {
//                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
//                    //竖屏
//                    if (portrait) {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
//                    if (!portrait) {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//                        orientationEventListener.disable();
//                    }
//                }
                }
            };
            portrait = getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            initHeight = activity.findViewById(R.id.app_video_box).getLayoutParams().height;
            onClick();
        }catch (Exception ex){}
    }

    ParticleSmasher smasher;
    private void showEgg(){
        if(MainApplicationContext.EGG_KEY!=null){//有了
            return;
        }
        MainApplicationContext.EGG_KEY=null;
        ApiFactory.getInstance().<EggBean>checkEgg(new TCallback<EggBean>() {
            @Override
            public void onNext(EggBean s) {
                Log.e("TAG",s.toString());
                EggBean.EggDataBean dataBean=s.getData();
                if(dataBean==null){
                    return;
                }
                if(!dataBean.getCode().isEmpty()&&dataBean.getCode().equals("001")){
                    MainApplicationContext.isRequest=false;
                    return;
                }
                if(dataBean.getCode().isEmpty()||dataBean.getPath().isEmpty()){
                    return;
                }
                MainApplicationContext.EGG_KEY=dataBean;
                MainApplicationContext.SHOWING=true;

                showEggResult();
            }
            @Override
            public void onError(Response<EggBean> response) {
                super.onError(response);
            }
        });
    }

    private void showEggResult(){
//        eggLinnearLayout.setVisibility(View.VISIBLE);
        smasher.reShowView(eggImg);
        eggImg.setVisibility(View.VISIBLE);
        eggImg.setFocusableInTouchMode(true);

        eggImg.load(MainApplicationContext.EGG_KEY.getPath());

        float scaleSmall=1.5f;
        float scaleLarge=2.0f;
        float shakeDegrees=5.0f;

        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(eggImg.SCALE_X,
                Keyframe.ofFloat(0f, scaleSmall),
                Keyframe.ofFloat(0.15f, scaleSmall),
                Keyframe.ofFloat(0.3f, scaleLarge),
                Keyframe.ofFloat(0.45f, scaleLarge),
                Keyframe.ofFloat(0.6f, scaleSmall)
        );
        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(eggImg.SCALE_Y,
                Keyframe.ofFloat(0f, scaleSmall),
                Keyframe.ofFloat(0.15f, scaleSmall),
                Keyframe.ofFloat(0.3f, scaleLarge),
                Keyframe.ofFloat(0.45f, scaleLarge),
                Keyframe.ofFloat(0.6f, scaleSmall)
        );

        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(eggImg.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );

        final ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(eggImg, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder);
        objectAnimator.setDuration(3000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    private void sendGetEgg(){
        if(MainApplicationContext.EGG_KEY==null){
            return;
        }
        ApiFactory.getInstance().<String>getEgg(MainApplicationContext.EGG_KEY.getCode(), new TCallback<String>() {
            @Override
            public void onNext(String s) {
                Log.e("TAG",s);
                //清空
                MainApplicationContext.EGG_KEY=null;
                MainApplicationContext.SHOWING=false;
                try {
                    LikeBean likeBean = new Gson().fromJson(s, LikeBean.class);
                    MainApplicationContext.showips(likeBean.getData(), null, TipsEnum.TO_TIPS);
                }catch (Exception ex){}
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                //请求出错，继续显示蛋
                smasher.reShowView(eggImg);
            }
        });
    }


    //倍速
    List<SelectModel> speeds= Arrays.asList(
            new SelectModel(0, "0.8X", 0, 0, 0.8f,0),
            new SelectModel(1, "1.0X", 0, 0, 1.0f,0),
            new SelectModel(2, "1.25X", 0, 0, 1.25f,0),
            new SelectModel(3, "1.5X", 0, 0, 1.5f,0),
            new SelectModel(4, "2.0X", 0, 0, 2.0f,0)
    );
    List<SelectModel> lines=new ArrayList<>();//线路
    List<SelectModel> qualtys=new ArrayList<>();//清晰度

    private int currentLinePos=-1;//当前线路
    private int currentQualtyPos=0;//当前清晰度
    private int currentSpeedPos=1;//当前倍速

    //设置当前的清晰度下标
    public void setQualty(int pos){
        this.currentQualtyPos=pos;
    }

    public void setLines(List<SelectModel> lines) {
        this.lines = lines;
    }
    public void setQualtys(List<SelectModel> qualtys) {
        this.qualtys = qualtys;
    }
    //选择了
    private void onSelectSetting(SelectModel model,int position){
        try {
            if (model.getvType() == 0) {//倍速
                if (currentSpeedPos == position) {
                    return;
                }
                currentSpeedPos = position;
                for (int i = 0; i < speeds.size(); i++) {
                    speeds.get(i).setPosition(0);
                }
                float v = (float) model.getValue();
                if (videoView != null) {
                    videoView.setSpeed(v);
                }
                $.id(R.id.txt_doubleSpeed).text(model.getName());
            } else if (model.getvType() == 1) {//切换清晰度
                if (currentQualtyPos == position) {
                    return;
                }
                currentQualtyPos = position;
                for (int i = 0; i < qualtys.size(); i++) {
                    qualtys.get(i).setPosition(0);
                }
                if (playerListener != null) {
                    int pos = videoView.getCurrentPosition();
                    playerListener.onStartPlayChangeQualty(getLine(), currentQualtyPos, pos);
                }
            } else if (model.getvType() == 2) {//切换线路
                if (currentLinePos == position) {
                    return;
                }
                currentLinePos = position;
                for (int i = 0; i < lines.size(); i++) {
                    lines.get(i).setPosition(0);
                }
                SelectModel lineMod = getLine();
                if (lineMod == null) {
                    return;
                }
                if (playerListener != null) {
                    currentPos = videoView.getCurrentPosition();
                    playerListener.onStartPlay(lineMod, (int) currentPos, currentQualtyPos);
                }
            }
            setLayout.setVisibility(View.GONE);
        }catch (Exception ex){}
    }
    //设置
    private void touchSetting(){
        try {
            $.id(R.id.app_video_top_box).gone();
            $.id(R.id.controllerLayout).gone();
            if (type == 0) {
                speeds.get(currentSpeedPos).setPosition(1);
                adapter.setData(speeds);
            } else if (type == 1) {
                if (qualtys != null && qualtys.size() > currentQualtyPos) {
                    qualtys.get(currentQualtyPos).setPosition(1);
                }
                adapter.setData(qualtys);
            } else if (type == 2) {
                if (lines != null && lines.size() > currentLinePos && currentLinePos >= 0) {
                    lines.get(currentLinePos).setPosition(1);
                }
                adapter.setData(lines);
            }
            setLayout.setVisibility(View.VISIBLE);
        }catch (Exception ex){}
    }
    //更新设置
    public void updateSetting(){
        try {
            SelectModel speedMod = speeds.get(currentSpeedPos);
            if (speedMod != null) {
                $.id(R.id.txt_speed).text(speedMod.getName());
            }
            if (lines != null && lines.size() > currentLinePos && currentLinePos >= 0) {
                SelectModel lineMod = lines.get(currentLinePos);
                if (lineMod != null) {
                    $.id(R.id.txt_line).text(lineMod.getName());
                }
            }
            if (qualtys != null && qualtys.size() > currentQualtyPos) {
                SelectModel qualtyMod = qualtys.get(currentQualtyPos);
                if (qualtyMod != null) {
                    $.id(R.id.txt_qualty).text(qualtyMod.getName());
                }
            }
        }catch (Exception ex){}
    }
    //得到当前线路
    private SelectModel getLine(){
        if(lines!=null&&lines.size()>currentLinePos&&currentPos>=0){
            SelectModel selectModel=lines.get(currentLinePos);
            return selectModel;
        }
        return null;
    }
    //切换线路
    public void changeLine(){
        currentLinePos++;
        try {
            SelectModel selectModel = getLine();
            if (selectModel == null) {//没有线路可以切换了,初始化
//                Toast.makeText(activity, "获取资源失败", Toast.LENGTH_SHORT).show();
//                adapter.setData(lines);
//                setLayout.setVisibility(View.VISIBLE);
                playerReset();
                return;
            }
            for(int i=0;i<lines.size();i++){
                lines.get(i).setPosition(0);
            }
            if (playerListener != null) {
                playerListener.onStartPlay(selectModel);
            }
        }catch (Exception ex){}
    }

    //点击
    private void onClick(){
        $.id(R.id.playButton).clicked(onClickListener);
        $.id(R.id.backLeft).clicked(onClickListener);

        $.id(R.id.full_icon).clicked(onClickListener);
        $.id(R.id.puaseButton).clicked(onClickListener);
        $.id(R.id.center_puase).clicked(onClickListener);

        $.id(R.id.txt_line).clicked(onClickListener);

        $.id(R.id.txt_doubleSpeed).clicked(onClickListener);
        $.id(R.id.txt_line).clicked(onClickListener);
        $.id(R.id.txt_qualty).clicked(onClickListener);
    }
    //更新播放,暂停按钮
    private void updatePuaseButton(boolean visible){
        try {
            if (!visible) {
                $.id(R.id.puaseButton).image(R.drawable.bofangxia);
                $.id(R.id.center_puase).image(R.drawable.bofang);
            } else {
                $.id(R.id.puaseButton).image(R.drawable.zantingxia);
                $.id(R.id.center_puase).image(R.drawable.zanting);
            }
        }catch (Exception ex){}
    }

    //控制条显示隐藏接口监听
    private OnControlPanelVisibilityChangeListener onControlPanelVisibilityChangeListener=new OnControlPanelVisibilityChangeListener() {
        @Override
        public void change(boolean isShowing) {
        }
    };
    private DolitBaseMediaPlayer.OnCompletionListener onCompletionListener;
    public void setOnComplete(DolitBaseMediaPlayer.OnCompletionListener onCompletionListener1){
        this.onCompletionListener=onCompletionListener1;
    }

    //显示标题,封面...
    public void show(){
        $.id(R.id.app_video_top_box).visible();
        $.id(R.id.frontCover).visible();
    }
    public void hide(){
        $.id(R.id.app_video_top_box).gone();
    }
    //开始播放
    public PlayerView startPlay(String url){
        modelUrl=url;
        streamId="";
        Log.e("TAG","开始播放呢...");
        if(playSpeedModel==null&&currentLinePos>=0){
            playSpeedModel=new PlaySpeedModel();
        }
        isStartPlay = true;
        IS_OPTION=false;
        try {
//        if(videoView!=null&&videoView.isPlaying()){//正在播放的话先停止
//            //先停止了
//            videoView.stopPlayback();
//        }
            //videoView.setVisibility(View.GONE);
            $.id(R.id.txt_speed).text("加载中,请稍后...");
            $.id(R.id.loadingLayout).visible();
            $.id(R.id.frontCover).gone();
            $.id(R.id.setLayout).gone();
            $.id(R.id.app_video_top_box).gone();
            if (videoView != null) {
                videoView.setOnCompletionListener(onCompletionListener);
                videoView.setVideoPath(url);
                videoView.start();
            }
            startGetSpeed();

        }catch (Exception ex){}
        return this;
    }
    public PlayerView startPlay(String url,int pos){
        startPlay(url);
        if(pos>0) {
            videoView.seekTo(pos);
        }
        return this;
    }

    public void setTorSpeed(String sp){
        $.id(R.id.frontCover).gone();
        $.id(R.id.loadingLayout).visible();
        $.id(R.id.txt_speed).text(sp);
    }
    //设置封面背景
    public PlayerView setBackground(String bgUrl){
        $.id(R.id.backgroundIcon).image(bgUrl);
        return this;
    }
    //重置/还原初始化
    public PlayerView playerReset(){
        try {
            //显示封面
            $.id(R.id.loadingLayout).gone();
            $.id(R.id.frontCover).visible();
            $.id(R.id.app_video_top_box).visible();
            currentLinePos = 0;
            currentSpeedPos = 1;
            currentQualtyPos = 0;
            stopGetSpeed();
            if (videoView != null) {
                videoView.pause();
                videoView.stopPlayback();
            }
        }catch (Exception ex){}
        return this;
    }

    //设置标题
    public PlayerView setTitle(String title){
        $.id(R.id.titleLabel).text(title);
        return this;
    }

    //得到屏幕方向
    private int getScreenOrientation() {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }
        return orientation;
    }
    //控制条显示/隐藏
    private void showBottomControl(boolean show) {
        $.id(R.id.controllerLayout).visibility(show ? View.VISIBLE : View.GONE);
        $.id(R.id.setLayout).gone();
    }

    public void onConfigurationChanged(final Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);
    }

    private void doOnConfigurationChanged(final boolean portrait) {
        if (videoView != null && !fullScreenOnly) {
            tryFullScreen(!portrait);
            if (portrait) {//坚屏
                $.id(R.id.app_video_box).height(initHeight, false);
            } else {//横屏
                int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                $.id(R.id.app_video_box).height(Math.min(heightPixels,widthPixels), false);
            }
            if(orientationEventListener!=null) {
                orientationEventListener.enable();
            }
        }
    }
    //横竖屏切换
    public void toggleFullScreen(){
        if(activity!=null) {
            if (getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }
    private void tryFullScreen(boolean fullScreen) {
        if(activity!=null) {
            if (activity instanceof AppCompatActivity) {
                ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
                if (supportActionBar != null) {
                    if (fullScreen) {
                        supportActionBar.hide();
                    } else {
                        supportActionBar.hide();//show();
                    }
                }
            }
        }
        setFullScreen(fullScreen);
    }
    //设置全屏或竖屏
    private void setFullScreen(boolean fullScreen) {
        try {
            if (activity != null) {
                setFull(fullScreen);
//            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                if (fullScreen) {
//                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    Rect rect = new Rect();
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                    double height = rect.bottom;// - rect.top;
                    double width = rect.right;// - rect.left;
                    videoView.changeSize(width, height);
                } else {
//                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                    Rect rect = new Rect();
                    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                    double height = rect.bottom;// - rect.top;
                    double width = rect.right;// - rect.left;
                    videoView.changeSize(width, initHeight);
                }
            }
        }catch (Exception ex){}
    }
    private void onlyFullScreen(){
        if(activity!=null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    //设置总时间
    public void setDuration(long duration){
        if(duration<=0){
            return;
        }
        String durationStr=generateTime(duration);
        //TODO  ...
        $.id(R.id.txt_total_time).text("/"+durationStr);
    }

    //返回
    public boolean onBackPressed() {
        if (IS_FULL_SCREEN) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    //查询类
    class Query {
        private final Activity activity;
        private View view;

        public Query(Activity activity) {
            this.activity=activity;
        }

        public Query id(int id) {
            view = activity.findViewById(id);
            return this;
        }

        public Query image(int resId) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }
        public Query image(String url){
            if(view==null){
                return this;
            }
            if(view instanceof  ImageView){
                ImageView img=((ImageView) view);
                CacheFactory.getInstance().getImage(null,img,url);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }
        //得到是否显示
        public int isVisible(){
            if(view!=null){
                return  view.getVisibility();
            }
            return View.GONE;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(View.OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view!=null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }
        private void size(boolean width, int n, boolean dip){
            try {
                if (view != null) {
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    if (n > 0 && dip) {
                        n = dip2pixel(activity, n);
                    }
                    if (width) {
                        lp.width = n;
                    } else {
                        lp.height = n;
                    }
                    view.setLayoutParams(lp);
                }
            }catch (Exception ex){}
        }

        public void height(int height, boolean dip) {
            size(false,height,dip);
        }

        public int dip2pixel(Context context, float n){
            int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
            return value;
        }
    }
    //手势监听
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean firstTouch;
        private boolean volumeControl;
        private boolean toSeek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//            videoView.toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if(!IS_OPTION){
                return super.onDown(e);
            }
            try {
                firstTouch = true;
                long position = videoView.getCurrentPosition();
                newPosition = position;
                oldX = e.getX();
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                oldY=e.getY();
            }catch (Exception ex){}
            return super.onDown(e);
        }

        private float oldX=0;
        private float oldY=0;
        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(!IS_OPTION){
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
            try {
                float mOldX = e1.getX(), mOldY = e1.getY();
                float deltaY = mOldY - e2.getY();
                float deltaX = mOldX - e2.getX();
                if (firstTouch) {
                    toSeek = Math.abs(distanceX) >= Math.abs(distanceY);
                    volumeControl = mOldX > screenWidthPixels * 0.5f;
                    firstTouch = false;
                }

                if (toSeek) {
                    if (!isLive) {
                        boolean toBoo = e2.getX() > oldX;
                        Log.e("E2X", "E2X=" + e2.getX());
//                    onProgressSlide(deltaX / videoView.getWidth());
                        onProgressSlide(toBoo ? 1 : -1);
                        oldX = e2.getX();
                    }
                } else {
                    newPosition = -1;
                    float percent = deltaY / videoView.getHeight();
                    boolean toY=e2.getY()>oldY;
                    Log.e("TAG","Y="+toY);
                    if (volumeControl) {
                        onVolumeSlide(toY);
                    } else {
                        onBrightnessSlide(percent);
                    }
                    oldY=e2.getY();
                }
            }catch (Exception ex){}
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(!isStartPlay){
                return true;
            }
            try {
                //TODO... 封面显示的时候点击不显示控制条
                int v = $.id(R.id.frontCover).isVisible();
                if (v == View.VISIBLE) {
                    return true;
                }
                if (isShowing) {
                    hide(false);
                } else {
                    show(defaultTimeout);
                }
            }catch (Exception ex){}
            return true;
        }
    }

    private double currentVolume;

    //滑动改变声音大小  根据高度来计算
    private void onVolumeSlide(float percent) {
        try {
            if (volume == -1) {
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (volume < 0)
                    volume = 0;
            }
            int index = (int) (percent * mMaxVolume) + volume;
            if (index > mMaxVolume)
                index = mMaxVolume;
            else if (index < 0)
                index = 0;

            // 变更声音
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);//ADJUST_RAISE
//            audioManager.adjustStreamVolume(AudioManager.FLAG_PLAY_SOUND, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);

            // 变更进度条
            int i = (int) (index * 1.0 / mMaxVolume * 100);
            String s = i + "%";
            if (i == 0) {
                s = "off";
            }
            //Log.e("onVideoSizeChanged", s);
            // 显示
            $.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
            $.id(R.id.app_video_brightness_box).gone();
            $.id(R.id.app_video_volume_box).visible();
            $.id(R.id.app_video_volume_box).visible();
            $.id(R.id.app_video_volume).text(s).visible();
        }catch (Exception ex){}
    }
    //获取当前的音量来每次加0.1
    private void onVolumeSlide(boolean boo) {
        try {
            $.id(R.id.center_puase).gone();
            if(boo){//减小
                currentVolume-=0.1;
            }else{//增加
                currentVolume+=0.1;
            }
            if(currentVolume<=0){
                currentVolume=0;
            }
            if(currentVolume>=mMaxVolume){
                currentVolume=mMaxVolume;
            }
            double v= (int) (currentVolume*1.0/mMaxVolume*100);
            // 变更声音
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) currentVolume, 0);

            // 变更进度条
            int i = (int) v;
            String s = i + "%";
            if (i == 0) {
                s = "off";
            }
            //Log.e("onVideoSizeChanged", s);
            // 显示
            $.id(R.id.app_video_volume_icon).image(i == 0 ? R.drawable.ic_volume_off_white_36dp : R.drawable.ic_volume_up_white_36dp);
            $.id(R.id.app_video_brightness_box).gone();
            $.id(R.id.app_video_volume_box).visible();
            $.id(R.id.app_video_volume_box).visible();
            $.id(R.id.app_video_volume).text(s).visible();
        }catch (Exception ex){}
    }
    //滑动进度
    private void onProgressSlide(float percent) {
        try {
            if (videoView == null) {
                return;
            }
            $.id(R.id.center_puase).gone();
            long duration = videoView.getDuration();
            if (percent < 0) {//快退
                newPosition -= 1000;
                $.id(R.id.app_video_fastForward).image(R.drawable.fr);
            } else {//快进
                newPosition += 1000;
                $.id(R.id.app_video_fastForward).image(R.drawable.ff);
            }
            if (newPosition <= 0) {
                newPosition = 0;
            }
            if (newPosition >= duration) {
                newPosition = duration;
            }
            int showDelta = (int) (newPosition / 1000);//(int) delta / 1000;
            //Log.e("TAG", "滑动进度::" + showDelta + "  POS:" + newPosition);
            if (showDelta >= 0) {
                $.id(R.id.app_video_fastForward_box).visible();
                $.id(R.id.app_video_fastForward_target).text(generateTime(newPosition) + "/");
                $.id(R.id.app_video_fastForward_all).text(generateTime(duration));
            }
        }catch (Exception ex){}
    }
    //得到时间
    private String generateTime(long time) {
        try {
            int totalSeconds = (int) (time / 1000);
            int seconds = totalSeconds % 60;
            int minutes = (totalSeconds / 60) % 60;
            int hours = totalSeconds / 3600;
            return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
        }catch ( Exception ex){}
        return "";
    }
    //滑动改变亮度 根据高度来计算
    private void onBrightnessSlide(float percent) {
        try {
            $.id(R.id.center_puase).gone();
            if (brightness < 0) {
                brightness = activity.getWindow().getAttributes().screenBrightness;
                if (brightness <= 0.00f) {
                    brightness = 0.50f;
                } else if (brightness < 0.01f) {
                    brightness = 0.01f;
                }
            }
            Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
            $.id(R.id.app_video_brightness_box).visible();
            WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
            lpa.screenBrightness = brightness + percent;
            if (lpa.screenBrightness > 1.0f) {
                lpa.screenBrightness = 1.0f;
            } else if (lpa.screenBrightness < 0.01f) {
                lpa.screenBrightness = 0.01f;
            }
            $.id(R.id.app_video_brightness).text(((int) (lpa.screenBrightness * 100)) + "%");
            activity.getWindow().setAttributes(lpa);
        }catch (Exception ex){}

    }
    /**
     * 手势结束
     */
    private void endGesture() {
        try {
            if (!IS_OPTION) {
                return;
            }
            volume = -1;
            brightness = -1f;
            if (newPosition >= 0) {
                handler.removeMessages(MESSAGE_SEEK_NEW_POSITION);
                handler.sendEmptyMessage(MESSAGE_SEEK_NEW_POSITION);
            }
            handler.removeMessages(MESSAGE_HIDE_CENTER_BOX);
            handler.sendEmptyMessageDelayed(MESSAGE_HIDE_CENTER_BOX, 500);
        }catch (Exception ex){}
    }
    //暂停播放
    public PlayerView onPause() {
        try {
            stopGetSpeed();
            if (videoView != null) {
                currentPos = videoView.getCurrentPosition();
                videoView.pause();
            }
        }catch (Exception ex){}
        return this;
    }
    //重新开始播放
    public PlayerView onResume() {
        try {
            if (videoView != null && currentPos > 0) {
                videoView.resume();
                videoView.seekTo(currentPos);
                //startGetSpeed();
            }
        }catch (Exception ex){}
        return this;
    }
    //销毁
    public void onDestroy() {
        try {
            if (videoView != null) {
                videoView.stopPlayback();
            }
            if (orientationEventListener != null) {
                orientationEventListener.disable();
            }
            if (playerListener != null) {
                playerListener.onExit(currentPos);
            }
            handler.removeCallbacksAndMessages(null);
        }catch (Exception ex){}
    }
}
