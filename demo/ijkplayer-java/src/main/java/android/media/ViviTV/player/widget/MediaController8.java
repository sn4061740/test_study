
package android.media.ViviTV.player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Locale;

import tv.danmaku.ijk.media.player.R;

public class MediaController8 implements View.OnClickListener {
    private static final String TAG = MediaController8.class.getSimpleName();

    public interface IMediaControllerCallback {
        void handleFullScreenClicked(View view);

        void handlePauseStartClicked(View view);

        void handlerLineClicked();

        void handlerQuailtyClicked();

        void handlerSpeedClicked();
    }

    private IMediaPlayerControl mPlayer;
    private Context mContext;
    private SeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private ImageButton mPauseButton;
    private ImageButton ibFullScreen;
    private IMediaControllerCallback callback;
    private TextView tvQuality;

    private TextView tvLine;
    private TextView speedView;

//    public MediaController8(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initController(context);
//    }
    public MediaController8(Context context) {
//        super(context);
        initController(context);
    }

    private boolean initController(Context context) {
        mContext = context;
        return true;
    }
    public void setCallback(IMediaControllerCallback callback) {
        this.callback = callback;
    }

    public void setAutoHideNavigation(boolean autoHideNavigation) {
//        this.autoHideNavigation = autoHideNavigation;
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
//    protected View makeControllerView() {
//        View v=LayoutInflater.from(mContext).inflate(R.layout.mediacontroller_new, this);
//        return v;
//    }
    public void initView(View v,int type){
        switch (type){
            case 0:
                mPauseButton = (ImageButton) v;// (ImageButton) v.findViewById(R.id.mediacontroller_play_pause);
                if (mPauseButton != null) {
                    mPauseButton.requestFocus();
                    mPauseButton.setOnClickListener(mPauseListener);
                }
                break;
            case 1:
                mProgress = (SeekBar) v;// (SeekBar) v.findViewById(R.id.mediacontroller_seekbar);
                if (mProgress != null) {
                    if (mProgress instanceof SeekBar) {
                        SeekBar seeker = (SeekBar) mProgress;
                        seeker.setOnSeekBarChangeListener(mSeekListener);
                        seeker.setThumbOffset(1);
                    }
                    mProgress.setMax(1000);
                }
                break;
            case 2:
                mEndTime = (TextView) v;//(TextView) v.findViewById(R.id.mediacontroller_time_total);
                break;
            case 3:
                mCurrentTime = (TextView) v;//.findViewById(R.id.mediacontroller_time_current);
                break;
            case 4:
                ibFullScreen = (ImageButton) v;//.findViewById(R.id.mediacontroller_fullscreen);
                ibFullScreen.setOnClickListener(this);
                break;
            case 5:
                tvQuality = (TextView) v;//.findViewById(R.id.tv_quality);
                tvQuality.setOnClickListener(this);
                break;
            case 6:
                tvLine= (TextView) v;//.findViewById(R.id.tv_line);
                tvLine.setOnClickListener(this);
                break;
            case 7:
                speedView= (TextView) v;//.findViewById(R.id.tv_speed);
                speedView.setOnClickListener(this);
                break;
        }
    }

    public void setMediaPlayer(IMediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Set the content of the file_name TextView
     *
     * @param name
     */
    public void setFileName(String name) {
//        mTitle = name;
    }

    /**
     * Set the View to hold some information when interact with the
     * MediaController
     *
     * @param v
     */
    public void setInfoView(OutlineTextView v) {
//        mInfoView = v;
    }

    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && !mPlayer.canPause())
                mPauseButton.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p>
     * <p>
     * If the controller is showing, calling this method will take effect only
     * the next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears and
     *                       disappears. Set to -1 for the default animation, 0 for no
     *                       animation, or a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
//        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    @SuppressLint("InlinedApi")
    public void show(int timeout) {
        try {
            if (!mShowing) {
                if (mPauseButton != null)
                    mPauseButton.requestFocus();
                disableUnsupportedButtons();

                mShowing = true;
                if (mShownListener != null)
                    mShownListener.onShown();
            }
        }catch (Exception e){}
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
                    timeout);
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    @SuppressLint("InlinedApi")
    public void hide() {
        if (mShowing) {
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
            } catch (IllegalArgumentException ex) {
                DebugLog.d(TAG, "MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null)
                mHiddenListener.onHidden();
        }
    }

    public interface OnShownListener {
        public void onShown();
    }

    private OnShownListener mShownListener;

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    private OnHiddenListener mHiddenListener;

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        long pos;
        switch (msg.what) {
            case FADE_OUT:
                hide();
                break;
            case SHOW_PROGRESS:
                pos = setProgress();
                if (!mDragging && mShowing) {
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    updatePausePlay();
                }
                break;
        }
        }
    };

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mDuration = duration;

        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

