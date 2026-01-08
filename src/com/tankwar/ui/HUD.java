package com.tankwar.ui;

import com.tankwar.entity.*;
import com.tankwar.game.GameMode;
import com.tankwar.system.ChipSystem;
import com.tankwar.util.Constants;
import java.awt.*;

/**
 * 游戏内HUD显示
 */
public class HUD {
    private int hudX;
    private int hudWidth;
    
    public HUD() {
        this.hudX = Constants.HUD_X;
        this.hudWidth = Constants.HUD_WIDTH;
    }
    
    public void render(Graphics2D g, PlayerTank player, int level, int enemiesLeft, 
                      int score, GameMode mode, String weatherName) {
        // HUD背景
        g.setColor(Constants.COLOR_HUD_BG);
        g.fillRect(hudX, 0, hudWidth, Constants.WINDOW_HEIGHT);
        
        // 分隔线
        g.setColor(new Color(80, 80, 90));
        g.fillRect(hudX, 0, 3, Constants.WINDOW_HEIGHT);
        
        int y = 30;
        int padding = 15;
        
        // 游戏标题
        g.setColor(Constants.COLOR_TEXT);
        g.setFont(new Font("微软雅黑", Font.BOLD, 18));
        drawCenteredString(g, "坦克大战", hudX, hudWidth, y);
        
        y += 35;
        
        // 游戏模式
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        g.setColor(new Color(150, 200, 255));
        drawCenteredString(g, mode.name, hudX, hudWidth, y);
        
        y += 40;
        
        // 关卡/波次
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g.drawString("第 " + level + " 关", hudX + padding, y);
        
        y += 30;
        
        // 剩余敌人
        g.setColor(new Color(255, 150, 150));
        g.drawString("敌人: " + enemiesLeft, hudX + padding, y);
        
        y += 40;
        
        // 分隔线
        g.setColor(new Color(60, 60, 70));
        g.drawLine(hudX + padding, y, hudX + hudWidth - padding, y);
        
        y += 25;
        
        // 玩家信息
        if (player != null) {
            renderPlayerInfo(g, player, y);
        }
        
        y += 200;
        
        // 分隔线
        g.setColor(new Color(60, 60, 70));
        g.drawLine(hudX + padding, y, hudX + hudWidth - padding, y);
        
        y += 25;
        
        // 分数
        g.setColor(Constants.COLOR_BASE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 18));
        g.drawString("得分", hudX + padding, y);
        y += 25;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(String.format("%,d", score), hudX + padding, y);
        
        y += 40;
        
        // 天气
        if (weatherName != null && !weatherName.equals("晴朗")) {
            g.setColor(new Color(200, 200, 100));
            g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            g.drawString("天气: " + weatherName, hudX + padding, y);
            y += 25;
        }
        
