package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;

/**
 * 游戏实体基类
 */
public abstract class Entity {
    protected double x, y;           // 中心坐标
    protected int width, height;     // 尺寸
    protected Direction direction;   // 朝向
    protected boolean alive = true;  // 存活状态
    protected double speed;          // 移动速度
    
    public Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.direction = Direction.UP;
        this.speed = 0;
    }
    
    public abstract void update(double deltaTime);
    public abstract void render(Graphics2D g);
    
    // Getters & Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public void destroy() { this.alive = false; }
    
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    
    /**
     * 获取碰撞边界（矩形）
     */
    public Rectangle getBounds() {
        return new Rectangle(
            (int)(x - width / 2.0),
            (int)(y - height / 2.0),
            width,
            height
        );
    }
    
    /**
     * 碰撞检测
     */
    public boolean collidesWith(Entity other) {
        if (other == null || !other.isAlive()) return false;
        return getBounds().intersects(other.getBounds());
    }
    
    /**
     * 检测与点的距离
     */
    public double distanceTo(double px, double py) {
        return Math.sqrt((x - px) * (x - px) + (y - py) * (y - py));
    }
    
    /**
     * 检测与另一实体的距离
     */
    public double distanceTo(Entity other) {
        return distanceTo(other.getX(), other.getY());
    }
    
    /**
     * 获取朝向另一实体的方向
     */
    public Direction getDirectionTo(Entity other) {
        double dx = other.getX() - x;
        double dy = other.getY() - y;
        
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
    }
    
    /**
     * 获取下一帧的预测位置边界
     */
    public Rectangle getNextBounds(Direction dir, double spd) {
        double nx = x + dir.dx * spd;
        double ny = y + dir.dy * spd;
        return new Rectangle(
            (int)(nx - width / 2.0),
            (int)(ny - height / 2.0),
            width,
            height
        );
    }
    
    /**
     * 移动
     */
    public void move() {
        x += direction.dx * speed;
        y += direction.dy * speed;
    }
    
    /**
     * 限制在地图范围内
     */
    public void clampToMap() {
        double halfW = width / 2.0;
        double halfH = height / 2.0;
        double maxX = Constants.MAP_COLS * Constants.TILE_SIZE - halfW;
        double maxY = Constants.MAP_ROWS * Constants.TILE_SIZE - halfH;
        
        x = Math.max(halfW, Math.min(x, maxX));
        y = Math.max(halfH, Math.min(y, maxY));
    }
}
