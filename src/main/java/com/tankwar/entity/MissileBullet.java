package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;
import java.util.List;

/**
 * 追踪导弹 - 自动追踪最近敌人
 */
public class MissileBullet extends Bullet {
    private Entity target;
    private double vx, vy;
    private double turnRate = 0.15;  // 增强转向速率（原0.08）
    private List<? extends Entity> potentialTargets;
    private long createTime;
    private static final long LIFETIME = 5000;  // 5秒后自动销毁
    
    public MissileBullet(double x, double y, Direction direction, Entity owner, List<? extends Entity> targets) {
        super(x, y, direction, owner);
        this.speed = Constants.MISSILE_SPEED;
        this.damage = (int)(Constants.BULLET_DAMAGE * 1.5);
        this.color = Constants.COLOR_MISSILE;
        this.potentialTargets = targets;
        this.width = 10;
        this.height = 10;
        this.createTime = System.currentTimeMillis();
        
        // 初始速度方向
        double angle = Math.toRadians(direction.getAngle() - 90);
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
        
        findTarget();
    }
    
    private void findTarget() {
        if (potentialTargets == null) return;
        
        double minDist = Double.MAX_VALUE;
        target = null;
        
        for (Entity e : potentialTargets) {
            if (e != null && e.isAlive() && e != owner) {
                double dist = distanceTo(e);
                if (dist < minDist) {
                    minDist = dist;
                    target = e;
                }
            }
        }
    }
    
    @Override
    public void update(double deltaTime) {
        // 每帧都尝试寻找/更新目标
        findTarget();
        
        // 追踪目标
        if (target != null && target.isAlive()) {
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist > 0) {
                // 期望速度方向
                double targetVx = (dx / dist) * speed;
                double targetVy = (dy / dist) * speed;
                
                // 平滑转向
                vx += (targetVx - vx) * turnRate;
                vy += (targetVy - vy) * turnRate;
                
                // 保持速度恒定
                double currentSpeed = Math.sqrt(vx * vx + vy * vy);
                if (currentSpeed > 0) {
                    vx = (vx / currentSpeed) * speed;
                    vy = (vy / currentSpeed) * speed;
                }
            }
        }
        
        x += vx;
        y += vy;
        
        // 超时销毁
        if (System.currentTimeMillis() - createTime > LIFETIME) {
            destroy();
        }
        
        if (!isInBounds()) {
            destroy();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        // 计算朝向角度
        double angle = Math.atan2(vy, vx);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.rotate(angle + Math.PI / 2);
        
        // 导弹主体
        g.setColor(color);
        int[] xPoints = {0, -5, 0, 5};
        int[] yPoints = {-8, 5, 2, 5};
        g2.fillPolygon(xPoints, yPoints, 4);
        
        // 尾焰
        g2.setColor(new Color(255, 100, 0));
        g2.fillOval(-3, 4, 6, 8);
        g2.setColor(Color.YELLOW);
        g2.fillOval(-2, 5, 4, 5);
        
        g2.dispose();
    }
    
    public void setTargets(List<? extends Entity> targets) {
        this.potentialTargets = targets;
        if (target == null || !target.isAlive()) {
            findTarget();
        }
    }
}
