package cn.sky.luckypillar.scoreboard;

import cn.sky.luckypillar.SkyLuckyPillar;
import cn.sky.luckypillar.config.SkyConfig;
import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.game.LuckyPillarPlayer;
import cn.sky.luckypillar.state.GameState;
import cn.sky.luckypillar.utils.chat.CC;
import cn.sky.luckypillar.utils.scoreboard.AssembleAdapter;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Scoreboard implements AssembleAdapter {

    private LuckyPillarGame game;
    private SkyConfig skyConfig;

    @Override
    public String getTitle(Player player) {
        return CC.translate(skyConfig.getScoreboardTitle());
    }

    @Override
    public List<String> getLines(Player player) {
        LuckyPillarPlayer lpPlayer = game.getPlayer(player);

        List<String> lines;
        if (lpPlayer == null) {
            lines = new ArrayList<>(skyConfig.getScoreboardSpectating());
        } else {
            GameState state = game.getStateManager().getCurrentState();
            lines = new ArrayList<>(switch (state) {
                case WAITING, STARTING -> skyConfig.getScoreboardWaiting();
                case PLAYING -> lpPlayer.isAlive() ? skyConfig.getScoreboardPlaying() : skyConfig.getScoreboardDead();
                case ENDING -> skyConfig.getScoreboardPlaying();
            });
        }

        LuckyPillarPlayer viewer = lpPlayer;
        lines.replaceAll(text -> replacePlaceholders(text, viewer));
        return CC.translate(lines);
    }

    private String replacePlaceholders(String text, LuckyPillarPlayer lpPlayer) {
        LuckyPillarGame game = SkyLuckyPillar.getInstance().getGame();
        String eventName = game.getEventScheduler() != null
                ? game.getEventScheduler().getEventManager().getCurrentEventName()
                : "N/A";

        String winnerName = "N/A";
        if (game.getWinner() != null) {
            LuckyPillarPlayer winnerPlayer = game.getPlayer(game.getWinner());
            if (winnerPlayer != null) {
                winnerName = winnerPlayer.getName();
            }
        }

        return text
                .replace("%map%", game.getConfig().getMapName())
                .replace("%players%", String.valueOf(game.getPlayers().size()))
                .replace("%countdown%", String.valueOf(game.getCountdown() + 1))
                .replace("%alive%", String.valueOf(game.getAlivePlayers().size()))
                .replace("%kills%", String.valueOf(lpPlayer != null ? lpPlayer.getKills() : 0))
                .replace("%time%", formatGameTime())
                .replace("%event%", eventName)
                .replace("%serverip%", SkyLuckyPillar.getInstance().getSkyConfig().getServer())
                .replace("%max%", String.valueOf(game.getMaxPlayer()))
                .replace("%winner%", winnerName);
    }

    private String formatGameTime() {
        LuckyPillarGame game = SkyLuckyPillar.getInstance().getGame();
        if (game.getGameStartTime() == 0) {
            return "00:00";
        }

        long elapsed = (System.currentTimeMillis() - game.getGameStartTime()) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

