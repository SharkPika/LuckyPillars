package cn.sky.luckypillar.event;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * 事件调度器
 * 负责定时触发游戏事件
 */
@Getter
public class EventScheduler {
    
    private final JavaPlugin plugin;
    private final LuckyPillarGame game;
    private final EventManager eventManager;
    private final LuckyPillarConfig config;
    
    private BukkitTask schedulerTask;
    private long lastEventTime;
    private boolean firstEvent;
    
    public EventScheduler(JavaPlugin plugin, LuckyPillarGame game, EventManager eventManager, LuckyPillarConfig config) {
        this.plugin = plugin;
        this.game = game;
        this.eventManager = eventManager;
        this.config = config;
        this.firstEvent = true;
    }
    
    /**
     * 启动事件调度器
     */
    public void start() {
        if (schedulerTask != null) {
            CC.send("&c事件调度器已经在运行");
            return;
        }
        
        lastEventTime = System.currentTimeMillis();
        firstEvent = true;
        
        // 每秒检查一次是否需要触发事件
        schedulerTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            // 检查游戏状态
            if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
                return;
            }
            
            // 检查是否有事件正在进行
            if (eventManager.isEventActive()) {
                return;
            }
            
            // 检查是否可以触发事件
            if (canTriggerEvent()) {
                eventManager.startRandomEvent();
                lastEventTime = System.currentTimeMillis();
                firstEvent = false;
            }
        }, 20L, 20L); // 每秒执行一次

        CC.send("&a事件调度器已启动");
    }
    
    /**
     * 停止事件调度器
     */
    public void stop() {
        if (schedulerTask != null) {
            schedulerTask.cancel();
            schedulerTask = null;
            CC.send("&c事件调度器已停止");
        }
        
        // 停止当前事件
        eventManager.stopCurrentEvent();
    }
    
    /**
     * 检查是否可以触发事件
     */
    private boolean canTriggerEvent() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastEvent = (currentTime - lastEventTime) / 1000; // 转换为秒
        
        if (firstEvent) {
            // 首次事件延迟
            return timeSinceLastEvent >= config.getFirstEventDelay();
        } else {
            // 事件冷却时间
            return timeSinceLastEvent >= config.getEventCooldown();
        }
    }
    
    /**
     * 获取距离下次事件的时间（秒）
     */
    public int getTimeUntilNextEvent() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastEvent = (currentTime - lastEventTime) / 1000;
        
        int delay = firstEvent ? config.getFirstEventDelay() : config.getEventCooldown();
        int remaining = delay - (int) timeSinceLastEvent;
        
        return Math.max(0, remaining);
    }
    
    /**
     * 立即触发事件（用于测试或管理员命令）
     */
    public void triggerEventNow() {
        if (eventManager.isEventActive()) {
            CC.warn("&c已有事件正在进行中");
            return;
        }
        
        eventManager.startRandomEvent();
        lastEventTime = System.currentTimeMillis();
        firstEvent = false;
    }
    
    /**
     * 重置调度器
     */
    public void reset() {
        stop();
        firstEvent = true;
        lastEventTime = 0;
    }
}
