package com.xcore.data.model;

import com.xcore.ui.enums.PlayTypeEnum;

public class SelectModel {
    private Integer id;
    private String name;
    private Integer icon;
    private Object value;
    private int position;//当前选中
    private int vType;

    public SelectModel(){}
    public SelectModel(Integer id,String name,Integer type,Integer icon,Object o){
        this.id=id;
        this.name=name;
        this.icon=icon;
        this.value=o;
    }
    public SelectModel(Integer id,String name,Integer type,Integer icon,Object o,int vtype){
        this.id=id;
        this.name=name;
        this.icon=icon;
        this.value=o;
        this.vType=vtype;
    }

    /**
     * 获取线路类型
     * @return
     */
    public PlayTypeEnum getType() {
        PlayTypeEnum playTypeEnum= PlayTypeEnum.values()[this.id-1];
        return playTypeEnum;
    }

    public int getvType() {
        return vType;
    }

    public void setvType(int vType) {
        this.vType = vType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
