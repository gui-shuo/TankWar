package com.tankwar.entity;

import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家坦克
 */
public class PlayerTank extends Tank {
    private int playerNumber;  // 1 or 2
    private int score = 0;
    private int lives;
    
    // 武器系统
    private WeaponType currentWeapon = WeaponType.NORMAL;
    private int[] weaponAmmo = new int[WeaponType.values().length];  // 各武器弹药
    
    // 技能系统
    private long lastPhaseTime = 0;
    private long lastEmpTime = 0;
    private long lastActiveSkillTime = 0;
    private boolean phaseReady = true;
    private boolean empReady = true;
    private boolean activeSkillReady = true;
    private static final long ACTIVE_SKILL_COOLDOWN = 8000;  // 8秒冷却
    
    // 芯片增益
    private boolean hasRicochet = false;      // 子弹弹射
    private boolean hasVampire = false;       // 击杀回血
    private boolean hasMineDrop = false;      // 移动留雷
    private boolean hasSpeedBoost = false;    // 移速提升
    private boolean hasDoubleDamage = false;  // 双倍伤害
    private boolean hasShield = false;        // 护盾（下次伤害减半）
    
    // 移动留雷计时
    private long lastMineDropTime = 0;
    private static final long MINE_DROP_INTERVAL = 2000;
    
    // 传送冷却
    private long lastTeleportTime = 0;
    private static final long TELEPORT_COOLDOWN = 1000;
    
    public PlayerTank(double x, double y, int playerNumber) {
        super(x, y, Constants.PLAYER_MAX_HP, 
              playerNumber == 1 ? Constants.COLOR_PLAYER : Constants.COLOR_PLAYER2);
        this.playerNumber = playerNumber;
        this.lives = Constants.PLAYER_LIVES;
        this.speed = Constants.PLAYER_SPEED;
        this.shootCooldown = Constants.PLAYER_SHOOT_COOLDOWN;
        
        // 初始化弹药
        weaponAmmo[WeaponType.NORMAL.ordinal()] = -1;  // 无限
        weaponAmmo[WeaponType.LASER.ordinal()] = 0;
        weaponAmmo[WeaponType.SHOTGUN.ordinal()] = 0;
        weaponAmmo[WeaponType.MISSILE.ordinal()] = 0;
    }
    
    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        
        long now = System.currentTimeMillis();
        
