package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 散弹 - 扇形发射3发子弹
 */
public class ShotgunBullet extends Bullet {
    private double angle;  // 发射角度（弧度）
    private double vx, vy; // 速度分量
    
    public ShotgunBullet(double x, double y, Direction direction, Entity owner, double spreadAngle) {
        super(x, y, direction, owner);
        this.damage = (int)(Constants.BULLET_DAMAGE * 0.7);  // 单发伤害略低
        this.color = new Color(255, 200, 100);
        
        // 计算基础角度
        double baseAngle = Math.toRadians(direction.getAngle() - 90);  // 转换为标准角度
        this.angle = baseAngle + spreadAngle;
        
        // 计算速度分量
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }
    
    @Override
    public void update(double deltaTime) {
        x += vx;
        y += vy;
        
        if (!isInBounds()) {
            destroy();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        int size = Constants.BULLET_SIZE;
        g.fillOval((int)(x - size/2), (int)(y - size/2), size, size);
        
        // 尾迹
        g.setColor(new Color(255, 200, 100, 100));
        double tx = x - vx * 0.5;
        double ty = y - vy * 0.5;
        g.drawLine((int)x, (int)y, (int)tx, (int)ty);
    }
    
    /**
     * 创建一组散弹（3发）
     */
    public static List<ShotgunBullet> createSpread(double x, double y, Direction direction, Entity owner) {
        List<ShotgunBullet> bullets = new ArrayList<>();
        double spreadAngle = Math.toRadians(15);  // 散射角度
        
        bullets.add(new ShotgunBullet(x, y, direction, owner, -spreadAngle));
        bullets.add(new ShotgunBullet(x, y, direction, owner, 0));
        bullets.add(new ShotgunBullet(x, y, direction, owner, spreadAngle));
        
        return bullets;
    }
}
