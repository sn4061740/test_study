package com.xcore.ui.enums;

public enum CDNType {
    API(0),
    RES(1),
    TOR(2),
    HTTP(3),
    M3U8(4),
    APK(5),
    JSON(6);

    private final int value;

    // 构造器默认也只能是private, 从而保证构造函数只能在内部使用
    CDNType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
