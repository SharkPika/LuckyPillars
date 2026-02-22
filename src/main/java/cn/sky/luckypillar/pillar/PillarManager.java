package cn.sky.luckypillar.pillar;

import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.config.serializer.LocationSerializer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 柱子管理器
 * 负责管理所有柱子的加载、保存、分配等操作
 */
@Getter
public class PillarManager {
    
    private final JavaPlugin plugin;
    private final LuckyPillarConfig config;
    private final PlatformBuilder platformBuilder;
    private final List<Pillar> pillars;
    private final LocationSerializer locationSerializer;
    
    public PillarManager(JavaPlugin plugin, LuckyPillarConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.platformBuilder = new PlatformBuilder();
        this.pillars = new ArrayList<>();
        this.locationSerializer = new LocationSerializer();
    }
    
    /**
     * 从配置文件加载柱子
     */
    public void loadPillarsFromConfig() {
        pillars.clear();
        
        File configFile = new File(plugin.getDataFolder(), "game.yml");
        if (!configFile.exists()) {
            CC.warn("&cgame.yml 不存在，无法加载柱子配置");
            return;
        }
        
        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection pillarsSection = gameConfig.getConfigurationSection("pillars.locations");
        
        if (pillarsSection == null) {
            CC.warn("&c未找到柱子配置，将使用空列表");
            return;
        }
        
        for (String key : pillarsSection.getKeys(false)) {
            ConfigurationSection pillarSection = pillarsSection.getConfigurationSection(key);
            if (pillarSection == null) continue;
            
            try {
                String id = pillarSection.getString("id", key);
                String topStr = pillarSection.getString("top");
                String bottomStr = pillarSection.getString("bottom");
                int height = pillarSection.getInt("height", config.getDefaultHeight());
                int platformSize = pillarSection.getInt("platform-size", config.getDefaultPlatformSize());
                String materialStr = pillarSection.getString("platform-material", config.getDefaultPlatformMaterial());
                
                if (topStr == null || bottomStr == null) {
                    CC.warn("&c柱子 " + id + " 配置不完整，跳过");
                    continue;
                }
                
                Location topLocation = locationSerializer.deserialize(topStr);
                Location bottomCenter = locationSerializer.deserialize(bottomStr);
                Material material = Material.valueOf(materialStr.toUpperCase());
                
                Pillar pillar = new Pillar(id, topLocation, bottomCenter, height, platformSize, material);
                pillars.add(pillar);
                
                CC.send("&a已加载柱子: &b" + id);
            } catch (Exception e) {
                CC.send("&c加载柱子 " + key + " 时出错", e);
            }
        }

        CC.send("&f共加载 &b" + pillars.size() + " &f个柱子");
    }
    
    /**
     * 保存柱子到配置文件
     */
    public void savePillar(Pillar pillar) {
        File configFile = new File(plugin.getDataFolder(), "game.yml");
        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(configFile);
        
        String path = "pillars.locations." + pillar.getId();
        gameConfig.set(path + ".id", pillar.getId());
        gameConfig.set(path + ".top", locationSerializer.serialize(pillar.getTopLocation()));
        gameConfig.set(path + ".bottom", locationSerializer.serialize(pillar.getBottomCenter()));
        gameConfig.set(path + ".height", pillar.getHeight());
        gameConfig.set(path + ".platform-size", pillar.getPlatformSize());
        gameConfig.set(path + ".platform-material", pillar.getPlatformMaterial().name());
        
        try {
            gameConfig.save(configFile);
            CC.send("&a已保存柱子: " + pillar.getId());
        } catch (IOException e) {
            CC.send("&c保存柱子配置时出错", e);
        }
    }
    
    /**
     * 移除柱子
     */
    public boolean removePillar(String id) {
        Pillar pillar = getPillarById(id);
        if (pillar == null) {
            return false;
        }
        
        // 清理平台
        platformBuilder.clearPlatform(pillar);
        
        // 从列表中移除
        pillars.remove(pillar);
        
        // 从配置文件中移除
        File configFile = new File(plugin.getDataFolder(), "game.yml");
        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(configFile);
        gameConfig.set("pillars.locations." + id, null);
        
        try {
            gameConfig.save(configFile);
            CC.send("&a已移除柱子: " + id);
            return true;
        } catch (IOException e) {
            CC.send("&c保存配置时出错", e);
            return false;
        }
    }
    
    /**
     * 添加柱子
     */
    public void addPillar(Pillar pillar) {
        pillars.add(pillar);
        savePillar(pillar);
    }
    
    /**
     * 根据ID获取柱子
     */
    public Pillar getPillarById(String id) {
        return pillars.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取可用的柱子（未被占用）
     */
    public Pillar getAvailablePillar() {
        return pillars.stream()
                .filter(p -> !p.isOccupied())
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有可用的柱子
     */
    public List<Pillar> getAvailablePillars() {
        return pillars.stream()
                .filter(p -> !p.isOccupied())
                .toList();
    }
    
    /**
     * 分配玩家到柱子
     */
    public void assignPlayersToPillars(List<LuckyPillarPlayer> players) {
        // 释放所有柱子
        pillars.forEach(Pillar::release);
        
        // 获取可用柱子列表
        List<Pillar> availablePillars = new ArrayList<>(pillars);
        
        if (availablePillars.size() < players.size()) {
            CC.send("&c柱子数量不足！需要 " + players.size() + " 个，但只有 " + availablePillars.size() + " 个");
        }
        
        // 打乱柱子顺序以实现随机分配
        Collections.shuffle(availablePillars);
        
        // 分配玩家到柱子
        for (int i = 0; i < players.size() && i < availablePillars.size(); i++) {
            LuckyPillarPlayer player = players.get(i);
            Pillar pillar = availablePillars.get(i);
            
            player.assignPillar(pillar);
            CC.send("&a玩家 " + player.getName() + " 被分配到柱子 " + pillar.getId());
        }
    }
    
    /**
     * 构建所有平台
     */
    public void buildAllPlatforms() {
        for (Pillar pillar : pillars) {
            platformBuilder.buildPlatform(pillar);
        }
        CC.send("&a已构建 " + pillars.size() + " 个平台");
    }
    
    /**
     * 清理所有平台
     */
    public void clearAllPlatforms() {
        for (Pillar pillar : pillars) {
            platformBuilder.clearPlatform(pillar);
        }
        CC.send("&a已清理 " + pillars.size() + " 个平台");
    }
    
    /**
     * 释放所有柱子
     */
    public void releaseAllPillars() {
        pillars.forEach(Pillar::release);
    }
    
    /**
     * 检查是否有足够的柱子
     */
    public boolean hasEnoughPillars(int playerCount) {
        return pillars.size() >= playerCount;
    }
    
    /**
     * 获取柱子数量
     */
    public int getPillarCount() {
        return pillars.size();
    }
}
