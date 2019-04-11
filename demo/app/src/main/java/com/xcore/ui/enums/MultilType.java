package com.xcore.ui.enums;

//多布局所有的类型
public enum MultilType {
    HEADER(0),
    FOOTER(1),
    CONTENT(2),
    BANNER(3),
    BUTTON(4),
    ITEM(5),
    TEXT(6),
    IMAGE(7),
    STRING(8);

    private final int value;

    // 构造器默认也只能是private, 从而保证构造函数只能在内部使用
    MultilType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
