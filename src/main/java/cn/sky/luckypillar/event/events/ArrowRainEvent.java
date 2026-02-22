package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * 箭雨事件
 * 从天空向玩家位置降下箭矢
 */
public class ArrowRainEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private final Random random;
    
    public ArrowRainEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
    }
    
    @Override
    public String getId() {
        return "arrow-rain";
    }
    
    @Override
    public String getName() {
        return "箭雨";
    }
    
    @Override
    public boolean isEnabled() {
        return config.isArrowRainEnabled();
    }
    
    @Override
    public int getDuration() {
        return config.getArrowRainDuration();
    }
    
    @Override
    public void onStart(LuckyPillarGame game) {
        // 事件开始时不做额外操作
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        // 每tick向存活玩家位置降下箭矢
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (!player.isOnline()) {
                continue;
            }
            
            // 根据配置的概率决定是否生成箭
            if (random.nextDouble() < config.getArrowChance()) {
                spawnArrow(player.getBukkitPlayer().getLocation());
            }
        }
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束时不做额外操作
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 箭雨！");
        game.broadcast("&e小心头顶的箭矢！");
    }
    
    /**
     * 在指定位置上方生成箭
     */
    private void spawnArrow(Location playerLoc) {
        if (playerLoc.getWorld() == null) {
            return;
        }
        
        // 在玩家上方20格随机位置生成箭
        double offsetX = (random.nextDouble() - 0.5) * 10;
        double offsetZ = (random.nextDouble() - 0.5) * 10;
        
        Location arrowLoc = playerLoc.clone().add(offsetX, 20, offsetZ);
        
        // 生成箭并设置向下的速度
        Arrow arrow = playerLoc.getWorld().spawnArrow(
            arrowLoc,
            new Vector(0, -1, 0),
            (float) config.getArrowSpeed(),
            5.0f
        );
        
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
    }
}
