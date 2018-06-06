package com.reefe.mqths.common.serializer.impl;

import com.reefe.mqths.common.enums.SerializeEnum;
import com.reefe.mqths.common.exception.MqthException;
import com.reefe.mqths.common.serializer.ObjectSerializer;

import java.io.*;

/**
 * java 原生序列化
 * @Auther: REEFE
 * @Date: 2018/6/5/005
 */
public class JavaSerializer implements ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws MqthException 异常信息
     */
    @Override
    public byte[] serialize(Object obj) throws MqthException {
        try {
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            ObjectOutput objectOutput = new ObjectOutputStream(arrayOutputStream);
            objectOutput.writeObject(obj);
            objectOutput.flush();
            objectOutput.close();
            return arrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new MqthException("JAVA serialize error " + e.getMessage());
        }
    }

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz java对象
     * @return 对象
     * @throws MqthException 异常信息
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws MqthException {
        try {
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(param);
            ObjectInput objectInput = new ObjectInputStream(arrayInputStream);
            return (T)objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MqthException("JAVA deSerialize error " + e.getMessage());
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return SerializeEnum.JDK.getSerialize();
    }
}
