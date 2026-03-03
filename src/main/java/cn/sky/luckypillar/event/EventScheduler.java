package cn.sky.luckypillar.event;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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

    public void start() {
        if (schedulerTask != null) {
            CC.send("&c事件调度器已在运行");
            return;
        }

        lastEventTime = System.currentTimeMillis();
        firstEvent = true;

        schedulerTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
                return;
            }
            if (eventManager.isEventActive()) {
                return;
            }

            if (canTriggerEvent()) {
                eventManager.startRandomEvent();
                lastEventTime = System.currentTimeMillis();
                firstEvent = false;
            }
        }, 20L, 20L);

        CC.send("&a事件调度器已启动");
    }

    public void stop() {
        if (schedulerTask != null) {
            schedulerTask.cancel();
            schedulerTask = null;
            CC.send("&c事件调度器已停止");
        }

        eventManager.stopCurrentEvent();
    }

    private boolean canTriggerEvent() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastEvent = (currentTime - lastEventTime) / 1000;
        int cooldown = getCurrentCooldownSeconds(currentTime);
        return timeSinceLastEvent >= cooldown;
    }

    public int getTimeUntilNextEvent() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastEvent = (currentTime - lastEventTime) / 1000;
        int cooldown = getCurrentCooldownSeconds(currentTime);
        return Math.max(0, cooldown - (int) timeSinceLastEvent);
    }

    private int getCurrentCooldownSeconds(long currentTime) {
        if (firstEvent) {
            return config.getFirstEventDelay();
        }

        long gameElapsed = Math.max(0, (currentTime - game.getGameStartTime()) / 1000);
        int minutesPlayed = (int) (gameElapsed / 60);
        return Math.max(30, config.getEventCooldown() - minutesPlayed * 15);
    }

    public void triggerEventNow() {
        if (eventManager.isEventActive()) {
            CC.warn("&c当前已有事件正在进行");
            return;
        }

        eventManager.startRandomEvent();
        lastEventTime = System.currentTimeMillis();
        firstEvent = false;
    }

    public void reset() {
        stop();
        firstEvent = true;
        lastEventTime = 0;
    }
}
