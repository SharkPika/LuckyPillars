package cn.sky.luckypillar.config;

import cn.sky.luckypillar.utils.config.Configuration;
import cn.sky.luckypillar.utils.config.annotation.ConfigData;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

/**
 * 消息配置类
 * 映射 config.yml 文件
 */
@Getter
public class SkyConfig extends Configuration {

    // 服务器信息
    @ConfigData("server")
    private final String server = "yourserver.ip";

    // 聊天格式
    @ConfigData("chatformat.waiting")
    private final String chatFormatWaiting = "&7[等待] &f%player_name%: %message%";

    @ConfigData("chatformat.playing")
    private final String chatFormatPlaying = "&a[游戏中] &f%player_name%: %message%";

    @ConfigData("chatformat.spectating")
    private final String chatFormatSpectating = "&7[观战] &8%player_name%: %message%";

    @ConfigData("chatformat.dead")
    private final String chatFormatDead = "&c[已死亡] &7%player_name%: %message%";

    // 游戏状态消息
    @ConfigData("messages.game-starting")
    private final String gameStarting = "&a游戏将在 &e%countdown% &a秒后开始！";

    @ConfigData("messages.game-started")
    private final String gameStarted = "&a游戏开始！祝你好运！";

    @ConfigData("messages.game-ended")
    private final String gameEnded = "&e游戏结束！";

    @ConfigData("messages.game-winner")
    private final String gameWinner = "&6恭喜 &e%winner% &6获得胜利！";

    @ConfigData("messages.game-timeout")
    private final String gameTimeout = "&c游戏超时！没有胜利者！";

    // 玩家消息
    @ConfigData("messages.player-joined")
    private final String playerJoined = "&a%player% &7加入了游戏 &8[&e%current%&7/&e%max%&8]";

    @ConfigData("messages.player-left")
    private final String playerLeft = "&c%player% &7离开了游戏";

    @ConfigData("messages.player-killed")
    private final String playerKilled = "&c%player% &7被 &e%killer% &7击杀";

    @ConfigData("messages.player-died")
    private final String playerDied = "&c%player% &7死亡：%reason%";

    @ConfigData("messages.player-eliminated")
    private final String playerEliminated = "&c%player% &7被淘汰！剩余 &e%remaining% &7名玩家";

    // 事件消息
    @ConfigData("messages.event-starting")
    private final String eventStarting = "&6[事件] &e%event_name% &6即将开始！";

    @ConfigData("messages.event-started")
    private final String eventStarted = "&6[事件] &e%event_name% &6已开始！持续 &c%duration% &6秒";

    @ConfigData("messages.event-ended")
    private final String eventEnded = "&6[事件] &e%event_name% &6已结束！";

    // 错误消息
    @ConfigData("messages.not-enough-players")
    private final String notEnoughPlayers = "&c玩家数量不足！至少需要 &e%min% &c名玩家";

    @ConfigData("messages.game-full")
    private final String gameFull = "&c游戏已满！";

    @ConfigData("messages.game-in-progress")
    private final String gameInProgress = "&c游戏正在进行中！";

    @ConfigData("messages.no-permission")
    private final String noPermission = "&c你没有权限执行此命令！";

    // 命令消息
    @ConfigData("messages.pillar-added")
    private final String pillarAdded = "&a成功添加柱子 &e%id%";

    @ConfigData("messages.pillar-removed")
    private final String pillarRemoved = "&a成功移除柱子 &e%id%";

    @ConfigData("messages.config-reloaded")
    private final String configReloaded = "&a配置已重新加载！";

    // 计分板配置
    @ConfigData("scoreboard.title")
    private final String scoreboardTitle = "&6&l幸运柱子";

    @ConfigData("scoreboard.lines.waiting")
    private final List<String> scoreboardWaiting = List.of(
            "&7&m-------------------",
            "&e游戏: &fLuckyPillar",
            "",
            "&e状态: &a等待中",
            "&e玩家: &f%players%/%max%",
            "",
            "&7&m-------------------"
    );

    @ConfigData("scoreboard.lines.playing")
    private final List<String> scoreboardPlaying = List.of(
            "&7&m-------------------",
            "&e游戏: &fLuckyPillar",
            "",
            "&e存活: &a%alive%",
            "&e击杀: &c%kills%",
            "&e时间: &f%time%",
            "",
            "&e当前事件:",
            "&f%event%",
            "",
            "&7&m-------------------"
    );

