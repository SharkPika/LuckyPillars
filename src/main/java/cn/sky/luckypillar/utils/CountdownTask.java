package cn.sky.luckypillar.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 倒计时任务
 */
public class CountdownTask extends BukkitRunnable {
    
    private final JavaPlugin plugin;
    private int countdown;
    private final Runnable onTick;
    private final Runnable onComplete;
    
    public CountdownTask(JavaPlugin plugin, int countdown, Runnable onTick, Runnable onComplete) {
        this.plugin = plugin;
        this.countdown = countdown;
        this.onTick = onTick;
        this.onComplete = onComplete;
    }
    
    @Override
    public void run() {
        if (countdown <= 0) {
            if (onComplete != null) {
                onComplete.run();
            }
            cancel();
            return;
        }
        
        if (onTick != null) {
            onTick.run();
        }
        
        countdown--;
    }
    
    /**
     * 获取剩余时间
     */
    public int getCountdown() {
        return countdown;
    }
    
    /**
     * 启动倒计时
     */
    public void start() {
        runTaskTimer(plugin, 0L, 20L); // 每秒执行一次
    }
}
