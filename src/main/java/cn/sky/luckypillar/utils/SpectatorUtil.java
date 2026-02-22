package cn.sky.luckypillar.utils;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * 观战工具类
 */
public class SpectatorUtil {
    
    /**
     * 设置玩家为观战模式
     */
    public static void setSpectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setInvulnerable(true);
    }
    
    /**
     * 恢复玩家为生存模式
     */
    public static void setSurvival(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setInvulnerable(false);
    }
    
    /**
     * 检查玩家是否在观战模式
     */
    public static boolean isSpectator(Player player) {
        return player.getGameMode() == GameMode.SPECTATOR;
    }
}
