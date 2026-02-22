package cn.sky.luckypillar.utils.config.serializer;

import cn.sky.luckypillar.utils.config.AbstractSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.StringJoiner;

public class LocationSerializer extends AbstractSerializer<Location> {

    @Override
    public String serialize(Location o) {
        if (o == null || o.getWorld() == null) {
            return null;
        }
        
        StringJoiner stringJoiner = new StringJoiner(":");
        stringJoiner.add(o.getWorld().getName());
        stringJoiner.add(String.valueOf(o.getX()));
        stringJoiner.add(String.valueOf(o.getY()));
        stringJoiner.add(String.valueOf(o.getZ()));
        stringJoiner.add(String.valueOf(o.getYaw()));
        stringJoiner.add(String.valueOf(o.getPitch()));
        return stringJoiner.toString();
    }

    @Override
    public Location deserialize(String s) {
        // 空值检查
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        
        // 格式验证
        String[] args = s.split(":");
        if (args.length < 6) {
            throw new IllegalArgumentException("无效的位置格式: " + s + " (需要 6 个参数)");
        }
        
        try {
            // 获取世界
            World world = Bukkit.getWorld(args[0]);
            if (world == null) {
                throw new IllegalArgumentException("世界不存在: " + args[0]);
            }
            
            // 解析坐标
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            float yaw = Float.parseFloat(args[4]);
            float pitch = Float.parseFloat(args[5]);
            
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的位置数值: " + s, e);
        }
    }
}
