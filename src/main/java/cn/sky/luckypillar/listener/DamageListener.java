package cn.sky.luckypillar.listener;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.state.PlayerState;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DamageListener implements Listener {
    
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (game.isSetupMode()) {
            event.setCancelled(true);
            return;
        }
        
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        if (lpPlayer == null) {
            return;
        }
        
        // 观战者不受伤害
        if (lpPlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
            return;
        }
        
        // 非游戏中状态不受伤害
        if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
            event.setCancelled(true);
            return;
        }
        
        // 检查伤害类型
        EntityDamageEvent.DamageCause cause = event.getCause();
        
        // 掉落伤害
        if (cause == EntityDamageEvent.DamageCause.FALL && !game.getConfig().isFallDamage()) {
            event.setCancelled(true);
            return;
        }
        
        // 火焰伤害
        if ((cause == EntityDamageEvent.DamageCause.FIRE || 
             cause == EntityDamageEvent.DamageCause.FIRE_TICK || 
             cause == EntityDamageEvent.DamageCause.LAVA) && 
            !game.getConfig().isFireDamage()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        
        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }
        if (game.isSetupMode()) {
            event.setCancelled(true);
            return;
        }
        
        LuckyPillarPlayer victimPlayer = game.getPlayer(victim);
        LuckyPillarPlayer attackerPlayer = game.getPlayer(attacker);
        
        if (victimPlayer == null || attackerPlayer == null) {
            return;
        }
        
        // 非游戏中状态不能PvP
        if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
            event.setCancelled(true);
            return;
        }
        
        // 检查是否启用PvP
        if (!game.getConfig().isPvpEnabled()) {
            event.setCancelled(true);
            return;
        }
        
        // 观战者不能攻击或被攻击
        if (victimPlayer.getState() == PlayerState.SPECTATING || 
            attackerPlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        if (lpPlayer == null || lpPlayer.getState() != PlayerState.ALIVE) {
            return;
        }
        
        // 检查是否保留物品
        if (game.getConfig().isKeepInventory()) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        } else if (!game.getConfig().isDropItemsOnDeath()) {
            event.getDrops().clear();
        }
        
        // 检查击杀者
        Player killer = player.getKiller();
        String deathReason;
        
        if (killer != null) {
            LuckyPillarPlayer killerPlayer = game.getPlayer(killer);
            if (killerPlayer != null) {
                killerPlayer.addKill();
                
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("player", player.getName());
                placeholders.put("killer", killer.getName());
                game.broadcast(skyConfig.format(skyConfig.getPlayerKilled(), placeholders));
                
                deathReason = "被 " + killer.getName() + " 击杀";
            } else {
                deathReason = "死亡";
            }
        } else {
            // 获取死亡原因
            EntityDamageEvent lastDamage = player.getLastDamageCause();
            if (lastDamage != null) {
                deathReason = switch (lastDamage.getCause()) {
                    case FALL -> "摔落死亡";
                    case LAVA -> "被岩浆烧死";
                    case FIRE, FIRE_TICK -> "被火烧死";
                    case DROWNING -> "溺水";
                    case SUFFOCATION -> "窒息";
                    case VOID -> "掉入虚空";
                    default -> "死亡";
                };
            } else {
                deathReason = "死亡";
            }
        }
        
        // 处理玩家死亡
        game.killPlayer(lpPlayer, deathReason);
        
        // 清空死亡消息
        event.setDeathMessage(null);
    }
}
