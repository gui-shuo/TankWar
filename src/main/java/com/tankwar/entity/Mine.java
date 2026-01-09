package com.tankwar.entity;

import com.tankwar.util.Constants;
import java.awt.*;

/**
 * 地雷实体
 */
public class Mine extends Entity {
    private Entity owner;
    private int damage = 50;
    private long createTime;
    private long armTime = 1000;  // 1秒后激活
    private boolean armed = false;
    private double triggerRadius = 20;
    
    public Mine(double x, double y, Entity owner) {
        super(x, y, 16, 16);
        this.owner = owner;
        this.createTime = System.currentTimeMillis();
    }
    
    @Override
    public void update(double deltaTime) {
        if (!armed && System.currentTimeMillis() - createTime >= armTime) {
            armed = true;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        int size = width;
        int px = (int)(x - size/2);
        int py = (int)(y - size/2);
        
        // 地雷主体
        Color baseColor = armed ? new Color(150, 50, 50) : new Color(100, 100, 100);
        g.setColor(baseColor);
        g.fillOval(px, py, size, size);
        
        // 触发器
        g.setColor(armed ? Color.RED : Color.GRAY);
        g.fillOval(px + size/4, py + size/4, size/2, size/2);
        
        // 闪烁效果（激活后）
        if (armed) {
            float flash = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 100.0));
            g.setColor(new Color(255, 0, 0, (int)(flash * 100)));
            g.fillOval(px - 2, py - 2, size + 4, size + 4);
        }
    }
    
    /**
     * 检查是否触发（只对敌人触发，不对友军触发）
     */
    public boolean checkTrigger(Entity target) {
        if (!armed) return false;
        // 不对所有者和友军（PlayerTank）触发
        if (target == owner) return false;
        if (owner instanceof PlayerTank && target instanceof PlayerTank) return false;
        return distanceTo(target) < triggerRadius + target.getWidth() / 2.0;
    }
    
    public int getDamage() { return damage; }
    public Entity getOwner() { return owner; }
    public boolean isArmed() { return armed; }
}
