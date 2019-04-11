package com.xcore.down;

public interface IM3u8Listener {
    void onComplete(int position);
    void onComplete(int position,CacheModel cacheModel);
}
