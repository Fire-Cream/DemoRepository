package com.cream.mqtt.enums;

/**
 * @Author: Cream
 * @Date: 2023-05-06-15:17
 * @Description:
 */
public enum QosEnum {
    Qos0(0), //最多一次
    Qos1(1), //最少一次
    Qos2(2); //仅一次

    private final int value;

    //枚举类构造方法
    QosEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
