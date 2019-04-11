package com.xcore.ui.enums;

public enum APIEnum {
    API(0),
    VERSION(1),
    CDN(2);

    private final int value;

    // 构造器默认也只能是private, 从而保证构造函数只能在内部使用
    APIEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
