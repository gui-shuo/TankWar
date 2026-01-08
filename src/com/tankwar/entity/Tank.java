package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;

/**
 * 坦克基类
 */
public abstract class Tank extends Entity {
    protected int hp;
    protected int maxHp;
    protected long lastShootTime;
    protected long shootCooldown;
    protected boolean canShoot = true;
    protected Color bodyColor;
    protected Color turretColor;
    
    // 状态效果
    protected boolean stunned = false;
    protected long stunEndTime = 0;
    protected boolean invincible = false;
    protected long invincibleEndTime = 0;
    
    public Tank(double x, double y, int maxHp, Color bodyColor) {
        super(x, y, Constants.TANK_SIZE, Constants.TANK_SIZE);
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.bodyColor = bodyColor;
        this.turretColor = bodyColor.brighter();
        this.shootCooldown = Constants.PLAYER_SHOOT_COOLDOWN;
    }
    
    @Override
    public void update(double deltaTime) {
        // 更新状态效果
        long now = System.currentTimeMillis();
        if (stunned && now >= stunEndTime) {
            stunned = false;
        }
        if (invincible && now >= invincibleEndTime) {
            invincible = false;
        }
        
        // 更新射击冷却
        if (!canShoot && now - lastShootTime >= shootCooldown) {
            canShoot = true;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!alive) return;
        
        int size = Constants.TANK_SIZE;
        int x = (int)(this.x - size / 2);
        int y = (int)(this.y - size / 2);
        
        // 眩晕闪烁效果
        if (stunned) {
            float flash = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 50.0));
            g.setColor(new Color(100, 100, 255, (int)(flash * 150)));
            g.fillOval(x - 5, y - 5, size + 10, size + 10);
        }
        
        // 无敌闪烁效果
        if (invincible) {
            float alpha = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 100.0));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }
        
        // 坦克主体
        g.setColor(bodyColor);
        g.fillRoundRect(x + 2, y + 2, size - 4, size - 4, 4, 4);
        
        // 履带
        g.setColor(bodyColor.darker().darker());
        switch (direction) {
            case UP:
            case DOWN:
                g.fillRect(x, y, 4, size);
                g.fillRect(x + size - 4, y, 4, size);
                break;
            case LEFT:
            case RIGHT:
                g.fillRect(x, y, size, 4);
                g.fillRect(x, y + size - 4, size, 4);
                break;
        }
        
        // 炮塔（圆形）
        g.setColor(turretColor);
        int turretSize = size / 2;
        g.fillOval((int)this.x - turretSize/2, (int)this.y - turretSize/2, turretSize, turretSize);
        
        // 炮管
        g.setColor(bodyColor.darker());
        int barrelLength = size / 2 + 4;
        int barrelWidth = 6;
        int bx = (int)this.x;
        int by = (int)this.y;
        
        switch (direction) {
            case UP:
                g.fillRect(bx - barrelWidth/2, by - barrelLength, barrelWidth, barrelLength);
                break;
            case DOWN:
                g.fillRect(bx - barrelWidth/2, by, barrelWidth, barrelLength);
                break;
            case LEFT:
                g.fillRect(bx - barrelLength, by - barrelWidth/2, barrelLength, barrelWidth);
                break;
            case RIGHT:
                g.fillRect(bx, by - barrelWidth/2, barrelLength, barrelWidth);
                break;
        }
        
        // 恢复透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
        // 血条
        renderHealthBar(g, x, y - 8, size);
    }
    
    protected void renderHealthBar(Graphics2D g, int x, int y, int width) {
        if (hp >= maxHp) return;  // 满血不显示
        
        int barHeight = 4;
        float hpPercent = (float) hp / maxHp;
        
        // 背景
        g.setColor(Constants.COLOR_HP_BG);
        g.fillRect(x, y, width, barHeight);
        
        // 血量
        Color hpColor = hpPercent > 0.5 ? Constants.COLOR_HP_BAR : 
                       hpPercent > 0.25 ? Color.YELLOW : Color.RED;
        g.setColor(hpColor);
        g.fillRect(x, y, (int)(width * hpPercent), barHeight);
        
        // 边框
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, barHeight);
    }
    
    /**
     * 受到伤害
     * @return 是否死亡
     */
    public boolean takeDamage(int damage) {
        if (invincible) return false;
        
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            destroy();
            return true;
        }
        return false;
    }
    
    public void heal(int amount) {
        hp = Math.min(hp + amount, maxHp);
    }
    
    public void stun(long duration) {
        stunned = true;
        stunEndTime = System.currentTimeMillis() + duration;
    }
    
    public void setInvincible(long duration) {
        invincible = true;
        invincibleEndTime = System.currentTimeMillis() + duration;
    }
    
    public boolean canShoot() {
        return canShoot && !stunned;
    }
    
    public void resetShootCooldown() {
        canShoot = false;
        lastShootTime = System.currentTimeMillis();
    }
    
    /**
     * 获取炮口位置
     */
    public Point getMuzzlePosition() {
        int offset = Constants.TANK_SIZE / 2 + 5;
        return new Point(
            (int)(x + direction.dx * offset),
            (int)(y + direction.dy * offset)
        );
    }
    
    // Getters
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean isStunned() { return stunned; }
    public boolean isInvincible() { return invincible; }
    public void setHp(int hp) { this.hp = Math.min(hp, maxHp); }
}
