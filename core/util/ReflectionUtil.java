/*
 * Decompiled with CFR 0.152.
 */
package ic2.core.util;

import ic2.core.IC2;
import ic2.core.network.DataEncoder;
import ic2.core.util.LogCategory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {
    public static Field getField(Class<?> clazz, String ... names) {
        for (String name : names) {
            try {
                Field ret = clazz.getDeclaredField(name);
                ret.setAccessible(true);
                return ret;
            }
            catch (NoSuchFieldException e) {
            }
            catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static Field getField(Class<?> clazz, Class<?> type) {
        Field ret = null;
        for (Field field : clazz.getDeclaredFields()) {
            if (!type.isAssignableFrom(field.getType())) continue;
            if (ret != null) {
                return null;
            }
            field.setAccessible(true);
            ret = field;
        }
        return ret;
    }

    public static Field getFieldRecursive(Class<?> clazz, String fieldName) {
        Field field = null;
        do {
            try {
                field = clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (field == null && clazz != null);
        return field;
    }

    public static Object getValue(Object object, Class<?> type) {
        Field field = ReflectionUtil.getField(object.getClass(), type);
        if (field == null) {
            return null;
        }
        try {
            return field.get(object);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getValueRecursive(Object object, String fieldName) throws NoSuchFieldException {
        Field field = ReflectionUtil.getFieldRecursive(object.getClass(), fieldName);
        if (field == null) {
            throw new NoSuchFieldException(fieldName);
        }
        field.setAccessible(true);
        try {
            return field.get(object);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean setValueRecursive(Object object, String fieldName, Object value) {
        Field field = ReflectionUtil.getFieldRecursive(object.getClass(), fieldName);
        if (field == null) {
            IC2.log.warn(LogCategory.General, "Can't find field %s in %s to set it to %s.", fieldName, object, value);
            return false;
        }
        field.setAccessible(true);
        if (field.getType().isEnum() && value instanceof Integer) {
            value = field.getType().getEnumConstants()[(Integer)value];
        }
        try {
            Object oldValue = field.get(object);
            if (!DataEncoder.copyValue(value, oldValue)) {
                field.set(object, value);
            }
            return true;
        }
        catch (Exception e) {
            throw new RuntimeException("can't set field " + fieldName + " in " + object + " to " + value, e);
        }
    }

    public static boolean setValue(Object object, Field field, Object value) {
        field.setAccessible(true);
        if (field.getType().isEnum() && value instanceof Integer) {
            value = field.getType().getEnumConstants()[(Integer)value];
        }
        try {
            Object oldValue = field.get(object);
            if (!DataEncoder.copyValue(value, oldValue)) {
                field.set(object, value);
            }
            return true;
        }
        catch (Exception e) {
            throw new RuntimeException("can't set field " + field.getName() + " in " + object + " to " + value, e);
        }
    }

    public static Method getMethod(Class<?> clazz, String[] names, Class<?> ... parameterTypes) {
        for (String name : names) {
            try {
                Method ret = clazz.getDeclaredMethod(name, parameterTypes);
                ret.setAccessible(true);
                return ret;
            }
            catch (NoSuchMethodException e) {
            }
            catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}

