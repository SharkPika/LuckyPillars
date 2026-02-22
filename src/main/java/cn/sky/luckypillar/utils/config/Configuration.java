package cn.sky.luckypillar.utils.config;

import cn.sky.luckypillar.utils.config.annotation.ConfigData;
import cn.sky.luckypillar.utils.config.serializer.LocationSerializer;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.lang.reflect.Field;

@Getter
public class Configuration {

    protected final JavaPlugin plugin;
    private final File configFile;
    private YamlConfiguration config;

    private final LocationSerializer locationSerializer;

    public Configuration(JavaPlugin plugin, String filename) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), filename);
        this.locationSerializer = new LocationSerializer();
    }

    public void onLoad() {
        try {
            if (!this.configFile.exists()) {
                try {
                    this.plugin.saveResource(this.configFile.getName(), false);
                } catch (IllegalArgumentException e) {
                    boolean success = this.configFile.createNewFile();
                    if (!success) {
                        throw new YAMLException("创建配置文件失败: " + this.configFile.getName());
                    }
                }
            }
            this.config = YamlConfiguration.loadConfiguration(this.configFile);

            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ConfigData.class)) continue;
                String path = field.getAnnotation(ConfigData.class).value();
                if (path == null || path.isEmpty() || !this.config.contains(path)) {
                    continue;
                }
                Object value = this.config.get(path);
                if (value == null) {
                    value = field.getType().newInstance();
                }
                field.setAccessible(true);
                field.set(this, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        if (value instanceof Location) {
            value = this.locationSerializer.serialize((Location) value);
        }
        this.config.set(path, value);
        this.saveConfig();
    }

    public void reload() {
        this.onLoad();
    }

    public void saveConfig() {
        try {
            this.config.save(this.configFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
