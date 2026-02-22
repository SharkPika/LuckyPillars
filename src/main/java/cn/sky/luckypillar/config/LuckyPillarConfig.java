package cn.sky.luckypillar.config;

import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.config.Configuration;
import cn.sky.luckypillar.utils.config.annotation.ConfigData;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * SkyLuckyPillar 游戏配置类
 * 映射 game.yml 文件
 */
@Getter
public class LuckyPillarConfig extends Configuration {

    // 游戏基础配置
    @ConfigData("game.name")
    private final String gameName = "SkyLuckyPillar";

    @ConfigData("game.map")
    private final String mapName = "world";

    @ConfigData("game.min-players")
    private final int minPlayers = 2;

    @ConfigData("game.countdown")
    private final int countdown = 10;

    @ConfigData("game.game-timeout")
    private final int gameTimeout = 1800;

    // 柱子配置
    @ConfigData("pillars.default-platform-size")
    private final int defaultPlatformSize = 1;

    @ConfigData("pillars.default-platform-material")
    private final String defaultPlatformMaterial = "BEDROCK";

    @ConfigData("pillars.default-height")
    private final int defaultHeight = 72;

    // 物品配置
    @ConfigData("items.ban-list")
    private final List<String> banList = List.of("AIR", "WATER", "LAVA", "BARRIER");

    // 事件配置
    @ConfigData("events.cooldown")
    private final int eventCooldown = 120;

    @ConfigData("events.first-event-delay")
    private final int firstEventDelay = 60;

    // 怪物狂潮事件
    @ConfigData("events.monster-frenzy.enabled")
    private final boolean monsterFrenzyEnabled = true;

    @ConfigData("events.monster-frenzy.duration")
    private final int monsterFrenzyDuration = 30;

    @ConfigData("events.monster-frenzy.monsters-per-player")
    private final int monstersPerPlayer = 6;

    @ConfigData("events.monster-frenzy.monster-types")
    private final List<String> monsterTypes = List.of("ZOMBIE", "SKELETON", "SPIDER", "CREEPER");

    // 箭雨事件
    @ConfigData("events.arrow-rain.enabled")
    private final boolean arrowRainEnabled = true;

    @ConfigData("events.arrow-rain.duration")
    private final int arrowRainDuration = 20;

    @ConfigData("events.arrow-rain.arrow-chance")
    private final double arrowChance = 0.3;

    @ConfigData("events.arrow-rain.arrow-speed")
    private final double arrowSpeed = 1.0;

    // 岩浆上升事件
    @ConfigData("events.lava-rise.enabled")
    private final boolean lavaRiseEnabled = true;

    @ConfigData("events.lava-rise.duration")
    private final int lavaRiseDuration = 40;

    @ConfigData("events.lava-rise.rise-speed")
    private final int lavaRiseSpeed = 1;

    @ConfigData("events.lava-rise.max-height")
    private final int lavaMaxHeight = 100;

    // 方块衰减事件
    @ConfigData("events.block-decay.enabled")
    private final boolean blockDecayEnabled = true;

    @ConfigData("events.block-decay.duration")
    private final int blockDecayDuration = 30;

    @ConfigData("events.block-decay.decay-chance")
    private final double decayChance = 0.05;

    @ConfigData("events.block-decay.exclude-materials")
    private final List<String> excludeMaterials = List.of("BEDROCK", "OBSIDIAN");

    // 空投补给事件
    @ConfigData("events.supply-drop.enabled")
    private final boolean supplyDropEnabled = true;

    @ConfigData("events.supply-drop.duration")
    private final int supplyDropDuration = 5;

    @ConfigData("events.supply-drop.drop-count")
    private final int dropCount = 3;

    @ConfigData("events.supply-drop.items-per-drop")
    private final int itemsPerDrop = 5;

    // 游戏规则
    @ConfigData("rules.void-death-height")
    private final int voidDeathHeight = -64;

    @ConfigData("rules.keep-inventory")
    private final boolean keepInventory = false;

    @ConfigData("rules.drop-items-on-death")
    private final boolean dropItemsOnDeath = true;

    @ConfigData("rules.max-build-height")
    private final int maxBuildHeight = 256;

    @ConfigData("rules.min-build-height")
    private final int minBuildHeight = 0;

    @ConfigData("rules.allow-block-break")
    private final boolean allowBlockBreak = true;

    @ConfigData("rules.allow-block-place")
    private final boolean allowBlockPlace = true;

    @ConfigData("rules.pvp-enabled")
    private final boolean pvpEnabled = true;

    @ConfigData("rules.friendly-fire")
    private final boolean friendlyFire = false;

    @ConfigData("rules.hunger-enabled")
    private final boolean hungerEnabled = true;

    @ConfigData("rules.natural-regeneration")
    private final boolean naturalRegeneration = true;

    @ConfigData("rules.fall-damage")
    private final boolean fallDamage = true;

    @ConfigData("rules.fire-damage")
    private final boolean fireDamage = true;

    // 观战模式
    @ConfigData("spectator.allow-flight")
    private final boolean spectatorAllowFlight = true;

    @ConfigData("spectator.allow-teleport")
    private final boolean spectatorAllowTeleport = true;

    @ConfigData("spectator.restrict-to-game-area")
    private final boolean restrictToGameArea = true;

    @ConfigData("spectator.show-alive-players")
    private final boolean showAlivePlayers = true;

    public LuckyPillarConfig(JavaPlugin plugin) {
        super(plugin, "game.yml");
    }

    public void validate() {
        // 验证玩家数量
        if (minPlayers <= 0) {
            throw new IllegalArgumentException("最小玩家数必须大于 0 当前值: " + minPlayers);
        }

        // 验证倒计时
        if (countdown <= 0) {
            throw new IllegalArgumentException("倒计时必须大于 0，当前值: " + countdown);
        }

        // 验证游戏超时
        if (gameTimeout <= 0) {
            throw new IllegalArgumentException("游戏超时时间必须大于 0，当前值: " + gameTimeout);
        }

        // 验证柱子配置
        if (defaultPlatformSize <= 0) {
            throw new IllegalArgumentException("平台大小必须大于 0，当前值: " + defaultPlatformSize);
        }

        if (defaultHeight <= 0) {
            throw new IllegalArgumentException("柱子高度必须大于 0，当前值: " + defaultHeight);
        }

        // 验证事件配置
        if (eventCooldown < 0) {
            throw new IllegalArgumentException("事件冷却时间不能为负数，当前值: " + eventCooldown);
        }

        if (firstEventDelay < 0) {
            throw new IllegalArgumentException("首次事件延迟不能为负数，当前值: " + firstEventDelay);
        }

        // 验证概率值
        if (arrowChance < 0 || arrowChance > 1) {
            throw new IllegalArgumentException("箭雨概率必须在 0-1 之间，当前值: " + arrowChance);
        }

        if (decayChance < 0 || decayChance > 1) {
            throw new IllegalArgumentException("方块衰减概率必须在 0-1 之间，当前值: " + decayChance);
        }

        // 验证高度限制
        if (minBuildHeight >= maxBuildHeight) {
            throw new IllegalArgumentException("最小建造高度必须小于最大建造高度，当前值: min=" + minBuildHeight + ", max=" + maxBuildHeight);
        }

        CC.send("&a配置文件加载成功");
    }

    /**
     * 加载配置并验证
     */
    public void onLoad() {
        super.onLoad();
        validate();
    }
}
