package com.tankwar.entity;

import java.awt.*;

/**
 * 道具类型
 */
public enum PowerUpType {
    HEALTH("生命", "恢复50点血量", new Color(255, 100, 100)),
    SHIELD("护盾", "获得2秒无敌", new Color(100, 200, 255)),
    SPEED("加速", "移动速度提升", new Color(255, 255, 100)),
    WEAPON_LASER("激光炮", "获得5发激光弹药", new Color(255, 50, 50)),
    WEAPON_SHOTGUN("散弹炮", "获得8发散弹弹药", new Color(255, 200, 100)),
    WEAPON_MISSILE("导弹", "获得3发追踪导弹", new Color(255, 150, 50)),
    EXTRA_LIFE("额外生命", "生命+1", new Color(255, 100, 200)),
    SCORE_BONUS("积分", "获得200分", new Color(255, 215, 0));
    
    public final String name;
    public final String description;
    public final Color color;
    
    PowerUpType(String name, String description, Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    public static PowerUpType random() {
        PowerUpType[] types = values();
        // 权重随机
        double r = Math.random();
        if (r < 0.25) return HEALTH;
        if (r < 0.40) return SHIELD;
        if (r < 0.50) return SPEED;
        if (r < 0.65) return WEAPON_LASER;
        if (r < 0.80) return WEAPON_SHOTGUN;
        if (r < 0.90) return WEAPON_MISSILE;
        if (r < 0.95) return EXTRA_LIFE;
        return SCORE_BONUS;
    }
}
