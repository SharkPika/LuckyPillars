package cn.sky.luckypillar.game;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.display.BossBarManager;
import cn.sky.luckypillar.display.TitleManager;
import cn.sky.luckypillar.event.EventScheduler;
import cn.sky.luckypillar.item.ItemDistributor;
import cn.sky.luckypillar.pillar.PillarManager;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.state.GameStateManager;
import cn.sky.luckypillar.state.PlayerState;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SkyLuckyPillar 游戏主控制器
 * 负责整个游戏的生命周期管理
 */

@Getter
@ToString
public class LuckyPillarGame {
    
    private final JavaPlugin plugin;
    private final LuckyPillarConfig config;
    private final SkyConfig skyConfig;
    
    // 管理器
    private final GameStateManager stateManager;
    private final PillarManager pillarManager;
    private final ItemDistributor itemDistributor;
    
    @Setter
    private BossBarManager bossBarManager;
    @Setter
    private TitleManager titleManager;
    
    @Setter
    private EventScheduler eventScheduler;

    private LuckyPillarItemRunnable gameRunnable;

    private UUID winner;
    
    // 玩家数据（使用线程安全的集合）
    private final Map<UUID, LuckyPillarPlayer> players;
    private final List<LuckyPillarPlayer> alivePlayers;
    private final List<LuckyPillarPlayer> spectators;
    
    // 游戏世界
    private final World gameWorld;
    
    // 倒计时任务
    private BukkitTask countdownTask;
    private int countdown;
    
    // 游戏开始时间
    private long gameStartTime;

    private final int maxPlayer;
    
    public LuckyPillarGame(JavaPlugin plugin, LuckyPillarConfig config, SkyConfig skyConfig) {
        this.plugin = plugin;
        this.config = config;
        this.skyConfig = skyConfig;
        
        this.stateManager = new GameStateManager(plugin, this);
        this.pillarManager = new PillarManager(plugin, config);
        this.itemDistributor = new ItemDistributor(plugin, config);
        
        this.players = new ConcurrentHashMap<>();
        this.alivePlayers = new CopyOnWriteArrayList<>();
        this.spectators = new CopyOnWriteArrayList<>();
        
        this.gameWorld = Bukkit.getWorld(config.getMapName());
        if (this.gameWorld == null) {
            CC.warn("&c游戏世界 " + config.getMapName() + " 不存在！");
            throw new IllegalArgumentException("游戏世界 " + config.getMapName() + " 不存在！");
        }
        this.gameWorld.setTime(1000);
        this.gameWorld.getEntities().forEach(Entity::remove);
        this.gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        this.countdown = -1;
        
        this.pillarManager.loadPillarsFromConfig();
        this.maxPlayer = this.pillarManager.getPillarCount();
    }
    
