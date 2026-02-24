package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 箭雨事件
 * 从天空向玩家位置降下箭矢
 */
public class ArrowRainEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private List<Location> arrowLocs;
    
    public ArrowRainEvent(LuckyPillarConfig config) {
        this.config = config;
        this.arrowLocs = null;
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
        if (game.getFarthest() == null) return;

        if (arrowLocs == null) {
            Location center = game.getCenter();
            Location farthest = game.getFarthest();

            double cx = center.getX();
            double cz = center.getZ();
            double fx = farthest.getX();
            double fz = farthest.getZ();
            double y = farthest.getY();

            double dx = fx - cx;
            double dz = fz - cz;
            double dist = Math.sqrt(dx * dx + dz * dz);

            double normX = dx / dist;
            double normZ = dz / dist;

            double newDist = dist + 15;

            double newFx = cx + newDist * normX;
            double newFz = cz + newDist * normZ;

            double newSx = cx - newDist * normX;
            double newSz = cz - newDist * normZ;

            arrowLocs = game.getRectanglePoints(
                    new Location(game.getGameWorld(), newFx, y + 25, newFz),
                    new Location(game.getGameWorld(), newSx, y + 25, newSz)
            );
        }
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        if (arrowLocs == null || arrowLocs.isEmpty()) return;

        for (Location arrowLoc : arrowLocs) {
            if (ThreadLocalRandom.current().nextDouble() < config.getArrowChance()) {
                this.spawnArrow(arrowLoc);
            }
        }
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 箭雨！");
        game.broadcast("&e小心头顶的箭矢！");
    }
    
    /**
     * 在指定位置上方生成箭
     */
    private void spawnArrow(Location arrowLoc) {
        if (arrowLoc == null || arrowLoc.getWorld() == null) {
            return;
        }
        Arrow arrow = arrowLoc.getWorld().spawnArrow(
            arrowLoc,
            new Vector(0, -1, 0),
            (float) config.getArrowSpeed(),
            5.0f
        );
        
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
    }
}
