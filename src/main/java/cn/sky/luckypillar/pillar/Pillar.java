package cn.sky.luckypillar.pillar;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

/**
 * 柱子数据类
 */
@Data
public class Pillar {
    /**
     * 柱子唯一标识
     */
    private String id;
    
    /**
     * 柱子顶部位置（玩家出生点）
     */
    private Location topLocation;
    
    /**
     * 柱子底部中心位置
     */
    private Location bottomCenter;
    
    /**
     * 柱子高度
     */
    private int height;
    
    /**
     * 平台大小（半径）
     */
    private int platformSize;
    
    /**
     * 平台材质
     */
    private Material platformMaterial;
    
    /**
     * 是否被占用
     */
    private boolean occupied;
    
    /**
     * 占用玩家的UUID
     */
    private UUID occupyingPlayer;
    
    public Pillar(String id, Location topLocation, Location bottomCenter, int height, int platformSize, Material platformMaterial) {
        this.id = id;
        this.topLocation = topLocation;
        this.bottomCenter = bottomCenter;
        this.height = height;
        this.platformSize = platformSize;
        this.platformMaterial = platformMaterial;
        this.occupied = false;
        this.occupyingPlayer = null;
    }
    
    /**
     * 分配玩家到此柱子
     */
    public void assignPlayer(UUID playerUuid) {
        this.occupied = true;
        this.occupyingPlayer = playerUuid;
    }
    
    /**
     * 释放柱子
     */
    public void release() {
        this.occupied = false;
        this.occupyingPlayer = null;
    }
}
