package cn.sky.luckypillar.utils.chat;

import cn.sky.luckypillar.utils.classutils.NMSUtil;
import cn.sky.luckypillar.utils.classutils.ReflectUtil;
import cn.sky.luckypillar.utils.version.VersionManager;
import org.bukkit.entity.Player;

public class Title {

    /**
     * 给一个玩家发送Title信息
     *
     * @param player   发送的玩家
     * @param fadeIn   淡入时间
     * @param stay     停留时间
     * @param fadeOut  淡出时间
     * @param title    主标题
     * @param subtitle 副标题
     */

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (VersionManager.is1_17OrAbove()) {
            sendTitleAbove1_17(player, title, subtitle, fadeIn, stay, fadeOut);
            return;
        }
        sendTitleUnder1_17(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitleUnder1_17(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            Class<?> PacketPlayOutTitle = NMSUtil.getNMSClass("PacketPlayOutTitle");
            Class<?> EnumTitleAction = NMSUtil.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            Class<?> IChatBaseComponent = NMSUtil.getNMSClass("IChatBaseComponent");
            Class<?> ChatSerializer = NMSUtil.getNMSClass("IChatBaseComponent$ChatSerializer");
            Object length;
            Object tit;
            Object bc;
            if (title != null && !title.isEmpty()) {
                title = CC.translate(title);
                bc = ChatSerializer.getMethod("a", String.class).invoke(null, "{'text': '" + title + "'}");
                tit = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent)
                        .newInstance(Enum.valueOf((Class<Enum>) EnumTitleAction, "TITLE"), bc);
                length = PacketPlayOutTitle.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
                NMSUtil.sendPacket(player, tit);
                NMSUtil.sendPacket(player, length);
            }
            subtitle = CC.translate(subtitle);
            bc = ChatSerializer.getMethod("a", String.class).invoke(null, "{'text': '" + subtitle + "'}");
            tit = PacketPlayOutTitle.getConstructor(EnumTitleAction, IChatBaseComponent)
                    .newInstance(Enum.valueOf((Class<Enum>) EnumTitleAction, "SUBTITLE"), bc);
            length = PacketPlayOutTitle.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
            NMSUtil.sendPacket(player, tit);
            NMSUtil.sendPacket(player, length);
        } catch (Throwable e) {
            CC.sendError("&fTitle发送失败: ", e);
        }
    }

    public static void sendTitleAbove1_17(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Class<?> clazz = player.getClass();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            ReflectUtil.invokeMethod(instance, "sendTitle", new Class[]{String.class, String.class, int.class, int.class, int.class}, title, subtitle, fadeIn, stay, fadeOut);
        } catch (Throwable e) {
            CC.sendError("&fTitle发送失败: ", e);
        }
    }
}
