package cn.sky.luckypillar.event.events;

import cn.sky.luckypillar.game.LuckyPillarGame;
import cn.sky.luckypillar.config.LuckyPillarConfig;
import cn.sky.luckypillar.event.GameEvent;
import cn.sky.luckypillar.pillar.Pillar;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 空投补给事件
 * 在地图随机位置生成补给箱
 */
public class SupplyDropEvent implements GameEvent {
    
    private final LuckyPillarConfig config;
    private final Random random;
    
    public SupplyDropEvent(LuckyPillarConfig config) {
        this.config = config;
        this.random = new Random();
    }
    
    @Override
    public String getId() {
        return "supply-drop";
    }
    
    @Override
    public String getName() {
        return "空投补给";
    }
    
    @Override
    public boolean isEnabled() {
        return config.isSupplyDropEnabled();
    }
    
    @Override
    public int getDuration() {
        return config.getSupplyDropDuration();
    }
    
    @Override
    public void onStart(LuckyPillarGame game) {
        // 生成补给箱
        List<Location> dropLocations = selectDropLocations(game);
        
        for (Location location : dropLocations) {
            game.getItemDistributor().dropSupplyCrate(location, config.getItemsPerDrop());
        }
        
        game.broadcast("&a已投放 " + dropLocations.size() + " 个补给箱！");
    }
    
    @Override
    public void onTick(LuckyPillarGame game) {
        // 每tick不做额外操作
    }
    
    @Override
    public void onEnd(LuckyPillarGame game) {
        // 事件结束时不做额外操作
    }
    
    @Override
    public void announce(LuckyPillarGame game) {
        game.broadcast("&a&l[事件] 空投补给！");
        game.broadcast("&e补给箱正在投放，快去寻找！");
    }
    
    /**
     * 选择补给箱投放位置
     */
    private List<Location> selectDropLocations(LuckyPillarGame game) {
        List<Location> locations = new ArrayList<>();
        List<Pillar> pillars = new ArrayList<>(game.getPillarManager().getPillars());
        
        if (pillars.isEmpty()) {
            return locations;
        }
        
        // 打乱柱子顺序
        Collections.shuffle(pillars);
        
        // 选择指定数量的柱子作为投放点
        int dropCount = Math.min(config.getDropCount(), pillars.size());
        
        for (int i = 0; i < dropCount; i++) {
            Pillar pillar = pillars.get(i);
            Location dropLoc = pillar.getBottomCenter().clone();
            
            // 在平台上方1格放置箱子
            dropLoc.add(0, 1, 0);
            
            locations.add(dropLoc);
        }
        
        return locations;
    }
}
