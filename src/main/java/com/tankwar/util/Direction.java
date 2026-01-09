package com.tankwar.util;

/**
 * 方向枚举
 */
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);
    
    public final int dx;
    public final int dy;
    
    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return UP;
        }
    }
    
    public static Direction random() {
        Direction[] dirs = values();
        return dirs[(int)(Math.random() * dirs.length)];
    }
    
    public double getAngle() {
        switch (this) {
            case UP: return 0;
            case RIGHT: return 90;
            case DOWN: return 180;
            case LEFT: return 270;
            default: return 0;
        }
    }
}
