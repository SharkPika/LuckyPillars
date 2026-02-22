package cn.sky.luckypillar.display;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Boss血条管理器
 */
public class BossBarManager {
    
    private final JavaPlugin plugin;
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;
    private BossBar currentBossBar;
    
    public BossBarManager(JavaPlugin plugin, LuckyPillarGame game, SkyConfig skyConfig) {
        this.plugin = plugin;
        this.game = game;
        this.skyConfig = skyConfig;
    }
    
    /**
     * 显示倒计时Boss血条
     */
    public void showCountdownBar(int countdown, int maxCountdown) {
        removeCurrentBar();
        
        String title = CC.translate(skyConfig.getBossbarCountdownTitle()
            .replace("%countdown%", String.valueOf(countdown)));
        
        BarColor color = parseColor(skyConfig.getBossbarCountdownColor());
        BarStyle style = parseStyle(skyConfig.getBossbarCountdownStyle());
        
        currentBossBar = Bukkit.createBossBar(title, color, style);
        currentBossBar.setProgress((double) countdown / maxCountdown);
        
        // 添加所有玩家
        for (LuckyPillarPlayer lpPlayer : game.getAllPlayers()) {
            if (lpPlayer.isOnline()) {
                currentBossBar.addPlayer(lpPlayer.getBukkitPlayer());
            }
        }
    }
    
    /**
     * 显示事件Boss血条
     */
    public void showEventBar(String eventName, int remainingTime, int totalTime) {
        removeCurrentBar();
        
        String title = CC.translate(skyConfig.getBossbarEventTitle()
            .replace("%event_name%", eventName)
            .replace("%time%", String.valueOf(remainingTime)));
        
        BarColor color = parseColor(skyConfig.getBossbarEventColor());
        BarStyle style = parseStyle(skyConfig.getBossbarEventStyle());
        
        currentBossBar = Bukkit.createBossBar(title, color, style);
        currentBossBar.setProgress((double) remainingTime / totalTime);
        
        // 添加所有玩家
        for (LuckyPillarPlayer lpPlayer : game.getAllPlayers()) {
            if (lpPlayer.isOnline()) {
                currentBossBar.addPlayer(lpPlayer.getBukkitPlayer());
            }
        }
    }
    
    /**
     * 更新Boss血条进度
     */
    public void updateProgress(double progress) {
        if (currentBossBar != null) {
            currentBossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        }
    }
    
    /**
     * 更新Boss血条标题
     */
    public void updateTitle(String title) {
        if (currentBossBar != null) {
            currentBossBar.setTitle(CC.translate(title));
        }
    }
    
    /**
     * 移除当前Boss血条
     */
    public void removeCurrentBar() {
        if (currentBossBar != null) {
            currentBossBar.removeAll();
            currentBossBar = null;
        }
    }
    
    /**
     * 添加玩家到Boss血条
     */
    public void addPlayer(Player player) {
        if (currentBossBar != null) {
            currentBossBar.addPlayer(player);
        }
    }
    
    /**
     * 从Boss血条移除玩家
     */
    public void removePlayer(Player player) {
        if (currentBossBar != null) {
            currentBossBar.removePlayer(player);
        }
    }
    
    /**
     * 解析颜色
     */
    private BarColor parseColor(String colorStr) {
        try {
            return BarColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarColor.RED;
        }
    }
    
    /**
     * 解析样式
     */
    private BarStyle parseStyle(String styleStr) {
        try {
            return BarStyle.valueOf(styleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BarStyle.SOLID;
        }
    }
    
    /**
     * 清理
     */
    public void cleanup() {
        removeCurrentBar();
    }
}
