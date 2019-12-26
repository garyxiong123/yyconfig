/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.gary.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * @author hanxiaorui
 * <p>
 * 公共工具类
 */
public class CommonUtil {

    /**
     * private constructor
     */
    private CommonUtil() {
    }

    /**
     * 匹配对象是否存在于数组中
     *
     * @param src     待匹配对象
     * @param destArr 待匹配数组
     * @return
     */
    @SafeVarargs
    public static <T> boolean matches(T src, T... destArr) {

        if (destArr == null || destArr.length == 0) {
            return false;
        }

        for (T t : destArr) {
            if (t == src) {
                return true;
            }
            if (t.equals(src)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 默认值保护
     *
     * @param value        表达式值
     * @param defaultValue 默认值
     * @return
     */
    public static <T> T defaultValue(T value, T defaultValue) {

        if (Objects.nonNull(value)) {
            return value;
        }

        return defaultValue;
    }

    /**
     * Coerces a value to a given type.
     *
     * @param value
     * @param type
     * @return The coerced value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object value, Class<? extends T> type) {
        if (type == null) {
            throw new NullPointerException();
        }

        Object coercedValue;
        if (value == null) {
            // Null values can only be coerced to null
            coercedValue = null;
        } else if (type.isAssignableFrom(value.getClass())) {
            // Value doesn't require coercion
            coercedValue = value;
        } else if (type == Boolean.class || type == Boolean.TYPE) {
            coercedValue = Boolean.valueOf(value.toString());
        } else if (type == Character.class || type == Character.TYPE) {
            coercedValue = value.toString().charAt(0);
        } else if (type == Byte.class || type == Byte.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).byteValue();
            } else {
                coercedValue = Byte.valueOf(value.toString());
            }
        } else if (type == Short.class || type == Short.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).shortValue();
            } else {
                coercedValue = Short.valueOf(value.toString());
            }
        } else if (type == Integer.class || type == Integer.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).intValue();
            } else {
                coercedValue = Integer.valueOf(value.toString());
            }
        } else if (type == Long.class || type == Long.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).longValue();
            } else {
                coercedValue = Long.valueOf(value.toString());
            }
        } else if (type == BigInteger.class) {
            if (value instanceof Number) {
                coercedValue = BigInteger.valueOf(((Number) value).longValue());
            } else {
                coercedValue = new BigInteger(value.toString());
            }
        } else if (type == Float.class || type == Float.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).floatValue();
            } else {
                coercedValue = Float.valueOf(value.toString());
            }
        } else if (type == Double.class || type == Double.TYPE) {
            if (value instanceof Number) {
                coercedValue = ((Number) value).doubleValue();
            } else {
                coercedValue = Double.valueOf(value.toString());
            }
        } else if (type == Number.class) {
            String number = value.toString();
            if (number.contains(".")) {
                coercedValue = Double.valueOf(number);
            } else {
                coercedValue = Long.valueOf(number);
            }
        } else if (type == BigDecimal.class) {
            if (value instanceof Number) {
                coercedValue = BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                coercedValue = new BigDecimal(value.toString());
            }
        } else {
            throw new IllegalArgumentException("type not support");
        }

        return (T) coercedValue;
    }

    /**
     * Returns an array containing the given arguments.
     *
     * @param <T>    the type of the array to return.
     * @param values the values to store in the array.
     * @return an array containing the given arguments.
     */
    @SafeVarargs
    public static <T> T[] array(T... values) {
        return values;
    }
}
