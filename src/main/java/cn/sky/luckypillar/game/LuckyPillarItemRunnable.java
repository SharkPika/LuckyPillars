package cn.sky.luckypillar.game;

import cn.sky.luckypillar.SkyLuckyPillar;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

public class LuckyPillarItemRunnable extends BukkitRunnable {

    private static final Random random = new Random();
    private static final Material[] filter = Arrays.stream(Material.values()).filter(m -> m.isItem() && !SkyLuckyPillar.getInstance().getGame().getConfig().getBanList().contains(m.name())).toArray(Material[]::new);

    @Override
    public void run() {
        LuckyPillarGame game = SkyLuckyPillar.getInstance().getGame();
        for (LuckyPillarPlayer player : game.getAlivePlayers()) {
            if (player.getBukkitPlayer().getInventory().firstEmpty() == -1) continue;
            player.giveItem(new ItemStack(filter[random.nextInt(filter.length)]));
        }
    }
}
