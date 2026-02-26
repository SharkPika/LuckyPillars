package cn.sky.luckypillar.listener;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.state.PlayerState;
import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * 方块事件监听器
 */
public class BlockListener implements Listener {
    
    private final LuckyPillarGame game;
    
    public BlockListener(LuckyPillarGame game) {
        this.game = game;
    }
    
    /**
     * 方块破坏事件
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (game.isSetupMode()) return;
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        // 不在游戏中的玩家不受限制
        if (lpPlayer == null) {
            return;
        }
        
        // 观战者不能破坏方块
        if (lpPlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
            return;
        }
        
        // 非游戏中状态不能破坏方块
        if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
            event.setCancelled(true);
            return;
        }
        
        // 检查是否允许破坏方块
        if (!game.getConfig().isAllowBlockBreak()) {
            event.setCancelled(true);
        }
    }
    
    /**
     * 方块放置事件
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (game.isSetupMode()) return;
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        // 不在游戏中的玩家不受限制
        if (lpPlayer == null) {
            return;
        }
        
        // 观战者不能放置方块
        if (lpPlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
            return;
        }
        
        // 非游戏中状态不能放置方块
        if (game.getStateManager().getCurrentState() != GameState.PLAYING) {
            event.setCancelled(true);
            return;
        }
        
        // 检查是否允许放置方块
        if (!game.getConfig().isAllowBlockPlace()) {
            event.setCancelled(true);
            return;
        }
        
        // 检查高度限制
        int y = event.getBlock().getY();
        if (y > game.getConfig().getMaxBuildHeight()) {
            event.setCancelled(true);
            CC.send(player, "&c不能在这个高度放置方块！");
            return;
        }
        
        if (y < game.getConfig().getMinBuildHeight()) {
            event.setCancelled(true);
            CC.send(player, "&c不能在这个高度放置方块！");
        }
    }
}
