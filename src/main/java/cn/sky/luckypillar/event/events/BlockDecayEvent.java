package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.pillar.PlatformBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 方块衰减事件
 * 随机移除平台上的方块
 */
public class BlockDecayEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private final Random random;
    
    public BlockDecayEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
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
        // 事件开始时不做额外操作
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        // 每tick随机移除一些平台方块
        PlatformBuilder builder = game.getPillarManager().getPlatformBuilder();
        
        for (Pillar pillar : game.getPillarManager().getPillars()) {
            // 根据配置的概率决定是否移除方块
            if (random.nextDouble() < config.getDecayChance()) {
                // 获取排除的材质列表
                List<Material> excludeMaterials = new ArrayList<>();
                for (String materialName : config.getExcludeMaterials()) {
                    try {
                        excludeMaterials.add(Material.valueOf(materialName.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        // 无效的材质名称
                    }
                }
                
                builder.removeRandomBlock(pillar, excludeMaterials);
            }
        }
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束时不做额外操作（不恢复方块）
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 方块衰减！");
        game.broadcast("&e平台正在崩塌 小心脚下！");
    }
}
