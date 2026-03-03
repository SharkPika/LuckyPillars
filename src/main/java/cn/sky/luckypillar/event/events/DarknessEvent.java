package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 失明黑暗事件
 * 给所有存活玩家施加失明效果
 */
public class DarknessEvent implements GameEvent {

    private final LuckyPillarConfig config;

    public DarknessEvent(LuckyPillarConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "darkness";
    }

    @Override
    public String getName() {
        return "暗夜降临";
    }

    @Override
    public boolean isEnabled() {
        return config.isDarknessEventEnabled();
    }

    @Override
    public int getDuration() {
        return config.getDarknessEventDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        // 给所有存活玩家施加失明效果
        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, getDuration() * 20, 0, true, false);
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (player.isOnline()) {
                player.getBukkitPlayer().addPotionEffect(blindness);
            }
        }
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        // 不做额外操作
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 移除失明效果
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (player.isOnline()) {
                player.getBukkitPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§5§l[事件] 暗夜降临！");
        game.broadcast("§e黑暗笼罩了一切 小心周围的敌人！");
    }
}
