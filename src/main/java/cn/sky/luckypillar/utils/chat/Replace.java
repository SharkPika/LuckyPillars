package cn.sky.luckypillar.utils.chat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Replace {
    public static String replaceAll(String str, Map<String, Object> replaces) {
        AtomicReference<String> s = new AtomicReference<>(str);
        replaces.forEach((key, value) -> {
            if ((s.get()).contains(key)) {
                s.set((s.get()).replace(key, value.toString()));
            }
        });
        return s.get();
    }
}
