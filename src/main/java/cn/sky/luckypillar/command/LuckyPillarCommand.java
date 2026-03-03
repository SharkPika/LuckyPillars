package cn.sky.luckypillar.command;

import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.event.EventManager;
import cn.sky.luckypillar.event.EventScheduler;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

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

    private void handleStart(CommandSender sender) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        game.getStateManager().changeState(GameState.STARTING);
        CC.send(sender, "&a已强制开始游戏");
    }

    private void handleStop(CommandSender sender) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        game.stopGame();
        CC.send(sender, "&a已停止游戏");
    }

    private void handleJoin(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c该命令仅玩家可用");
            return;
        }

        GameState state = game.getStateManager().getCurrentState();
        boolean canJoinAsReady = (state == GameState.WAITING || state == GameState.STARTING)
                && game.getPlayers().size() < game.getMaxPlayer();

        if (canJoinAsReady) {
            if (game.addPlayer(player)) {
                CC.send(sender, "&a你已加入准备队列");
                return;
            }
            CC.send(sender, "&c加入游戏失败 请稍后重试");
            return;
        }

        if (game.addQueuedSpectator(player)) {
            CC.send(sender, "&e房间已满或游戏进行中 你已进入旁观队列");
            return;
        }
        CC.send(sender, "&c加入游戏失败 请稍后重试");
    }

    private void handleLeave(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c该命令仅玩家可用");
            return;
        }

        if (game.getStateManager().isEnding()) {
            CC.send(sender, "&c游戏正在结束阶段 暂时不能退出");
            return;
        }

        if (game.isInGame(player)) {
            game.removePlayer(player);
            game.getPlayers().remove(player.getUniqueId());

            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);

            CC.send(sender, "&a你已退出本局游戏");
            return;
        }

        if (game.isSpectator(player)) {
            game.removeSpectator(player.getUniqueId());
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setFlying(false);
            CC.send(sender, "&a你已退出旁观队列");
            return;
        }

        CC.send(sender, "&e你当前不在本局游戏中");
    }

    private void handleSetup(CommandSender sender, String[] args) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

        if (!(sender instanceof Player player)) {
            CC.send(sender, "&c该命令仅玩家可用");
            return;
        }

        if (args.length < 2) {
            CC.send(sender, "&c用法: /pillar setup <enable|add|remove|list>");
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "enable" -> {
                if (game.isSetupMode()) {
                    CC.send(sender, "&c搭建模式已开启");
                    return;
                }
                if (!game.getStateManager().isWaiting()) {
                    CC.send(sender, "&c仅可在等待状态开启搭建模式");
                    return;
                }

                game.setSetupMode(true);
                CC.send(sender, "&a已开启搭建模式");
                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !p.hasPermission("luckypillar.admin"))
                        .forEach(p -> p.kickPlayer("管理员正在搭建地图 请稍后再试"));
            }
            case "add" -> {
                if (args.length < 4) {
                    CC.send(sender, "&c用法: /pillar setup add <id> <height>");
                    return;
                }

                String id = args[2];
                int height;
                try {
                    height = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    CC.send(sender, "&c高度必须是整数");
                    return;
                }

                if (height <= 0) {
                    CC.send(sender, "&c高度必须大于 0");
                    return;
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
                    CC.send(sender, "&c未找到柱子: " + id);
                }
            }
            case "list" -> {
                CC.send(sender, "&e=== 柱子列表 ===");
                for (Pillar pillar : game.getPillarManager().getPillars()) {
                    CC.send(sender, "&7- &f" + pillar.getId() + " &7(高度: " + pillar.getHeight() + ")");
                }
                CC.send(sender, "&e总数: &f" + game.getPillarManager().getPillarCount());
            }
            default -> CC.send(sender, "&c用法: /pillar setup <enable|add|remove|list>");
        }
    }

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
            CC.send(sender, "&c当前游戏未进行 无法操作事件");
            return;
        }

        EventScheduler eventScheduler = game.getEventScheduler();
        EventManager eventManager = eventScheduler.getEventManager();
        String action = args[1].toLowerCase();

        switch (action) {
            case "start" -> {
                if (args.length < 3) {
                    if (eventScheduler.getSchedulerTask() != null) {
                        CC.send(sender, "&c事件调度器已在运行");
                        return;
                    }
                    eventScheduler.start();
                    CC.send(sender, "&a事件调度器已启动");
                    return;
                }

                String id = args[2];
                if (id.equalsIgnoreCase("current")) {
                    if (eventScheduler.getSchedulerTask() == null) {
                        CC.send(sender, "&c事件调度器未启动");
                        return;
                    }
                    eventScheduler.triggerEventNow();
                    return;
                }

                GameEvent event = id.equalsIgnoreCase("random")
                        ? eventManager.selectRandomEvent()
                        : eventManager.getRegisteredEvents().get(id);

                if (event == null) {
                    CC.send(sender, "&c未找到事件: " + id);
                    return;
                }
                eventManager.startEvent(event, true);
            }
            case "stop" -> {
                if (eventScheduler.getSchedulerTask() == null) {
                    CC.send(sender, "&c事件调度器未启动");
                    return;
                }
                eventScheduler.stop();
                CC.send(sender, "&c事件调度器已停止");
            }
            case "end" -> {
                if (!eventManager.isEventActive()) {
                    CC.send(sender, "&c当前没有进行中的事件");
                    return;
                }
                eventManager.stopCurrentEvent();
                CC.send(sender, "&a已结束当前事件");
            }
            case "reset" -> {
                eventScheduler.reset();
                CC.send(sender, "&6事件调度器已重置");
            }
            case "status" -> {
                CC.send(sender, "&e=== 事件状态 ===");
                if (eventManager.isEventActive()) {
                    CC.send(sender, "&7- &f" + eventManager.getCurrentEvent().getName()
                            + " &7(已持续: " + eventManager.getEventTickCounter() + "秒)");
                } else {
                    CC.send(sender, "&7- &c当前没有进行中的事件");
                }

                CC.send(sender, "&e=== 调度器状态 ===");
                if (eventScheduler.getSchedulerTask() != null) {
                    CC.send(sender, "&7- &a运行中");
                    CC.send(sender, "&7- &f距离下次事件: " + eventScheduler.getTimeUntilNextEvent() + "秒");
                } else {
                    CC.send(sender, "&7- &c已停止");
                }
            }
            default -> CC.send(sender, "&c用法: /pillar event <start|stop|end|reset|status> [id]");
        }
    }

    private void handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, skyConfig.format(skyConfig.getNoPermission(), new HashMap<>()));
            return;
        }

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
                    CC.send(sender, "&c未找到玩家: " + playerName);
                    return;
                }

                LuckyPillarPlayer targetPlayer = game.getPlayer(player.getUniqueId());
                if (targetPlayer == null) {
                    CC.send(sender, "&c该玩家不在本局游戏中: " + playerName);
                    return;
                }

                CC.send(sender, targetPlayer.toString());
                CC.debug(targetPlayer.toString());
            }
            default -> CC.send(sender, "&c用法: /pillar debug <game|events|player> [option]");
        }
    }

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

    private void sendHelp(CommandSender sender) {
        CC.send(sender, "&6=== 幸运之柱 帮助 ===");
        CC.send(sender, "&e/pillar join &7- 加入游戏或旁观队列");
        CC.send(sender, "&e/pillar leave &7- 退出游戏或旁观队列");

        if (sender.hasPermission("luckypillar.admin")) {
            CC.send(sender, "&c管理员命令:");
            CC.send(sender, "&e/pillar start &7- 强制开始");
            CC.send(sender, "&e/pillar stop &7- 停止游戏");
            CC.send(sender, "&e/pillar setup enable &7- 开启搭建模式");
            CC.send(sender, "&e/pillar setup add <id> <height>");
            CC.send(sender, "&e/pillar setup remove <id>");
            CC.send(sender, "&e/pillar setup list");
            CC.send(sender, "&e/pillar event <start|stop|end|reset|status> [id]");
            CC.send(sender, "&e/pillar debug <game|events|player> [option]");
            CC.send(sender, "&e/pillar reload");
        }
    }
}
