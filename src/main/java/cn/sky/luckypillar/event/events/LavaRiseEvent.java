package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.pillar.Pillar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * 岩浆上升事件
 * 岩浆从底部逐渐上升
 */
public class LavaRiseEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private int currentHeight;
    
    public LavaRiseEvent(LuckyPillarConfig config) {
        this.config = config;
    }
    
    @Override
    public String getId() {
        return "lava-rise";
    }
    
    @Override
    public String getName() {
        return "岩浆上升";
    }
    
    @Override
    public boolean isEnabled() {
        return config.isLavaRiseEnabled();
    }
    
    @Override
    public int getDuration() {
        return config.getLavaRiseDuration();
    }
    
    @Override
    public void onStart(LuckyPillarGame game) {
        // 从虚空高度开始
        currentHeight = config.getVoidDeathHeight();
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        // 每tick上升指定格数
        currentHeight += config.getLavaRiseSpeed();
        
        // 检查是否达到最大高度
        if (currentHeight > config.getLavaMaxHeight()) {
            return;
        }
        
        // 在所有柱子底部周围放置岩浆
        for (Pillar pillar : game.getPillarManager().getPillars()) {
            placeLavaLayer(pillar, currentHeight);
        }
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
        // 清除岩浆
        for (Pillar pillar : game.getPillarManager().getPillars()) {
            clearLava(pillar);
        }
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 岩浆上升！");
        game.broadcast("&e岩浆正在从底部上升 快往高处跑！");
    }
    
    /**
     * 在指定高度放置岩浆层
     */
    private void placeLavaLayer(Pillar pillar, int height) {
        Location center = pillar.getBottomCenter();
        World world = center.getWorld();
        
        if (world == null) {
            return;
        }
        
        int radius = pillar.getPlatformSize() + 5; // 比平台稍大
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                
                if (distance <= radius) {
                    Location loc = new Location(world, centerX + x, height, centerZ + z);
                    if (loc.getBlock().getType() == Material.AIR) {
                        loc.getBlock().setType(Material.LAVA);
                    }
                }
            }
        }
    }
    
    /**
     * 清除岩浆
     */
    private void clearLava(Pillar pillar) {
        Location center = pillar.getBottomCenter();
        World world = center.getWorld();
        
        if (world == null) {
            return;
        }
        
        int radius = pillar.getPlatformSize() + 5;
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();
        
        for (int y = config.getVoidDeathHeight(); y <= config.getLavaMaxHeight(); y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    
                    if (distance <= radius) {
                        Location loc = new Location(world, centerX + x, y, centerZ + z);
                        if (loc.getBlock().getType() == Material.LAVA) {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}
