package com.xcore.ui.enums;

public enum PlayTypeEnum {
    TORRENT(1),
    HTTP(2),
    M3U8(3),
    LOCAL(4);

    private final int value;

    // 构造器默认也只能是private, 从而保证构造函数只能在内部使用
    PlayTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
