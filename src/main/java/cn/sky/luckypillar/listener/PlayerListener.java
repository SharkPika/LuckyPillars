package cn.sky.luckypillar.listener;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.state.PlayerState;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

@AllArgsConstructor
public class PlayerListener implements Listener {
    
    private final LuckyPillarGame game;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if ((game.getStateManager().isWaiting() || game.getStateManager().isStarting()) && Bukkit.getOnlinePlayers().size() >= game.getMaxPlayer()) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, "§c房间人数已满");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        player.getInventory().clear();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);

        if (lpPlayer != null) {
            game.respawnAsSpectator(lpPlayer);
            return;
        }

        if (Bukkit.getOnlinePlayers().size() > game.getMaxPlayer()) {
            LuckyPillarPlayer lpSpectator = new LuckyPillarPlayer(player);
            game.respawnAsSpectator(lpSpectator);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals(game.getGameWorld().getName())) {
            player.teleport(game.getGameWorld().getSpawnLocation());
        }

        game.addPlayer(player);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
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
            game.killPlayer(lpPlayer, "掉入虚空");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (game.isInGame(player)) {
            game.removePlayer(player);
            if (game.getStateManager().isWaiting() || game.getStateManager().isStarting()) {
                game.getPlayers().remove(player.getUniqueId());
                game.getSpectators().stream().filter(spectator -> !spectator.getUuid().equals(player.getUniqueId()) && !game.getPlayers().containsKey(player.getUniqueId()))
                    .findFirst()
                    .ifPresent(spectator -> {
                        game.getSpectators().removeIf(s -> s.getUuid().equals(spectator.getUuid()));
                        game.addPlayer(spectator.getBukkitPlayer());
                    });
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity().getFoodLevel() != 20) {
            event.getEntity().setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        if (lpPlayer != null && lpPlayer.getState() == PlayerState.DEAD) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);

        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        LuckyPillarPlayer lpPlayer = game.getPlayer(player);

        if (game.getStateManager().isWaiting() || game.getStateManager().isStarting() || (lpPlayer != null && lpPlayer.getState() == PlayerState.SPECTATING)) {
            event.setCancelled(true);
        }
    }
}
