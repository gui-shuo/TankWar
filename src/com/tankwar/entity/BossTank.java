package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Boss坦克 - 多阶段Boss战
 */
public class BossTank extends Tank {
    private int phase = 1;
    private int maxPhase = 3;
    private int phaseHp;
    
    // 攻击模式
    private long lastSpecialAttack = 0;
    private long specialAttackCooldown = 3000;
    private int attackPattern = 0;
    
    // 弹幕相关
    private boolean isBarrageMode = false;
    private long barrageStartTime;
    private int barrageCount = 0;
    
    // 移动
    private double targetX, targetY;
    private boolean isMovingToTarget = false;
    
    public BossTank(double x, double y) {
        super(x, y, 500, Constants.COLOR_BOSS);
        this.width = 50;
        this.height = 50;
        this.phaseHp = maxHp / maxPhase;
        this.speed = 1.0;
        this.shootCooldown = 800;
        
        bodyColor = Constants.COLOR_BOSS;
        turretColor = new Color(255, 200, 50);
    }
    
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        
        if (stunned) return;
        
        long now = System.currentTimeMillis();
        
        // 阶段转换
        updatePhase();
        
        // 移动AI
        updateMovement();
        
        // 特殊攻击
        if (now - lastSpecialAttack > specialAttackCooldown) {
            lastSpecialAttack = now;
        }
        
