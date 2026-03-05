package cn.sky.luckypillar.game;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.display.BossBarManager;
import cn.sky.luckypillar.display.TitleManager;
import cn.sky.luckypillar.event.EventScheduler;
import cn.sky.luckypillar.item.ItemDistributor;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.pillar.PillarManager;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.state.GameStateManager;
import cn.sky.luckypillar.state.PlayerState;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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

    private final GameStateManager stateManager;
    private final PillarManager pillarManager;
    private final ItemDistributor itemDistributor;
    private final Map<UUID, LuckyPillarPlayer> players;
    private final List<LuckyPillarPlayer> alivePlayers;
    private final List<LuckyPillarPlayer> spectators;
    private final Location center;
    private final Location farthest;
    private final int maxPlayer;
    @Setter
    private BossBarManager bossBarManager;
    @Setter
    private TitleManager titleManager;
    @Setter
    private EventScheduler eventScheduler;
    private LuckyPillarItemRunnable gameRunnable;
    @Setter
    private boolean setupMode;
    private UUID winner;
    private World gameWorld;
    private BukkitTask countdownTask;
    private int countdown;
    private long gameStartTime;
    private boolean lobbyItemMaterialWarned;

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
            WorldCreator creator = new WorldCreator(config.getMapName());
            creator.type(WorldType.NORMAL);
            this.gameWorld = creator.createWorld();
        }
        this.gameWorld.setTime(1000);
        this.gameWorld.getEntities().forEach(Entity::remove);
        this.gameWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

        this.countdown = -1;

        this.pillarManager.loadPillarsFromConfig();
        if (pillarManager.getPillars() != null) {
            this.center = this.getGeometricCenter(pillarManager.getPillars().stream()
                    .map(Pillar::getTopLocation)
                    .collect(Collectors.toList()));
            this.farthest = this.getFarthestLocationFromCenter(pillarManager.getPillars().stream()
                    .map(Pillar::getTopLocation)
                    .collect(Collectors.toList()));
        } else {
            this.center = null;
            this.farthest = null;
        }
        this.maxPlayer = this.pillarManager.getPillarCount();
        this.setupMode = this.maxPlayer == 0;
    }

    public boolean addPlayer(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return false;
        }

        if (players.size() >= maxPlayer) {
            CC.send(player, skyConfig.format(skyConfig.getGameFull(), new HashMap<>()));
            return false;
        }

        if (stateManager.getCurrentState() != GameState.WAITING
                && stateManager.getCurrentState() != GameState.STARTING) {
            CC.send(player, skyConfig.format(skyConfig.getGameInProgress(), new HashMap<>()));
            return false;
        }

        removeSpectator(player.getUniqueId());

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

        Pillar pillar = lpPlayer.assignNewPillar();
        if (pillar == null) {
            players.remove(player.getUniqueId());
            CC.send(player, skyConfig.format(skyConfig.getGameFull(), new HashMap<>()));
            return false;
        }
        lpPlayer.getBukkitPlayer().teleport(pillar.getTopLocation());
        giveLobbyItem(player);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        placeholders.put("current", String.valueOf(players.size()));
        placeholders.put("max", String.valueOf(maxPlayer));
        broadcast(skyConfig.format(skyConfig.getPlayerJoined(), placeholders));

        checkStartCondition();

        return true;
    }

    public void removePlayer(Player player) {
        LuckyPillarPlayer lpPlayer = players.get(player.getUniqueId());
        if (lpPlayer == null) {
            return;
        }

        alivePlayers.remove(lpPlayer);
        spectators.remove(lpPlayer);

        if (lpPlayer.getAssignedPillar() != null) {
            lpPlayer.getAssignedPillar().release();
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        broadcast(skyConfig.format(skyConfig.getPlayerLeft(), placeholders));

        int remainingPlayers = players.containsKey(player.getUniqueId()) ? players.size() - 1 : players.size();
        if (stateManager.isStarting() && remainingPlayers < config.getMinPlayers()) {
            cancelCountdown();
            stateManager.changeState(GameState.WAITING);
        } else if (stateManager.isGameRunning()) {
            checkWinCondition();
        }
    }

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

    public void startGame() {
        if (!stateManager.canStartGame() && stateManager.getCurrentState() != GameState.STARTING) {
            CC.warn("&c当前状态不允许开始游戏: " + stateManager.getCurrentState());
            return;
        }

        if (!pillarManager.hasEnoughPillars(players.size())) {
            CC.warn("&c柱子数量不足：需要 " + players.size() + " 个，当前可用 " + pillarManager.getPillarCount() + " 个");
            broadcast("&c柱子数量不足 游戏无法开始");
            return;
        }

        titleManager.showGameStartTitleToAll();

        stateManager.changeState(GameState.PLAYING);
    }

    public void stopGame() {
        if (stateManager.getCurrentState() == GameState.WAITING) {
            return;
        }

        cancelCountdown();
        stateManager.changeState(GameState.ENDING);
    }

    public void endGame(LuckyPillarPlayer winner) {
        if (winner != null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("winner", winner.getName());
            broadcast(skyConfig.format(skyConfig.getGameWinner(), placeholders));

            winner.getBukkitPlayer().setAllowFlight(true);
            winner.getBukkitPlayer().setFlying(true);
            CC.send("&a获胜者: &e" + winner.getName());

            titleManager.showVictoryTitle(winner.getBukkitPlayer());
            titleManager.showGameOverTitleToAll();
        } else {
            broadcast(skyConfig.format(skyConfig.getGameTimeout(), new HashMap<>()));
        }

        gameRunnable.cancel();
        stateManager.changeState(GameState.ENDING);
    }

    public void resetGame() {
        pillarManager.clearAllPlatforms();

        pillarManager.releaseAllPillars();

        for (LuckyPillarPlayer lpPlayer : players.values()) {
            lpPlayer.reset();

            if (lpPlayer.isOnline()) {
                Player player = lpPlayer.getBukkitPlayer();
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.getInventory().clear();
            }
        }

        alivePlayers.clear();
        spectators.clear();

        stateManager.reset();

        CC.send("&c游戏已重置");
    }

    public void killPlayer(LuckyPillarPlayer lpPlayer, String reason) {
        if (lpPlayer.getState() != PlayerState.ALIVE) {
            return;
        }

        lpPlayer.setState(PlayerState.DEAD);
        lpPlayer.addDeath();
        lpPlayer.calculateSurvivalTime();
        alivePlayers.remove(lpPlayer);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", lpPlayer.getName());
        placeholders.put("reason", reason);
        placeholders.put("remaining", String.valueOf(alivePlayers.size()));
        broadcast(skyConfig.format(skyConfig.getPlayerEliminated(), placeholders));

        titleManager.showPlayerKilledTitle(lpPlayer.getBukkitPlayer(), lpPlayer.getName());

        respawnAsSpectator(lpPlayer);

        checkWinCondition();
    }

    public void respawnAsSpectator(LuckyPillarPlayer lpPlayer) {
        if (lpPlayer == null || !lpPlayer.isOnline()) {
            return;
        }

        lpPlayer.setState(PlayerState.SPECTATING);
        boolean exists = spectators.stream().anyMatch(s -> s.getUuid().equals(lpPlayer.getUuid()));
        if (!exists) {
            spectators.add(lpPlayer);
        }

        Player player = lpPlayer.getBukkitPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(this.center);
    }

    public boolean isSpectator(Player player) {
        return spectators.stream().anyMatch(s -> s.getUuid().equals(player.getUniqueId()));
    }

    public boolean addQueuedSpectator(Player player) {
        if (player == null) {
            return false;
        }
        if (players.containsKey(player.getUniqueId())) {
            return false;
        }
        if (isSpectator(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.setAllowFlight(true);
            player.setFlying(true);
            giveLobbyItem(player);
            return true;
        }

        LuckyPillarPlayer spectator = new LuckyPillarPlayer(player);
        respawnAsSpectator(spectator);
        giveLobbyItem(player);
        return true;
    }

    public void removeSpectator(UUID uuid) {
        spectators.removeIf(s -> s.getUuid().equals(uuid));
    }

    public boolean promoteFirstQueuedSpectator() {
        for (LuckyPillarPlayer spectator : spectators) {
            if (players.containsKey(spectator.getUuid())) {
                continue;
            }
            if (!spectator.isOnline()) {
                spectators.remove(spectator);
                continue;
            }

            spectators.remove(spectator);
            if (addPlayer(spectator.getBukkitPlayer())) {
                return true;
            }
            respawnAsSpectator(spectator);
            return false;
        }
        return false;
    }

    public void checkWinCondition() {
        if (!stateManager.isGameRunning()) {
            return;
        }

        if (alivePlayers.size() == 1) {
            LuckyPillarPlayer winner = alivePlayers.get(0);
            this.winner = winner.getUuid();
            endGame(winner);
        } else if (alivePlayers.isEmpty()) {
            endGame(null);
        }
    }

    public void giveLobbyItem(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        ItemStack item = createLobbyItem();

        int slot = Math.max(0, Math.min(35, skyConfig.getLobbyItemSlot()));
        player.getInventory().setItem(slot, item);
        player.updateInventory();
    }

    public void removeLobbyItem(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack current = contents[i];
            if (isLobbyItem(current)) {
                player.getInventory().setItem(i, null);
            }
        }
    }

    public boolean isLobbyItem(ItemStack item) {
        if (item == null) {
            return false;
        }

        ItemStack template = createLobbyItem();
        if (template.getType() != item.getType()) {
            return false;
        }

        if (!item.hasItemMeta() || !template.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta templateMeta = template.getItemMeta();
        if (itemMeta == null || templateMeta == null) {
            return false;
        }

        String itemName = itemMeta.hasDisplayName() ? ChatColor.stripColor(itemMeta.getDisplayName()) : "";
        String templateName = templateMeta.hasDisplayName() ? ChatColor.stripColor(templateMeta.getDisplayName()) : "";
        return Objects.equals(itemName, templateName);
    }

    public void sendPlayerToLobby(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        String serverName = skyConfig.getLobbyServername();
        if (serverName == null || serverName.isBlank()) {
            CC.send(player, "&c未配置大厅服务器 请联系管理员");
            return;
        }

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(byteStream);
            out.writeUTF("Connect");
            out.writeUTF(serverName);
            player.sendPluginMessage(plugin, "BungeeCord", byteStream.toByteArray());
        } catch (IOException e) {
            CC.send(player, "&c发送回大厅请求失败 请稍后重试");
            CC.sendError("&c发送 BungeeCord Connect 消息失败", e);
        }
    }

    private ItemStack createLobbyItem() {
        Material material = resolveLobbyItemMaterial();
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(CC.translate(skyConfig.getLobbyItemName()));
        List<String> lore = skyConfig.getLobbyItemLore();
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(CC.translate(lore));
        }
        item.setItemMeta(meta);
        return item;
    }

    private Material resolveLobbyItemMaterial() {
        String materialName = skyConfig.getLobbyItemMaterial();
        Material material = materialName == null ? null : Material.matchMaterial(materialName.toUpperCase());
        if (material != null) {
            return material;
        }

        if (!lobbyItemMaterialWarned) {
            lobbyItemMaterialWarned = true;
            CC.warn("&e大厅返回物品材质无效，已使用默认材质 NETHER_STAR。配置值: "
                    + skyConfig.getLobbyItemMaterial());
        }
        return Material.NETHER_STAR;
    }

    public void broadcast(String message) {
        CC.broadcast(message);
    }

    public LuckyPillarPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public LuckyPillarPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public boolean isInGame(Player player) {
        return players.containsKey(player.getUniqueId());
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    public void onEnterWaiting() {
        resetGame();
    }

    public void onEnterStarting() {
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

    public void onEnterPlaying() {
        for (LuckyPillarPlayer lpPlayer : players.values()) {
            lpPlayer.setState(PlayerState.ALIVE);
            lpPlayer.startGameTimer();
            lpPlayer.getBukkitPlayer().setLevel(0);
            alivePlayers.add(lpPlayer);
        }

        pillarManager.buildAllPlatforms();

        for (LuckyPillarPlayer lpPlayer : alivePlayers) {
            if (lpPlayer.getAssignedPillar() != null && lpPlayer.isOnline()) {
                Player player = lpPlayer.getBukkitPlayer();
                player.teleport(lpPlayer.getAssignedPillar().getTopLocation());
                player.setGameMode(GameMode.SURVIVAL);
                player.setHealth(20.0);
                player.setFoodLevel(20);
                removeLobbyItem(player);
            }
        }

        itemDistributor.distributeStarterKitToAll(alivePlayers);

        broadcast(skyConfig.format(skyConfig.getGameStarted(), new HashMap<>()));

        gameStartTime = System.currentTimeMillis();

        if (eventScheduler != null) {
            eventScheduler.start();
        }

        gameRunnable = new LuckyPillarItemRunnable();
        gameRunnable.runTaskTimer(plugin, 0L, config.getItemCountdown() * 20L);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (stateManager.isGameRunning()) {
                broadcast("&c游戏时间已到！");
                endGame(null);
            }
        }, config.getGameTimeout() * 20L);
    }

    public void onEnterEnding() {
        List<LuckyPillarPlayer> ranked = new ArrayList<>(players.values());
        ranked.sort((a, b) -> b.getKills() - a.getKills());

        StringBuilder sb = new StringBuilder();
        sb.append("&6&l===== 游戏结束 =====\n");
        for (int i = 0; i < ranked.size(); i++) {
            LuckyPillarPlayer p = ranked.get(i);
            String medal = switch (i) {
                case 0 -> "&6🥇";
                case 1 -> "&7🥈";
                case 2 -> "&c🥉";
                default -> "&f" + (i + 1);
            };
            sb.append(medal).append(" &e").append(p.getName())
                    .append(" &7- &c").append(p.getKills()).append(" 击杀")
                    .append(" &7| &a").append(p.getFormattedSurvivalTime()).append(" 存活时间\n");
        }
        broadcast(sb.toString());

        if (winner != null) {
            LuckyPillarPlayer winnerPlayer = players.get(winner);
            if (winnerPlayer != null && winnerPlayer.isOnline()) {
                java.util.Random rng = new java.util.Random();
                BukkitTask fireworkTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    Location loc = winnerPlayer.getBukkitPlayer().getLocation().add(0, 2, 0);
                    if (loc.getWorld() == null)
                        return;
                    Firework fw = loc.getWorld().spawn(loc, Firework.class);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffect.builder()
                            .withColor(Color.fromRGB(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)))
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withFlicker().withTrail().build());
                    meta.setPower(1);
                    fw.setFireworkMeta(meta);
                }, 0L, 20L);

                Bukkit.getScheduler().runTaskLater(plugin, fireworkTask::cancel, 200L);
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer("§c游戏结束"));
            Bukkit.getServer().shutdown();
        }, 300L);
    }

    public Location getGeometricCenter(Collection<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return null;
        }

        double x = 0, y = 0, z = 0;
        int count = 0;
        Location first = null;

        for (Location loc : locations) {
            if (first == null) {
                first = loc;
            }
            if (!Objects.requireNonNull(first.getWorld()).equals(loc.getWorld())) {
                throw new IllegalArgumentException("所有 Location 必须在同一世界");
            }
            x += loc.getX();
            y += loc.getY();
            z += loc.getZ();
            count++;
        }

        return new Location(
                first.getWorld(),
                x / count,
                y / count,
                z / count);
    }

    public Location getFarthestLocationFromCenter(Collection<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return null;
        }

        Location center = this.center;
        if (center == null) {
            return this.getGeometricCenter(locations);
        }

        double maxX = 0;
        double maxY = 0;
        double maxZ = 0;

        for (Location loc : locations) {
            if (maxX < Math.abs(loc.getX() - center.getX())) {
                maxX = Math.abs(loc.getX() - center.getX());
            }
            if (maxY < Math.abs(loc.getY() - center.getY())) {
                maxY = Math.abs(loc.getY() - center.getY());
            }
            if (maxZ < Math.abs(loc.getZ() - center.getZ())) {
                maxZ = Math.abs(loc.getZ() - center.getZ());
            }
        }

        return new Location(
                center.getWorld(),
                center.getX() + maxX,
                center.getY() + maxY,
                center.getZ() + maxZ);
    }

    public List<Location> getRectanglePoints(Location loc1, Location loc2) {
        if (!Objects.requireNonNull(loc1.getWorld()).equals(loc2.getWorld())) {
            throw new IllegalArgumentException("两个点必须在同一世界");
        }

        World world = loc1.getWorld();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        List<Location> result = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                result.add(new Location(world, x, loc1.getY(), z));
            }
        }

        return result;
    }

    public List<LuckyPillarPlayer> getAlivePlayers() {
        return new ArrayList<>(alivePlayers);
    }

    public Collection<LuckyPillarPlayer> getAllPlayers() {
        return players.values();
    }
}
