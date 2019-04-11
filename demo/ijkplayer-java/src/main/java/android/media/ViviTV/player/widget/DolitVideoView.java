/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.media.ViviTV.player.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnCompletionListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnErrorListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnInfoListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnPreparedListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnSeekCompleteListener;
import android.media.ViviTV.player.widget.DolitBaseMediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p>
 * VideoView also provide many wrapper methods for
 * {io.vov.vitamio.MediaPlayer}, such as { #getVideoWidth()},
 * {#setSubShown(boolean)}
 */
public class DolitVideoView extends SurfaceView implements IMediaPlayerControl {
    private static final String TAG = DolitVideoView.class.getName();

    private Uri mUri;
    private long mDuration;
    private String mUserAgent;
    private Map<String, String> mHeaders;

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_SUSPEND = 6;
    public static final int STATE_RESUME = 7;
    public static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    //private int mVideoLayout = VIDEO_LAYOUT_ZOOM;//之前软解使用的
    private int m_videoLayoutScale = 0;
    public final static int A_4X3 = 1;
    public final static int A_16X9 = 2;
    public final static int A_RAW = 4; // 原始大小（目前沒有使用）
    public final static int A_DEFALT = 0; // 原始比例

    //原始大小
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    //按视频原始比率拉伸到屏幕的最大，不超过屏幕
    public static final int VIDEO_LAYOUT_SCALE = 1;
    //拉伸到屏幕大小。
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    //占据整个屏幕，并有一部分视频显示不出来。
    //按屏幕宽高比和视频宽高比，宽度使用最大的，高度选用最小的
    public static final int VIDEO_LAYOUT_ZOOM = 3;

    private SurfaceHolder mSurfaceHolder = null;
    private DolitBaseMediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    @SuppressWarnings("unused")
    private int mVideoSarNum;
    @SuppressWarnings("unused")
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private WindowManager mWindowManager;
    private MediaController8 mMediaController;
    private View mBufferingIndicator;
    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private DolitBaseMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private long mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private Context mContext;
    private boolean mIsHardDecode = true;//解码方式。是不是硬解。

    protected String mUser_Mac = "";
    protected String mLiveSeek = "0";
    protected String mLiveEpg = "-";
    protected String mLiveNextEpg = "-";
    protected String mLiveNextUrl = "-";
    protected String mLiveCookie = "";

    protected String mLive_Range = "mediaTV/range";
    protected String mLive_Referer = "mediaTV/user/|support|android-tvbox";
    protected String mLive_key = "";
    private boolean autoPlayAfterSurfaceCreated = true;
    private boolean mediaCodecEnabled = false;
    private boolean speedEnable=false;//是否支持设置倍速

    private DolitBaseMediaPlayer.OnRotationChangeListener onRotationListener;

    public void setOnRotationListener(DolitBaseMediaPlayer.OnRotationChangeListener onRotationListener) {
        this.onRotationListener = onRotationListener;
    }

    public DolitVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public DolitVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DolitVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    public int getmCurrentState() {
        return mCurrentState;
    }

    public void setIsHardDecode(boolean bHardDecode) {
        this.mIsHardDecode = bHardDecode;
    }

    private void initVideoView(Context ctx) {
        mContext = ctx;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoSarNum = 0;
        mVideoSarDen = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity)
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public boolean isValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    public void setRequestWidthHeight(int width, int height) {
        if (mSurfaceHolder == null) {
            return;
        }

        mSurfaceHolder.setFixedSize(width, height);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setUserAgent(String ua) {
        mUserAgent = ua;
    }

    public void setAutoPlayAfterSurfaceCreated(boolean autoPlayAfterSurfaceCreated) {
        this.autoPlayAfterSurfaceCreated = autoPlayAfterSurfaceCreated;
    }

    public void stopPlayback() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
                handler.removeCallbacks(TimeOutError);
            }
        }catch (Exception ex){}
    }

    Handler handler = new Handler(Looper.getMainLooper());

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null)
            return;

        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release(false);

        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            DolitBaseMediaPlayer mediaPlayer = null;
            if (mUri != null) {
                if (mIsHardDecode) {
                    mediaPlayer = new HardMediaPlayer();
                }
                else {
                    mediaPlayer = new SoftMediaPlayer(mediaCodecEnabled);
                    speedEnable = true;
                }
                if (mediaPlayer.init(mContext) == false) {
                    DebugLog.e(TAG, "Init ViviTVMediaPlayer engine failed, please check Key!");
                    return;
                }
                if (mUserAgent != null) {
                    mediaPlayer.setUserAgent(mUserAgent);
                }
            }
            mMediaPlayer = mediaPlayer;
            mMediaPlayer.setLiveKey(mLive_key);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);

            if (mUri != null) {
                mMediaPlayer.setDataSource(mContext, mUri.toString(), mHeaders);
            }
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            handler.postDelayed(TimeOutError, TIMEOUTDEFAULT);
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            DebugLog.e(TAG, "Unable to open content(IOException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            mErrorListener.onOpenError(ex,mUri);
            return;
        } catch (IllegalArgumentException ex) {
            DebugLog.e(TAG, "Unable to open content(IllegalArgumentException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onOpenError(ex,mUri);
//            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            return;
        } catch (IllegalStateException ex) {
            DebugLog.e(TAG, "Unable to open content(IllegalStateException): " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onOpenError(ex,mUri);
//            mErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_ErrorBeforePlay, 0, 0);
            return;
        }catch (Exception ex){
            mErrorListener.onOpenError(ex,mUri);
            return;
        }
    }
    public void setSpeed(float speed){
        if(!speedEnable){
            return;
        }
        if(mMediaPlayer instanceof SoftMediaPlayer){
            ((SoftMediaPlayer)mMediaPlayer).setSpeed(speed);
        }else if(mMediaPlayer instanceof HardMediaPlayer){
            ((HardMediaPlayer) mMediaPlayer).setSpeed(speed);
        }
    }
    public void setLooping(boolean boo){
        if(mMediaPlayer instanceof SoftMediaPlayer){
            ((SoftMediaPlayer)mMediaPlayer).setLooping(boo);
        }else if(mMediaPlayer instanceof HardMediaPlayer){
            ((HardMediaPlayer) mMediaPlayer).setLooping(boo);
        }
    }
    public long getTcpSpeed(){
        try {
            return ((SoftMediaPlayer) mMediaPlayer).getSpeed();
        }catch (Exception ex){}
        return 0;
    }
    public void setMediaController(MediaController8 controller) {
        if (mMediaController != null)
            mMediaController.hide();
        mMediaController =controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View bufferingIndicator) {
        if (this.mBufferingIndicator != null)
            hideBuffingTip();
        this.mBufferingIndicator = bufferingIndicator;
    }
    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
//            mMediaController.setAnchorView(this);
//            mMediaController.setEnabled(isInPlaybackState());
//            if (mUri != null) {
//                List<String> paths = mUri.getPathSegments();
//                String name = paths == null || paths.isEmpty() ? "null" : paths
//                        .get(paths.size() - 1);
//                mMediaController.setFileName(name);
//            }
        }
    }

    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(Object mp, int width, int height, int videoWidth, int videoHeight, int sarNum, int sarDen) {

            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;

            DebugLog.dfmt(TAG, "onVideoSizeChanged: " +width+"*"+ height+"---"+videoWidth+"x"+videoHeight+"----"+sarNum+"-"+sarDen);
            //初始化这里要 用接口返回去在上一层调用
            if(onRotationListener!=null){
                onRotationListener.onRotationChangeListener();
            }
        }
    };