    /**
     * 玩家加入游戏
     */
    public boolean addPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return false;
        }
        
        if (players.size() >= maxPlayer) {
            player.sendMessage(skyConfig.format(skyConfig.getGameFull(), new HashMap<>()));
            return false;
        }
        
        if (stateManager.getCurrentState() != GameState.WAITING) {
            player.sendMessage(skyConfig.format(skyConfig.getGameInProgress(), new HashMap<>()));
            return false;
        }
        
        LuckyPillarPlayer lpPlayer = new LuckyPillarPlayer(player);
        players.put(player.getUniqueId(), lpPlayer);

        if (player.isDead()) {
            player.spigot().respawn();
        }
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setGameMode(GameMode.SURVIVAL);
        player.setLevel(this.getCountdown() == -1 ? 0 : this.getCountdown());

        // 广播玩家加入消息
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("current", String.valueOf(players.size()));
        placeholders.put("max", String.valueOf(maxPlayer));
        broadcast(skyConfig.format(skyConfig.getPlayerJoined(), placeholders));
        
        // 检查是否可以开始游戏
        checkStartCondition();
        
        return true;
    }
    
    /**
     * 玩家离开游戏
     */
    public void removePlayer(Player player) {
        LuckyPillarPlayer lpPlayer = players.remove(player.getUniqueId());
        if (lpPlayer == null) {
            return;
        }
        
        alivePlayers.remove(lpPlayer);
        spectators.remove(lpPlayer);
        
        // 释放柱子
        if (lpPlayer.getAssignedPillar() != null) {
            lpPlayer.getAssignedPillar().release();
        }
        
        // 广播玩家离开消息
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        broadcast(skyConfig.format(skyConfig.getPlayerLeft(), placeholders));
        
        // 检查游戏状态
        if (stateManager.isStarting() && players.size() < config.getMinPlayers()) {
            // 人数不足，返回等待状态
            cancelCountdown();
            stateManager.changeState(GameState.WAITING);
        } else if (stateManager.isGameRunning()) {
            // 检查胜利条件
            checkWinCondition();
        }
    }
    
    /**
     * 检查是否可以开始游戏
     */
    private void checkStartCondition() {
        if (stateManager.getCurrentState() == GameState.WAITING
            && players.size() >= maxPlayer) {
            stateManager.changeState(GameState.STARTING);
            return;
        }
        if (stateManager.getCurrentState() == GameState.WAITING 
            && players.size() >= config.getMinPlayers()
            && countdown == -1) {
            countdown = 60;
            players.values().forEach(player -> player.getBukkitPlayer().setLevel(countdown));
            countdownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (countdown <= 10) {
                        this.cancel();
                        stateManager.changeState(GameState.STARTING);
                        return;
                    }

                    players.values().forEach(player -> player.getBukkitPlayer().setLevel(countdown));
                    countdown--;
                }
            }.runTaskTimer(plugin, 0, 20L);
        }
    }
    
    /**
     * 开始游戏
     */
    public void startGame() {
        if (!stateManager.canStartGame() && stateManager.getCurrentState() != GameState.STARTING) {
            CC.warn("&c无法开始游戏，当前状态: " + stateManager.getCurrentState());
            return;
        }
        
        // 检查柱子数量
        if (!pillarManager.hasEnoughPillars(players.size())) {
            CC.warn("&c柱子数量不足！需要 " + players.size() + " 个，但只有 " + pillarManager.getPillarCount() + " 个");
            broadcast("&c柱子数量不足，无法开始游戏！");
            return;
        }

        titleManager.showGameStartTitleToAll();

        stateManager.changeState(GameState.PLAYING);
    }
    
    /**
     * 停止游戏
     */
    public void stopGame() {
        if (stateManager.getCurrentState() == GameState.WAITING) {
            return;
        }
        
        cancelCountdown();
        stateManager.changeState(GameState.ENDING);
    }
    
    /**
     * 结束游戏
     */
    public void endGame(LuckyPillarPlayer winner) {
        if (winner != null) {
            // 宣布胜利者
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("winner", winner.getName());
            broadcast(skyConfig.format(skyConfig.getGameWinner(), placeholders));

            winner.getBukkitPlayer().setAllowFlight(true);
            winner.getBukkitPlayer().setFlying(true);
            CC.send("&a玩家 " + winner.getName() + " 获得胜利！");

            titleManager.showVictoryTitle(winner.getBukkitPlayer());
            titleManager.showGameOverTitleToAll();
        } else {
            // 没有胜利者（超时或其他原因）
            broadcast(skyConfig.format(skyConfig.getGameTimeout(), new HashMap<>()));
        }

        gameRunnable.cancel();
        stateManager.changeState(GameState.ENDING);
    }
    
    /**
     * 重置游戏
     */
    public void resetGame() {
        // 清理平台
        pillarManager.clearAllPlatforms();
        
        // 释放所有柱子
        pillarManager.releaseAllPillars();
        
        // 重置玩家数据
        for (LuckyPillarPlayer lpPlayer : players.values()) {
            lpPlayer.reset();
            
            // 恢复玩家状态
            if (lpPlayer.isOnline()) {
                Player player = lpPlayer.getBukkitPlayer();
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.getInventory().clear();
            }
        }
        
        // 清空列表
        alivePlayers.clear();
        spectators.clear();
        
        // 重置状态
        stateManager.reset();

        CC.send("&c游戏已重置");
    }
    
    /**
     * 玩家死亡处理
     */
    public void killPlayer(LuckyPillarPlayer lpPlayer, String reason) {
        if (lpPlayer.getState() != PlayerState.ALIVE) {
            return;
        }
        
        lpPlayer.setState(PlayerState.DEAD);
        lpPlayer.addDeath();
        lpPlayer.calculateSurvivalTime();
        alivePlayers.remove(lpPlayer);
        
        // 广播死亡消息
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", lpPlayer.getName());
        placeholders.put("reason", reason);
        placeholders.put("remaining", String.valueOf(alivePlayers.size()));
        broadcast(skyConfig.format(skyConfig.getPlayerEliminated(), placeholders));

        titleManager.showPlayerKilledTitle(lpPlayer.getBukkitPlayer(), lpPlayer.getName());
        
        // 转为观战模式
        respawnAsSpectator(lpPlayer);
        
        // 检查胜利条件
        checkWinCondition();
    }
    
    /**
     * 转为观战模式
     */
    public void respawnAsSpectator(LuckyPillarPlayer lpPlayer) {
        if (!lpPlayer.isOnline()) {
            return;
        }
        
        lpPlayer.setState(PlayerState.SPECTATING);
        spectators.add(lpPlayer);
        
        Player player = lpPlayer.getBukkitPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
    }
    
    /**
     * 检查胜利条件
     */
    public void checkWinCondition() {
        if (!stateManager.isGameRunning()) {
            return;
        }
        
        if (alivePlayers.size() == 1) {
            // 只剩一名玩家，游戏结束
            LuckyPillarPlayer winner = alivePlayers.get(0);
            this.winner = winner.getUuid();
            endGame(winner);
        } else if (alivePlayers.isEmpty()) {
            // 没有存活玩家
            endGame(null);
        }
    }
    
    /**
     * 广播消息给所有玩家
     */
    public void broadcast(String message) {
        CC.broadcast(message);
    }
    
    /**
     * 获取玩家
     */
    public LuckyPillarPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }
    
    /**
     * 获取玩家
     */
    public LuckyPillarPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    
    /**
     * 检查玩家是否在游戏中
     */
    public boolean isInGame(Player player) {
        return players.containsKey(player.getUniqueId());
    }
    
    /**
     * 取消倒计时
     */
    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }
    
    // ========== 状态进入处理 ==========
    
    /**
     * 进入等待状态
     */
    public void onEnterWaiting() {
        CC.send("&a进入等待状态");
        resetGame();
    }
    
    /**
     * 进入开始倒计时状态
     */
    public void onEnterStarting() {
        CC.send("&a进入开始倒计时状态");
        countdown = config.getCountdown();
        
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (countdown <= 0) {
                cancelCountdown();
                startGame();
                return;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                player.setLevel(countdown);
            });

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("countdown", String.valueOf(countdown));
            broadcast(skyConfig.format(skyConfig.getGameStarting(), placeholders));
            
            countdown--;
        }, 0L, 20L);
    }
    
    /**
     * 进入游戏中状态
     */
    public void onEnterPlaying() {
        CC.send("&a进入游戏中状态");
        
        // 设置所有玩家为存活状态
        alivePlayers.clear();
        for (LuckyPillarPlayer lpPlayer : players.values()) {
            lpPlayer.setState(PlayerState.ALIVE);
            lpPlayer.startGameTimer();
            lpPlayer.getBukkitPlayer().setLevel(0);
            alivePlayers.add(lpPlayer);
        }
        
        // 分配玩家到柱子
        //pillarManager.assignPlayersToPillars(alivePlayers);
        
        // 构建平台
        //pillarManager.buildAllPlatforms();
        
        // 传送玩家到柱子顶部
        for (LuckyPillarPlayer lpPlayer : alivePlayers) {
            if (lpPlayer.getAssignedPillar() != null && lpPlayer.isOnline()) {
                Player player = lpPlayer.getBukkitPlayer();
                //player.teleport(lpPlayer.getAssignedPillar().getTopLocation());
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);
            }
        }
        
        // 分发初始装备
        //itemDistributor.distributeStarterKitToAll(alivePlayers);
        
        // 广播游戏开始消息
        broadcast(skyConfig.format(skyConfig.getGameStarted(), new HashMap<>()));
        
        // 记录游戏开始时间
        gameStartTime = System.currentTimeMillis();
        
        // 启动事件调度器
        if (eventScheduler != null) {
            eventScheduler.start();
        }

        gameRunnable = new LuckyPillarItemRunnable();
        gameRunnable.runTaskTimer(plugin, 0L, config.getItemCountdown() * 20L);
    }
    
    /**
     * 进入结束状态
     */
    public void onEnterEnding() {
        CC.send("&a进入结束状态");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer("§c游戏正在重置中"));
            Bukkit.getServer().shutdown();
        }, 200L);
    }
    
    /**
     * 获取存活玩家列表
     */
    public List<LuckyPillarPlayer> getAlivePlayers() {
        return new ArrayList<>(alivePlayers);
    }
    
    /**
     * 获取所有玩家列表
     */
    public Collection<LuckyPillarPlayer> getAllPlayers() {
        return players.values();
    }
}
