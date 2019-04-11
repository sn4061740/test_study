package android.media.ViviTV.player.widget;

import android.content.Context;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;


/**
 * 播放器的视频视图，这个是处理显示用的核心类
 * 支持联播、设置http的头等信息
 * TODO：客户反馈有视频会播放一段时间后退出（m3u8，需要测试）
 */


public class SoftMediaPlayer extends DolitBaseMediaPlayer {
    private IMediaPlayer mMediaPlayer = null;
    private OnInfoListener mThisOnInfoListener = null;
    private OnCompletionListener mThisOnCompletionListener = null;
    private OnPreparedListener mThisOnPreparedListener = null;
    private OnBufferingUpdateListener mThisOnBufferingUpdateListener = null;
    private OnSeekCompleteListener mThisOnSeekCompleteListener = null;
    private OnErrorListener mThisOnErrorListener = null;
    private OnVideoSizeChangedListener mThisOnVideoSizeChangedListener = null;
    private boolean mediaCodecEnabled = false;

    public static final String TAG = SoftMediaPlayer.class.getName();

    //
    public static final String strPlayEngineKey1 = "3Rg34NqyW7OYlqMX";//"McsCtjV1VEushEGv";
    public static final String strPlayEngineKey2 = "ONPpCWZFlRnXSKbU";//"cy6CkbsG82KM03tc";

    public SoftMediaPlayer(boolean enableMediaCodec) {
        mMediaPlayer = new IjkMediaPlayer();
        mediaCodecEnabled = enableMediaCodec;
    }
    public void setSpeed(float speed){
        ((IjkMediaPlayer)mMediaPlayer).setSpeed(speed);
    }
    public long getSpeed(){
        try {
            return ((IjkMediaPlayer) mMediaPlayer).getTcpSpeed();
        }catch (Exception ex){}
        return 0;
    }
    //设置循环播放
    public void setLooping(boolean boo){
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(boo);
        }
    }
    @Override
    public void setDisplay(SurfaceHolder sh) {
        if (this.mMediaPlayer != null)
            mMediaPlayer.setDisplay(sh);
    }

    @Override
    public void setDataSource(Context context, String path, Map<String, String> headers) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException {
        this.mContext = context;
        if (this.mMediaPlayer != null) {
            mDataSource = path;
            mMediaPlayer.setDataSource(mDataSource);
        }
    }

    @Override
    public String getDataSource() {
        return mDataSource;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.start();

    }

    @Override
    public void stop() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.stop();

    }

    @Override
    public void pause() throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.pause();

    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setScreenOnWhilePlaying(screenOn);

    }

    @Override
    public int getVideoWidth() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getVideoWidth();
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getVideoHeight();
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.isPlaying();
        return false;
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.seekTo((int) msec);

    }

    @Override
    public long getCurrentPosition() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getCurrentPosition();
        return 0;
    }

    @Override
    public long getDuration() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer.getDuration();
        return 0;
    }

    @Override
    public void release() {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.release();

    }

    @Override
    public void reset() {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.getVideoHeight();

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setVolume(leftVolume, rightVolume);

    }

    @Override
    public Object getRealMediaPlayer() {
        if (this.mMediaPlayer != null)
            return this.mMediaPlayer;
        return null;
    }

    @Override
    public void setAudioStreamType(int streamtype) {
        if (this.mMediaPlayer == null)
            this.mMediaPlayer.setAudioStreamType(streamtype);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setWakeMode(Context context, int mode) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setWakeMode(context, mode);
    }

    @Override
    public void setSurface(Surface surface) {
        if (this.mMediaPlayer != null)
            this.mMediaPlayer.setSurface(surface);
    }


    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.mThisOnPreparedListener = listener;
        this.mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.mThisOnCompletionListener = listener;
        this.mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        this.mThisOnBufferingUpdateListener = listener;
        this.mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        this.mThisOnSeekCompleteListener = listener;
        this.mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        this.mThisOnVideoSizeChangedListener = listener;
        this.mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        this.mThisOnErrorListener = listener;
        this.mMediaPlayer.setOnErrorListener(mOnErrorListener);
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        this.mThisOnInfoListener = listener;
        this.mMediaPlayer.setOnInfoListener(mOnInfoListener);

    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                       int sar_num, int sar_den) {

            if (mThisOnVideoSizeChangedListener != null)
                mThisOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height, mp.getVideoWidth(), mp.getVideoHeight(), sar_num, sar_den);

        }
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (mThisOnInfoListener != null) {
                if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    what = DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START;
                } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    what = DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END;
                }
                return mThisOnInfoListener.onInfo(mp, what, extra);
            }
            return false;
        }

    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            if (mThisOnCompletionListener != null) {
                mThisOnCompletionListener.onCompletion(mp);
            }
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (mThisOnErrorListener != null) {
                return mThisOnErrorListener.onError(mp, what, extra, mp.getCurrentPosition());
            }
            return false;
        }
        @Override
        public boolean onMediaError(int what) {
            if (mThisOnErrorListener != null) {
                return mThisOnErrorListener.onMediaError(what);
            }
            return false;
        }
    };

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (mThisOnPreparedListener != null) {
                mThisOnPreparedListener.onPrepared(mp, mp.getVideoWidth(), mp.getVideoHeight());
            }
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (mThisOnBufferingUpdateListener != null) {
                mThisOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (mThisOnSeekCompleteListener != null) {
                mThisOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    @Override
    public boolean init(Context context) {
        IjkMediaPlayer dlMediaPlayer = (IjkMediaPlayer) mMediaPlayer;
        int ret = dlMediaPlayer.Init(strPlayEngineKey1, strPlayEngineKey2, context);
        if (ret != 0) {
            return false;
        }
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        //设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);//48  0
        //跳帧处理,放CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 12);//12  5
        //设置硬解码方式
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", mediaCodecEnabled ? 1 : 0);
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"mediacodec-auto-rotate", mediaCodecEnabled ? 1 : 0);
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", mediaCodecEnabled ? 1 : 0);
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_mpeg4", 1);
        //播放一段后断开后重连  播放重连次数
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        //设置播放前的探测时间 1,达到首屏秒开效果
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzeduration",1);
//        设置播放前的最大探测时间
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"analyzemaxduration",100L);
        //播放前的探测Size，默认是1M, 改小一点会出画面更快
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"probesize",1024*1024*2);

//
        //设置0，就是变速不变调的状态，设置1，就为变速变调的状态。
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
//        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        dlMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fldv_key", "eljpzorkbwdevhoumdabllwbfdbwnbvq");//pJYOv16QtCYfMlzm
        return true;
    }

    @Override
    public void setUserAgent(String strUserAgent) {
        if (strUserAgent != null) {
            ((IjkMediaPlayer) mMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", strUserAgent);
        }
    }
}
