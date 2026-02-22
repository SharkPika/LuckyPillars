package cn.sky.luckypillar.state;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 游戏状态管理器
 * 负责管理游戏状态的转换和验证
 */
@Getter
public class GameStateManager {
    
    private final JavaPlugin plugin;
    private final LuckyPillarGame game;
    private GameState currentState;
    
    public GameStateManager(JavaPlugin plugin, LuckyPillarGame game) {
        this.plugin = plugin;
        this.game = game;
        this.currentState = GameState.WAITING;
    }
    
    /**
     * 改变游戏状态
     */
    public void changeState(GameState newState) {
        if (currentState == newState) {
            return;
        }
        
        GameState oldState = currentState;
        
        // 验证状态转换是否合法
        if (!isValidTransition(oldState, newState)) {
            CC.warn("&c非法的状态转换: &e" + oldState + " &7-> &c" + newState);
            return;
        }
        
        // 执行状态退出逻辑
        onStateExit(oldState);
        
        // 更新状态
        currentState = newState;
        CC.send("&a游戏状态变更: &e" + oldState + " &7-> &b" + newState);
        
        // 执行状态进入逻辑
        onStateEnter(newState);
    }
    
    /**
     * 验证状态转换是否合法
     */
    private boolean isValidTransition(GameState from, GameState to) {
        return switch (from) {
            case WAITING -> to == GameState.STARTING;
            case STARTING -> to == GameState.PLAYING || to == GameState.WAITING;
            case PLAYING -> to == GameState.ENDING;
            case ENDING -> to == GameState.WAITING;
        };
    }
    
    /**
     * 状态退出时的处理
     */
    private void onStateExit(GameState state) {
        switch (state) {
            case WAITING -> {
                // 退出等待状态
            }
            case STARTING -> {
                // 退出开始倒计时状态
            }
            case PLAYING -> {
                // 退出游戏中状态
            }
            case ENDING -> {
                // 退出结束状态
            }
        }
    }
    
    /**
     * 状态进入时的处理
     */
    private void onStateEnter(GameState state) {
        switch (state) {
            case WAITING -> {
                // 进入等待状态
                game.onEnterWaiting();
            }
            case STARTING -> {
                // 进入开始倒计时状态
                game.onEnterStarting();
            }
            case PLAYING -> {
                // 进入游戏中状态
                game.onEnterPlaying();
            }
            case ENDING -> {
                // 进入结束状态
                game.onEnterEnding();
            }
        }
    }
    
    /**
     * 检查是否可以开始游戏
     */
    public boolean canStartGame() {
        return currentState == GameState.WAITING;
    }
    
    /**
     * 检查游戏是否正在进行
     */
    public boolean isGameRunning() {
        return currentState == GameState.PLAYING;
    }
    
    /**
     * 检查游戏是否在等待
     */
    public boolean isWaiting() {
        return currentState == GameState.WAITING;
    }
    
    /**
     * 检查游戏是否在倒计时
     */
    public boolean isStarting() {
        return currentState == GameState.STARTING;
    }
    
    /**
     * 检查游戏是否已结束
     */
    public boolean isEnding() {
        return currentState == GameState.ENDING;
    }
    
    /**
     * 重置状态到等待
     */
    public void reset() {
        currentState = GameState.WAITING;
        CC.send("&a游戏状态已重置为 &eWAITING");
    }
}
