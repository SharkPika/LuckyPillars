package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.utils.compat.VersionCompat;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 位置交换事件
 * 随机打乱所有存活玩家的位置
 */
public class ShuffleEvent implements GameEvent {

    private final LuckyPillarConfig config;

    public ShuffleEvent(LuckyPillarConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "shuffle";
    }

    @Override
    public String getName() {
        return "位置交换";
    }

    @Override
    public boolean isEnabled() {
        return config.isShuffleEventEnabled();
    }

    @Override
    public int getDuration() {
        // 瞬时事件，持续 1 秒
        return 1;
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        List<LuckyPillarPlayer> alive = game.getAlivePlayers();
        if (alive.size() < 2)
            return;

        // 收集所有存活玩家位置
        List<Location> locations = new ArrayList<>();
        for (LuckyPillarPlayer player : alive) {
            if (player.isOnline()) {
                locations.add(player.getBukkitPlayer().getLocation().clone());
            }
        }

        // 打乱位置列表
        Collections.shuffle(locations);

        // 将玩家传送到打乱后的位置
        int i = 0;
        for (LuckyPillarPlayer player : alive) {
            if (player.isOnline() && i < locations.size()) {
                player.getBukkitPlayer().teleport(locations.get(i));
                // 使用 VersionCompat 兼容不同版本的传送音效
                VersionCompat.playTeleportSound(player.getBukkitPlayer());
                i++;
            }
        }

        game.broadcast("§d所有玩家的位置已被随机交换！");
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        // 瞬时事件，不做额外操作
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 瞬时事件，不做额外操作
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§d§l[事件] 位置交换！");
        game.broadcast("§e所有玩家的位置即将被打乱！");
    }
}
