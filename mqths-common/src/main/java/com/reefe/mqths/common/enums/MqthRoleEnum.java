package com.reefe.mqths.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 事务类型（角色）
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public enum MqthRoleEnum {
    /**
     * Start tcc role enum.
     */
    START(1, "发起者"),


    /**
     * Consumer tcc role enum.
     */
    LOCAL(2, "本地执行"),


    /**
     * Provider tcc role enum.
     */
    PROVIDER(3, "提供者");


    private int code;

    private String desc;

    MqthRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MqthRoleEnum acquireByCode(int code) {

        return Arrays.stream(MqthRoleEnum.values())
                .filter(v->Objects.equals(v.getCode(),code))
                .findFirst()
                .orElse(MqthRoleEnum.START);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
