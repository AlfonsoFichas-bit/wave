package com.sonymobile.runtimeskinning;

import android.util.Log;
import java.lang.Thread;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
class ReflectionUtils {
    ReflectionUtils() {
    }

    /* loaded from: C:\Users\alfon\AppData\Local\Temp\rea-extracted-1caeeaee-1d17-42e3-a354-f68a099582c9\classes.dex */
    static class Accessor<T> {
        private Class<T> mClass;
        private final Field mField;

        public Accessor(Class<T> clazz, Field field) {
            this.mClass = clazz;
            this.mField = field;
        }

        public T get(Object obj) {
            return (T) ReflectionUtils.readField(this.mField, obj, this.mClass);
        }

        public boolean set(Object o, T value) {
            return ReflectionUtils.setField(this.mField, o, value);
        }
    }

    public static <T> T invokeMethod(Method method, Object obj, Class<T> cls, Object... objArr) {
        return (T) invokeMethod(method, obj, null, cls, objArr);
    }

    public static <T> T invokeMethod(Method method, Object instance, Thread.UncaughtExceptionHandler handler, Class<T> returnType, Object... args) {
        Object obj = null;
        try {
            obj = method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            if (handler != null) {
                handler.uncaughtException(Thread.currentThread(), e);
            }
            Log.w(Constants.TAG, "Error invoking " + method.getName(), e);
        } catch (IllegalArgumentException e2) {
            if (handler != null) {
                handler.uncaughtException(Thread.currentThread(), e2);
            }
            Log.w(Constants.TAG, "Error invoking " + method.getName(), e2);
        } catch (InvocationTargetException e3) {
            if (handler != null) {
                handler.uncaughtException(Thread.currentThread(), e3);
            }
            Log.w(Constants.TAG, "Error invoking " + method.getName(), e3);
        }
        return returnType.cast(obj);
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... parameters) {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(name, parameters);
            makeAccessible(method);
            if (!returnType.isAssignableFrom(method.getReturnType())) {
                return null;
            }
            return method;
        } catch (NoSuchMethodException e) {
            return method;
        }
    }

    public static <T> T readField(Field field, Object instance, Class<T> fieldType) {
        Object obj = null;
        try {
            obj = field.get(instance);
        } catch (IllegalAccessException e) {
            Log.w(Constants.TAG, "Error reading " + field.getName(), e);
        } catch (IllegalArgumentException e2) {
            Log.w(Constants.TAG, "Error reading " + field.getName(), e2);
        }
        return fieldType.cast(obj);
    }

    public static boolean setField(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
            return true;
        } catch (IllegalAccessException e) {
            Log.w(Constants.TAG, "Error setting " + field.getName(), e);
            return false;
        } catch (IllegalArgumentException e2) {
            Log.w(Constants.TAG, "Error setting " + field.getName(), e2);
            return false;
        }
    }

    public static Field getField(Class<?> clazz, String name, Class<?> type) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
            makeAccessible(field);
            if (!type.isAssignableFrom(field.getType())) {
                return null;
            }
            return field;
        } catch (NoSuchFieldException e) {
            return field;
        }
    }

    public static <T> Accessor<T> getFieldAccessor(Class<?> clazz, String name, Class<?> fieldType, Class<T> type) {
        Field field = getField(clazz, name, fieldType);
        if (field == null) {
            return null;
        }
        return new Accessor<>(type, field);
    }

    private static void makeAccessible(AccessibleObject obj) {
        if (!obj.isAccessible()) {
            obj.setAccessible(true);
        }
    }
}
