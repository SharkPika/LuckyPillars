package cn.sky.luckypillar.utils.compat;

import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.version.ServerVersion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class VersionCompat {

    private static final Map<String, Sound> SOUND_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Enum<?>> PARTICLE_CACHE = new ConcurrentHashMap<>();
    private static final Set<String> WARNED_KEYS = ConcurrentHashMap.newKeySet();

    private static final Class<? extends Enum<?>> PARTICLE_ENUM_CLASS;
    private static final Method SPAWN_PARTICLE_WITH_EXTRA_METHOD;
    private static final Method SPAWN_PARTICLE_BASE_METHOD;

    static {
        Class<? extends Enum<?>> particleEnumClass = null;
        Method spawnWithExtra = null;
        Method spawnBase = null;

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) Class.forName("org.bukkit.Particle");
            particleEnumClass = clazz;

            spawnWithExtra = World.class.getMethod(
                    "spawnParticle",
                    clazz,
                    Location.class,
                    int.class,
                    double.class,
                    double.class,
                    double.class,
                    double.class
            );
        } catch (Throwable ignored) {
        }

        if (particleEnumClass != null && spawnWithExtra == null) {
            try {
                spawnBase = World.class.getMethod(
                        "spawnParticle",
                        particleEnumClass,
                        Location.class,
                        int.class,
                        double.class,
                        double.class,
                        double.class
                );
            } catch (Throwable ignored) {
            }
        }

        PARTICLE_ENUM_CLASS = particleEnumClass;
        SPAWN_PARTICLE_WITH_EXTRA_METHOD = spawnWithExtra;
        SPAWN_PARTICLE_BASE_METHOD = spawnBase;
    }

    private VersionCompat() {
        throw new IllegalStateException("工具类不允许实例化");
    }

    public static ServerVersion getServerVersion() {
        return ServerVersion.getServerVersion();
    }

    public static void spawnParticle(World world, Location loc, int count,
                                     double offsetX, double offsetY, double offsetZ,
                                     double speed, String... particleNames) {
        if (world == null || loc == null || count <= 0) {
            return;
        }

        Enum<?> particle = getParticle(particleNames);
        if (particle == null) {
            return;
        }

        try {
            if (SPAWN_PARTICLE_WITH_EXTRA_METHOD != null) {
                SPAWN_PARTICLE_WITH_EXTRA_METHOD.invoke(world, particle, loc, count, offsetX, offsetY, offsetZ, speed);
                return;
            }
            if (SPAWN_PARTICLE_BASE_METHOD != null) {
                SPAWN_PARTICLE_BASE_METHOD.invoke(world, particle, loc, count, offsetX, offsetY, offsetZ);
            }
        } catch (Throwable e) {
            warnOnce("particle-error-" + String.join("/", particleNames),
                    "&c粒子播放失败: &f" + String.join("/", particleNames));
            CC.sendError("&c粒子兼容调用异常", e);
        }
    }

    public static void spawnCritParticle(Location loc, int count) {
        spawnParticle(getWorld(loc), loc, count, 0.5, 0.5, 0.5, 0.1,
                "ENCHANTED_HIT", "CRIT_MAGIC", "CRIT");
    }

    public static void spawnSmokeParticle(Location loc, int count) {
        spawnParticle(getWorld(loc), loc, count, 0.3, 0.5, 0.3, 0.05,
                "LARGE_SMOKE", "SMOKE_LARGE", "SMOKE_NORMAL", "SMOKE");
    }

    public static void spawnExplosionParticle(Location loc, int count) {
        spawnParticle(getWorld(loc), loc, count, 0.5, 0.5, 0.5, 0.0,
                "EXPLOSION", "EXPLOSION_LARGE", "EXPLOSION_NORMAL");
    }

    public static void spawnFireworkParticle(Location loc, int count) {
        spawnParticle(getWorld(loc), loc, count, 0.5, 0.5, 0.5, 0.1,
                "FIREWORK", "FIREWORKS_SPARK");
    }

    public static Sound getSound(String... names) {
        for (String rawName : names) {
            String normalized = normalizeEnumName(rawName);
            if (normalized == null) {
                continue;
            }

            Sound cached = SOUND_CACHE.get(normalized);
            if (cached != null) {
                return cached;
            }

            try {
                Sound sound = Sound.valueOf(normalized);
                SOUND_CACHE.put(normalized, sound);
                return sound;
            } catch (IllegalArgumentException ignored) {
            }
        }
        return null;
    }

    public static void playSound(Player player, float volume, float pitch, String... soundNames) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Sound sound = getSound(soundNames);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void playSoundAtLocation(Location loc, float volume, float pitch, String... soundNames) {
        World world = getWorld(loc);
        if (world == null || loc == null) {
            return;
        }

        Sound sound = getSound(soundNames);
        if (sound != null) {
            world.playSound(loc, sound, volume, pitch);
        }
    }

    public static PotionEffectType getPotionEffectType(String... names) {
        for (String rawName : names) {
            String normalized = normalizeEnumName(rawName);
            if (normalized == null) {
                continue;
            }

            PotionEffectType type = PotionEffectType.getByName(normalized);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public static boolean addPotionEffect(Player player, int durationTicks, int amplifier, String... effectNames) {
        if (player == null || !player.isOnline()) {
            return false;
        }

        PotionEffectType type = getPotionEffectType(effectNames);
        if (type == null) {
            return false;
        }

        player.addPotionEffect(new PotionEffect(type, durationTicks, amplifier, true, true));
        return true;
    }

    public static void removePotionEffect(Player player, String... effectNames) {
        if (player == null || !player.isOnline()) {
            return;
        }

        PotionEffectType type = getPotionEffectType(effectNames);
        if (type != null) {
            player.removePotionEffect(type);
        }
    }

    public static void playKillSound(Player player) {
        playSound(player, 1.0f, 1.0f,
                "ENTITY_EXPERIENCE_ORB_PICKUP", "ORB_PICKUP");
    }

    public static void playDeathSound(Location loc) {
        playSoundAtLocation(loc, 1.0f, 1.0f,
                "ENTITY_PLAYER_DEATH", "HURT_FLESH");
    }

    public static void playTeleportSound(Player player) {
        playSound(player, 1.0f, 1.0f,
                "ENTITY_ENDERMAN_TELEPORT", "ENDERMAN_TELEPORT");
    }

    public static void playCountdownSound(Player player, float pitch) {
        playSound(player, 1.0f, pitch,
                "BLOCK_NOTE_BLOCK_PLING", "BLOCK_NOTE_BLOCK_HARP", "NOTE_PLING", "NOTE_PIANO");
    }

    public static void playEventSound(Player player) {
        playSound(player, 1.0f, 1.0f,
                "ENTITY_ENDER_DRAGON_GROWL", "ENDERDRAGON_GROWL");
    }

    public static void playLevelUpSound(Player player) {
        playSound(player, 1.0f, 1.0f,
                "ENTITY_PLAYER_LEVELUP", "LEVEL_UP");
    }

    public static void playVoidFallSound(Player player) {
        playSound(player, 1.0f, 0.5f,
                "ENTITY_ENDERMAN_TELEPORT", "ENDERMAN_TELEPORT");
    }

    private static Enum<?> getParticle(String... names) {
        if (PARTICLE_ENUM_CLASS == null) {
            warnOnce("particle-api-missing", "&e当前服务端不支持 Particle API，已跳过粒子效果");
            return null;
        }

        for (String rawName : names) {
            String normalized = normalizeEnumName(rawName);
            if (normalized == null) {
                continue;
            }

            Enum<?> cached = PARTICLE_CACHE.get(normalized);
            if (cached != null) {
                return cached;
            }

            try {
                @SuppressWarnings({"rawtypes", "unchecked"})
                Enum<?> particle = Enum.valueOf((Class) PARTICLE_ENUM_CLASS, normalized);
                PARTICLE_CACHE.put(normalized, particle);
                return particle;
            } catch (IllegalArgumentException ignored) {
            }
        }

        warnOnce("particle-not-found-" + String.join("/", names),
                "&e未找到兼容粒子: &f" + String.join("/", names));
        return null;
    }

    private static String normalizeEnumName(String rawName) {
        if (rawName == null) {
            return null;
        }

        String name = rawName.trim();
        if (name.isEmpty()) {
            return null;
        }

        return name.toUpperCase().replace('-', '_').replace(' ', '_');
    }

    private static World getWorld(Location location) {
        return location == null ? null : location.getWorld();
    }

    private static void warnOnce(String key, String message) {
        if (WARNED_KEYS.add(key)) {
            CC.warn(message);
        }
    }
}