//    private int setRotion(int rotation){
//        int orientation=0;
//        // if the device's natural orientation is portrait:
//        if ((rotation == Surface.ROTATION_0
//                || rotation == Surface.ROTATION_180) ||
//                (rotation == Surface.ROTATION_90
//                        || rotation == Surface.ROTATION_270)) {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//                case Surface.ROTATION_90:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_180:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                    break;
//                default:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//            }
//        }
//        // if the device's natural orientation is landscape or if the device
//        // is square:
//        else {
//            switch (rotation) {
//                case Surface.ROTATION_0:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_90:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                    break;
//                case Surface.ROTATION_180:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                    break;
//                case Surface.ROTATION_270:
//                    orientation =
//                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                    break;
//                default:
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                    break;
//            }
//        }
//        return orientation;
//    }

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(Object mp, int videoWidth, int videoHeight) {
//            DebugLog.d(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;

            mCanPause = mCanSeekBack = mCanSeekForward = true;
            handler.removeCallbacks(TimeOutError);

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer, videoWidth, videoHeight);
//            if (mMediaController != null)
//                mMediaController.setEnabled(true);
            mVideoWidth = videoWidth;
            mVideoHeight = videoHeight;

            long seekToPosition = mSeekWhenPrepared;

            if (seekToPosition != 0)
                seekTo(seekToPosition);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //setVideoLayout(mVideoLayout);
                //selectScales(m_videoLayoutScale);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth
                        && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null)
                            mMediaController.show();
                    } else if (!isPlaying()
                            && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null)
                            mMediaController.show(0);
                    }
                }
            } else if (mTargetState == STATE_PLAYING) {
                start();
            }
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(Object mp) {
            //DebugLog.d("TAG", "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(Object mp, int framework_err, int impl_err, long currentPosition) {
            DebugLog.dfmt(TAG, "Error: %d, %d", framework_err, impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();

            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err, currentPosition);
            }

