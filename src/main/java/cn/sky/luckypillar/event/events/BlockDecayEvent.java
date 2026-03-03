package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.pillar.PlatformBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockDecayEvent implements GameEvent {

    private final LuckyPillarConfig config;
    private final Random random;
    private final List<Material> excludeMaterials;

    public BlockDecayEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
        this.excludeMaterials = new ArrayList<>();
    }

    @Override
    public String getId() {
        return "block-decay";
    }

    @Override
    public String getName() {
        return "方块衰减";
    }

    @Override
    public boolean isEnabled() {
        return config.isBlockDecayEnabled();
    }

    @Override
    public int getDuration() {
        return config.getBlockDecayDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        excludeMaterials.clear();
        for (String materialName : config.getExcludeMaterials()) {
            try {
                excludeMaterials.add(Material.valueOf(materialName.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        PlatformBuilder builder = game.getPillarManager().getPlatformBuilder();

        for (Pillar pillar : game.getPillarManager().getPillars()) {
            if (random.nextDouble() < config.getDecayChance()) {
                builder.removeRandomBlock(pillar, excludeMaterials);
            }
        }
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 方块衰减！");
        game.broadcast("&e平台正在崩塌 小心脚下！");
    }
}
