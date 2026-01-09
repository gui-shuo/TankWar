package com.tankwar.entity;

/**
 * 武器类型枚举
 */
public enum WeaponType {
    NORMAL("普通炮", "标准弹药"),
    LASER("激光炮", "穿透直线所有目标"),
    SHOTGUN("散弹炮", "扇形发射3发子弹"),
    MISSILE("追踪导弹", "自动追踪最近敌人");
    
    public final String name;
    public final String description;
    
    WeaponType(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public WeaponType next() {
        WeaponType[] types = values();
        return types[(ordinal() + 1) % types.length];
    }
}
