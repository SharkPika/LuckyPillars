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
    private String server = "yourserver.ip";

    // 聊天格式
    @ConfigData("chatformat.waiting")
    private String chatFormatWaiting = "&7[等待] &f%player_name%: %message%";

    @ConfigData("chatformat.playing")
    private String chatFormatPlaying = "&a[游戏中] &f%player_name%: %message%";

    @ConfigData("chatformat.spectating")
    private String chatFormatSpectating = "&7[观战] &8%player_name%: %message%";

    @ConfigData("chatformat.dead")
    private String chatFormatDead = "&c[已死亡] &7%player_name%: %message%";

    // 游戏状态消息
    @ConfigData("messages.game-starting")
    private String gameStarting = "&a游戏将在 &e%countdown% &a秒后开始！";

    @ConfigData("messages.game-started")
    private String gameStarted = "&a游戏开始！祝你好运！";

    @ConfigData("messages.game-ended")
    private String gameEnded = "&e游戏结束！";

    @ConfigData("messages.game-winner")
    private String gameWinner = "&6恭喜 &e%winner% &6获得胜利！";

    @ConfigData("messages.game-timeout")
    private String gameTimeout = "&c游戏超时！没有胜利者！";

    // 玩家消息
    @ConfigData("messages.player-joined")
    private String playerJoined = "&a%player% &7加入了游戏 &8[&e%current%&7/&e%max%&8]";

    @ConfigData("messages.player-left")
    private String playerLeft = "&c%player% &7离开了游戏";

    @ConfigData("messages.player-killed")
    private String playerKilled = "&c%player% &7被 &e%killer% &7击杀";

    @ConfigData("messages.player-died")
    private String playerDied = "&c%player% &7死亡：%reason%";

    @ConfigData("messages.player-eliminated")
    private String playerEliminated = "&c%player% &7被淘汰！剩余 &e%remaining% &7名玩家";

    // 事件消息
    @ConfigData("messages.event-starting")
    private String eventStarting = "&6[事件] &e%event_name% &6即将开始！";

    @ConfigData("messages.event-started")
    private String eventStarted = "&6[事件] &e%event_name% &6已开始！持续 &c%duration% &6秒";

    @ConfigData("messages.event-ended")
    private String eventEnded = "&6[事件] &e%event_name% &6已结束！";

    // 错误消息
    @ConfigData("messages.not-enough-players")
    private String notEnoughPlayers = "&c玩家数量不足！至少需要 &e%min% &c名玩家";

    @ConfigData("messages.game-full")
    private String gameFull = "&c游戏已满！";

    @ConfigData("messages.game-in-progress")
    private String gameInProgress = "&c游戏正在进行中！";

    @ConfigData("messages.no-permission")
    private String noPermission = "&c你没有权限执行此命令！";

    // 命令消息
    @ConfigData("messages.pillar-added")
    private String pillarAdded = "&a成功添加柱子 &e%id%";

    @ConfigData("messages.pillar-removed")
    private String pillarRemoved = "&a成功移除柱子 &e%id%";

    @ConfigData("messages.config-reloaded")
    private String configReloaded = "&a配置已重新加载！";

    // 计分板配置
    @ConfigData("scoreboard.title")
    private String scoreboardTitle = "&6&l幸运柱子";

    @ConfigData("scoreboard.lines.waiting")
    private List<String> scoreboardWaiting = List.of(
            "&7&m-------------------",
            "&e游戏: &fLuckyPillar",
            "",
            "&e状态: &a等待中",
            "&e玩家: &f%players%/%max%",
            "",
            "&7&m-------------------"
    );

    @ConfigData("scoreboard.lines.playing")
    private List<String> scoreboardPlaying = List.of(
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

    @ConfigData("scoreboard.lines.ending")
    private List<String> scoreboardEnding = List.of(
            "&7&m-------------------",
            "&e地图: &f%map%",
            "",
            "&e击杀: &c%kills%",
            "&6胜利者: &e%winner%",
            "",
            "&c&l游戏结束",
            "",
            "&7&m-------------------"
    );

    @ConfigData("scoreboard.lines.dead")
    private List<String> scoreboardDead = List.of(
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
    private List<String> scoreboardSpectating = List.of(
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
    private String bossbarEventColor = "RED";

    @ConfigData("bossbar.event.style")
    private String bossbarEventStyle = "SOLID";

    @ConfigData("bossbar.event.title")
    private String bossbarEventTitle = "&c[事件] &e%event_name% &7- &f%time%s";

    @ConfigData("bossbar.countdown.color")
    private String bossbarCountdownColor = "GREEN";

    @ConfigData("bossbar.countdown.style")
    private String bossbarCountdownStyle = "SEGMENTED_10";

    @ConfigData("bossbar.countdown.title")
    private String bossbarCountdownTitle = "&a游戏开始倒计时: &e%countdown%";

    @ConfigData("titles.game-start.title")
    private String titleGameStartTitle = "&a游戏开始";

    @ConfigData("titles.game-start.subtitle")
    private String titleGameStartSubtitle = "&e祝你好运！";

    @ConfigData("titles.game-start.fade-in")
    private int titleGameStartFadeIn = 10;

    @ConfigData("titles.game-start.stay")
    private int titleGameStartStay = 40;

    @ConfigData("titles.game-start.fade-out")
    private int titleGameStartFadeOut = 10;

    @ConfigData("titles.player-killed.title")
    private String titlePlayerKilledTitle = "&c你被击杀了";

    @ConfigData("titles.player-killed.subtitle")
    private String titlePlayerKilledSubtitle = "&7击杀者: &e%killer%";

    @ConfigData("titles.player-killed.fade-in")
    private int titlePlayerKilledFadeIn = 10;

    @ConfigData("titles.player-killed.stay")
    private int titlePlayerKilledStay = 60;

    @ConfigData("titles.player-killed.fade-out")
    private int titlePlayerKilledFadeOut = 20;

    @ConfigData("titles.victory.title")
    private String titleVictoryTitle = "&6&l胜利！";

    @ConfigData("titles.victory.subtitle")
    private String titleVictorySubtitle = "&e你是最后的幸存者！";

    @ConfigData("titles.victory.fade-in")
    private int titleVictoryFadeIn = 10;

    @ConfigData("titles.victory.stay")
    private int titleVictoryStay = 80;

    @ConfigData("titles.victory.fade-out")
    private int titleVictoryFadeOut = 20;

    @ConfigData("titles.game-over.title")
    private String titleGameOverTitle = "&c&l游戏结束";

    @ConfigData("titles.game-over.subtitle")
    private String titleGameOverSubtitle;

    @ConfigData("titles.game-over.fade-in")
    private int titleGameOverFadeIn = 10;

    @ConfigData("titles.game-over.stay")
    private int titleGameOverStay = 80;

    @ConfigData("titles.game-over.fade-out")
    private int titleGameOverFadeOut = 20;

    @ConfigData("titles.event-start.title")
    private String titleEventStartTitle = "&6事件开始";

    @ConfigData("titles.event-start.subtitle")
    private String titleEventStartSubtitle = "&e%event_name%";

    @ConfigData("titles.event-start.fade-in")
    private int titleEventStartFadeIn = 5;

    @ConfigData("titles.event-start.stay")
    private int titleEventStartStay = 40;

    @ConfigData("titles.event-start.fade-out")
    private int titleEventStartFadeOut = 10;

    // 音效配置
    @ConfigData("sounds.game-start")
    private String soundGameStart = "ENTITY_PLAYER_LEVELUP";

    @ConfigData("sounds.game-end")
    private String soundGameEnd = "UI_TOAST_CHALLENGE_COMPLETE";

    @ConfigData("sounds.player-death")
    private String soundPlayerDeath = "ENTITY_PLAYER_DEATH";

    @ConfigData("sounds.player-kill")
    private String soundPlayerKill = "ENTITY_EXPERIENCE_ORB_PICKUP";

    @ConfigData("sounds.event-start")
    private String soundEventStart = "ENTITY_ENDER_DRAGON_GROWL";

    @ConfigData("sounds.countdown")
    private String soundCountdown = "BLOCK_NOTE_BLOCK_PLING";

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
