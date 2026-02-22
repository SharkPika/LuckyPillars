package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Random;

/**
 * 怪物狂潮事件
 * 在每个存活玩家附近生成大量怪物
 */
public class MonsterFrenzyEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private final Random random;
    
    public MonsterFrenzyEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
    }
    
    @Override
    public String getId() {
        return "monster-frenzy";
    }
    
    @Override
    public String getName() {
        return "怪物狂潮";
    }
    
    @Override
    public boolean isEnabled() {
        return config.isMonsterFrenzyEnabled();
    }
    
    @Override
    public int getDuration() {
        return config.getMonsterFrenzyDuration();
    }
    
    @Override
    public void onStart(LuckyPillarGame game) {
        // 在每个存活玩家附近生成怪物
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            
            Location playerLoc = player.getBukkitPlayer().getLocation();
            int monsterCount = config.getMonstersPerPlayer();
            
            for (int i = 0; i < monsterCount; i++) {
                spawnRandomMonster(playerLoc);
            }
        }
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        // 每tick不做额外操作
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束时不做额外操作
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 怪物狂潮！");
        game.broadcast("&e大量怪物正在接近...");
    }
    
    /**
     * 在指定位置附近生成随机怪物
     */
    private void spawnRandomMonster(Location center) {
        if (center.getWorld() == null) {
            return;
        }
        
        // 在玩家周围10格内随机位置生成
        double offsetX = (random.nextDouble() - 0.5) * 20;
        double offsetZ = (random.nextDouble() - 0.5) * 20;
        
        Location spawnLoc = center.clone().add(offsetX, 0, offsetZ);
        spawnLoc.setY(center.getY());
        
        // 随机选择怪物类型
        List<String> monsterTypes = config.getMonsterTypes();
        if (monsterTypes.isEmpty()) {
            return;
        }
        
        String monsterType = monsterTypes.get(random.nextInt(monsterTypes.size()));
        
        try {
            EntityType entityType = EntityType.valueOf(monsterType.toUpperCase());
            center.getWorld().spawnEntity(spawnLoc, entityType);
        } catch (IllegalArgumentException e) {
            // 无效的实体类型
        }
    }
}
