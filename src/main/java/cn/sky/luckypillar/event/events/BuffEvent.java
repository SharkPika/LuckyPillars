package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 增益 Buff 事件
 * 给所有存活玩家随机施加一个正面增益效果
 */
public class BuffEvent implements GameEvent {

    private final LuckyPillarConfig config;
    private final Random random;
    private PotionEffectType appliedEffect;

    // 可用的增益效果列表（使用 getByName 兼容不同版本）
    private static final List<PotionEffectType> BUFF_TYPES = new ArrayList<>();
    private static final List<String> BUFF_NAMES = new ArrayList<>();

    static {
        addBuff("SPEED", "速度提升");
        addBuff("INCREASE_DAMAGE", "力量增强");
        addBuff("DAMAGE_RESISTANCE", "伤害抗性");
        addBuff("JUMP", "跳跃提升");
        addBuff("REGENERATION", "生命恢复");
        addBuff("FIRE_RESISTANCE", "火焰抗性");
    }

    @SuppressWarnings("deprecation")
    private static void addBuff(String name, String displayName) {
        PotionEffectType type = PotionEffectType.getByName(name);
        if (type != null) {
            BUFF_TYPES.add(type);
            BUFF_NAMES.add(displayName);
        }
    }

    public BuffEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
    }

    @Override
    public String getId() {
        return "buff";
    }

    @Override
    public String getName() {
        return "增益 Buff";
    }

    @Override
    public boolean isEnabled() {
        return config.isBuffEventEnabled() && !BUFF_TYPES.isEmpty();
    }

    @Override
    public int getDuration() {
        return config.getBuffEventDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        if (BUFF_TYPES.isEmpty())
            return;

        // 随机选择一种增益效果
        int index = random.nextInt(BUFF_TYPES.size());
        appliedEffect = BUFF_TYPES.get(index);
        String buffName = BUFF_NAMES.get(index);

        // 给所有存活玩家施加效果
        PotionEffect effect = new PotionEffect(appliedEffect, getDuration() * 20, 1, true, true);
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (player.isOnline()) {
                player.getBukkitPlayer().addPotionEffect(effect);
            }
        }

        game.broadcast("§a§l全体增益: §e" + buffName + " II §7持续 " + getDuration() + " 秒");
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        // 不做额外操作
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 移除增益效果
        if (appliedEffect != null) {
            for (LuckyPillarPlayer player : game.getAlivePlayers()) {
                if (player.isOnline()) {
                    player.getBukkitPlayer().removePotionEffect(appliedEffect);
                }
            }
        }
        appliedEffect = null;
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§a§l[事件] 增益 Buff！");
        game.broadcast("§e全体玩家将获得随机增益效果！");
    }
}
