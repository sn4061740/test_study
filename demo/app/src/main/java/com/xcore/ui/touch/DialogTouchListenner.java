package com.xcore.ui.touch;

import com.xcore.data.bean.CommentBean;

public interface DialogTouchListenner {
    void onTouch(CommentBean.CommentDataBean commentDataBean,boolean isLike,int position);
}
