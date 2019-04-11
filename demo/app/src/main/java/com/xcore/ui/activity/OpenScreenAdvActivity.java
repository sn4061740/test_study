package com.xcore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer;
import android.media.ViviTV.player.widget.DolitVideoView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xcore.MainApplicationContext;
import com.xcore.R;
import com.xcore.base.BaseActivity;
import com.xcore.data.bean.AdvBean;
import com.xcore.ext.ImageViewExt;
import com.xcore.ui.IADVListener;
import com.xcore.ui.LoginController;
import com.xcore.utils.AdvUtils;
import com.xcore.utils.JumpUtils;
import com.xcore.utils.LogUtils;
import com.xcore.utils.MediaCodecUtil;
import com.xcore.utils.NumberUtils;

import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class OpenScreenAdvActivity extends BaseActivity {

    private static AdvBean advBean;
    private Timer timer;
    static String lock_type;
    //把广告的模型传进来
    public static void toActivity(Context context,AdvBean advBean1,String lockType){
        advBean=advBean1;
        advBean.setType(-99);
        lock_type=lockType;
//        String[] vRes=new String[]{
//                "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f040000bgc5jqo2saj1v4h7cmk0&line=0",
//                "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200f310000bg9k0lakr6gf3rdd4mf0&line=0",
//                "https://aweme.snssdk.com/aweme/v1/playwm/?video_id=v0200faf0000bg5qk0nl54dcmihn43og&line=0"
//        };
//        int n=NumberUtils.getRandom(vRes.length);
//        advBean.setImagePath("https://www.appsapk.com/wp-content/uploads/2013/08/720x1280-Wallpaper_AppsApk.com_-14.jpg");
//        advBean.setVideoPath(vRes[n]);
//        advBean.setJump(8);
//        advBean.setTime(5);
//        advBean.setShortId("-99");
//        advBean.setToUrl("https://bcdn.bvgqp.com/1545489139412.apk");
//        advBean.setContent("广告测试测试");

        tTime=advBean.getTime();

        Intent intent=new Intent(context,OpenScreenAdvActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_open_screen_adv;
    }
    TextView timeTxt;
    static int tTime=10;
    DolitVideoView player;
    ImageViewExt imageViewExt;
    AudioManager audioManager;
    ImageView volumeImage;
    boolean volumeBoo=false;
    private LoginController loginController;
    private int currentVolume =0;

    boolean isShow=false;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        loginController=new LoginController(OpenScreenAdvActivity.this);

        timeTxt=findViewById(R.id.txt_time);
        volumeImage=findViewById(R.id.volume_image);
        volumeImage.setVisibility(View.GONE);

        timeTxt.setText(tTime+"");
        imageViewExt= findViewById(R.id.screenImage);
        imageViewExt.setVisibility(View.GONE);
        imageViewExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             JumpUtils.to(OpenScreenAdvActivity.this,advBean);
            }
        });

        timeTxt.setOnClickListener(onClickListener);
        volumeImage.setOnClickListener(onClickListener);

        player= findViewById(R.id.dolitView);
        player.setVisibility(View.GONE);
        findViewById(R.id.play_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            JumpUtils.to(OpenScreenAdvActivity.this,advBean);
            }
        });
        if(TextUtils.isEmpty(advBean.getVideoPath())) {
            AdvUtils.loadImage(advBean.getImagePath(), imageViewExt, iadvListener);
        }
        AdvUtils.loadVideo(advBean.getVideoPath(),iadvListener);
//        showImage();
//        startPlay();
        startTimer();
    }

    IADVListener iadvListener=new IADVListener() {
        @Override
        public void onLoadVideoSuccess(String path) {
            Log.e("TAG","视频加载完成"+path);
            startPlay(path);
        }
        @Override
        public void onLoadImageSuccess(String path) {
            Log.e("TAG","图片加载完成");
        }
    };

    //开始计时
    private void startTimer(){
        if(advBean==null){
            return;
        }
        //开启时间器
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tTime--;
                if(tTime<=0){
                    timer.cancel();
                    timer=null;
                }
                if(timeTxt==null){
                    return;
                }
                timeTxt.post(new Runnable() {
                    @Override
                    public void run() {
                        if(timeTxt==null){
                            return;
                        }
                        if(tTime<=0){
                            timeTxt.setText("X");
                        }else {
                            timeTxt.setText(tTime + "");
                        }
                    }
                });
            }
        },1500,1000);
    }
    //开始播放
    private void startPlay(String path){
        imageViewExt.setVisibility(View.GONE);
        player.setVisibility(View.VISIBLE);
        volumeImage.setVisibility(View.VISIBLE);
        boolean boo=MediaCodecUtil.isSupportMediaCodecHardDecoder();

        player.setMediaCodecEnabled(boo);
        player.setIsHardDecode(false);
        player.setLooping(true);
        player.setOnInfoListener(onInfoListener);
        player.setOnCompletionListener(onCompletionListener);

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeBoo=currentVolume<=0;
        if(volumeBoo){
            currentVolume=2;
        }
        player.setVideoPath(path);
        //getSpeed();
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()){
            case R.id.volume_image:
                updateVolume();
                break;
            case R.id.txt_time:
                if(tTime<=0) {
                    toLogin();
                }
                break;
        }
        }
    };
    private void updateVolume(){
        volumeBoo=!volumeBoo;
        if(volumeBoo) {//静音
            volumeImage.setImageResource(R.drawable.ic_volume_off_white_36dp);
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        }else{
            volumeImage.setImageResource(R.drawable.ic_volume_up_white_36dp);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        }
    }

    //去判断登录
    private void toLogin(){
        stopSpeed();
        loginController.toLogin();
        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                if (player != null) {
                    player.pause();
                    player.stopPlayback();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            }
        }).start();
    }

    DolitBaseMediaPlayer.OnCompletionListener onCompletionListener=new DolitBaseMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(Object mp) {
          player.start();
        }
    };
    DolitBaseMediaPlayer.OnInfoListener onInfoListener=new DolitBaseMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(Object mp, int what, int extra) {
        if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {//开始缓冲
            getSpeed();
            LogUtils.showLog("开始缓冲");
        }else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            LogUtils.showLog("结束缓冲");
            stopSpeed();
        }else if(what== IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){//刚进去的时候缓冲读取信息 开始播放
            LogUtils.showLog("开始播放");
            stopSpeed();
            imageViewExt.setVisibility(View.GONE);
        }
        return false;
        }
    };
    Timer spTimer;
    //获取速度
    private void getSpeed(){
        if(player!=null&&spTimer==null) {
            spTimer = new Timer();
            spTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    long speed = player.getTcpSpeed();
                    LogUtils.showLog("速度:" + speed);
                }
            }, 10, 2000);
        }
    }
    private void stopSpeed(){
        if(spTimer!=null){
            spTimer.cancel();
        }
        spTimer=null;
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isShow&&imageViewExt!=null){
            imageViewExt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        MainApplicationContext.onWindowFocusChanged(hasFocus,OpenScreenAdvActivity.this);
    }

    @Override
    public void onDestroy() {
        loginController=null;
        stopSpeed();
        spTimer=null;
        if(timer!=null){
            timer.cancel();
        }
        timer=null;
        super.onDestroy();
    }
}
