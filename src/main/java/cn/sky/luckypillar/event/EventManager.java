package cn.sky.luckypillar.event;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * 事件管理器
 * 负责注册、触发和管理游戏事件
 */
@Getter
@ToString
public class EventManager {

    private final JavaPlugin plugin;
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;
    private final Map<String, GameEvent> registeredEvents;
    private final Random random;

    private GameEvent currentEvent;
    private GameEvent lastTriggeredEvent; // 防止连续触发相同事件
    private BukkitTask eventTask;
    private int eventTickCounter;

    public EventManager(JavaPlugin plugin, LuckyPillarGame game, SkyConfig skyConfig) {
        this.plugin = plugin;
        this.game = game;
        this.skyConfig = skyConfig;
        this.registeredEvents = new HashMap<>();
        this.random = new Random();
    }

    /**
     * 注册事件
     */
    public void registerEvent(String id, GameEvent event) {
        registeredEvents.put(id, event);
        CC.send("&f已注册事件: &a" + event.getName() + " &b(" + id + ")");
    }

    /**
     * 触发随机事件
     */
    public void startRandomEvent() {
        if (isEventActive()) {
            CC.warn("&c已有事件正在进行中，无法触发新事件");
            return;
        }

        GameEvent event = selectRandomEvent();
        if (event == null) {
            CC.warn("&c没有可用的事件");
            return;
        }

        startEvent(event, false);
    }

    /**
     * 触发指定事件
     */
    public void startEvent(GameEvent event, boolean force) {
        if (isEventActive() && !force) {
            CC.warn("&c已有事件正在进行中");
            return;
        }

        if (force && currentEvent != null) {
            // 调用当前事件结束
            currentEvent.onEnd(game);
            Map<String, String> p = new HashMap<>();
            p.put("event_name", currentEvent.getName());
            game.broadcast(skyConfig.format(skyConfig.getEventEnded(), p));
            CC.send("&a事件 " + currentEvent.getName() + " 已结束");
            if (eventTask != null) {
                eventTask.cancel();
                eventTask = null;
            }
        }

        currentEvent = event;
        eventTickCounter = 0;

        // 宣布事件开始
        event.announce(game);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("event_name", event.getName());
        placeholders.put("duration", String.valueOf(event.getDuration()));
        game.broadcast(skyConfig.format(skyConfig.getEventStarted(), placeholders));
        game.getTitleManager().showEventStartTitleToAll(event.getName());

        // 调用事件开始
        event.onStart(game);

        // 启动事件tick任务
        eventTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (currentEvent == null) {
                stopCurrentEvent();
                return;
            }

            eventTickCounter++;

            // 调用事件tick
            currentEvent.onTick(game);

            // 检查是否到达持续时间
            if (eventTickCounter >= currentEvent.getDuration()) {
                stopCurrentEvent();
            }
        }, 20L, 20L); // 每秒执行一次

        CC.send("&a事件 " + event.getName() + " 已开始");
    }

    /**
     * 停止当前事件
     */
    public void stopCurrentEvent() {
        if (currentEvent == null) {
            return;
        }

        // 调用事件结束
        currentEvent.onEnd(game);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("event_name", currentEvent.getName());
        game.broadcast(skyConfig.format(skyConfig.getEventEnded(), placeholders));

        CC.send("&a事件 " + currentEvent.getName() + " 已结束");

        // 取消任务
        if (eventTask != null) {
            eventTask.cancel();
            eventTask = null;
        }

        currentEvent = null;
        eventTickCounter = 0;
    }

    /**
     * 随机选择一个启用的事件
     */
    public GameEvent selectRandomEvent() {
        // 首先尝试排除上次触发的事件，避免连续重复
        List<GameEvent> enabledEvents = registeredEvents.values().stream()
                .filter(GameEvent::isEnabled)
                .filter(e -> e != lastTriggeredEvent)
                .toList();

        // 如果过滤后没有可用事件，允许重复
        if (enabledEvents.isEmpty()) {
            enabledEvents = registeredEvents.values().stream()
                    .filter(GameEvent::isEnabled)
                    .toList();
        }

        if (enabledEvents.isEmpty()) {
            return null;
        }

        GameEvent selected = enabledEvents.get(random.nextInt(enabledEvents.size()));
        lastTriggeredEvent = selected;
        return selected;
    }

    /**
     * 检查是否有事件正在进行
     */
    public boolean isEventActive() {
        return currentEvent != null;
    }

    /**
     * 获取当前事件名称
     */
    public String getCurrentEventName() {
        return currentEvent != null ? currentEvent.getName() : "无";
    }

    /**
     * 获取当前事件剩余时间
     */
    public int getRemainingTime() {
        if (currentEvent == null) {
            return 0;
        }
        return currentEvent.getDuration() - eventTickCounter;
    }

    /**
     * 清理所有事件
     */
    public void cleanup() {
        stopCurrentEvent();
        registeredEvents.clear();
    }
}
