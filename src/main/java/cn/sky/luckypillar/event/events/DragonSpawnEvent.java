package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import org.bukkit.entity.EnderDragon;

public class DragonSpawnEvent implements GameEvent {

    private EnderDragon spawnedDragon;

    @Override
    public String getId() {
        return "dragon_spawn";
    }

    @Override
    public String getName() {
        return "末影龙出没";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getDuration() {
        return 60;
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        spawnedDragon = game.getGameWorld().spawn(game.getCenter(), EnderDragon.class);
        spawnedDragon.setPhase(EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET);
    }

    @Override
    public void onTick(LuckyPillarGame game) {

    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束时清理末影龙
        if (spawnedDragon != null && !spawnedDragon.isDead()) {
            spawnedDragon.remove();
        }
        spawnedDragon = null;
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&5&l[事件] 生成末影龙！");
        game.broadcast("&e末影龙已生成 快躲避它！");
    }
}
