package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;

import java.util.Random;

public class DragonSpawnEvent implements GameEvent {
    @Override
    public String getId() {
        return "dragon_spawn";
    }

    @Override
    public String getName() {
        return "生成末影龙";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        LuckyPillarPlayer lpPlayer = game.getAlivePlayers().get(new Random().nextInt(game.getAlivePlayers().size()));
        EnderDragon dragon = game.getGameWorld().spawn(lpPlayer.getBukkitPlayer().getLocation(), EnderDragon.class);
        dragon.setPhase(EnderDragon.Phase.HOVER);

    }

    @Override
    public void onTick(LuckyPillarGame game) {

    }

    @Override
    public void onEnd(LuckyPillarGame game) {

    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&5&l[事件] 生成末影龙！");
        game.broadcast("&e末影龙已生成 快躲避它！");
    }
}
