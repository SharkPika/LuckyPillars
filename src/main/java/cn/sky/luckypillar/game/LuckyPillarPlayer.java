package cn.sky.luckypillar.game;

import cn.sky.luckypillar.SkyLuckyPillar;
import cn.sky.luckypillar.pillar.Pillar;
import cn.sky.luckypillar.state.PlayerState;
import cn.sky.luckypillar.utils.chat.CC;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
public class LuckyPillarPlayer {

    private final UUID uuid;

    private final String name;

    private final Player bukkitPlayer;

    private PlayerState state;

    private Pillar assignedPillar;

    private int kills;

    private int deaths;

    private long survivalTime;

    private Location lastLocation;

    private boolean ready;

    private long gameStartTime;
    
    public LuckyPillarPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.bukkitPlayer = player;
        this.state = PlayerState.WAITING;
        this.kills = 0;
        this.deaths = 0;
        this.survivalTime = 0;
        this.ready = false;
    }

    public void assignPillar(Pillar pillar) {
        this.assignedPillar = pillar;
        if (pillar != null) {
            pillar.assignPlayer(this.uuid);
        }
    }

    public Pillar assignNewPillar() {
        LuckyPillarGame game = SkyLuckyPillar.getInstance().getGame();
        Pillar pillar = game.getPillarManager().getAvailablePillar();
        if (pillar == null) {
            CC.warn("&c柱子数量不足！需要 " + game.getPlayers().size() + " 个，但只有 " + game.getPillarManager().getPillars().size() + " 个");
            return null;
        }
        this.assignedPillar = pillar;
        pillar.assignPlayer(this.uuid);
        CC.send("&f玩家 &e" + bukkitPlayer.getName() + " &f被分配到柱子 &b" + pillar.getId());
        return pillar;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void reset() {
        this.state = PlayerState.WAITING;
        this.assignedPillar = null;
        this.kills = 0;
        this.deaths = 0;
        this.survivalTime = 0;
        this.ready = false;
        this.gameStartTime = 0;
    }

    public void sendMessage(String message) {
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(message);
        }
    }

    public void playSound(Sound sound) {
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void playSound(Sound sound, float volume, float pitch) {
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound, volume, pitch);
        }
    }

    public void giveItem(ItemStack item) {
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.getInventory().addItem(item);
        }
    }

    public void giveItems(ItemStack... items) {
        for (ItemStack item : items) {
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                bukkitPlayer.getInventory().addItem(item);
            }
        }
    }

    public boolean isOnline() {
        return bukkitPlayer != null && bukkitPlayer.isOnline();
    }

    public boolean isAlive() {
        return state == PlayerState.ALIVE;
    }

    public boolean isSpectating() {
        return state == PlayerState.SPECTATING;
    }

    public void startGameTimer() {
        this.gameStartTime = System.currentTimeMillis();
    }

    public void calculateSurvivalTime() {
        if (gameStartTime > 0) {
            this.survivalTime = System.currentTimeMillis() - gameStartTime;
        }
    }

    public String getFormattedSurvivalTime() {
        long seconds = survivalTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