    @ConfigData("scoreboard.lines.dead")
    private final List<String> scoreboardDead = List.of(
            "&7&m-------------------",
            "&e游戏: &fLuckyPillar",
            "",
            "&c你已被淘汰",
            "",
            "&e存活: &a%alive%",
            "&e你的击杀: &c%kills%",
            "",
            "&7&m-------------------"
    );

    @ConfigData("scoreboard.lines.spectating")
    private final List<String> scoreboardSpectating = List.of(
            "&7&m-------------------",
            "&e游戏: &fLuckyPillar",
            "",
            "&7观战中",
            "",
            "&e存活: &a%alive%",
            "",
            "&7&m-------------------"
    );

    // Boss血条配置
    @ConfigData("bossbar.event.color")
    private final String bossbarEventColor = "RED";

    @ConfigData("bossbar.event.style")
    private final String bossbarEventStyle = "SOLID";

    @ConfigData("bossbar.event.title")
    private final String bossbarEventTitle = "&c[事件] &e%event_name% &7- &f%time%s";

    @ConfigData("bossbar.countdown.color")
    private final String bossbarCountdownColor = "GREEN";

    @ConfigData("bossbar.countdown.style")
    private final String bossbarCountdownStyle = "SEGMENTED_10";

    @ConfigData("bossbar.countdown.title")
    private final String bossbarCountdownTitle = "&a游戏开始倒计时: &e%countdown%";

    // 标题配置
    @ConfigData("titles.game-start.title")
    private final String titleGameStartTitle = "&a游戏开始";

    @ConfigData("titles.game-start.subtitle")
    private final String titleGameStartSubtitle = "&e祝你好运！";

    @ConfigData("titles.game-start.fade-in")
    private final int titleGameStartFadeIn = 10;

    @ConfigData("titles.game-start.stay")
    private final int titleGameStartStay = 40;

    @ConfigData("titles.game-start.fade-out")
    private final int titleGameStartFadeOut = 10;

    @ConfigData("titles.player-killed.title")
    private final String titlePlayerKilledTitle = "&c你被击杀了";

    @ConfigData("titles.player-killed.subtitle")
    private final String titlePlayerKilledSubtitle = "&7击杀者: &e%killer%";

    @ConfigData("titles.player-killed.fade-in")
    private final int titlePlayerKilledFadeIn = 10;

    @ConfigData("titles.player-killed.stay")
    private final int titlePlayerKilledStay = 60;

    @ConfigData("titles.player-killed.fade-out")
    private final int titlePlayerKilledFadeOut = 20;

    @ConfigData("titles.victory.title")
    private final String titleVictoryTitle = "&6&l胜利！";

    @ConfigData("titles.victory.subtitle")
    private final String titleVictorySubtitle = "&e你是最后的幸存者！";

    @ConfigData("titles.victory.fade-in")
    private final int titleVictoryFadeIn = 10;

    @ConfigData("titles.victory.stay")
    private final int titleVictoryStay = 80;

    @ConfigData("titles.victory.fade-out")
    private final int titleVictoryFadeOut = 20;

    @ConfigData("titles.event-start.title")
    private final String titleEventStartTitle = "&6事件开始";

    @ConfigData("titles.event-start.subtitle")
    private final String titleEventStartSubtitle = "&e%event_name%";

    @ConfigData("titles.event-start.fade-in")
    private final int titleEventStartFadeIn = 5;

    @ConfigData("titles.event-start.stay")
    private final int titleEventStartStay = 40;

    @ConfigData("titles.event-start.fade-out")
    private final int titleEventStartFadeOut = 10;

    // 音效配置
    @ConfigData("sounds.game-start")
    private final String soundGameStart = "ENTITY_PLAYER_LEVELUP";

    @ConfigData("sounds.game-end")
    private final String soundGameEnd = "UI_TOAST_CHALLENGE_COMPLETE";

    @ConfigData("sounds.player-death")
    private final String soundPlayerDeath = "ENTITY_PLAYER_DEATH";

    @ConfigData("sounds.player-kill")
    private final String soundPlayerKill = "ENTITY_EXPERIENCE_ORB_PICKUP";

    @ConfigData("sounds.event-start")
    private final String soundEventStart = "ENTITY_ENDER_DRAGON_GROWL";

    @ConfigData("sounds.countdown")
    private final String soundCountdown = "BLOCK_NOTE_BLOCK_PLING";

    public SkyConfig(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }

    /**
     * 格式化消息，替换占位符
     */
    public String format(String message, Map<String, String> placeholders) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }
}