        // 更新技能冷却
        if (!phaseReady && now - lastPhaseTime >= Constants.SKILL_PHASE_COOLDOWN) {
            phaseReady = true;
        }
        if (!empReady && now - lastEmpTime >= Constants.SKILL_EMP_COOLDOWN) {
            empReady = true;
        }
        if (!activeSkillReady && now - lastActiveSkillTime >= ACTIVE_SKILL_COOLDOWN) {
            activeSkillReady = true;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        super.render(g);
        
        // 玩家编号标识
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        String label = "P" + playerNumber;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, (int)x - fm.stringWidth(label)/2, (int)y + 4);
    }
    
    /**
     * 发射子弹
     */
    public List<Bullet> shoot(List<? extends Entity> enemies) {
        if (!canShoot()) return null;
        
        List<Bullet> bullets = new ArrayList<>();
        Point muzzle = getMuzzlePosition();
        
        // 检查弹药
        if (currentWeapon != WeaponType.NORMAL) {
            if (weaponAmmo[currentWeapon.ordinal()] <= 0) {
                currentWeapon = WeaponType.NORMAL;
            }
        }
        
        switch (currentWeapon) {
            case LASER:
                bullets.add(new LaserBullet(muzzle.x, muzzle.y, direction, this));
                weaponAmmo[currentWeapon.ordinal()]--;
                break;
                
            case SHOTGUN:
                bullets.addAll(ShotgunBullet.createSpread(muzzle.x, muzzle.y, direction, this));
                weaponAmmo[currentWeapon.ordinal()]--;
                break;
                
            case MISSILE:
                bullets.add(new MissileBullet(muzzle.x, muzzle.y, direction, this, enemies));
                weaponAmmo[currentWeapon.ordinal()]--;
                break;
                
            case NORMAL:
            default:
                Bullet bullet = new Bullet(muzzle.x, muzzle.y, direction, this);
                if (hasRicochet) {
                    bullet.setPiercing(true);
                }
                if (hasDoubleDamage) {
                    bullet.setDamage(bullet.getDamage() * 2);
                }
                bullets.add(bullet);
                break;
        }
        
        resetShootCooldown();
        return bullets;
    }
    
    /**
     * 使用相位移动技能
     */
    public boolean usePhaseShift() {
        if (!phaseReady || stunned) return false;
        
        x += direction.dx * Constants.PHASE_DISTANCE;
        y += direction.dy * Constants.PHASE_DISTANCE;
        clampToMap();
        
        phaseReady = false;
        lastPhaseTime = System.currentTimeMillis();
        setInvincible(500);  // 闪现后短暂无敌
        
        return true;
    }
    
    /**
     * 使用电磁脉冲技能
     */
    public boolean useEMP() {
        if (!empReady || stunned) return false;
        
        empReady = false;
        lastEmpTime = System.currentTimeMillis();
        return true;
    }
    
    /**
     * 使用主动技能（K键护盾）
     */
    public boolean useActiveSkill() {
        if (!activeSkillReady || stunned) return false;
        
        activeSkillReady = false;
        lastActiveSkillTime = System.currentTimeMillis();
        return true;
    }
    
    /**
     * 切换武器
     */
    public void switchWeapon() {
        WeaponType next = currentWeapon.next();
        // 跳过没有弹药的武器（除了普通武器）
        int attempts = 0;
        while (next != WeaponType.NORMAL && weaponAmmo[next.ordinal()] <= 0 && attempts < 4) {
            next = next.next();
            attempts++;
        }
        currentWeapon = next;
    }
    
    /**
     * 添加武器弹药
     */
    public void addAmmo(WeaponType type, int amount) {
        weaponAmmo[type.ordinal()] += amount;
    }
    
    /**
     * 获取当前武器弹药
     */
    public int getCurrentAmmo() {
        if (currentWeapon == WeaponType.NORMAL) return -1;
        return weaponAmmo[currentWeapon.ordinal()];
    }
    
    /**
     * 是否可以留下地雷
     */
    public boolean canDropMine() {
        if (!hasMineDrop) return false;
        return System.currentTimeMillis() - lastMineDropTime >= MINE_DROP_INTERVAL;
    }
    
    public void markMineDropped() {
        lastMineDropTime = System.currentTimeMillis();
    }
    
    /**
     * 是否可以传送
     */
    public boolean canTeleport() {
        return System.currentTimeMillis() - lastTeleportTime >= TELEPORT_COOLDOWN;
    }
    
    public void markTeleported() {
        lastTeleportTime = System.currentTimeMillis();
    }
    
    /**
     * 击杀敌人（触发回血等效果）
     */
    public void onEnemyKill(int scoreValue) {
        score += scoreValue;
        if (hasVampire) {
            heal(10);
        }
    }
    
    /**
     * 重生
     */
    public void respawn(double spawnX, double spawnY) {
        if (lives > 0) {
            lives--;
            hp = maxHp;
            x = spawnX;
            y = spawnY;
            alive = true;
            direction = Direction.UP;
            setInvincible(2000);  // 重生无敌2秒
        }
    }
    
    /**
     * 完全重置（新游戏）
     */
    public void fullReset(double spawnX, double spawnY) {
        lives = Constants.PLAYER_LIVES;
        score = 0;
        hp = maxHp;
        x = spawnX;
        y = spawnY;
        alive = true;
        direction = Direction.UP;
        currentWeapon = WeaponType.NORMAL;
        for (int i = 1; i < weaponAmmo.length; i++) {
            weaponAmmo[i] = 0;
        }
        // 重置芯片
        hasRicochet = false;
        hasVampire = false;
        hasMineDrop = false;
        hasSpeedBoost = false;
        hasDoubleDamage = false;
        hasShield = false;
    }
    
    @Override
    public boolean takeDamage(int damage) {
        if (hasShield) {
            damage /= 2;
            hasShield = false;
        }
        return super.takeDamage(damage);
    }
    
    // 芯片相关Getter/Setter
    public void setHasRicochet(boolean v) { hasRicochet = v; }
    public void setHasVampire(boolean v) { hasVampire = v; }
    public void setHasMineDrop(boolean v) { hasMineDrop = v; }
    public void setHasSpeedBoost(boolean v) { 
        hasSpeedBoost = v;
        if (v) speed = Constants.PLAYER_SPEED * 1.3;
    }
    public void setHasDoubleDamage(boolean v) { hasDoubleDamage = v; }
    public void setHasShield(boolean v) { hasShield = v; }
    
    public boolean hasRicochet() { return hasRicochet; }
    public boolean hasVampire() { return hasVampire; }
    public boolean hasMineDrop() { return hasMineDrop; }
    
    // Getters
    public int getPlayerNumber() { return playerNumber; }
    public int getScore() { return score; }
    public void addScore(int s) { score += s; }
    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public WeaponType getCurrentWeapon() { return currentWeapon; }
    public boolean isPhaseReady() { return phaseReady; }
    public boolean isEmpReady() { return empReady; }
    
    public long getPhaseCooldownRemaining() {
        if (phaseReady) return 0;
        return Math.max(0, Constants.SKILL_PHASE_COOLDOWN - (System.currentTimeMillis() - lastPhaseTime));
    }
    
    public long getEmpCooldownRemaining() {
        if (empReady) return 0;
        return Math.max(0, Constants.SKILL_EMP_COOLDOWN - (System.currentTimeMillis() - lastEmpTime));
    }
    
    public long getActiveSkillCooldownRemaining() {
        if (activeSkillReady) return 0;
        return Math.max(0, ACTIVE_SKILL_COOLDOWN - (System.currentTimeMillis() - lastActiveSkillTime));
    }
    
    public boolean isActiveSkillReady() { return activeSkillReady; }
}
