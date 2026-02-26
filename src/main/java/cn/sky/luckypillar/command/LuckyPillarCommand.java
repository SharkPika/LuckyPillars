package cn.sky.luckypillar.command;

import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.event.EventManager;
import cn.sky.luckypillar.event.EventScheduler;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * SkyLuckyPillar 主命令处理器
 * 命令: /luckypillar 或 /pillar
 */
public class LuckyPillarCommand implements CommandExecutor {
    
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;
    
    public LuckyPillarCommand(LuckyPillarGame game, SkyConfig skyConfig) {
        this.game = game;
        this.skyConfig = skyConfig;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "start" -> handleStart(sender);
            case "stop" -> handleStop(sender);
            case "join" -> handleJoin(sender);
            case "leave" -> handleLeave(sender);
            case "setup" -> handleSetup(sender, args);
            case "event" -> handleEvent(sender, args);
            case "debug" -> handleDebug(sender, args);
            case "reload" -> handleReload(sender);
            default -> sendHelp(sender);
        }
        
        return true;
    }
    
    /**
     * 处理 start 命令
     */
    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        game.getStateManager().changeState(GameState.STARTING);
        CC.send(sender, "&a游戏已强制开始！");
    }
    
    /**
     * 处理 stop 命令
     */
    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }
        
        game.stopGame();
        CC.send(sender, "&a游戏已停止！");
    }
    
    /**
     * 处理 join 命令
     */
    private void handleJoin(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c只有玩家可以执行此命令！");
            return;
        }
        
        if (game.addPlayer(player)) {
            CC.send(sender, "&a你已加入游戏！");
        }
    }
    
    /**
     * 处理 leave 命令
     */
    private void handleLeave(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c只有玩家可以执行此命令！");
            return;
        }

        if (game.getStateManager().isEnding()) {
            CC.send(sender, "&c游戏已结束 无法离开");
            return;
        }

        game.removePlayer(player);
        game.respawnAsSpectator(game.getPlayer(player));
        if (!game.getStateManager().isGameRunning()) {
            game.getPlayers().remove(player.getUniqueId());
        }
        CC.send(sender, "&a你已离开游戏！");
    }
    
    /**
     * 处理 setup 命令
     */
    private void handleSetup(CommandSender sender, String[] args) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }
        
        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c只有玩家可以执行此命令！");
            return;
        }
        
        if (args.length < 2) {
            CC.send(sender, "&c用法: /pillar setup <add|remove|list> [id]");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "enable" -> {
                if (game.isSetupMode()) {
                    CC.send(sender, "&c配置模式已开启");
                    return;
                }
                if (!game.getStateManager().isWaiting()) {
                    CC.send(sender, "&c游戏正在运行中");
                    return;
                }
                game.setSetupMode(true);
                CC.send(sender, "&a配置模式已开启");
                Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasPermission("luckypillar.admin")).forEach(p -> p.kickPlayer("§c管理员正在配置地图中 暂时无法开启游戏"));
            }
            case "add" -> {
                if (args.length < 3) {
                    CC.send(sender, "&c用法: /pillar setup add <id> <height>");
                    return;
                }
                
                String id = args[2];
                int height;
                try {
                    height = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    CC.send("&c设置柱子高度只能使用整数");
                    throw new IllegalArgumentException("设置柱子高度只能使用整数");
                }

                Location loc = player.getLocation();
                
                Location topLoc = loc.clone();
                Location bottomLoc = loc.clone();
                bottomLoc.setY(bottomLoc.getBlockY() - height);

                Pillar pillar = new Pillar(
                    id,
                    topLoc,
                    bottomLoc,
                    height,
                    game.getConfig().getDefaultPlatformSize(),
                    Material.valueOf(game.getConfig().getDefaultPlatformMaterial())
                );
                
                game.getPillarManager().addPillar(pillar);
                
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("id", id);
                CC.send(sender, skyConfig.format(skyConfig.getPillarAdded(), placeholders));
            }
            case "remove" -> {
                if (args.length < 3) {
                    CC.send(sender, "&c用法: /pillar setup remove <id>");
                    return;
                }
                
                String id = args[2];
                if (game.getPillarManager().removePillar(id)) {
                    HashMap<String, String> placeholders = new HashMap<>();
                    placeholders.put("id", id);
                    CC.send(sender, skyConfig.format(skyConfig.getPillarRemoved(), placeholders));
                } else {
                    CC.send(sender, "&c柱子不存在: " + id);
                }
            }
            case "list" -> {
                CC.send(sender, "&e=== 柱子列表 ===");
                for (Pillar pillar : game.getPillarManager().getPillars()) {
                    CC.send(sender, "&7- &f" + pillar.getId() + " &7(高度: " + pillar.getHeight() + ")");
                }
                CC.send(sender, "&e总计: &f" + game.getPillarManager().getPillarCount() + " &e个柱子");
            }
            default -> CC.send(sender, "&c用法: /pillar setup <enable|add|remove|list> [id]");
        }
    }

     /**
     * 处理 event 命令
     */
    private void handleEvent(CommandSender sender, String[] args) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        if (args.length < 2) {
            CC.send(sender, "&c用法: /pillar event <start|stop|end|reset|status> [id]");
            return;
        }

        if (!game.getStateManager().isGameRunning()) {
            CC.send(sender, "&c游戏不在运行中 无法操作事件");
            return;
        }

        EventScheduler eventScheduler = game.getEventScheduler();
        EventManager eventManager = game.getEventScheduler().getEventManager();
        String action = args[1].toLowerCase();
        switch (action) {
            case "start" -> {
                if (args.length < 3) {
                    if (eventScheduler.getSchedulerTask() != null) {
                        CC.send(sender, "&c事件调度器没有停止");
                        return;
                    }
                    eventScheduler.start();
                    CC.send(sender, "&a事件调度器已启动");
                    return;
                }
                String id = args[2];
                if (id.equalsIgnoreCase("current")) {
                    if (eventScheduler.getSchedulerTask() == null) {
                        CC.send(sender, "&c事件调度器没有启动");
                        return;
                    }
                    eventScheduler.triggerEventNow();
                    return;
                }
                GameEvent event = eventManager.getRegisteredEvents().get(id);
                if (id.equalsIgnoreCase("random")) {
                    event = eventManager.selectRandomEvent();
                }
                if (event == null) {
                    CC.send(sender, "&c事件不存在: " + id);
                    return;
                }
                eventManager.startEvent(event, true);
            }
            case "stop" -> {
                if (eventScheduler.getSchedulerTask() == null) {
                    CC.send(sender, "&c事件调度器没有启动");
                    return;
                }
                eventScheduler.stop();
                CC.send(sender, "&c事件调度器已停止");
            }
            case "end" -> {
                if (!eventManager.isEventActive()) {
                    CC.send(sender, "&c没有正在进行的事件");
                    return;
                }
                eventManager.stopCurrentEvent();
                CC.send(sender, "&c当前事件已结束");
            }
            case "reset" -> {
                eventScheduler.reset();
                CC.send(sender, "&6事件调度器已重置");
            }
            case "status" -> {
                CC.send(sender, "&e=== 当前事件状态 ===");
                if (eventManager.isEventActive()) {
                    CC.send(sender, "&7- &f" + eventManager.getCurrentEvent().getName() + " &7(持续时间: " + eventManager.getEventTickCounter() + " 秒)");
                } else {
                    CC.send(sender, "&7- &c没有正在进行的事件");
                }
                CC.send(sender, "&e=== 事件调度器状态 ===");
                if (eventScheduler.getSchedulerTask() != null) {
                    CC.send(sender, "&7- &a事件调度器已启动");
                    CC.send(sender, "&7- &f" + eventScheduler.getTimeUntilNextEvent() + " 秒后触发下一个事件");
                } else {
                    CC.send(sender, "&7- &c事件调度器未启动");
                }
            }
            default -> CC.send(sender, "&c用法: /pillar event <start|stop|end|reset|status> [id]");
        }
    }

    /**
     * 处理 debug 命令
     */
    private void handleDebug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            CC.send(sender, "&c用法: /pillar debug <game|events|player> [option]");
            return;
        }
        String target = args[1].toLowerCase();

        switch (target) {
            case "game" -> {
                CC.send(sender, game.toString());
                CC.debug(game.toString());
            }
            case "events" -> {
                CC.send(sender, game.getEventScheduler().getEventManager().toString());
                CC.debug(game.getEventScheduler().getEventManager().toString());
            }
            case "player" -> {
                if (args.length < 3) {
                    CC.send(sender, "&c用法: /pillar debug player <player>");
                    return;
                }
                String playerName = args[2];
                Player player = Bukkit.getServer().getPlayer(playerName);
                if (player == null || !player.isOnline()) {
                    CC.send(sender, "&c玩家不存在: " + playerName);
                    return;
                }
                CC.send(sender, game.getPlayer(player.getUniqueId()) == null ? "&c玩家不存在: " + playerName : game.getPlayer(player.getUniqueId()).toString());
                CC.debug(game.getPlayer(player.getUniqueId()).toString());
            }
        }
    }

    /**
     * 处理 reload 命令
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        game.getConfig().reload();
        skyConfig.reload();
        game.getPillarManager().loadPillarsFromConfig();
        game.getItemDistributor().reload();

        CC.send(sender, skyConfig.format(skyConfig.getConfigReloaded(), new HashMap<>()));
    }
    
    /**
     * 发送帮助信息
     */
    private void sendHelp(CommandSender sender) {
        CC.send(sender, "&6=== SkyLuckyPillar 命令帮助 ===");
        CC.send(sender, "&e/pillar join &7- 加入游戏");
        CC.send(sender, "&e/pillar leave &7- 离开游戏");
        
        if (sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, "&c管理员命令:");
            CC.send(sender, "&e/pillar start &7- 强制开始游戏");
            CC.send(sender, "&e/pillar stop &7- 停止游戏");
            CC.send(sender, "&e/pillar setup enable &7- 启动地图配置模式");
            CC.send(sender, "&e/pillar setup add <id> <height> &7- 在当前位置添加柱子");
            CC.send(sender, "&e/pillar setup remove <id> &7- 移除柱子");
            CC.send(sender, "&e/pillar setup list &7- 列出所有柱子");
            CC.send(sender, "&e/pillar event start [id|random] &7- 开启事件调度器 [强制开启指定事件或随机事件]");
            CC.send(sender, "&e/pillar event stop &7- 停止事件调度器");
            CC.send(sender, "&e/pillar event end &7- 强制结束当前事件");
            CC.send(sender, "&e/pillar event reset &7- 重置事件调度器");
            CC.send(sender, "&e/pillar event status &7- 显示当前事件状态");
            CC.send(sender, "&e/pillar debug <game|events|player> [option] &7- 调试信息");
            CC.send(sender, "&e/pillar reload &7- 重新加载配置");
        }
    }
}
