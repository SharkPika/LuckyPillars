package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.game.LuckyPillarGame;
import org.bukkit.Location;
import org.bukkit.WorldBorder;

/**
 * 世界边界收缩事件
 * 逐步收缩世界边界，迫使玩家向中心聚拢
 */
public class BorderShrinkEvent implements GameEvent {

    private final LuckyPillarConfig config;
    private double originalSize;

    public BorderShrinkEvent(LuckyPillarConfig config) {
        this.config = config;
    }

    @Override
    public String getId() {
        return "border-shrink";
    }

    @Override
    public String getName() {
        return "边界收缩";
    }

    @Override
    public boolean isEnabled() {
        return config.isBorderShrinkEnabled();
    }

    @Override
    public int getDuration() {
        return config.getBorderShrinkDuration();
    }

    @Override
    public void onStart(LuckyPillarGame game) {
        if (game.getCenter() == null)
            return;

        WorldBorder border = game.getGameWorld().getWorldBorder();
        Location center = game.getCenter();

        // 记录原始边界大小
        originalSize = border.getSize();

        // 设置边界中心
        border.setCenter(center);

        // 如果当前边界很大(默认值)，初始化为合理大小
        if (originalSize > 1000) {
            originalSize = config.getBorderInitialSize();
            border.setSize(originalSize);
        }

        // 在事件持续时间内缩小到目标大小
        double targetSize = config.getBorderMinSize();
        border.setSize(targetSize, getDuration());
    }

    @Override
    public void onTick(LuckyPillarGame game) {
        // WorldBorder API 自动处理渐进缩小，不需要手动 tick
    }

    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束后恢复边界
        WorldBorder border = game.getGameWorld().getWorldBorder();
        border.setSize(originalSize, 5); // 5 秒内恢复
    }

    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("§c§l[事件] 边界收缩！");
        game.broadcast("§e世界边界正在缩小 快往中心聚集！");
    }
}
