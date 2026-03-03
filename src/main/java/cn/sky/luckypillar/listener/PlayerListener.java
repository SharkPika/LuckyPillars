package cn.sky.luckypillar.listener;

import cn.sky.luckypillar.SkyLuckyPillar;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.state.PlayerState;
import cn.sky.luckypillar.utils.compat.VersionCompat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final LuckyPillarGame game;

    public PlayerListener(LuckyPillarGame game) {
        this.game = game;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (game.isSetupMode() && !event.getPlayer().hasPermission("luckypillar.admin")) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "管理员正在搭建地图 请稍后进入");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        player.getInventory().clear();

        LuckyPillarPlayer current = game.getPlayer(player);
        if (current != null) {
            game.respawnAsSpectator(current);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals(game.getGameWorld().getName())) {
            player.teleport(game.getGameWorld().getSpawnLocation());
        }

        GameState state = game.getStateManager().getCurrentState();
        boolean canJoinAsReady = (state == GameState.WAITING || state == GameState.STARTING)
                && game.getPlayers().size() < game.getMaxPlayer();

        if (canJoinAsReady && game.addPlayer(player)) {
            return;
        }

        game.addQueuedSpectator(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);

        if ((game.getStateManager().isWaiting() || game.getStateManager().isStarting()) && lpPlayer != null) {
            Location loc = event.getTo().clone();
            loc.setX(event.getFrom().getX());
            loc.setY(event.getFrom().getY());
            loc.setZ(event.getFrom().getZ());
            event.setTo(loc);
            return;
        }

        if (lpPlayer == null || lpPlayer.getState() != PlayerState.ALIVE) {
            return;
        }

        if (event.getTo().getY() < game.getConfig().getVoidDeathHeight()) {
            if (game.getAlivePlayers().size() == 1) {
                lpPlayer.getBukkitPlayer().teleport(lpPlayer.getAssignedPillar().getTopLocation());
                return;
            }
            VersionCompat.playVoidFallSound(player);
            game.killPlayer(lpPlayer, "坠入虚空");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();

        if (game.isInGame(player)) {
            game.removePlayer(player);
            if (game.getStateManager().isWaiting() || game.getStateManager().isStarting()) {
                game.getPlayers().remove(player.getUniqueId());
                game.promoteFirstQueuedSpectator();
            }
            return;
        }

        game.removeSpectator(player.getUniqueId());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!game.getConfig().isHungerEnabled()) {
            event.setCancelled(true);
            event.getEntity().setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!game.isInGame(player) && !game.isSpectator(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(SkyLuckyPillar.getInstance(), () -> {
            player.spigot().respawn();
            if (game.getCenter() != null) {
                player.teleport(game.getCenter());
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        if (lpPlayer != null && lpPlayer.getState() == PlayerState.DEAD) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        boolean isSpectator = (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)
                || game.isSpectator(player);

        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || isSpectator) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        boolean isSpectator = (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)
                || game.isSpectator(player);

        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || isSpectator) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (game.isSetupMode()) {
            return;
        }

        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        boolean isSpectator = (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)
                || game.isSpectator(player);

        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || isSpectator) {
            event.setCancelled(true);
        }
    }
}
