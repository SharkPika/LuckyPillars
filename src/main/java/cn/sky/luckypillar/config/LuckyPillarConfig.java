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
    private String gameName = "SkyLuckyPillar";

    @ConfigData("game.map")
    private String mapName = "world";

    @ConfigData("game.min-players")
    private int minPlayers = 2;

    @ConfigData("game.countdown")
    private int countdown = 10;

    @ConfigData("game.game-timeout")
    private int gameTimeout = 1800;

    @ConfigData("game.item-countdown")
    private int itemCountdown = 5;

    // 柱子配置
    @ConfigData("pillars.default-platform-size")
    private int defaultPlatformSize = 1;

    @ConfigData("pillars.default-platform-material")
    private String defaultPlatformMaterial = "BEDROCK";

    @ConfigData("pillars.default-height")
    private int defaultHeight = 72;

    // 物品配置
    @ConfigData("items.ban-list")
    private List<String> banList = List.of("AIR", "WATER", "LAVA", "BARRIER");

    // 事件配置
    @ConfigData("events.cooldown")
    private int eventCooldown = 120;

    @ConfigData("events.first-event-delay")
    private int firstEventDelay = 60;

    // 怪物狂潮事件
    @ConfigData("events.monster-frenzy.enabled")
    private boolean monsterFrenzyEnabled = true;

    @ConfigData("events.monster-frenzy.duration")
    private int monsterFrenzyDuration = 30;

    @ConfigData("events.monster-frenzy.monsters-per-player")
    private int monstersPerPlayer = 6;

    @ConfigData("events.monster-frenzy.monster-types")
    private List<String> monsterTypes = List.of("ZOMBIE", "SKELETON", "SPIDER", "CREEPER");

    // 箭雨事件
    @ConfigData("events.arrow-rain.enabled")
    private boolean arrowRainEnabled = true;

    @ConfigData("events.arrow-rain.duration")
    private int arrowRainDuration = 20;

    @ConfigData("events.arrow-rain.arrow-chance")
    private double arrowChance = 0.3;

    @ConfigData("events.arrow-rain.arrow-speed")
    private double arrowSpeed = 1.0;

    // 岩浆上升事件
    @ConfigData("events.lava-rise.enabled")
    private boolean lavaRiseEnabled = true;

    @ConfigData("events.lava-rise.duration")
    private int lavaRiseDuration = 40;

    @ConfigData("events.lava-rise.rise-speed")
    private int lavaRiseSpeed = 1;

    @ConfigData("events.lava-rise.max-height")
    private int lavaMaxHeight = 100;

    // 方块衰减事件
    @ConfigData("events.block-decay.enabled")
    private boolean blockDecayEnabled = true;

    @ConfigData("events.block-decay.duration")
    private int blockDecayDuration = 30;

    @ConfigData("events.block-decay.decay-chance")
    private double decayChance = 0.05;

    @ConfigData("events.block-decay.exclude-materials")
    private List<String> excludeMaterials = List.of("BEDROCK", "OBSIDIAN");

    // 空投补给事件
    @ConfigData("events.supply-drop.enabled")
    private boolean supplyDropEnabled = true;

    @ConfigData("events.supply-drop.duration")
    private int supplyDropDuration = 5;

    @ConfigData("events.supply-drop.drop-count")
    private int dropCount = 3;

    @ConfigData("events.supply-drop.items-per-drop")
    private int itemsPerDrop = 5;

    // 游戏规则
    @ConfigData("rules.void-death-height")
    private int voidDeathHeight = -64;

    @ConfigData("rules.keep-inventory")
    private boolean keepInventory = false;

    @ConfigData("rules.drop-items-on-death")
    private boolean dropItemsOnDeath = true;

    @ConfigData("rules.max-build-height")
    private int maxBuildHeight = 256;

    @ConfigData("rules.min-build-height")
    private int minBuildHeight = 0;

    @ConfigData("rules.allow-block-break")
    private boolean allowBlockBreak = true;

    @ConfigData("rules.allow-block-place")
    private boolean allowBlockPlace = true;

    @ConfigData("rules.pvp-enabled")
    private boolean pvpEnabled = true;

    @ConfigData("rules.friendly-fire")
    private boolean friendlyFire = false;

    @ConfigData("rules.hunger-enabled")
    private boolean hungerEnabled = true;

    @ConfigData("rules.natural-regeneration")
    private boolean naturalRegeneration = true;

    @ConfigData("rules.fall-damage")
    private boolean fallDamage = true;

    @ConfigData("rules.fire-damage")
    private boolean fireDamage = true;

    // 观战模式
    @ConfigData("spectator.allow-flight")
    private boolean spectatorAllowFlight = true;

    @ConfigData("spectator.allow-teleport")
    private boolean spectatorAllowTeleport = true;

    @ConfigData("spectator.restrict-to-game-area")
    private boolean restrictToGameArea = true;

    @ConfigData("spectator.show-alive-players")
    private boolean showAlivePlayers = true;

    // 增益 Buff 事件
    @ConfigData("events.buff.enabled")
    private boolean buffEventEnabled = true;

    @ConfigData("events.buff.duration")
    private int buffEventDuration = 15;

    // TNT 雨事件
    @ConfigData("events.tnt-rain.enabled")
    private boolean tntRainEnabled = true;

    @ConfigData("events.tnt-rain.duration")
    private int tntRainDuration = 15;

    @ConfigData("events.tnt-rain.spawn-chance")
    private double tntRainChance = 0.15;

    // 暗夜降临事件
    @ConfigData("events.darkness.enabled")
    private boolean darknessEventEnabled = true;

    @ConfigData("events.darkness.duration")
    private int darknessEventDuration = 15;

    // 位置交换事件
    @ConfigData("events.shuffle.enabled")
    private boolean shuffleEventEnabled = true;

    // 宝藏雨事件
    @ConfigData("events.treasure-rain.enabled")
    private boolean treasureRainEnabled = true;

    @ConfigData("events.treasure-rain.duration")
    private int treasureRainDuration = 15;

    @ConfigData("events.treasure-rain.items-per-tick")
    private int treasureRainItemsPerTick = 3;

    // 边界收缩事件
    @ConfigData("events.border-shrink.enabled")
    private boolean borderShrinkEnabled = true;

    @ConfigData("events.border-shrink.duration")
    private int borderShrinkDuration = 60;

    @ConfigData("events.border-shrink.initial-size")
    private double borderInitialSize = 200;

    @ConfigData("events.border-shrink.min-size")
    private double borderMinSize = 30;

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
            throw new IllegalArgumentException(
                    "最小建造高度必须小于最大建造高度，当前值: min=" + minBuildHeight + ", max=" + maxBuildHeight);
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
