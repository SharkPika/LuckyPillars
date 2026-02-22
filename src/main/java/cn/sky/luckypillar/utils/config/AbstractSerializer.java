package cn.sky.luckypillar.utils.config;

public abstract class AbstractSerializer<T> {

    public abstract String serialize(T o);

    public abstract T deserialize(String s);
}
