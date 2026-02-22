package cn.sky.luckypillar.utils;

import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 世界工具类
 */
public class WorldUtil {

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            if (children == null) return false;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (success) continue;
                return false;
            }
        }
        return dir.delete();
    }

    public static void copyDir(String fromDir, String toDir) throws IOException {
        final File dirSouce = new File(fromDir);
        if (!dirSouce.isDirectory()) {
            return;
        }
        final File destDir = new File(toDir);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        final File[] files  = dirSouce.listFiles();
        if (files == null) return;
        for (File file : files) {
            final String strFrom = fromDir + File.separator + file.getName();
            final String strTo = toDir + File.separator + file.getName();
            if (file.isDirectory()) {
                copyDir(strFrom, strTo);
            }
            if (!file.isFile()) continue;
            copyFile(strFrom, strTo);
        }
    }

    public static void copyFile(String fromFile, String toFile) throws IOException {
        final FileInputStream in = new FileInputStream(fromFile);
        final FileOutputStream out = new FileOutputStream(toFile);
        final byte[] bs = new byte[0x100000];
        int count;
        while ((count = in.read(bs)) != -1) {
            out.write(bs, 0, count);
        }
        in.close();
        out.flush();
        out.close();
    }
    
    /**
     * 设置世界边界
     */
    public static void setWorldBorder(World world, double centerX, double centerZ, double size) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(size);
    }
    
    /**
     * 重置世界边界
     */
    public static void resetWorldBorder(World world) {
        WorldBorder border = world.getWorldBorder();
        border.reset();
    }
    
    /**
     * 设置世界时间
     */
    public static void setTime(World world, long time) {
        world.setTime(time);
    }
    
    /**
     * 设置世界天气
     */
    public static void setWeather(World world, boolean storm, boolean thunder) {
        world.setStorm(storm);
        world.setThundering(thunder);
    }
}