        return position;
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds).toString();
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        show(sDefaultTimeout);
//        return true;
//    }
//
//    @Override
//    public boolean onTrackballEvent(MotionEvent ev) {
//        show(sDefaultTimeout);
//        return false;
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        int keyCode = event.getKeyCode();
//        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
//                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
//            doPauseResume();
//            show(sDefaultTimeout);
//            if (mPauseButton != null)
//                mPauseButton.requestFocus();
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
//            if (mPlayer.isPlaying()) {
//                mPlayer.pause();
//                updatePausePlay();
//            }
//            return true;
//        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
//            hide();
//            return true;
//        } else {
//            show(sDefaultTimeout);
//        }
//        return super.dispatchKeyEvent(event);
//    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private void updatePausePlay() {
        try {
//            if (mPlayer.isPlaying())
//                mPauseButton.setImageResource(R.drawable.mediacontroller_pause_button);
//            else
//                mPauseButton.setImageResource(R.drawable.mediacontroller_play_button);
        }catch (Exception ex){}
    }

    public void doPauseResume() {
        try {
            if (mPlayer.isPlaying())
                mPlayer.pause();
            else
                mPlayer.start();
            updatePausePlay();

            // notify outside
            if (callback != null) {
                callback.handlePauseStartClicked(mPauseButton);
            }
        }catch (Exception ex){}
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
//            if (mInstantSeeking)
//                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
//            if (mInfoView != null) {
//                mInfoView.setText("");
//                mInfoView.setVisibility(View.VISIBLE);
//            }
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser)
                return;
            Log.e("SEEKBAR","onProgressChanged"+mInstantSeeking);
            long newposition = (mDuration * progress) / 1000;
            String time = generateTime(newposition);
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
//            if (mInfoView != null)
//                mInfoView.setText(time);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            Log.e("SEEKBAR","mInstantSeeking"+mInstantSeeking);
            if (!mInstantSeeking)
                mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
//            if (mInfoView != null) {
//                mInfoView.setText("");
//                mInfoView.setVisibility(View.GONE);
//            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
//            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

//    @Override
//    public void setEnabled(boolean enabled) {
//        if (mPauseButton != null)
//            mPauseButton.setEnabled(enabled);
//        if (mProgress != null)
//            mProgress.setEnabled(enabled);
//        disableUnsupportedButtons();
////        super.setEnabled(enabled);
//    }
    public void showFull(){
        //ibFullScreen.setImageResource(R.drawable.full_no);
    }

    public void hideFull(){
//        ibFullScreen.setImageResource(R.drawable.quanping);
    }

    @Override
    public void onClick(View v) {
        if (v == ibFullScreen) {
            if (callback != null) {
                callback.handleFullScreenClicked(v);
            }
        } else if (tvQuality == v) {
//            doOnQualityClicked();
            if(callback!=null){
                callback.handlerQuailtyClicked();
            }
        }else if(v==tvLine){//线路
            if (callback != null) {
                callback.handlerLineClicked();
            }
        }else if(v==speedView){//倍速
            if(callback!=null){
                callback.handlerSpeedClicked();
            }
        }
    }
    private OnSelectQualityListener onSelectQualityListener;
    public void setOnSelectQualityListener(OnSelectQualityListener l){
        this.onSelectQualityListener=l;
    }
    //没有用了。。
    public interface OnSelectQualityListener{
        void onSelectQuality(String msg);
        void onSelectQuality(String msg, int postion);
    }
    public void setTvQualityText(final String txt){
        if(tvQuality!=null) {
            tvQuality.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvQuality.setText(txt);
                }
            },100);
        }
    }
    //显示倍速
    public void setTvSpeedText(final String txt){
        if(speedView!=null){
            speedView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speedView.setText(txt);
                }
            },100);
        }
    }
    public void setTvLineText(final String txt){
        if(tvLine!=null){
            tvLine.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvLine.setText(txt);
                }
            },100);
        }
    }

}