//            if (getWindowToken() != null) {
//                int message = framework_err == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.vitamio_videoview_error_text_invalid_progressive_playback
//                        : R.string.vitamio_videoview_error_text_unknown;
////
//                new AlertDialog.Builder(mContext)
//                        .setTitle(R.string.vitamio_videoview_error_title)
//                        .setMessage(message)
//                        .setPositiveButton(
//                                R.string.vitamio_videoview_error_button,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                            int whichButton) {
//                                        if (mOnCompletionListener != null)
//                                            mOnCompletionListener
//                                                    .onCompletion(mMediaPlayer);
//                                    }
//                                }).setCancelable(false).show();
//            }
            return true;
        }

        @Override
        public boolean onMediaError(int what) {
            if (mOnErrorListener != null) {
                mOnErrorListener.onMediaError(what);
            }
            return false;
        }

        @Override
        public void onOpenError(Exception e,Uri uri) {
            if (mOnErrorListener != null) {
                mOnErrorListener.onOpenError(e,uri);
            }
        }
    };

    private DolitBaseMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new DolitBaseMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(Object mp, int percent) {
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(Object mp, int what, int extra) {
        if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            showBuffingTip();
        } else if (what == DolitBaseMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            hideBuffingTip();
        }
        if (mOnInfoListener != null) {
            mOnInfoListener.onInfo(mp, what, extra);
        }
        return true;
        }
    };

    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(Object mp) {
            DebugLog.d(TAG, "onSeekComplete"+mMediaPlayer.getCurrentPosition());
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };

    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(DolitBaseMediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }
    public void init(){
        mMediaPlayer=new SoftMediaPlayer(mediaCodecEnabled);
    }
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
            }
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0)
                    seekTo(mSeekWhenPrepared);
                start();
                if (mMediaController != null) {
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
                    && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);

                if (autoPlayAfterSurfaceCreated) {
                    resume();
                }
            } else {
                if (autoPlayAfterSurfaceCreated) {
                    openVideo();
                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            hideBuffingTip();
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            if (mCurrentState != STATE_SUSPEND)
                release(true);
        }
    };

    private void release(boolean cleartargetstate) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mCurrentState = STATE_IDLE;
                if (cleartargetstate)
                    mTargetState = STATE_IDLE;
            }
            handler.removeCallbacks(TimeOutError);
        }catch (Exception ex){}
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisiblity();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported
                && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_SPACE) {
                if (isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && isPlaying()) {
                pause();
                mMediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState1()) {
            try {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState1()) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mCurrentState = STATE_PAUSED;
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState1()) {
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        mDuration = -1;
        return (int) mDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState1()) {
            long position = mMediaPlayer.getCurrentPosition();
            return (int) position;
        }
        return 0;
    }

    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState1()) {
            try {
                Log.e("TAG","滑动播放时间跳转到："+msec);
                mMediaPlayer.seekTo(msec);
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
            mMediaPlayer.start();
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState1() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public boolean isInPlaybackState() {
        return (mMediaPlayer != null);
//        && mCurrentState != STATE_ERROR
//                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }
    protected boolean isInPlaybackState1() {
        return (mMediaPlayer != null
        && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }
    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    //设置显示比例
    public void setScale(int flg) {
        //目前只有三种
        this.m_videoLayoutScale = flg % 3;
    }

    private void showBuffingTip() {
        if (this.mBufferingIndicator != null && this.mBufferingIndicator.getParent() == null) {
            Log.i(TAG, " ------------ set loading...");
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.format = PixelFormat.TRANSPARENT;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            lp.width = LayoutParams.WRAP_CONTENT;
            lp.height = LayoutParams.WRAP_CONTENT;
            // lp.token = getWindowToken();
            lp.gravity = Gravity.CENTER;
            this.mWindowManager.addView(mBufferingIndicator, lp);
        }
    }

    private void hideBuffingTip() {
        if (this.mBufferingIndicator != null &&
                this.mBufferingIndicator.getParent() != null&&mBufferingIndicator instanceof View) {
            try {
                if(this.mWindowManager!=null&&mBufferingIndicator!=null) {
                    this.mWindowManager.removeView(mBufferingIndicator);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getVideoLayoutScale() {
        return m_videoLayoutScale;
    }

    /**
     * 全屏状态 才可以使用 选择比例
     *
     * @param flg
     */
    public void selectScales(int flg) {
        m_videoLayoutScale = flg;
        if (getWindowToken() != null) {
            Rect rect = new Rect();
            getWindowVisibleDisplayFrame(rect);
            double height = rect.bottom - rect.top;
            double width = rect.right - rect.left;

            if (height <= 0.0 || width <= 0.0 || mVideoHeight <= 0.0
                    || mVideoWidth <= 0.0) {
                return;
            }
//            changeSize(width,height);
//            RelativeLayout.LayoutParams param =null;////getLayoutParams();
//            switch (flg) {
//                case A_4X3:
//                    if (width / height >= 4.0 / 3.0) { // 屏幕 宽了 以屏幕高为基
//                        param.height = (int) height;
//                        param.width = (int) (4 * height / 3);
//                    } else { // 屏幕 高了 以宽为基
//                        param.width = (int) width;
//                        param.height = (int) (3 * width / 4);
//                    }
//                    System.out.println("A_4X3 === " + param.width + ":"
//                            + param.height);
//                    setLayoutParams(param);
//                    break;
//                case A_16X9:
////                    if (width / height >= 16.0 / 9.0) { // 屏幕 宽了 以屏幕高为基
////                        param.height = (int) height;
////                        param.width = (int) (16 * height / 9);
////                    } else { // 屏幕 高了 以宽为基
////                        param.width = (int) width;
////                        param.height = (int) (9 * width / 16);
////                    }
////                    param.width = (int) width;
////                    param.height = (int) (9 * width / 16);
////                    System.out.println("A_16X9 === " + param.width + ":"
////                            + param.height);
////                    setLayoutParams(param);
////                    int tH=getHeight();
//                    double rH=height*1.0/mVideoHeight;
//                    Log.e("onVideoSizeChanged",width+"---"+height+"---"+rH+"");
//                    int w= (int) (mVideoWidth*rH);
//                    int h= (int) (mVideoHeight*rH);
//                    RelativeLayout.LayoutParams layoutParams1= new RelativeLayout.LayoutParams(w,h);
//                    layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
//                    setLayoutParams(layoutParams1);
//                    break;
//                case A_DEFALT: //
//                    // shaoheyi 修复原始比例显示不正确问题参照ijkplayer demo把mVideoSarNum / mVideoSarDen考虑进来
//                    // 本次修改删除onMeasure方法，因为我们通过setLayoutParams已经正确设置了宽高，这个父类的onMeasure会考虑进去的
//                    // 在selectScales也调用了requestLayout
////                    float videoWidthHeightRatio = mVideoWidth * 1.0f / mVideoHeight;
////                    if (mVideoSarNum > 0 && mVideoSarDen > 0) {
////                        videoWidthHeightRatio = videoWidthHeightRatio * mVideoSarNum / mVideoSarDen;
////                    }
////
////                    boolean videoViewShouldBeWider = videoWidthHeightRatio > width / height;
////                    if (videoViewShouldBeWider) {
////                        // too wide, fix width
////                        param.width = (int) width;
////                        param.height = (int) (width / videoWidthHeightRatio);
////                    } else {
////                        // too high, fix height
////                        param.height = (int) height;
////                        param.width = (int) (height * videoWidthHeightRatio);
////                    }
//
//                    int wx= (int) (mVideoWidth*1.8);
//                    int hx= (int) (mVideoHeight*1.8);
//                    RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(wx,hx);
//                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
//                    setLayoutParams(layoutParams);
//                    break;
//            }
//            RelativeLayout.LayoutParams attrs =null;//new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);// this.getWindow().getAttributes();
//            if (flg==2) {
//                attrs =new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
////                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
////                    activity.getWindow().setAttributes(attrs);
////                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////                    videoView.selectScales(DolitVideoView.A_16X9);
////                    attrs.gravity=Gravity.CENTER;
//                attrs.alignWithParent=true;
//                setLayoutParams(attrs);
//            } else {
//                attrs =new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
////                    attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
////                    activity.getWindow().setAttributes(attrs);
////                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////                    attrs.gravity=Gravity.CENTER;
//                setLayoutParams(attrs);
//            }

            if(mMediaController==null){
                return;
            }
            if(flg==2){
                mMediaController.showFull();
            }else{
                mMediaController.hideFull();
            }
        }
    }
    public void changeSize(double width,double height){
        //TODO...
//        setRotation(0);
        Log.e("onVideoSizeChanged","W="+width+"-- H:"+height);
        double rH = height * 1.0 / mVideoHeight;
        Log.e("onVideoSizeChanged", width + "---" + height + "---" + rH + "");
        int w = (int) (mVideoWidth * rH);
        int h = (int) (mVideoHeight * rH);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(w, h);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        setLayoutParams(layoutParams1);
    }
    //全屏
    public void onFull(){
        float rotation=getRotation();
        Log.e("onVideoSizeChanged","当前旋转度数:"+rotation);
//        setRotation(rotation+180);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams1);
    }

    public void setUserMac(String strUserMac) {
        this.mUser_Mac = strUserMac;
    }

    public void setLiveSeek(String strLiveSeek) {
        this.mLiveSeek = strLiveSeek;
    }

    public void setLiveEpg(String strLiveEpg) {
        this.mLiveEpg = strLiveEpg;
    }

    public void setLiveCookie(String strLiveCookie) {
        this.mLiveCookie = strLiveCookie;
    }

    public void setLiveRange(String strLiveRange) {
        this.mLive_Range = strLiveRange;
    }

    public void setLiveReferer(String strLive_Referer) {
        this.mLive_Referer = strLive_Referer;
    }

    public void setLiveKey(String strLivKey) {
        this.mLive_key = strLivKey;
    }

    private static final long TIMEOUTDEFAULT = 50000;
    //private static final int MEDIA_ERROR_TIMED_OUT = 0xffffff92;
    private Runnable TimeOutError = new Runnable() {

        @Override
        public void run() {
//            Log.e(TAG, "open video time out : Uri = " + mUri);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;

            if (mOnErrorListener != null) {
                mOnErrorListener.onError(mMediaPlayer, DolitBaseMediaPlayer.MEDIA_INFO_TIMEOUT, -100, getCurrentPosition());
            }
        }
    };

    public void setMediaCodecEnabled(boolean mediaCodecEnabled) {
        this.mediaCodecEnabled = mediaCodecEnabled;
    }
}
