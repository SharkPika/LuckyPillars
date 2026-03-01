package cn.sky.luckypillar.utils.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CC {
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void send(CommandSender sender, String message, Object ... objects) {
        message = String.format(message, objects);
        message = CC.translate(message);
        sender.sendMessage(message);
    }

    public static void send(String message, Object ... objects) {
        message = String.format(message, objects);
        message = CC.translate(message);
        console.sendMessage("§bSkyLuckyPillar §7>> " + message);
    }

    public static void broadcast(String message,Object ... objects) {
        Bukkit.getOnlinePlayers().forEach(player -> CC.send(player, message, objects));
    }

    public static void broadcast(String message, String permission, Object ... objects) {
        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission) || permission.isEmpty()).forEach(player -> CC.send(player, message, objects));
    }

    public static void debug(String message, Object ... objects) {
        message = String.format(message, objects);
        message = CC.translate(message);
        console.sendMessage("§bSkyLuckyPillar-§7Debug §e" + message);
    }

    public static void warn(String message, Object ... objects) {
        message = String.format(message, objects);
        message = CC.translate(message);
        console.sendMessage("§bSkyLuckyPillar-§6Warn §7>> " + message);
    }

    public static void sendError(String message, Throwable e) {
        if (message != null) {
            console.sendMessage(CC.translate(message));
        }
        e.printStackTrace();
        console.sendMessage(CC.translate("&c" + e.getMessage()));
        for (StackTraceElement element : e.getStackTrace()) {
            console.sendMessage(CC.translate("&cAt " + element));
        }
        console.sendMessage(CC.translate("&c发生了一个错误，请完整截图此信息并联系开发者！"));
    }

    public static void sendError(String message, CommandSender sender, Throwable e) {
        if (message != null) {
            console.sendMessage(CC.translate(message));
        }
        console.sendMessage(CC.translate("&c" + e.getMessage()));
        for (StackTraceElement element : e.getStackTrace()) {
            console.sendMessage(CC.translate("&cAt " + element));
            sender.sendMessage(CC.translate("&cAt " + element));
        }
        sender.sendMessage(CC.translate("&c发生了一个错误，请完整截图此信息并联系开发者！"));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> lines) {
        return lines.stream().map(CC::translate).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> translate(String[] lines) {
        return Arrays.stream(lines).filter(Objects::nonNull).map(CC::translate).collect(Collectors.toCollection(ArrayList::new));
    }

    public static void sendError(CommandSender sender, Throwable e) {
        if (sender != null) {
            sender.sendMessage(translate(throwableToString(e).replace("\r", "").replace("\t", "  ")));
        }
    }

    public static void sendError(Player player, Throwable e) {
        if (player != null) {
            player.sendMessage(translate(throwableToString(e).replace("\r", "").replace("\t", "  ")));
        }
    }

    private static String throwableToString(Throwable t) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final PrintStream temp = new PrintStream(out, true);
            t.printStackTrace(temp);
            return out.toString("UTF-8");
        } catch (final Throwable e) {
            return throwableToString(e);
        }
    }
}
