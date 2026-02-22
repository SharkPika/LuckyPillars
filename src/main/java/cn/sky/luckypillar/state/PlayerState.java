package cn.sky.luckypillar.state;

/**
 * 玩家状态枚举
 */
public enum PlayerState {
    /**
     * 等待大厅
     */
    WAITING,
    
    /**
     * 存活中
     */
    ALIVE,
    
    /**
     * 已死亡
     */
    DEAD,
    
    /**
     * 观战中
     */
    SPECTATING
}
