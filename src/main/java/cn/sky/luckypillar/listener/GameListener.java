package cn.sky.luckypillar.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onArrowRain(ProjectileHitEvent event) {
        if (event.getEntity().getType() == EntityType.ARROW) {
            Arrow arrow = (Arrow) event.getEntity();
            if (event.getHitBlock() != null) {
                arrow.remove();
            }
        }
    }
}
