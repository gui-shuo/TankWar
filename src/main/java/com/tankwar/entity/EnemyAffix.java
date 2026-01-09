package com.tankwar.entity;

/**
 * 敌人词缀类型
 */
public enum EnemyAffix {
    NONE("普通", "标准敌人", null),
    EXPLOSIVE("自爆", "死亡时爆炸", new java.awt.Color(255, 100, 50)),
    TOUGH("刚毅", "免疫正面伤害，需绕后", new java.awt.Color(100, 100, 180)),
    VAMPIRE("吸血", "攻击命中回复生命", new java.awt.Color(180, 50, 80)),
    FAST("迅捷", "移动速度翻倍", new java.awt.Color(100, 200, 255)),
    SHIELD("护盾", "受伤减半", new java.awt.Color(200, 200, 100));
    
    public final String name;
    public final String description;
    public final java.awt.Color color;
    
    EnemyAffix(String name, String description, java.awt.Color color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    public static EnemyAffix randomElite() {
        EnemyAffix[] elites = {EXPLOSIVE, TOUGH, VAMPIRE, FAST, SHIELD};
        return elites[(int)(Math.random() * elites.length)];
    }
}
