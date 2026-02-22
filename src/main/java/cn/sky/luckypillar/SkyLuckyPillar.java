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
import cn.sky.luckypillar.listener.BlockListener;
import cn.sky.luckypillar.listener.ChatListener;
import cn.sky.luckypillar.listener.DamageListener;
import cn.sky.luckypillar.listener.PlayerListener;
import cn.sky.luckypillar.scoreboard.Scoreboard;
import cn.sky.luckypillar.utils.WorldUtil;
import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.scoreboard.Assemble;
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
        skyConfig = new SkyConfig(this);
        gameConfig = new LuckyPillarConfig(this);

        try {
            skyConfig.onLoad();
            gameConfig.onLoad();
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
        game = new LuckyPillarGame(this, gameConfig, skyConfig);

        // 初始化显示管理器
        bossBarManager = new BossBarManager(this, game, skyConfig);
        titleManager = new TitleManager(this, game, skyConfig);

        game.setBossBarManager(bossBarManager);
        game.setTitleManager(titleManager);

        // 初始化事件系统
        eventManager = new EventManager(this, game, skyConfig);
        eventScheduler = new EventScheduler(this, game, eventManager, gameConfig);

        // 将事件调度器注入到游戏中
        game.setEventScheduler(eventScheduler);

        // 注册游戏事件
        registerGameEvents();

        // 注册监听器
        registerListeners();

        // 注册命令
        registerCommands();

        scoreboard = new Scoreboard(game, skyConfig);
        assemble = new Assemble(this, scoreboard);
        assemble.setTicks(4);

        CC.send("&bSkyLuckyPillar &a插件加载成功");
        CC.send("&f游戏地图: &e" + gameConfig.getMapName());
        CC.send("&f柱子数量: &e" + game.getPillarManager().getPillarCount());
        CC.send("&f作者 &bpi_ka");
    }

    @Override
    public void onDisable() {
        if (assemble != null) {
            assemble.cleanup();
        }

        if (eventScheduler != null) {
            eventScheduler.stop();
        }

        if (eventManager != null) {
            eventManager.cleanup();
        }

        Bukkit.getScheduler().cancelTasks(this);

        CC.send("&bSkyLuckyPillar &c插件卸载成功");
        CC.send("&f作者 &bpi_ka");
    }

    private void loadMap() {
        CC.send("加载地图...");
        try {
            File configWorld = new File(this.getDataFolder().getPath() + "/" + gameConfig.getMapName());
            if (!configWorld.exists() || configWorld.listFiles().length == 0) {
                configWorld.mkdirs();
                Files.delete(new File("./" + gameConfig.getMapName()).toPath());
                WorldUtil.copyDir("./" + gameConfig.getMapName(), configWorld.getPath());
                return;
            }
            for (File file : configWorld.listFiles()) {
                File file2;
                String[] split = file.getPath().split("/");
                if (split.length == 1) {
                    split = file.getPath().split("\\\\");
                }
                if (!(file2 = new File("./" + split[split.length - 1])).isDirectory()) continue;
                WorldUtil.deleteDir(file2);
            }
            WorldUtil.copyDir(configWorld.getPath(), "./" + gameConfig.getMapName());
        } catch (Throwable e) {
            CC.sendError("&f加载世界错误: ", e);
        }
        CC.send("&a地图加载成功");
    }

    /**
     * 注册游戏事件
     */
    private void registerGameEvents() {
        eventManager.registerEvent("monster-frenzy", new MonsterFrenzyEvent(gameConfig));
        eventManager.registerEvent("arrow-rain", new ArrowRainEvent(gameConfig));
        eventManager.registerEvent("lava-rise", new LavaRiseEvent(gameConfig));
        eventManager.registerEvent("block-decay", new BlockDecayEvent(gameConfig));
        //eventManager.registerEvent("supply-drop", new SupplyDropEvent(gameConfig));

        CC.send("&f已注册 &b" + eventManager.getRegisteredEvents().size() + " &f个游戏事件");
    }

    /**
     * 注册监听器
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(game), this);
        getServer().getPluginManager().registerEvents(new BlockListener(game), this);
        getServer().getPluginManager().registerEvents(new DamageListener(game, skyConfig), this);
        getServer().getPluginManager().registerEvents(new ChatListener(game, skyConfig), this);

        CC.send("&f已注册 &b4 &f个事件监听器");
    }

    /**
     * 注册命令
     */
    private void registerCommands() {
        LuckyPillarCommand commandExecutor = new LuckyPillarCommand(game, skyConfig);
        getCommand("luckypillar").setExecutor(commandExecutor);
        getCommand("lpillar").setExecutor(commandExecutor);
        getCommand("pillar").setExecutor(commandExecutor);

        CC.send("&f已注册命令: &a/luckypillar, /lpillar, /pillar");
    }

}