package cn.sky.luckypillar.game;

import cn.sky.luckypillar.SkyLuckyPillar;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class LuckyPillarItemRunnable extends BukkitRunnable {

    private static final Random RANDOM = new Random();

    @Override
    public void run() {
        LuckyPillarGame game = SkyLuckyPillar.getInstance().getGame();
        Material[] available = Arrays.stream(Material.values())
                .filter(Material::isItem)
                .filter(material -> !game.getConfig().getBanList().contains(material.name()))
                .toArray(Material[]::new);

        if (available.length == 0) {
            return;
        }

        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) {
                continue;
            }
            player.giveItem(new ItemStack(available[RANDOM.nextInt(available.length)]));
        }
    }
}
