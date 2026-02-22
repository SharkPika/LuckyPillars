package cn.sky.luckypillar.display;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 标题管理器
 */
public class TitleManager {

    private final JavaPlugin plugin;
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;

    public TitleManager(JavaPlugin plugin, LuckyPillarGame game, SkyConfig skyConfig) {
        this.plugin = plugin;
        this.game = game;
        this.skyConfig = skyConfig;
    }

    /**
     * 显示游戏开始标题
     */
    public void showGameStartTitle(Player player) {
        String title = CC.translate(skyConfig.getTitleGameStartTitle());
        String subtitle = CC.translate(skyConfig.getTitleGameStartSubtitle());

        player.sendTitle(
                title,
                subtitle,
                skyConfig.getTitleGameStartFadeIn(),
                skyConfig.getTitleGameStartStay(),
                skyConfig.getTitleGameStartFadeOut()
        );
    }

    /**
     * 显示玩家被击杀标题
     */
    public void showPlayerKilledTitle(Player player, String killerName) {
        String title = CC.translate(skyConfig.getTitlePlayerKilledTitle());
        String subtitle = CC.translate(skyConfig.getTitlePlayerKilledSubtitle()
                .replace("%killer%", killerName));

        player.sendTitle(
                title,
                subtitle,
                skyConfig.getTitlePlayerKilledFadeIn(),
                skyConfig.getTitlePlayerKilledStay(),
                skyConfig.getTitlePlayerKilledFadeOut()
        );
    }

    /**
     * 显示胜利标题
     */
    public void showVictoryTitle(Player player) {
        String title = CC.translate(skyConfig.getTitleVictoryTitle());
        String subtitle = CC.translate(skyConfig.getTitleVictorySubtitle());

        player.sendTitle(
                title,
                subtitle,
                skyConfig.getTitleVictoryFadeIn(),
                skyConfig.getTitleVictoryStay(),
                skyConfig.getTitleVictoryFadeOut()
        );
    }

    /**
     * 向所有玩家显示胜利标题
     */
    public void showVictoryTitleToAll() {
        String title = CC.translate(skyConfig.getTitleVictoryTitle());
        String subtitle = CC.translate(skyConfig.getTitleVictorySubtitle());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    title,
                    subtitle,
                    skyConfig.getTitleVictoryFadeIn(),
                    skyConfig.getTitleVictoryStay(),
                    skyConfig.getTitleVictoryFadeOut()
            );

        }
    }

    /**
     * 显示事件开始标题
     */
    public void showEventStartTitle(Player player, String eventName) {
        String title = CC.translate(skyConfig.getTitleEventStartTitle());
        String subtitle = CC.translate(skyConfig.getTitleEventStartSubtitle()
                .replace("%event_name%", eventName));

        player.sendTitle(
                title,
                subtitle,
                skyConfig.getTitleEventStartFadeIn(),
                skyConfig.getTitleEventStartStay(),
                skyConfig.getTitleEventStartFadeOut()
        );
    }

    /**
     * 向所有玩家显示游戏开始标题
     */
    public void showGameStartTitleToAll() {
        for (LuckyPillarPlayer lpPlayer : game.getAllPlayers()) {
            if (lpPlayer.isOnline()) {
                showGameStartTitle(lpPlayer.getBukkitPlayer());
            }
        }
    }

    /**
     * 向所有玩家显示事件开始标题
     */
    public void showEventStartTitleToAll(String eventName) {
        for (LuckyPillarPlayer lpPlayer : game.getAllPlayers()) {
            if (lpPlayer.isOnline()) {
                showEventStartTitle(lpPlayer.getBukkitPlayer(), eventName);
            }
        }
    }

    /**
     * 清除玩家标题
     */
    public void clearTitle(Player player) {
        player.sendTitle("", "", 0, 0, 0);
    }
}
