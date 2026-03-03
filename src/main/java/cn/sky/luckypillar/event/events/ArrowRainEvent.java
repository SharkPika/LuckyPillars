package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowRainEvent implements GameEvent {

    private static final int BASE_ARROWS_PER_PLAYER = 6;
    private static final int MAX_ARROWS_PER_TICK = 120;

    private final LuckyPillarConfig config;
    private final Random random;

    private World world;
    private Location center;
    private double spawnRadius;
    private double spawnY;

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
        if (game.getCenter() == null || game.getFarthest() == null || game.getGameWorld() == null) {
            world = null;
            center = null;
            return;
        }

        world = game.getGameWorld();
        center = game.getCenter().clone();

        double dx = game.getFarthest().getX() - center.getX();
        double dz = game.getFarthest().getZ() - center.getZ();
        spawnRadius = Math.max(8.0, Math.sqrt(dx * dx + dz * dz) + 10.0);
        spawnY = game.getFarthest().getY() + 25.0;
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        if (world == null || center == null) {
            return;
        }

        double chance = Math.max(0.0, Math.min(1.0, config.getArrowChance()));
        if (chance <= 0.0) {
            return;
        }

        int alivePlayers = Math.max(1, game.getAlivePlayers().size());
        int arrowsPerTick = (int) Math.ceil(alivePlayers * BASE_ARROWS_PER_PLAYER * chance);
        arrowsPerTick = Math.max(1, Math.min(MAX_ARROWS_PER_TICK, arrowsPerTick));

        for (int i = 0; i < arrowsPerTick; i++) {
            spawnArrow(randomSpawnLocation());
        }
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        world = null;
        center = null;
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 箭雨来袭！");
        game.broadcast("&e小心头顶落下的箭矢！");
    }

    private Location randomSpawnLocation() {
        double angle = random.nextDouble() * Math.PI * 2;
        double distance = Math.sqrt(random.nextDouble()) * spawnRadius;

        double x = center.getX() + Math.cos(angle) * distance;
        double z = center.getZ() + Math.sin(angle) * distance;

        return new Location(world, x, spawnY, z);
    }

    private void spawnArrow(Location arrowLoc) {
        Arrow arrow = world.spawnArrow(
                arrowLoc,
                new Vector(0, -1, 0),
                (float) config.getArrowSpeed(),
                5.0f
        );
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
    }
}