        // 弹幕模式
        if (isBarrageMode) {
            if (now - barrageStartTime > 5000) {
                isBarrageMode = false;
            }
        }
    }
    
    private void updatePhase() {
        int newPhase = (maxHp - hp) / phaseHp + 1;
        if (newPhase > phase && newPhase <= maxPhase) {
            phase = newPhase;
            onPhaseChange();
        }
    }
    
    private void onPhaseChange() {
        // 阶段转换时触发特效和行为变化
        setInvincible(1000);  // 短暂无敌
        
        switch (phase) {
            case 2:
                speed = 1.5;
                shootCooldown = 600;
                specialAttackCooldown = 2500;
                break;
            case 3:
                speed = 2.0;
                shootCooldown = 400;
                specialAttackCooldown = 2000;
                isBarrageMode = true;
                barrageStartTime = System.currentTimeMillis();
                break;
        }
    }
    
    private void updateMovement() {
        // Boss移动逻辑
        if (!isMovingToTarget || distanceTo(targetX, targetY) < 10) {
            // 随机选择新目标点
            targetX = 100 + Math.random() * (Constants.GAME_WIDTH - 200);
            targetY = 100 + Math.random() * 200;  // 在上半部分移动
            isMovingToTarget = true;
        }
        
        // 移动向目标
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist > 0) {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
            
            // 更新朝向
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                direction = dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }
    
    /**
     * 发射普通子弹
     */
    public Bullet shoot() {
        if (!canShoot()) return null;
        Point muzzle = getMuzzlePosition();
        Bullet bullet = new Bullet(muzzle.x, muzzle.y, direction, this);
        bullet.setDamage(40);
        resetShootCooldown();
        return bullet;
    }
    
    /**
     * 发射弹幕
     */
    public List<Bullet> shootBarrage() {
        List<Bullet> bullets = new ArrayList<>();
        
        switch (attackPattern % 3) {
            case 0:
                // 圆形弹幕
                for (int i = 0; i < 8; i++) {
                    double angle = i * Math.PI / 4;
                    Direction dir = angleToDirection(angle);
                    Bullet b = new Bullet(x, y, dir, this);
                    b.setDamage(20);
                    // 设置实际速度方向
                    bullets.add(new DirectionalBullet(x, y, angle, this));
                }
                break;
                
            case 1:
                // 十字弹幕
                for (Direction dir : Direction.values()) {
                    for (int i = 0; i < 3; i++) {
                        Point muzzle = getMuzzlePosition();
                        Bullet b = new Bullet(x + dir.dx * (i * 15), y + dir.dy * (i * 15), dir, this);
                        b.setDamage(25);
                        bullets.add(b);
                    }
                }
                break;
                
            case 2:
                // 散射
                for (int i = -2; i <= 2; i++) {
                    double angle = Math.toRadians(direction.getAngle() - 90 + i * 20);
                    bullets.add(new DirectionalBullet(x, y, angle, this));
                }
                break;
        }
        
        attackPattern++;
        return bullets;
    }
    
    private Direction angleToDirection(double angle) {
        double deg = Math.toDegrees(angle);
        if (deg < 0) deg += 360;
        
        if (deg >= 315 || deg < 45) return Direction.RIGHT;
        if (deg >= 45 && deg < 135) return Direction.DOWN;
        if (deg >= 135 && deg < 225) return Direction.LEFT;
        return Direction.UP;
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!alive) return;
        
        int size = width;
        int px = (int)(x - size/2);
        int py = (int)(y - size/2);
        
        // Boss光环
        float pulse = (float)(0.5 + 0.3 * Math.sin(System.currentTimeMillis() / 300.0));
        g.setColor(new Color(255, 100, 0, (int)(pulse * 100)));
        g.fillOval(px - 10, py - 10, size + 20, size + 20);
        
        // 主体
        g.setColor(bodyColor);
        g.fillRoundRect(px, py, size, size, 8, 8);
        
        // 装甲纹理
        g.setColor(bodyColor.darker());
        g.drawLine(px + 5, py + 5, px + size - 5, py + 5);
        g.drawLine(px + 5, py + size - 5, px + size - 5, py + size - 5);
        g.drawRect(px + 2, py + 2, size - 4, size - 4);
        
        // 炮塔
        g.setColor(turretColor);
        int turretSize = size / 2;
        g.fillOval((int)x - turretSize/2, (int)y - turretSize/2, turretSize, turretSize);
        
        // 双炮管
        g.setColor(bodyColor.darker());
        int barrelLength = size / 2 + 10;
        int barrelWidth = 8;
        
        switch (direction) {
            case UP:
                g.fillRect((int)x - 8, (int)y - barrelLength, barrelWidth, barrelLength);
                g.fillRect((int)x + 2, (int)y - barrelLength, barrelWidth, barrelLength);
                break;
            case DOWN:
                g.fillRect((int)x - 8, (int)y, barrelWidth, barrelLength);
                g.fillRect((int)x + 2, (int)y, barrelWidth, barrelLength);
                break;
            case LEFT:
                g.fillRect((int)x - barrelLength, (int)y - 8, barrelLength, barrelWidth);
                g.fillRect((int)x - barrelLength, (int)y + 2, barrelLength, barrelWidth);
                break;
            case RIGHT:
                g.fillRect((int)x, (int)y - 8, barrelLength, barrelWidth);
                g.fillRect((int)x, (int)y + 2, barrelLength, barrelWidth);
                break;
        }
        
        // 阶段指示
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("BOSS P" + phase, (int)x - 25, (int)y - size/2 - 15);
        
        // 血条（Boss用大血条）
        renderBossHealthBar(g, px, py - 25, size);
    }
    
    private void renderBossHealthBar(Graphics2D g, int x, int y, int width) {
        int barHeight = 8;
        float hpPercent = (float) hp / maxHp;
        
        // 背景
        g.setColor(Constants.COLOR_HP_BG);
        g.fillRect(x, y, width, barHeight);
        
        // 阶段分隔线
        int phaseWidth = width / maxPhase;
        for (int i = 1; i < maxPhase; i++) {
            g.setColor(Color.BLACK);
            g.drawLine(x + i * phaseWidth, y, x + i * phaseWidth, y + barHeight);
        }
        
        // 血量
        Color hpColor = phase == 1 ? Constants.COLOR_HP_BAR : 
                       phase == 2 ? Color.YELLOW : Color.RED;
        g.setColor(hpColor);
        g.fillRect(x, y, (int)(width * hpPercent), barHeight);
        
        // 边框
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, barHeight);
    }
    
    public int getPhase() { return phase; }
    public boolean isBarrageMode() { return isBarrageMode; }
    
    public void startBarrage() {
        isBarrageMode = true;
        barrageStartTime = System.currentTimeMillis();
    }
    
    @Override
    public int getHp() { return hp; }
    @Override
    public int getMaxHp() { return maxHp; }
    
    /**
     * 获取击杀分数
     */
    public int getScoreValue() {
        return Constants.SCORE_BOSS;
    }
    
    /**
     * 方向性子弹（用于弹幕）
     */
    public static class DirectionalBullet extends Bullet {
        private double vx, vy;
        
        public DirectionalBullet(double x, double y, double angle, Entity owner) {
            super(x, y, Direction.UP, owner);
            this.vx = Math.cos(angle) * speed;
            this.vy = Math.sin(angle) * speed;
            this.damage = 20;
            this.color = new Color(255, 150, 50);
        }
        
        @Override
        public void update(double deltaTime) {
            x += vx;
            y += vy;
            if (!isInBounds()) destroy();
        }
    }
}
