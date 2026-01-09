package com.tankwar.util;

import java.awt.Color;

/**
 * 游戏常量定义
 */
public final class Constants {
    private Constants() {}
    
    // 窗口设置
    public static final String GAME_TITLE = "坦克大战 - 创新版";
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    
    // 游戏区域（左侧）
    public static final int GAME_WIDTH = 780;
    public static final int GAME_HEIGHT = 780;
    
    // HUD区域（右侧）
    public static final int HUD_X = 780;
    public static final int HUD_WIDTH = 244;
    
    // 游戏设置
    public static final int FPS = 60;
    public static final int FRAME_TIME = 1000 / FPS;
    
    // 地图设置
    public static final int TILE_SIZE = 30;
    public static final int MAP_COLS = 26;
    public static final int MAP_ROWS = 26;
    
    // 坦克设置
    public static final int TANK_SIZE = 28;
    public static final double PLAYER_SPEED = 3.0;
    public static final double ENEMY_SPEED = 1.5;
    public static final int PLAYER_MAX_HP = 100;
    public static final int PLAYER_LIVES = 3;
    public static final long PLAYER_SHOOT_COOLDOWN = 300;
    public static final long ENEMY_SHOOT_COOLDOWN = 1500;
    
    // 子弹设置
    public static final int BULLET_SIZE = 6;
    public static final double BULLET_SPEED = 8.0;
    public static final int BULLET_DAMAGE = 25;
    public static final double LASER_SPEED = 20.0;
    public static final double MISSILE_SPEED = 5.0;
    
    // 技能设置
    public static final long SKILL_PHASE_COOLDOWN = 5000;  // 相位移动冷却
    public static final long SKILL_EMP_COOLDOWN = 10000;   // 电磁脉冲冷却
    public static final int PHASE_DISTANCE = 90;           // 闪现距离
    public static final int EMP_RADIUS = 150;              // 电磁脉冲范围
    public static final long EMP_STUN_DURATION = 3000;     // 眩晕时长
    
    // 颜色定义
    public static final Color COLOR_PLAYER = new Color(60, 180, 75);
    public static final Color COLOR_PLAYER2 = new Color(70, 130, 220);
    public static final Color COLOR_ENEMY = new Color(200, 80, 80);
    public static final Color COLOR_ENEMY_ELITE = new Color(180, 50, 180);
    public static final Color COLOR_BOSS = new Color(255, 100, 0);
    public static final Color COLOR_BULLET = new Color(255, 255, 100);
    public static final Color COLOR_LASER = new Color(255, 50, 50);
    public static final Color COLOR_MISSILE = new Color(255, 150, 50);
    
    public static final Color COLOR_BRICK = new Color(180, 100, 60);
    public static final Color COLOR_STEEL = new Color(160, 160, 170);
    public static final Color COLOR_WATER = new Color(60, 120, 200);
    public static final Color COLOR_GRASS = new Color(50, 150, 50);
    public static final Color COLOR_BASE = new Color(255, 215, 0);
    public static final Color COLOR_BARREL = new Color(200, 50, 50);
    public static final Color COLOR_PORTAL = new Color(150, 50, 255);
    
    public static final Color COLOR_BG = new Color(30, 30, 35);
    public static final Color COLOR_HUD_BG = new Color(40, 45, 50);
    public static final Color COLOR_TEXT = new Color(240, 240, 240);
    public static final Color COLOR_HP_BAR = new Color(60, 200, 60);
    public static final Color COLOR_HP_BG = new Color(100, 40, 40);
    
    // 积分
    public static final int SCORE_ENEMY = 100;
    public static final int SCORE_ELITE = 200;
    public static final int SCORE_BOSS = 500;
    
    // 商店价格
    public static final int PRICE_TURRET = 300;
    public static final int PRICE_REPAIR_WALL = 50;
    public static final int PRICE_UPGRADE_WALL = 150;
}
