package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import com.tankwar.world.GameMap;
import java.awt.*;

/**
 * 敌人坦克
 */
public class EnemyTank extends Tank {
    private EnemyAffix affix;
    private boolean isElite;
    
    // AI相关
    private long lastDirectionChange = 0;
    private long directionChangeInterval = 2000;
    private long lastAiUpdate = 0;
    private PlayerTank target;
    private Point baseTarget;  // 攻击基地目标
    
    private int moveCounter = 0;
    private boolean isMoving = true;
    
    public EnemyTank(double x, double y, boolean elite) {
        super(x, y, 50, elite ? Constants.COLOR_ENEMY_ELITE : Constants.COLOR_ENEMY);
        this.isElite = elite;
        this.speed = Constants.ENEMY_SPEED;
        this.shootCooldown = Constants.ENEMY_SHOOT_COOLDOWN;
        
        if (elite) {
            this.affix = EnemyAffix.randomElite();
            this.maxHp = 75;
            this.hp = maxHp;
            applyAffixEffects();
        } else {
            this.affix = EnemyAffix.NONE;
        }
        
        this.direction = Direction.DOWN;
        this.directionChangeInterval = 1500 + (int)(Math.random() * 2000);
    }
    
    private void applyAffixEffects() {
        if (affix == EnemyAffix.FAST) {
            speed = Constants.ENEMY_SPEED * 2;
        } else if (affix == EnemyAffix.SHIELD) {
            maxHp = 100;
            hp = maxHp;
        }
        
        if (affix.color != null) {
            bodyColor = affix.color;
            turretColor = affix.color.brighter();
        }
    }
    
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        
        if (stunned) return;
        
        long now = System.currentTimeMillis();
        
        // AI决策
        if (now - lastAiUpdate > 100) {
            lastAiUpdate = now;
            updateAI();
        }
        
        // 定期改变方向
        if (now - lastDirectionChange > directionChangeInterval) {
            changeDirection();
            lastDirectionChange = now;
        }
    }
    
    private void updateAI() {
        // 简单AI：朝玩家或基地移动
        if (target != null && target.isAlive() && Math.random() < 0.3) {
            direction = getDirectionTo(target);
        } else if (baseTarget != null && Math.random() < 0.2) {
            // 朝基地移动
            double dx = baseTarget.x - x;
            double dy = baseTarget.y - y;
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? Direction.RIGHT : Direction.LEFT;
            } else {
                direction = dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }
    
    private void changeDirection() {
        // 随机改变方向，但有概率保持当前方向
        if (Math.random() < 0.3) {
            return;
        }
        
        Direction[] dirs = Direction.values();
        direction = dirs[(int)(Math.random() * dirs.length)];
    }
    
    /**
     * 尝试射击
     */
    public Bullet tryShoot() {
        if (!canShoot()) return null;
        
        Point muzzle = getMuzzlePosition();
        Bullet bullet = new Bullet(muzzle.x, muzzle.y, direction, this);
        resetShootCooldown();
        return bullet;
    }
    
    /**
     * 检查是否应该射击（面向玩家方向）
     */
    public boolean shouldShoot(PlayerTank player) {
        if (player == null || !player.isAlive()) return Math.random() < 0.1;
        
        // 检查是否在射击线上
        double dx = player.getX() - x;
        double dy = player.getY() - y;
        
        switch (direction) {
            case UP:
                return dy < 0 && Math.abs(dx) < Constants.TANK_SIZE;
            case DOWN:
                return dy > 0 && Math.abs(dx) < Constants.TANK_SIZE;
            case LEFT:
                return dx < 0 && Math.abs(dy) < Constants.TANK_SIZE;
            case RIGHT:
                return dx > 0 && Math.abs(dy) < Constants.TANK_SIZE;
        }
        return false;
    }
    
    @Override
    public void render(Graphics2D g) {
        super.render(g);
        
        // 精英标识
        if (isElite && affix != EnemyAffix.NONE) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 9));
            String label = affix.name.substring(0, 1);
            g.drawString(label, (int)x - 3, (int)y + 3);
            
            // 光环效果
            float alpha = (float)(0.3 + 0.2 * Math.sin(System.currentTimeMillis() / 200.0));
            g.setColor(new Color(affix.color.getRed(), affix.color.getGreen(), 
                                affix.color.getBlue(), (int)(alpha * 255)));
            g.drawOval((int)x - width/2 - 3, (int)y - height/2 - 3, width + 6, height + 6);
        }
    }
    
    @Override
    public boolean takeDamage(int damage) {
        // 刚毅词缀：免疫正面伤害
        if (affix == EnemyAffix.TOUGH) {
            // 简化：只有绕后才能造成伤害（这里简化为概率判定）
            // 实际应该检测子弹来向
            // 这里假设有30%概率是正面命中
            if (Math.random() < 0.3) {
                return false;  // 正面免疫
            }
        }
        
        // 护盾词缀：伤害减半
        if (affix == EnemyAffix.SHIELD) {
            damage /= 2;
        }
        
        return super.takeDamage(damage);
    }
    
    /**
     * 检测是否被从后方攻击
     */
    public boolean isHitFromBehind(Direction bulletDir) {
        return bulletDir == direction;  // 子弹方向与坦克朝向相同说明从后方打来
    }
    
    public void setTarget(PlayerTank target) {
        this.target = target;
    }
    
    public void setBaseTarget(Point base) {
        this.baseTarget = base;
    }
    
    public EnemyAffix getAffix() { return affix; }
    public boolean isElite() { return isElite; }
    
    /**
     * 获取击杀分数
     */
    public int getScoreValue() {
        return isElite ? Constants.SCORE_ELITE : Constants.SCORE_ENEMY;
    }
    
    /**
     * 被阻挡时换方向
     */
    public void onBlocked() {
        changeDirection();
        lastDirectionChange = System.currentTimeMillis();
    }
    
    /**
     * 获取移动后的边界
     */
    public Rectangle getNextBounds() {
        return getNextBounds(direction, speed);
    }
}
