package cn.sky.luckypillar.utils.classutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ReflectUtil {
    private ReflectUtil() {
    }

    public static void forEachFields(Class<?> Type2, Consumer<Field> consumer) {
        ReflectUtil.forEachSuper(Type2, SuperType -> {
            Field[] fields = SuperType.getDeclaredFields();
            Arrays.asList(fields).forEach(consumer);
        });
    }

    public static void forEachMethods(Class<?> Type2, Consumer<Method> consumer) {
        ReflectUtil.forEachSuper(Type2, SuperType -> {
            Method[] methods = SuperType.getDeclaredMethods();
            Arrays.asList(methods).forEach(consumer);
        });
    }

    public static void forEachSuper(Class<?> Type2, Consumer<Class<?>> consumer) {
        Objects.requireNonNull(Type2);
        for (Class<?> SuperType = Type2; SuperType != null && !SuperType.equals(Object.class); SuperType = SuperType.getSuperclass()) {
            consumer.accept(SuperType);
        }
    }

    public static <T> Field getField(T instance, String FieldName) {
        AtomicReference<Field> field = new AtomicReference<>();
        Class<?> type = instance.getClass();
        ReflectUtil.forEachFields(type, field1 -> {
            if (field1.getName().equals(FieldName)) {
                field.set(field1);
            }
        });
        return field.get();
    }

    public static <T> Object invokeMethod(T instance, String MethodName, Object ... args) {
        AtomicReference<Method> resmethod = new AtomicReference<>();
        ReflectUtil.forEachMethods(instance.getClass(), method -> {
            if (method.getName().equals(MethodName)) {
                method.setAccessible(true);
                resmethod.set(method);
            }
        });
        try {
            return resmethod.get().invoke(instance, args);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setFieldValue(T instance, String FieldName, Object value) {
        ReflectUtil.forEachFields(instance.getClass(), field -> {
            if (field.getName().equals(FieldName)) {
                field.setAccessible(true);
                try {
                    field.set(instance, value);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static <T> Object getFieldValue(T instance, String FieldName) {
        Field field = ReflectUtil.getField(instance, FieldName);
        field.setAccessible(true);
        try {
            return field.get(instance);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createInstance(Class<T> clazz, Object ... args) {
        try {
            T instance = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                fields[i].setAccessible(true);
                fields[i].set(instance, args[i]);
            }
            return instance;
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}

