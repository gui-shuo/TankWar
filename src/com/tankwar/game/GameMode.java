package com.tankwar.game;

/**
 * 游戏模式枚举
 */
public enum GameMode {
    CLASSIC("经典模式", "保护基地，消灭所有敌人"),
    ESCORT("护送模式", "保护物资车安全抵达终点"),
    PVP("双人对战", "本地双人竞技对战"),
    BOSS_RUSH("Boss挑战", "连续挑战强力Boss");
    
    public final String name;
    public final String description;
    
    GameMode(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
