package cn.sky.luckypillar;

import cn.sky.luckypillar.command.LuckyPillarCommand;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.display.BossBarManager;
import cn.sky.luckypillar.display.TitleManager;
import cn.sky.luckypillar.event.EventManager;
import cn.sky.luckypillar.event.EventScheduler;
import cn.sky.luckypillar.event.events.*;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.listener.*;
import cn.sky.luckypillar.scoreboard.Scoreboard;
import cn.sky.luckypillar.utils.WorldUtil;
import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.scoreboard.Assemble;
import cn.sky.luckypillar.utils.version.VersionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;

@Getter
public class SkyLuckyPillar extends JavaPlugin {

    @Getter
    public static SkyLuckyPillar instance;

    private LuckyPillarGame game;

    private LuckyPillarConfig gameConfig;
    private SkyConfig skyConfig;

    private EventManager eventManager;
    private EventScheduler eventScheduler;

    private BossBarManager bossBarManager;
    private TitleManager titleManager;

    private Scoreboard scoreboard;
    private Assemble assemble;

    @Override
    public void onLoad() {
        instance = this;

        VersionManager.init();

        this.skyConfig = new SkyConfig(this);
        this.gameConfig = new LuckyPillarConfig(this);

        try {
            this.skyConfig.onLoad();
            this.gameConfig.onLoad();
        } catch (Exception e) {
            CC.send("&c配置加载失败", e);
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.loadMap();
    }

    @Override
    public void onEnable() {
        // 初始化游戏
        this.game = new LuckyPillarGame(this, this.gameConfig, this.skyConfig);

        // 初始化显示管理器
        this.bossBarManager = new BossBarManager(this, this.game, this.skyConfig);
        this.titleManager = new TitleManager(this.game, this.skyConfig);

        this.game.setBossBarManager(this.bossBarManager);
        this.game.setTitleManager(this.titleManager);

        // 初始化事件系统
        this.eventManager = new EventManager(this, this.game, this.skyConfig);
        this.eventScheduler = new EventScheduler(this, this.game, this.eventManager, this.gameConfig);

        // 将事件调度器注入到游戏中
        this.game.setEventScheduler(this.eventScheduler);

        // 注册游戏事件
        this.registerGameEvents();

        // 注册监听器
        this.registerListeners();

        // 注册命令
        this.registerCommands();

        this.scoreboard = new Scoreboard(this.game, this.skyConfig);
        this.assemble = new Assemble(this, this.scoreboard);
        this.assemble.setTicks(4);

        CC.send("&bSkyLuckyPillar &a插件加载成功");
        CC.send("&f游戏地图: &e" + this.gameConfig.getMapName());
        CC.send("&f柱子数量: &e" + this.game.getPillarManager().getPillarCount());
        CC.send("&f作者 &bpi_ka");
    }

    @Override
    public void onDisable() {
        if (this.assemble != null) {
            this.assemble.cleanup();
        }

        if (this.eventScheduler != null) {
            this.eventScheduler.stop();
        }

        if (this.eventManager != null) {
            this.eventManager.cleanup();
        }

        Bukkit.getScheduler().cancelTasks(this);

        CC.send("&bSkyLuckyPillar &c插件卸载成功");
        CC.send("&f作者 &bpi_ka");
    }

    private void loadMap() {
        CC.send("加载地图...");
        try {
            File configWorld = new File(this.getDataFolder().getPath() + "/" + this.gameConfig.getMapName());
            File world = new File("./" + this.gameConfig.getMapName());
            if (!configWorld.exists() || configWorld.listFiles().length == 0) {
                configWorld.mkdirs();
                if (world.exists() || world.listFiles().length > 0) {
                    WorldUtil.copyDir(world.getPath(), configWorld.getPath());
                    CC.send("&a地图加载成功");
                } else {
                    CC.warn("&c无法找到任何有效地图");
                }
                return;
            }
            /*for (File file : configWorld.listFiles()) {
                File file2;
                String[] split = file.getPath().split("/");
                if (split.length == 1) {
                    split = file.getPath().split("\\\\");
                }
                if (!(file2 = new File("./" + split[split.length - 1])).isDirectory()) continue;
                WorldUtil.deleteDir(file2);
            }*/
            Files.deleteIfExists(world.toPath());
            world.mkdirs();
            WorldUtil.copyDir(configWorld.getPath(), world.getPath());
            CC.send("&a地图加载成功");
        } catch (Throwable e) {
            CC.sendError("&f加载世界错误: ", e);
        }
    }

    /**
     * 注册游戏事件
     */
    private void registerGameEvents() {
        this.eventManager.registerEvent("monster-frenzy", new MonsterFrenzyEvent(this.gameConfig));
        this.eventManager.registerEvent("arrow-rain", new ArrowRainEvent(this.gameConfig));
        this.eventManager.registerEvent("lava-rise", new LavaRiseEvent(this.gameConfig));
        this.eventManager.registerEvent("block-decay", new BlockDecayEvent(this.gameConfig));
        //this.eventManager.registerEvent("supply-drop", new SupplyDropEvent(gameConfig));

        CC.send("&f已注册 &b" + this.eventManager.getRegisteredEvents().size() + " &f个游戏事件");
    }

    /**
     * 注册监听器
     */
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this.game), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(this.game), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(this.game, this.skyConfig), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this.game, this.skyConfig), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);

        CC.send("&f已注册 &b5 &f个事件监听器");
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        LuckyPillarCommand commandExecutor = new LuckyPillarCommand(this.game, this.skyConfig);
        this.getCommand("luckypillar").setExecutor(commandExecutor);
        this.getCommand("lpillar").setExecutor(commandExecutor);
        this.getCommand("pillar").setExecutor(commandExecutor);

        CC.send("&f已注册命令: &a/luckypillar, /lpillar, /pillar");
    }

}