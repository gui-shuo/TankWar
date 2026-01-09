package com.tankwar.entity;

import java.awt.*;

/**
 * 道具实体
 */
public class PowerUp extends Entity {
    private PowerUpType type;
    private long createTime;
    private long lifetime = 10000;  // 10秒后消失
    private float bobOffset = 0;
    
    public PowerUp(double x, double y, PowerUpType type) {
        super(x, y, 24, 24);
        this.type = type;
        this.createTime = System.currentTimeMillis();
    }
    
    @Override
    public void update(double deltaTime) {
        // 上下浮动动画
        bobOffset = (float)(Math.sin(System.currentTimeMillis() / 200.0) * 3);
        
        // 超时消失
        if (System.currentTimeMillis() - createTime > lifetime) {
            destroy();
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        int size = width;
        int px = (int)(x - size/2);
        int py = (int)(y - size/2 + bobOffset);
        
        // 发光效果
        float pulse = (float)(0.6 + 0.4 * Math.sin(System.currentTimeMillis() / 150.0));
        g.setColor(new Color(type.color.getRed(), type.color.getGreen(), 
                            type.color.getBlue(), (int)(pulse * 100)));
        g.fillOval(px - 5, py - 5, size + 10, size + 10);
        
        // 主体
        g.setColor(type.color);
        g.fillRoundRect(px, py, size, size, 8, 8);
        
        // 边框
        g.setColor(type.color.brighter());
        g.drawRoundRect(px, py, size - 1, size - 1, 8, 8);
        
        // 图标/文字
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        String icon = getIcon();
        int textX = (int)x - fm.stringWidth(icon) / 2;
        int textY = (int)(y + bobOffset) + 4;
        g.drawString(icon, textX, textY);
        
        // 即将消失时闪烁
        long remaining = lifetime - (System.currentTimeMillis() - createTime);
        if (remaining < 3000) {
            float alpha = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 100.0));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha * 0.5f));
        }
    }
    
    private String getIcon() {
        switch (type) {
            case HEALTH: return "♥";
            case SHIELD: return "◇";
            case SPEED: return "»";
            case WEAPON_LASER: return "L";
            case WEAPON_SHOTGUN: return "S";
            case WEAPON_MISSILE: return "M";
            case EXTRA_LIFE: return "+1";
            case SCORE_BONUS: return "$";
            default: return "?";
        }
    }
    
    /**
     * 应用道具效果
     */
    public void apply(PlayerTank player) {
        switch (type) {
            case HEALTH:
                player.heal(50);
                break;
            case SHIELD:
                player.setInvincible(3000);
                break;
            case SPEED:
                player.setHasSpeedBoost(true);
                break;
            case WEAPON_LASER:
                player.addAmmo(WeaponType.LASER, 5);
                break;
            case WEAPON_SHOTGUN:
                player.addAmmo(WeaponType.SHOTGUN, 8);
                break;
            case WEAPON_MISSILE:
                player.addAmmo(WeaponType.MISSILE, 3);
                break;
            case EXTRA_LIFE:
                player.setLives(player.getLives() + 1);
                break;
            case SCORE_BONUS:
                player.addScore(200);
                break;
        }
        destroy();
    }
    
    public PowerUpType getType() { return type; }
}
