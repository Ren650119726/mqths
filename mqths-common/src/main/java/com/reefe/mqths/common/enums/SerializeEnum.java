package com.reefe.mqths.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 序列化枚举
 * @Auther: REEFE
 * @Date: 2018/6/4
 */
public enum SerializeEnum {

    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk"),
    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo");

    private String serialize;

    SerializeEnum(String serialize){
        this.serialize = serialize;
    }

    /**
     * 获取序列化协议 枚举
     *
     * @param serialize
     * @return enum
     */
    public static SerializeEnum acquire(String serialize) {
        Optional<SerializeEnum> serializeEnum = Arrays.stream(SerializeEnum.values())
                .filter(v -> Objects.equals(v.getSerialize(), serialize))
                .findFirst();
        return serializeEnum.orElse(SerializeEnum.KRYO);
    }

    public String getSerialize() {
        return serialize;
    }
}
