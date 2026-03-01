package cn.sky.luckypillar.utils.version;

import lombok.Getter;

@Getter
public enum ServerVersion {
    v1_8_R1("1.8", "1.8-R1", 8, new String[] { "1.8" }),
    v1_8_R2("1.8", "1.8-R2", 8, new String[] { "1.8.3" }),
    v1_8_R3("1.8", "1.8-R3", 8, new String[] { "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8" }),
    v1_9_R1("1.9", "1.9-R1", 9, new String[] { "1.9", "1.9.2" }),
    v1_9_R2("1.9", "1.9-R2", 9, new String[] { "1.9.4" }),
    v1_10_R1("1.10", "1.10-R1", 10, new String[] { "1.10", "1.10.2" }),
    v1_11_R1("1.11", "1.11-R1", 11, new String[] { "1.11", "1.11.1", "1.11.2" }),
    v1_12_R1("1.12", "1.12-R1", 12, new String[] { "1.12", "1.12.1", "1.12.2" }),
    v1_13_R1("1.13", "1.13-R1", 13, new String[] { "1.13" }),
    v1_13_R2("1.13", "1.13-R2", 13, new String[] { "1.13.1", "1.13.2" }),
    v1_14_R1("1.14", "1.14-R1", 14, new String[] { "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4" }),
    v1_15_R1("1.15", "1.15-R1", 15, new String[] { "1.15", "1.15.1", "1.15.2" }),
    v1_16_R1("1.16", "1.16-R1", 16, new String[] { "1.16.1" }),
    v1_16_R2("1.16", "1.16-R2", 16, new String[] { "1.16.2", "1.16.3" }),
    v1_16_R3("1.16", "1.16-R3", 16, new String[] { "1.16.4", "1.16.5" }),
    v1_17_R1("1.17", "1.17-R1", 17, new String[] { "1.17", "1.17.1" }),
    v1_18_R1("1.18", "1.18-R1", 18, new String[] { "1.18", "1.18.1" }),
    v1_18_R2("1.18", "1.18-R2", 18, new String[] { "1.18.2" }),
    v1_19_R1("1.19", "1.19-R1", 19, new String[] { "1.19", "1.19.1", "1.19.2" }),
    v1_19_R2("1.19", "1.19-R2", 19, new String[] { "1.19.3" }),
    v1_19_R3("1.19", "1.19-R3", 19, new String[] { "1.19.4" }),
    v1_20_R1("1.20", "1.20-R1", 20, new String[] { "1.20", "1.20.1" }),
    v1_20_R2("1.20", "1.20-R2", 20, new String[] { "1.20.2" }),
    v1_20_R3("1.20", "1.20-R3", 20, new String[] { "1.20.3", "1.20.4" }),
    v1_20_R4("1.20", "1.20-R4", 20, new String[] { "1.20.5", "1.20.6" }),
    v1_21_R1("1.21", "1.21-R1", 21, new String[] { "1.21", "1.21.1" }),
    v1_21_R2("1.21", "1.21-R2", 21, new String[] { "1.21.2", "1.21.3" }),
    v1_21_R3("1.21", "1.21-R3", 21, new String[] { "1.21.4" }),
    v1_21_R4("1.21", "1.21-R4", 21, new String[] { "1.21.5" }),
    v1_21_R5("1.21", "1.21-R5", 21, new String[] { "1.21.6", "1.21.7", "1.21.8" }),
    v1_21_R6("1.21", "1.21-R6", 21, new String[] { "1.21.9", "1.21.10" }),
    v1_21_R7("1.21", "1.21-R7", 21, new String[] { "1.21.11" });

    private final String name;
    private final String detailName;
    private final int currentVersionNumber;
    private final String[] spigotReleases;
    private static ServerVersion serverVersion = null;

    ServerVersion(final String name, final String detailName, final int currentVersionNumber, final String[] spigotReleases) {
        this.name = name;
        this.detailName = detailName;
        this.currentVersionNumber = currentVersionNumber;
        this.spigotReleases = spigotReleases;
    }

    public static ServerVersion getServerVersion() {
        return ServerVersion.serverVersion;
    }

    public static void setServerVersion(final ServerVersion serverVersion) {
        ServerVersion.serverVersion = serverVersion;
    }

    public static ServerVersion valueOfSpigotRelease(final String anotherString) {
        for (final ServerVersion serverVersion : values()) {
            final String[] spigotReleases = serverVersion.getSpigotReleases();
            for (int length2 = spigotReleases.length, j = 0; j < length2; ++j) {
                if (spigotReleases[j].equalsIgnoreCase(anotherString)) {
                    return serverVersion;
                }
            }
        }
        return null;
    }
}
