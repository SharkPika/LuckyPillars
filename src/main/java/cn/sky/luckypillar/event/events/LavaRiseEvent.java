package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.pillar.Pillar;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

public class LavaRiseEvent implements GameEvent {

    private final LuckyPillarConfig config;
    private final Set<Block> placedLavaBlocks;
    private int currentHeight;

    public LavaRiseEvent(LuckyPillarConfig config) {
        this.config = config;
        this.placedLavaBlocks = new HashSet<>();
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
        currentHeight = config.getVoidDeathHeight();
        placedLavaBlocks.clear();
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        currentHeight += config.getLavaRiseSpeed();
        if (currentHeight > config.getLavaMaxHeight()) {
            return;
        }

        for (Pillar pillar : game.getPillarManager().getPillars()) {
            placeLavaLayer(pillar, currentHeight);
        }
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        for (Block block : placedLavaBlocks) {
            if (block.getType() == Material.LAVA) {
                block.setType(Material.AIR);
            }
        }
        placedLavaBlocks.clear();
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&c&l[事件] 岩浆上升！");
        game.broadcast("&e岩浆正在从下方蔓延，快去高处！");
    }

    private void placeLavaLayer(Pillar pillar, int height) {
        Location center = pillar.getBottomCenter();
        World world = center.getWorld();
        if (world == null) {
            return;
        }

        int radius = pillar.getPlatformSize() + 5;
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance > radius) {
                    continue;
                }

                Block block = world.getBlockAt(centerX + x, height, centerZ + z);
                if (block.getType() == Material.AIR) {
                    block.setType(Material.LAVA);
                    placedLavaBlocks.add(block);
                }
            }
        }
    }
}
