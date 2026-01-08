package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;
import java.util.List;

/**
 * 自动哨戒炮
 */
public class Turret extends Entity {
    private int hp = 100;
    private int maxHp = 100;
    private long lastShootTime = 0;
    private long shootCooldown = 1500;
    private Entity owner;
    private double range = 200;
    private Entity target;
    
    public Turret(double x, double y, Entity owner) {
        super(x, y, 24, 24);
        this.owner = owner;
        this.direction = Direction.UP;
    }
    
    @Override
    public void update(double deltaTime) {
        // 不需要移动
    }
    
    /**
     * 寻找并攻击目标
     */
    public Bullet updateAndShoot(List<? extends Entity> enemies) {
        if (!alive) return null;
        
        // 寻找最近目标
        target = null;
        double minDist = range;
        
        for (Entity e : enemies) {
            if (e != null && e.isAlive()) {
                double dist = distanceTo(e);
                if (dist < minDist) {
                    minDist = dist;
                    target = e;
                }
            }
        }
        
        // 更新朝向
        if (target != null) {
            direction = getDirectionTo(target);
            
            // 尝试射击
            if (System.currentTimeMillis() - lastShootTime >= shootCooldown) {
                lastShootTime = System.currentTimeMillis();
                Point muzzle = getMuzzlePosition();
                return new Bullet(muzzle.x, muzzle.y, direction, this);
            }
        }
        
        return null;
    }
    
    private Point getMuzzlePosition() {
        int offset = width / 2 + 5;
        return new Point(
            (int)(x + direction.dx * offset),
            (int)(y + direction.dy * offset)
        );
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!alive) return;
        
        int px = (int)(x - width/2);
        int py = (int)(y - height/2);
        
        // 基座
        g.setColor(new Color(80, 80, 90));
        g.fillRect(px, py, width, height);
        g.setColor(new Color(60, 60, 70));
        g.drawRect(px, py, width - 1, height - 1);
        
        // 炮塔
        g.setColor(Constants.COLOR_PLAYER.darker());
        int turretSize = width / 2;
        g.fillOval((int)x - turretSize/2, (int)y - turretSize/2, turretSize, turretSize);
        
        // 炮管
        g.setColor(new Color(50, 50, 60));
        int barrelLength = width / 2 + 6;
        int barrelWidth = 4;
        
        switch (direction) {
            case UP:
                g.fillRect((int)x - barrelWidth/2, (int)y - barrelLength, barrelWidth, barrelLength);
                break;
            case DOWN:
                g.fillRect((int)x - barrelWidth/2, (int)y, barrelWidth, barrelLength);
                break;
            case LEFT:
                g.fillRect((int)x - barrelLength, (int)y - barrelWidth/2, barrelLength, barrelWidth);
                break;
            case RIGHT:
                g.fillRect((int)x, (int)y - barrelWidth/2, barrelLength, barrelWidth);
                break;
        }
        
        // 攻击范围指示（调试用）
        // g.setColor(new Color(100, 255, 100, 30));
        // g.drawOval((int)(x - range), (int)(y - range), (int)(range * 2), (int)(range * 2));
        
        // 血条
        if (hp < maxHp) {
            int barWidth = width;
            int barHeight = 3;
            float hpPercent = (float) hp / maxHp;
            
            g.setColor(Constants.COLOR_HP_BG);
            g.fillRect(px, py - 6, barWidth, barHeight);
            g.setColor(Constants.COLOR_HP_BAR);
            g.fillRect(px, py - 6, (int)(barWidth * hpPercent), barHeight);
        }
    }
    
    public boolean takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            destroy();
            return true;
        }
        return false;
    }
    
    public Entity getOwner() { return owner; }
}
