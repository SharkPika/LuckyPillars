package cn.sky.luckypillar.event;

import cn.sky.luckypillar.game.LuckyPillarGame;

/**
 * 游戏事件接口
 * 所有游戏事件都需要实现此接口
 */
public interface GameEvent {
    
    /**
     * 获取事件ID
     */
    String getId();
    
    /**
     * 获取事件名称
     */
    String getName();
    
    /**
     * 检查事件是否启用
     */
    boolean isEnabled();
    
    /**
     * 获取事件持续时间（秒）
     */
    int getDuration();
    
    /**
     * 事件开始时调用
     */
    void onStart(LuckyPillarGame game);
    
    /**
     * 事件每tick调用（每秒调用一次）
     */
    void onTick(LuckyPillarGame game);
    
    /**
     * 事件结束时调用
     */
    void onEnd(LuckyPillarGame game);
    
    /**
     * 向玩家宣布事件
     */
    void announce(LuckyPillarGame game);
}
