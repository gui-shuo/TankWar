package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;

/**
 * 激光子弹 - 穿透型
 */
public class LaserBullet extends Bullet {
    private int length = 30;  // 激光长度
    
    public LaserBullet(double x, double y, Direction direction, Entity owner) {
        super(x, y, direction, owner);
        this.speed = Constants.LASER_SPEED;
        this.damage = Constants.BULLET_DAMAGE * 2;
        this.piercing = true;
        this.pierceCount = 5;  // 可穿透5个目标
        this.color = Constants.COLOR_LASER;
        this.width = direction == Direction.LEFT || direction == Direction.RIGHT ? length : 4;
        this.height = direction == Direction.UP || direction == Direction.DOWN ? length : 4;
    }
    
    @Override
    public void render(Graphics2D g) {
        // 激光主体
        g.setColor(color);
        
        int w, h;
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            w = length;
            h = 4;
        } else {
            w = 4;
            h = length;
        }
        
        g.fillRect((int)(x - w/2), (int)(y - h/2), w, h);
        
        // 发光效果
        g.setColor(new Color(255, 100, 100, 80));
        g.fillRect((int)(x - w/2 - 2), (int)(y - h/2 - 2), w + 4, h + 4);
        
        // 核心高亮
        g.setColor(Color.WHITE);
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            g.fillRect((int)(x - w/2), (int)(y - 1), w, 2);
        } else {
            g.fillRect((int)(x - 1), (int)(y - h/2), 2, h);
        }
    }
}
