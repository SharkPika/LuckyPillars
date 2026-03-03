package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TNT 雨事件
 * 从天空向玩家位置随机掉落点燃的 TNT
 */
public class TNTRainEvent implements GameEvent {

    private final LuckyPillarConfig config;

    public TNTRainEvent(LuckyPillarConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "tnt-rain";
    }

    @Override
    public String getName() {
        return "TNT 雨";
    }

    @Override
    public boolean isEnabled() {
        return config.isTntRainEnabled();
    }

    @Override
    public int getDuration() {
        return config.getTntRainDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        // 开始时不做额外操作
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        // 每 tick 在存活玩家上方随机掉落 TNT
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (!player.isOnline())
                continue;

            if (ThreadLocalRandom.current().nextDouble() < config.getTntRainChance()) {
                Location playerLoc = player.getBukkitPlayer().getLocation();
                // 在玩家上方 15~25 格随机偏移位置生成 TNT
                double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 16;
                double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 16;
                Location spawnLoc = playerLoc.clone().add(offsetX, 20, offsetZ);

                if (spawnLoc.getWorld() != null) {
                    TNTPrimed tnt = spawnLoc.getWorld().spawn(spawnLoc, TNTPrimed.class);
                    tnt.setFuseTicks(60); // 3 秒引线
                }
            }
        }
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束不做额外操作
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§c§l[事件] TNT 雨！");
        game.broadcast("§e炸弹从天而降 快找掩护！");
    }
}
