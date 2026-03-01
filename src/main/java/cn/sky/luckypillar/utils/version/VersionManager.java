package cn.sky.luckypillar.utils.version;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionManager {
    public static void init() {
        if (getBukkitVersion() != null) {
            ServerVersion.setServerVersion(ServerVersion.valueOf(getBukkitVersion()));
        } else {
            ServerVersion.setServerVersion(ServerVersion.valueOfSpigotRelease(getMinecraftVersion()));
        }
    }

    public static boolean isPaperServer() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (final ClassNotFoundException ex) {
            return Bukkit.getName().equals("Paper");
        }
    }

    public static boolean is1_8OrAbove() {
        return is1_8Version() || is1_9OrAbove();
    }

    public static boolean is1_9OrAbove() {
        return is1_9Version() || is1_10OrAbove();
    }

    public static boolean is1_10OrAbove() {
        return is1_10Version() || is1_11OrAbove();
    }

    public static boolean is1_11OrAbove() {
        return is1_11Version() || is1_12OrAbove();
    }

    public static boolean is1_12OrAbove() {
        return is1_12Version() || is1_13OrAbove();
    }

    public static boolean is1_13OrAbove() {
        return is1_13Version() || is1_14OrAbove();
    }

    public static boolean is1_14OrAbove() {
        return is1_14Version() || is1_15OrAbove();
    }

    public static boolean is1_15OrAbove() {
        return is1_15Version() || is1_16OrAbove();
    }

    public static boolean is1_16OrAbove() {
        return is1_16Version() || is1_17OrAbove();
    }

    public static boolean is1_17OrAbove() {
        return is1_17Version() || is1_18OrAbove();
    }

    public static boolean is1_18OrAbove() {
        return is1_18Version() || is1_19OrAbove();
    }

    public static boolean is1_19OrAbove() {
        return is1_19Version() || is1_20OrAbove();
    }

    public static boolean is1_20OrAbove() {
        return is1_20Version() || is1_21OrAbove();
    }

     public static boolean is1_21OrAbove() {
        return is1_21Version();
    }

    public static boolean is1_8Version() {
        return is1_8_R1Version() || is1_8_R2Version() || is1_8_R3Version();
    }

    public static boolean is1_9Version() {
        return is1_9_R1Version() || is1_9_R2Version();
    }

    public static boolean is1_10Version() {
        return is1_10_R1Version();
    }

    public static boolean is1_11Version() {
        return is1_11_R1Version();
    }

    public static boolean is1_12Version() {
        return is1_12_R1Version();
    }

    public static boolean is1_13Version() {
        return is1_13_R1Version() || is1_13_R2Version();
    }

    public static boolean is1_14Version() {
        return is1_14_R1Version();
    }

    public static boolean is1_15Version() {
        return is1_15_R1Version();
    }

    public static boolean is1_16Version() {
        return is1_16_R1Version() || is1_16_R2Version() || is1_16_R3Version();
    }

    public static boolean is1_17Version() {
        return is1_17_R1Version();
    }

    public static boolean is1_18Version() {
        return is1_18_R1Version() || is1_18_R2Version();
    }

    public static boolean is1_19Version() {
        return is1_19_R1Version() || is1_19_R2Version() || is1_19_R3Version();
    }

    public static boolean is1_20Version() {
        return is1_20_R1Version() || is1_20_R2Version() || is1_20_R3Version() || is1_20_R4Version();
    }

    public static boolean is1_21Version() {
        return is1_21_R1Version() || is1_21_R2Version() || is1_21_R3Version() || is1_21_R4Version() || is1_21_R5Version() || is1_21_R6Version() || is1_21_R7Version();
    }

    public static boolean is1_8_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_8_R1);
    }

    public static boolean is1_8_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_8_R2);
    }

    public static boolean is1_8_R3Version() {
        return isVersionMinorEquals(ServerVersion.v1_8_R3);
    }

    public static boolean is1_9_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_9_R1);
    }

    public static boolean is1_9_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_9_R2);
    }

    public static boolean is1_10_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_10_R1);
    }

    public static boolean is1_11_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_11_R1);
    }

    public static boolean is1_12_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_12_R1);
    }

    public static boolean is1_13_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_13_R1);
    }

    public static boolean is1_13_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_13_R2);
    }

    public static boolean is1_14_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_14_R1);
    }

    public static boolean is1_15_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_15_R1);
    }

    public static boolean is1_16_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_16_R1);
    }

    public static boolean is1_16_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_16_R2);
    }

    public static boolean is1_16_R3Version() {
        return isVersionMinorEquals(ServerVersion.v1_16_R3);
    }

    public static boolean is1_17_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_17_R1);
    }

    public static boolean is1_18_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_18_R1);
    }

    public static boolean is1_18_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_18_R2);
    }

    public static boolean is1_19_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_19_R1);
    }

    public static boolean is1_19_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_19_R2);
    }

    public static boolean is1_19_R3Version() {
        return isVersionMinorEquals(ServerVersion.v1_19_R3);
    }

    public static boolean is1_20_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_20_R1);
    }

    public static boolean is1_20_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_20_R2);
    }

    public static boolean is1_20_R3Version() {
        return isVersionMinorEquals(ServerVersion.v1_20_R3);
    }

    public static boolean is1_20_R4Version() {
        return isVersionMinorEquals(ServerVersion.v1_20_R4);
    }

    public static boolean is1_21_R1Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R1);
    }

    public static boolean is1_21_R2Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R2);
    }

    public static boolean is1_21_R3Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R3);
    }

    public static boolean is1_21_R4Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R4);
    }

    public static boolean is1_21_R5Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R5);
    }

    public static boolean is1_21_R6Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R6);
    }

    public static boolean is1_21_R7Version() {
        return isVersionMinorEquals(ServerVersion.v1_21_R7);
    }

    public static boolean isVersionMinorEquals(final ServerVersion serverVersion) {
        return ServerVersion.getServerVersion() == serverVersion;
    }

    public static String getBukkitVersion() {
        final Matcher matcher = Pattern.compile("v\\d+_\\d+_R\\d+").matcher(Bukkit.getServer().getClass().getPackage().getName());
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String getMinecraftVersion() {
        final Matcher matcher = Pattern.compile("(\\(MC: )([\\d\\.]+)(\\))").matcher(Bukkit.getVersion());
        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    private static int compare(final String s, final String s2) {
        final String[] split = s.split("\\.");
        final String[] split2 = s2.split("\\.");
        final int max = Math.max(split.length, split2.length);
        final int[] array = new int[max];
        final int[] array2 = new int[max];
        for (int i = 0; i < split.length; ++i) {
            array[i] = Integer.parseInt(split[i]);
        }
        for (int j = 0; j < split2.length; ++j) {
            array2[j] = Integer.parseInt(split2[j]);
        }
        for (int k = 0; k < max; ++k) {
            final int n = array[k] - array2[k];
            if (n > 0) {
                return 1;
            }
            if (n < 0) {
                return -1;
            }
        }
        return 0;
    }

    private static int compare(final String s, final String s2, final int n) {
        final String[] split = s.split("\\.");
        final String[] split2 = s2.split("\\.");
        final int max = Math.max(split.length, split2.length);
        final int[] array = new int[max];
        final int[] array2 = new int[max];
        for (int i = 0; i < n; ++i) {
            array[i] = Integer.parseInt(split[i]);
        }
        for (int j = 0; j < n; ++j) {
            array2[j] = Integer.parseInt(split2[j]);
        }
        for (int k = 0; k < max; ++k) {
            final int n2 = array[k] - array2[k];
            if (n2 > 0) {
                return 1;
            }
            if (n2 < 0) {
                return -1;
            }
        }
        return 0;
    }

    public static boolean isVersionGreater(final String s, final String s2) {
        return compare(s, s2) > 0;
    }

    public static boolean isVersionGreaterEqual(final String s, final String s2) {
        return compare(s, s2) >= 0;
    }

    public static boolean isVersionLessEqual(final String s, final String s2) {
        return compare(s, s2) <= 0;
    }

    public static boolean isVersionBetweenEqual(final String s, final String s2, final String s3) {
        return isVersionGreaterEqual(s, s2) && isVersionLessEqual(s, s3);
    }

    public static boolean isSupported(final String s, final String s2, final String s3) {
        return compare(s, s2, 2) >= 0 && compare(s, s3, 2) <= 0;
    }

    public static boolean isClassExists(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final Throwable t) {
            return false;
        }
    }
}
