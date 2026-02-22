package cn.sky.luckypillar.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {
	private static int dontStack = 0;
	private ItemStack is;

	public ItemBuilder(Material mat) {
		this.is = new ItemStack(mat);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;
	}

	public ItemBuilder material(Material mat) {
		this.is = new ItemStack(mat);
		return this;
	}

	public ItemBuilder amount(int amount) {
		this.is.setAmount(amount);
		return this;
	}

	public ItemBuilder name(String name) {
		ItemMeta meta = this.is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder setLetherColor(Color color) {
		LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
		im.setColor(color);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setSkullOwner(String owner) {
		SkullMeta im = (SkullMeta) is.getItemMeta();
		im.setOwner(owner);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setSkullProperty(String texture) {
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		GameProfile gp = new GameProfile(UUID.randomUUID(), null);
		gp.getProperties().put("textures", new Property("textures", texture));
		try {
			Field field = meta.getClass().getDeclaredField("profile");
			field.setAccessible(true);
			field.set(meta, gp);
		} catch (Exception ignored) {
		}
		is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(String name) {
		ItemMeta meta = this.is.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		}

		lore.add(ChatColor.translateAlternateColorCodes('&', name));
		meta.setLore(lore);
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder lore(String... lore) {
		ItemMeta meta = this.is.getItemMeta();
		meta.setLore(Arrays.stream(lore).map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList()));
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder enchant(Enchantment enchantment, int i) {
		this.is.addUnsafeEnchantment(enchantment, i);
		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		ItemMeta meta = this.is.getItemMeta();
		meta.setLore(lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList()));
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder durability(int durability) {
		this.is.setDurability((short) durability);
		return this;
	}
	/*public ItemBuilder recordEnchantments(List<EnchantmentRecord> records) {
		StringBuilder builder = new StringBuilder();
		if (records.size() > 5) {
			records = records.subList(records.size() - 5, records.size());
		}
		for (EnchantmentRecord record : records) {
			if (!builder.isEmpty()) {
				builder.append(";");
			}
			builder.append(record.getEnchanter()).append("|").append(record.getDescription()).append("|").append(record.getTimestamp());
		}
		this.changeNbt("records", builder.toString());
		return this;
	}*/

	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		this.is.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemBuilder enchantment(Enchantment enchantment) {
		this.is.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemBuilder shiny() {
		return this.enchant(Enchantment.LURE, 1).flags(ItemFlag.values());
	}

	public ItemBuilder flags(ItemFlag... flags) {
		ItemMeta itemMeta = this.is.getItemMeta();
		itemMeta.addItemFlags(flags);
		this.is.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder type(Material material) {
		this.is.setType(material);
		return this;
	}

	public ItemBuilder clearLore() {
		ItemMeta meta = this.is.getItemMeta();
		meta.setLore(new ArrayList<>());
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemBuilder clearEnchantments() {
        this.is.getEnchantments().keySet().forEach(e -> this.is.removeEnchantment(e));
		return this;
	}

	public ItemBuilder addPotionEffect(PotionEffect effect, boolean b) {
		if (this.is.getItemMeta() instanceof PotionMeta) {
			final PotionMeta meta = (PotionMeta) this.is.getItemMeta();
			meta.addCustomEffect(effect, b);
			this.is.setItemMeta(meta);
		}

		return this;
	}
}
