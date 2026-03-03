package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 宝藏雨事件
 * 从天空掉落随机物品实体，玩家可以争抢
 */
public class TreasureRainEvent implements GameEvent {

    private final LuckyPillarConfig config;

    // 宝藏物品列表（有价值的物品）
    private static final Material[] TREASURE_ITEMS = {
            Material.DIAMOND, Material.GOLDEN_APPLE, Material.IRON_INGOT,
            Material.ENDER_PEARL, Material.ARROW, Material.COOKED_BEEF,
            Material.IRON_SWORD, Material.BOW, Material.SHIELD,
            Material.COBBLESTONE, Material.GOLDEN_CARROT,
            Material.IRON_HELMET, Material.IRON_BOOTS
    };

    public TreasureRainEvent(LuckyPillarConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "treasure-rain";
    }

    @Override
    public String getName() {
        return "宝藏雨";
    }

    @Override
    public boolean isEnabled() {
        return config.isTreasureRainEnabled();
    }

    @Override
    public int getDuration() {
        return config.getTreasureRainDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        // 开始时不做额外操作
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        if (game.getCenter() == null)
            return;

        Location center = game.getCenter();
        int itemsPerTick = config.getTreasureRainItemsPerTick();

        for (int i = 0; i < itemsPerTick; i++) {
            // 在游戏区域中心附近随机位置掉落物品
            double offsetX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 40;
            double offsetZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 40;
            Location dropLoc = center.clone().add(offsetX, 25, offsetZ);

            if (dropLoc.getWorld() == null)
                continue;

            // 随机选择宝藏物品
            Material mat = TREASURE_ITEMS[ThreadLocalRandom.current().nextInt(TREASURE_ITEMS.length)];
            ItemStack item = new ItemStack(mat, 1);

            dropLoc.getWorld().dropItemNaturally(dropLoc, item);
        }
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束不做额外操作
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§a§l[事件] 宝藏雨！");
        game.broadcast("§e宝物从天而降 快去争抢！");
    }
}
