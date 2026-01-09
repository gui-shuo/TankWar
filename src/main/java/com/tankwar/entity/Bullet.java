package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;

/**
 * 子弹基类
 */
public class Bullet extends Entity {
    protected Entity owner;     // 发射者
    protected int damage;       // 伤害
    protected boolean piercing; // 是否穿透
    protected Color color;
    protected int pierceCount;  // 剩余穿透次数
    
    public Bullet(double x, double y, Direction direction, Entity owner) {
        super(x, y, Constants.BULLET_SIZE, Constants.BULLET_SIZE);
        this.direction = direction;
        this.owner = owner;
        this.speed = Constants.BULLET_SPEED;
        this.damage = Constants.BULLET_DAMAGE;
        this.piercing = false;
        this.pierceCount = 0;
        this.color = Constants.COLOR_BULLET;
    }
    
    @Override
    public void update(double deltaTime) {
        move();
        
        // 超出边界销毁
        if (!isInBounds()) {
            destroy();
        }
    }
    
    protected boolean isInBounds() {
        return x >= 0 && x < Constants.MAP_COLS * Constants.TILE_SIZE &&
               y >= 0 && y < Constants.MAP_ROWS * Constants.TILE_SIZE;
    }
    
    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        int size = Constants.BULLET_SIZE;
        g.fillOval((int)(x - size/2), (int)(y - size/2), size, size);
        
        // 发光效果
        g.setColor(new Color(255, 255, 200, 100));
        g.fillOval((int)(x - size), (int)(y - size), size * 2, size * 2);
    }
    
    public Entity getOwner() { return owner; }
    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
    
    public boolean isPiercing() { return piercing; }
    public void setPiercing(boolean piercing) { 
        this.piercing = piercing; 
        if (piercing) this.pierceCount = 3;
    }
    
    /**
     * 穿透后减少计数
     */
    public void pierce() {
        if (piercing && pierceCount > 0) {
            pierceCount--;
            if (pierceCount <= 0) {
                destroy();
            }
        }
    }
    
    public boolean canPierce() {
        return piercing && pierceCount > 0;
    }
}

