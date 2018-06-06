package com.reefe.mqths.common.utils;


import com.reefe.mqths.common.exception.MqthRuntimeException;

/**
 *
 * @author REEFE
 */
public class AssertUtils {

    private AssertUtils() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new MqthRuntimeException(message);
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new MqthRuntimeException("argument invalid,Please check");
        }
    }

    public static void checkConditionArgument(boolean condition, String message) {
        if (!condition) {
            throw new MqthRuntimeException(message);
        }
    }

}
