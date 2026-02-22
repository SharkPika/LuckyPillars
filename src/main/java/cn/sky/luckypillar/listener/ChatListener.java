package cn.sky.luckypillar.listener;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.state.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天事件监听器
 */
public class ChatListener implements Listener {
    
    private final LuckyPillarGame game;
    private final SkyConfig skyConfig;
    
    public ChatListener(LuckyPillarGame game, SkyConfig skyConfig) {
        this.game = game;
        this.skyConfig = skyConfig;
    }
    
    /**
     * 玩家聊天事件
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);
        
        if (lpPlayer == null) {
            return;
        }
        
        // 取消原始消息
        event.setCancelled(true);
        
        // 根据玩家状态格式化聊天消息
        String format = getChatFormat(lpPlayer.getState());
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player_name", player.getName());
        placeholders.put("message", event.getMessage());
        
        String formattedMessage = skyConfig.format(format, placeholders);
        
        // 广播消息
        game.broadcast(formattedMessage);
    }
    
    /**
     * 根据玩家状态获取聊天格式
     */
    private String getChatFormat(PlayerState state) {
        return switch (state) {
            case WAITING -> skyConfig.getChatFormatWaiting();
            case ALIVE -> skyConfig.getChatFormatPlaying();
            case DEAD -> skyConfig.getChatFormatDead();
            case SPECTATING -> skyConfig.getChatFormatSpectating();
        };
    }
}
