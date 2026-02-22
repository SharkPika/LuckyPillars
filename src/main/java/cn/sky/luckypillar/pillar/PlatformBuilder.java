package cn.sky.luckypillar.pillar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * 平台建造工具类
 * 负责构建和清理柱子底部的平台
 */
public class PlatformBuilder {
    
    /**
     * 构建平台
     * 
     * @param pillar 柱子对象
     */
    public void buildPlatform(Pillar pillar) {
        Location center = pillar.getBottomCenter();
        int radius = pillar.getPlatformSize();
        Material material = pillar.getPlatformMaterial();
        World world = center.getWorld();
        
        if (world == null) {
            return;
        }
        
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        
        // 在底部中心位置构建圆形平台
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 计算距离中心的距离
                double distance = Math.sqrt(x * x + z * z);
                
                // 只在半径范围内放置方块
                if (distance <= radius) {
                    Block block = world.getBlockAt(centerX + x, centerY, centerZ + z);
                    block.setType(material);
                }
            }
        }
    }
    
    /**
     * 清理平台
     * 
     * @param pillar 柱子对象
     */
    public void clearPlatform(Pillar pillar) {
        Location center = pillar.getBottomCenter();
        int radius = pillar.getPlatformSize();
        World world = center.getWorld();
        
        if (world == null) {
            return;
        }
        
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        
        // 清除平台范围内的所有方块
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                
                if (distance <= radius) {
                    Block block = world.getBlockAt(centerX + x, centerY, centerZ + z);
                    block.setType(Material.AIR);
                }
            }
        }
    }
    
    /**
     * 获取平台上的所有方块
     * 
     * @param pillar 柱子对象
     * @return 方块列表
     */
    public List<Block> getPlatformBlocks(Pillar pillar) {
        List<Block> blocks = new ArrayList<>();
        Location center = pillar.getBottomCenter();
        int radius = pillar.getPlatformSize();
        World world = center.getWorld();
        
        if (world == null) {
            return blocks;
        }
        
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                
                if (distance <= radius) {
                    Block block = world.getBlockAt(centerX + x, centerY, centerZ + z);
                    if (block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }
        
        return blocks;
    }
    
    /**
     * 移除平台上的随机方块（用于方块衰减事件）
     * 
     * @param pillar 柱子对象
     * @param excludeMaterials 排除的材质列表
     * @return 是否成功移除方块
     */
    public boolean removeRandomBlock(Pillar pillar, List<Material> excludeMaterials) {
        List<Block> blocks = getPlatformBlocks(pillar);
        
        if (blocks.isEmpty()) {
            return false;
        }
        
        // 过滤掉排除的材质
        blocks.removeIf(block -> excludeMaterials.contains(block.getType()));
        
        if (blocks.isEmpty()) {
            return false;
        }
        
        // 随机选择一个方块并移除
        Block randomBlock = blocks.get((int) (Math.random() * blocks.size()));
        randomBlock.setType(Material.AIR);
        
        return true;
    }
    
    /**
     * 检查位置是否在平台范围内
     * 
     * @param pillar 柱子对象
     * @param location 要检查的位置
     * @return 是否在平台范围内
     */
    public boolean isOnPlatform(Pillar pillar, Location location) {
        Location center = pillar.getBottomCenter();
        int radius = pillar.getPlatformSize();
        
        if (!location.getWorld().equals(center.getWorld())) {
            return false;
        }
        
        if (location.getBlockY() != center.getBlockY()) {
            return false;
        }
        
        double distance = Math.sqrt(
            Math.pow(location.getBlockX() - center.getBlockX(), 2) +
            Math.pow(location.getBlockZ() - center.getBlockZ(), 2)
        );
        
        return distance <= radius;
    }
}
