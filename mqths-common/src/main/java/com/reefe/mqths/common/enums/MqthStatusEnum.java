package com.reefe.mqths.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 事务状态
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public enum MqthStatusEnum {

    COMMIT(1,"已经提交"),

    BEGIN(2,"开始"),

    FAILURE(4, "失败");

    private int code;

    private String desc;

    MqthStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MqthStatusEnum acquireByCode(int code) {

        return Arrays.stream(MqthStatusEnum.values())
                .filter(v -> Objects.equals(v.getCode(),code))
                .findFirst()
                .orElse(MqthStatusEnum.BEGIN);
    }

    public static String acquireDescByCode(int code) {
        return acquireByCode(code).getDesc();
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