        // 控制说明（底部）
        renderControls(g);
    }
    
    private void renderPlayerInfo(Graphics2D g, PlayerTank player, int startY) {
        int padding = 15;
        int y = startY;
        
        // 玩家标题
        g.setColor(player.getPlayerNumber() == 1 ? Constants.COLOR_PLAYER : Constants.COLOR_PLAYER2);
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g.drawString("玩家 " + player.getPlayerNumber(), hudX + padding, y);
        
        y += 25;
        
        // 生命
        g.setColor(new Color(255, 100, 150));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        StringBuilder lives = new StringBuilder("生命: ");
        for (int i = 0; i < player.getLives(); i++) {
            lives.append("♥ ");
        }
        g.drawString(lives.toString(), hudX + padding, y);
        
        y += 25;
        
        // 血量条
        g.setColor(Color.GRAY);
        g.drawString("HP:", hudX + padding, y);
        
        int barX = hudX + padding + 30;
        int barWidth = hudWidth - padding * 2 - 35;
        int barHeight = 16;
        
        // 背景
        g.setColor(Constants.COLOR_HP_BG);
        g.fillRect(barX, y - 12, barWidth, barHeight);
        
        // 血量
        float hpPercent = (float) player.getHp() / player.getMaxHp();
        Color hpColor = hpPercent > 0.5 ? Constants.COLOR_HP_BAR : 
                       hpPercent > 0.25 ? Color.YELLOW : Color.RED;
        g.setColor(hpColor);
        g.fillRect(barX, y - 12, (int)(barWidth * hpPercent), barHeight);
        
        // 数值
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        String hpText = player.getHp() + "/" + player.getMaxHp();
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hpText, barX + (barWidth - fm.stringWidth(hpText)) / 2, y + 1);
        
        y += 28;
        
        // 当前武器
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        WeaponType weapon = player.getCurrentWeapon();
        String ammoText = player.getCurrentAmmo() == -1 ? "∞" : String.valueOf(player.getCurrentAmmo());
        g.drawString("武器: " + weapon.name + " (" + ammoText + ")", hudX + padding, y);
        
        y += 28;
        
        // 技能冷却
        renderSkillBar(g, "相位移动 [Q]", player.getPhaseCooldownRemaining(), 
                      Constants.SKILL_PHASE_COOLDOWN, hudX + padding, y, hudWidth - padding * 2);
        
        y += 25;
        
        renderSkillBar(g, "电磁脉冲 [E]", player.getEmpCooldownRemaining(), 
                      Constants.SKILL_EMP_COOLDOWN, hudX + padding, y, hudWidth - padding * 2);
    }
    
    private void renderSkillBar(Graphics2D g, String name, long cooldownRemaining, 
                                long maxCooldown, int x, int y, int width) {
        boolean ready = cooldownRemaining <= 0;
        
        // 技能名
        g.setColor(ready ? new Color(100, 255, 150) : Color.GRAY);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        g.drawString(name, x, y);
        
        // 冷却条
        int barWidth = 80;
        int barHeight = 8;
        int barX = x + width - barWidth;
        int barY = y - 8;
        
        g.setColor(new Color(40, 40, 50));
        g.fillRect(barX, barY, barWidth, barHeight);
        
        if (ready) {
            g.setColor(new Color(100, 255, 150));
            g.fillRect(barX, barY, barWidth, barHeight);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 8));
            g.drawString("READY", barX + 25, barY + 7);
        } else {
            float progress = 1 - (float) cooldownRemaining / maxCooldown;
            g.setColor(new Color(100, 150, 200));
            g.fillRect(barX, barY, (int)(barWidth * progress), barHeight);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 8));
            g.drawString(String.format("%.1fs", cooldownRemaining / 1000.0), barX + 30, barY + 7);
        }
    }
    
    private void renderControls(Graphics2D g) {
        int y = Constants.WINDOW_HEIGHT - 180;
        int padding = 15;
        
        g.setColor(new Color(60, 60, 70));
        g.drawLine(hudX + padding, y - 15, hudX + hudWidth - padding, y - 15);
        
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("微软雅黑", Font.BOLD, 12));
        g.drawString("操作说明", hudX + padding, y);
        
        y += 20;
        g.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        g.setColor(new Color(180, 180, 190));
        
        String[] controls = {
            "W/A/S/D - 移动",
            "空格 - 射击",
            "Q - 相位移动",
            "E - 电磁脉冲",
            "TAB - 切换武器",
            "P/ESC - 暂停",
            "B - 商店"
        };
        
        for (String control : controls) {
            g.drawString(control, hudX + padding, y);
            y += 18;
        }
    }
    
    private void drawCenteredString(Graphics2D g, String text, int areaX, int areaWidth, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = areaX + (areaWidth - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }
    
    /**
     * 渲染已获得的芯片列表
     */
    public void renderChips(Graphics2D g, ChipSystem chipSystem, int startY) {
        int padding = 15;
        int y = startY;
        
        g.setColor(new Color(200, 180, 100));
        g.setFont(new Font("微软雅黑", Font.BOLD, 12));
        g.drawString("战术芯片", hudX + padding, y);
        
        y += 18;
        
        java.util.List<ChipSystem.ChipType> chips = chipSystem.getAcquiredChips();
        if (chips.isEmpty()) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            g.drawString("(暂无)", hudX + padding, y);
        } else {
            g.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            for (ChipSystem.ChipType chip : chips) {
                g.setColor(chip.color);
                g.drawString("● " + chip.name, hudX + padding, y);
                y += 16;
            }
        }
    }
}
