package cn.sky.luckypillar.item;

import cn.sky.luckypillar.utils.chat.CC;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * 物品池类
 * 管理游戏中的物品配置和权重
 */
@Data
public class ItemPool {

    private final JavaPlugin plugin;
    private final Map<ItemCategory, List<WeightedItem>> itemsByCategory;
    private final List<ItemStack> starterKit;

    public ItemPool(JavaPlugin plugin) {
        this.plugin = plugin;
        this.itemsByCategory = new EnumMap<>(ItemCategory.class);
        this.starterKit = new ArrayList<>();

        // 初始化每个分类的列表
        for (ItemCategory category : ItemCategory.values()) {
            itemsByCategory.put(category, new ArrayList<>());
        }
    }

    /**
     * 从配置文件加载物品池
     */
    public void loadFromConfig(ConfigurationSection config) {
        if (config == null) {
            CC.warn("&c物品配置为空，使用默认配置");
            loadDefaultItems();
            return;
        }

        // 加载初始装备
        loadStarterKit(config.getConfigurationSection("starter-kit"));

        // 加载物品池
        ConfigurationSection poolSection = config.getConfigurationSection("item-pool");
        if (poolSection != null) {
            loadCategoryItems(poolSection, "weapons", ItemCategory.WEAPON);
            loadCategoryItems(poolSection, "armor", ItemCategory.ARMOR);
            loadCategoryItems(poolSection, "blocks", ItemCategory.BLOCK);
            loadCategoryItems(poolSection, "food", ItemCategory.FOOD);
            loadCategoryItems(poolSection, "potions", ItemCategory.POTION);
            loadCategoryItems(poolSection, "tools", ItemCategory.TOOL);
            loadCategoryItems(poolSection, "special", ItemCategory.SPECIAL);
        }

        CC.send("&a物品池加载完成");
    }

    /**
     * 加载初始装备
     */
    private void loadStarterKit(ConfigurationSection section) {
        if (section == null || !section.getBoolean("enabled", true)) {
            return;
        }

        List<?> items = section.getList("items");
        if (items == null) {
            return;
        }

        for (Object obj : items) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                try {
                    String type = (String) itemMap.get("type");
                    int amount = (int) itemMap.getOrDefault("amount", 1);

                    Material material = Material.valueOf(type.toUpperCase());
                    starterKit.add(new ItemStack(material, amount));
                } catch (Exception e) {
                    CC.sendError("加载初始装备时出错: ", e);
                }
            }
        }

        CC.send("&a已加载 " + starterKit.size() + " 个初始装备");
    }

    /**
     * 加载指定分类的物品
     */
    private void loadCategoryItems(ConfigurationSection poolSection, String key, ItemCategory category) {
        List<?> items = poolSection.getList(key);
        if (items == null) {
            return;
        }

        List<WeightedItem> categoryItems = itemsByCategory.get(category);

        for (Object obj : items) {
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> itemMap = (Map<String, Object>) obj;
                try {
                    String type = (String) itemMap.get("type");
                    int weight = (int) itemMap.getOrDefault("weight", 50);
                    int amount = (int) itemMap.getOrDefault("amount", 1);
                    int minAmount = (int) itemMap.getOrDefault("min-amount", amount);
                    int maxAmount = (int) itemMap.getOrDefault("max-amount", amount);

                    Material material = Material.valueOf(type.toUpperCase());
                    WeightedItem weightedItem = new WeightedItem(material, weight, minAmount, maxAmount);
                    categoryItems.add(weightedItem);
                } catch (Exception e) {
                    CC.sendError("加载物品时出错: ", e);
                }
            }
        }

        CC.send("&f已加载 &b" + categoryItems.size() + " &f个 &a" + category.name() + " &f物品");
    }

    /**
     * 加载默认物品配置
     */
    private void loadDefaultItems() {
        // 默认初始装备
        starterKit.add(new ItemStack(Material.STONE_SWORD, 1));
        starterKit.add(new ItemStack(Material.BOW, 1));
        starterKit.add(new ItemStack(Material.ARROW, 16));
        starterKit.add(new ItemStack(Material.COBBLESTONE, 64));
        starterKit.add(new ItemStack(Material.COOKED_BEEF, 8));

        // 默认武器
        List<WeightedItem> weapons = itemsByCategory.get(ItemCategory.WEAPON);
        weapons.add(new WeightedItem(Material.IRON_SWORD, 50, 1, 1));
        weapons.add(new WeightedItem(Material.DIAMOND_SWORD, 10, 1, 1));
        weapons.add(new WeightedItem(Material.BOW, 40, 1, 1));

        // 默认方块
        List<WeightedItem> blocks = itemsByCategory.get(ItemCategory.BLOCK);
        blocks.add(new WeightedItem(Material.COBBLESTONE, 100, 16, 64));
        //blocks.add(new WeightedItem(Material.OAK_PLANKS, 80, 16, 48));

        // 默认食物
        List<WeightedItem> food = itemsByCategory.get(ItemCategory.FOOD);
        food.add(new WeightedItem(Material.COOKED_BEEF, 60, 4, 12));
        food.add(new WeightedItem(Material.GOLDEN_APPLE, 20, 1, 1));
    }

    /**
     * 获取指定分类的物品列表
     */
    public List<WeightedItem> getItemsByCategory(ItemCategory category) {
        return itemsByCategory.getOrDefault(category, new ArrayList<>());
    }

    /**
     * 带权重的物品类
     */
    public record WeightedItem(Material material, int weight, int minAmount, int maxAmount) {
        /**
         * 生成物品堆
         */
        public ItemStack createItemStack(Random random) {
            int amount = minAmount;
            if (maxAmount > minAmount) {
                amount = minAmount + random.nextInt(maxAmount - minAmount + 1);
            }
            return new ItemStack(material, amount);
        }
    }
}
