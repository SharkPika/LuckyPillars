package cn.sky.luckypillar.utils.scoreboard;

import cn.sky.luckypillar.utils.scoreboard.events.AssembleBoardCreateEvent;
import cn.sky.luckypillar.utils.scoreboard.events.AssembleBoardDestroyEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Assemble Listener.
 *
 * @param assemble instance.
 */
public record AssembleListener(Assemble assemble) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(event.getPlayer());

        Bukkit.getPluginManager().callEvent(createEvent);
        if (createEvent.isCancelled()) {
            return;
        }
        assemble().getBoards().put(event.getPlayer().getUniqueId(), new AssembleBoard(event.getPlayer(), assemble()));

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AssembleBoardDestroyEvent destroyEvent = new AssembleBoardDestroyEvent(event.getPlayer());

        Bukkit.getPluginManager().callEvent(destroyEvent);
        if (destroyEvent.isCancelled()) {
            return;
        }

        assemble().getBoards().remove(event.getPlayer().getUniqueId());
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
}
