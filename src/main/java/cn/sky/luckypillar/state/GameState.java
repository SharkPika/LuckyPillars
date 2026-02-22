package cn.sky.luckypillar.state;

/**
 * 游戏状态枚举
 */
public enum GameState {
    /**
     * 等待玩家加入
     */
    WAITING,
    
    /**
     * 游戏即将开始（倒计时中）
     */
    STARTING,
    
    /**
     * 游戏进行中
     */
    PLAYING,
    
    /**
     * 游戏结束（展示胜利者）
     */
    ENDING
}
