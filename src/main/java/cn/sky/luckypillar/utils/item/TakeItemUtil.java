package cn.sky.luckypillar.utils.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TakeItemUtil {
	public static void TakeItem(Player player, ItemStack stack) {
		if (player.getInventory().getItemInHand() != null) {
			ItemStack itemInHand = player.getInventory().getItemInHand();
			if (itemInHand.getType() == stack.getType()) {
				itemInHand.setAmount(itemInHand.getAmount() - 1);
				player.getInventory().setItemInHand(itemInHand);
				return;
			}
		}

		ItemMeta meta = stack.getItemMeta();
		ItemStack itemStack = new ItemStack(stack.getType(), 1);
		itemStack.setItemMeta(meta);
		player.getInventory().removeItem(itemStack);
	}
}
