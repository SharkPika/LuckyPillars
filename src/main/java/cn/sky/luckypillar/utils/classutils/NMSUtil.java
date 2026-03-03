package cn.sky.luckypillar.utils.classutils;

import cn.sky.luckypillar.utils.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtil {
	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (Exception e) {
			CC.sendError("&c获取 NMS 类失败: " + name, e);
			return null;
		}
	}

	public static Class<?> getClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (Exception e) {
			CC.sendError("&c获取 CraftBukkit 类失败: " + name, e);
			return null;
		}
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			CC.sendError("&c发送 NMS 数据包失败: " + player.getName(), e);
		}
	}
}
