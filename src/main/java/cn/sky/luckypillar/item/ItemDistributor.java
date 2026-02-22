package cn.sky.luckypillar.item;

import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * 物品分发器
 * 负责向玩家分发物品
 */
public class ItemDistributor {
    
    private final JavaPlugin plugin;
    private final LuckyPillarConfig config;
    @Getter
    private final ItemPool itemPool;
    private final Random random;
    
    public ItemDistributor(JavaPlugin plugin, LuckyPillarConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.itemPool = new ItemPool(plugin);
        this.random = new Random();
        
        loadItemPool();
    }
    
    /**
     * 加载物品池配置
     */
    private void loadItemPool() {
        File configFile = new File(plugin.getDataFolder(), "game.yml");
        if (!configFile.exists()) {
            CC.warn("&cgame.yml 不存在，使用默认物品配置");
            itemPool.loadFromConfig(null);
            return;
        }
        
        FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(configFile);
        itemPool.loadFromConfig(gameConfig.getConfigurationSection("items"));
    }
    
    /**
     * 给玩家分发初始装备
     */
    public void distributeStarterKit(Player player) {
        player.getInventory().clear();
        
        for (ItemStack item : itemPool.getStarterKit()) {
            player.getInventory().addItem(item.clone());
        }
        
        CC.send("&a已向玩家 " + player.getName() + " 分发初始装备");
    }
    
    /**
     * 给所有玩家分发初始装备
     */
    public void distributeStarterKitToAll(List<LuckyPillarPlayer> players) {
        for (LuckyPillarPlayer lpPlayer : players) {
            if (lpPlayer.isOnline()) {
                distributeStarterKit(lpPlayer.getBukkitPlayer());
            }
        }
    }
    
    /**
     * 给玩家分发随机物品
     */
    public void distributeRandomItems(Player player, int count) {
        for (int i = 0; i < count; i++) {
            ItemStack item = getRandomItem();
            if (item != null) {
                player.getInventory().addItem(item);
            }
        }
    }
    
    /**
     * 从物品池中获取随机物品
     */
    public ItemStack getRandomItem() {
        // 随机选择一个分类
        ItemCategory category = ItemCategory.values()[random.nextInt(ItemCategory.values().length)];
        return getRandomItem(category);
    }
    
    /**
     * 从指定分类获取随机物品
     */
    public ItemStack getRandomItem(ItemCategory category) {
        List<ItemPool.WeightedItem> items = itemPool.getItemsByCategory(category);
        
        if (items.isEmpty()) {
            return null;
        }
        
        // 计算总权重
        int totalWeight = items.stream().mapToInt(ItemPool.WeightedItem::weight).sum();
        
        if (totalWeight == 0) {
            return null;
        }
        
        // 根据权重随机选择
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for (ItemPool.WeightedItem weightedItem : items) {
            currentWeight += weightedItem.weight();
            if (randomWeight < currentWeight) {
                return weightedItem.createItemStack(random);
            }
        }
        
        // 如果没有选中，返回第一个
        return items.get(0).createItemStack(random);
    }
    
    /**
     * 生成补给箱（用于空投事件）
     */
    public void dropSupplyCrate(Location location, int itemCount) {
        if (location == null || location.getWorld() == null) {
            CC.warn("&c无法生成补给箱：位置或世界为空");
            return;
        }
        
        // 在指定位置生成箱子
        location.getBlock().setType(Material.CHEST);
        
        // 获取箱子的库存（添加类型检查）
        BlockState state = location.getBlock().getState();
        
        if (!(state instanceof Chest chest)) {
            CC.warn("&c无法生成补给箱：方块不是箱子类型");
            return;
        }

        Inventory inventory = chest.getInventory();
        
        // 添加随机物品
        for (int i = 0; i < itemCount; i++) {
            ItemStack item = getRandomItem();
            if (item != null) {
                inventory.addItem(item);
            }
        }
        
        CC.send("&a在 " + location + " 生成了补给箱");
    }
    
    /**
     * 生成多个补给箱
     */
    public void dropMultipleSupplyCrates(List<Location> locations, int itemsPerCrate) {
        for (Location location : locations) {
            dropSupplyCrate(location, itemsPerCrate);
        }
    }

    /**
     * 重新加载物品池
     */
    public void reload() {
        loadItemPool();
    }
}
