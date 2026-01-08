package com.tankwar.ui;

import com.tankwar.util.Constants;
import java.awt.*;

/**
 * UI管理器 - 处理各种界面显示
 */
public class UIManager {
    
    /**
     * 渲染暂停界面
     */
    public void renderPause(Graphics2D g, int selectedIndex) {
        int width = Constants.GAME_WIDTH;
        int height = Constants.GAME_HEIGHT;
        
        // 半透明遮罩
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width, height);
        
        // 暂停框
        int boxWidth = 300;
        int boxHeight = 250;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        g.setColor(new Color(40, 45, 55));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g.setColor(new Color(100, 100, 120));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 32));
        String title = "游戏暂停";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, boxX + (boxWidth - fm.stringWidth(title)) / 2, boxY + 50);
        
        // 菜单项
        String[] items = {"继续游戏", "重新开始", "返回主菜单"};
        g.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        
        int itemY = boxY + 100;
        for (int i = 0; i < items.length; i++) {
            boolean selected = (i == selectedIndex);
            
            if (selected) {
                g.setColor(new Color(255, 215, 0));
                g.drawString("▶ ", boxX + 40, itemY);
            } else {
                g.setColor(new Color(180, 180, 190));
            }
            
            g.drawString(items[i], boxX + 70, itemY);
            itemY += 45;
        }
    }
    
    /**
     * 渲染游戏结束界面
     */
    public void renderGameOver(Graphics2D g, int score, int level, boolean victory) {
        int width = Constants.GAME_WIDTH;
        int height = Constants.GAME_HEIGHT;
        
        // 背景
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);
        
        // 结果框
        int boxWidth = 400;
        int boxHeight = 350;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        Color boxColor = victory ? new Color(40, 60, 50) : new Color(60, 40, 40);
        g.setColor(boxColor);
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        
        Color borderColor = victory ? new Color(100, 200, 100) : new Color(200, 100, 100);
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        
        int y = boxY + 60;
        
        // 标题
        g.setFont(new Font("微软雅黑", Font.BOLD, 40));
        String title = victory ? "胜 利 !" : "游戏结束";
        g.setColor(victory ? new Color(100, 255, 100) : new Color(255, 100, 100));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, boxX + (boxWidth - fm.stringWidth(title)) / 2, y);
        
        y += 60;
        
        // 统计
        g.setFont(new Font("微软雅黑", Font.PLAIN, 22));
        g.setColor(Color.WHITE);
        
        g.drawString("最终关卡: " + level, boxX + 80, y);
        y += 40;
        
        g.drawString("最终得分: " + String.format("%,d", score), boxX + 80, y);
        y += 40;
        
        // 评级
        String rank = getRank(score);
        g.setColor(getRankColor(rank));
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        g.drawString("评级: " + rank, boxX + 80, y);
        
        y += 60;
        
        // 提示
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        String hint = "按 Enter 重新开始  |  ESC 返回菜单";
        fm = g.getFontMetrics();
        g.drawString(hint, boxX + (boxWidth - fm.stringWidth(hint)) / 2, y);
    }
    
    private String getRank(int score) {
        if (score >= 10000) return "S";
        if (score >= 7000) return "A";
        if (score >= 5000) return "B";
        if (score >= 3000) return "C";
        return "D";
    }
    
    private Color getRankColor(String rank) {
        switch (rank) {
            case "S": return new Color(255, 215, 0);
            case "A": return new Color(200, 100, 255);
            case "B": return new Color(100, 200, 255);
            case "C": return new Color(100, 255, 100);
            default: return new Color(180, 180, 180);
        }
    }
    
    /**
     * 渲染关卡完成界面
     */
    public void renderLevelComplete(Graphics2D g, int level, int score, int enemiesKilled) {
        int width = Constants.GAME_WIDTH;
        int height = Constants.GAME_HEIGHT;
        
        // 背景
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, width, height);
        
        // 完成框
        int boxWidth = 350;
        int boxHeight = 280;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        g.setColor(new Color(40, 55, 70));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g.setColor(new Color(100, 180, 255));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        int y = boxY + 50;
        
        // 标题
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        String title = "第 " + level + " 关 完成!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, boxX + (boxWidth - fm.stringWidth(title)) / 2, y);
        
        y += 50;
        
        // 统计
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        g.drawString("消灭敌人: " + enemiesKilled, boxX + 60, y);
        y += 35;
        g.drawString("当前得分: " + String.format("%,d", score), boxX + 60, y);
        
        y += 60;
        
        // 提示
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        String hint = "选择战术芯片后进入下一关";
        fm = g.getFontMetrics();
        g.drawString(hint, boxX + (boxWidth - fm.stringWidth(hint)) / 2, y);
    }
    
    /**
     * 渲染商店界面
     */
    public void renderShop(Graphics2D g, int playerScore, int selectedIndex) {
        int width = Constants.GAME_WIDTH;
        int height = Constants.GAME_HEIGHT;
        
        // 背景
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, width, height);
        
        // 商店框
        int boxWidth = 500;
        int boxHeight = 400;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        g.setColor(new Color(50, 45, 40));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g.setColor(new Color(200, 180, 100));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        int y = boxY + 45;
        int padding = 30;
        
        // 标题
        g.setColor(new Color(255, 215, 0));
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        g.drawString("基地商店", boxX + padding, y);
        
        // 积分
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        String scoreText = "积分: " + String.format("%,d", playerScore);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(scoreText, boxX + boxWidth - padding - fm.stringWidth(scoreText), y);
        
        y += 50;
        
        // 商品列表
        String[][] items = {
            {"1. 自动哨戒炮", String.valueOf(Constants.PRICE_TURRET), "部署一座自动攻击炮台"},
            {"2. 修复围墙", String.valueOf(Constants.PRICE_REPAIR_WALL), "修复已损坏的砖墙"},
            {"3. 升级围墙", String.valueOf(Constants.PRICE_UPGRADE_WALL), "将砖墙升级为钢墙"},
            {"4. 恢复生命", "200", "恢复50点HP"},
            {"5. 额外武器弹药", "150", "获得各类武器弹药"}
        };
        
        g.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        
        for (int i = 0; i < items.length; i++) {
            boolean selected = (i == selectedIndex);
            int price = Integer.parseInt(items[i][1]);
            boolean canAfford = playerScore >= price;
            
            // 背景
            if (selected) {
                g.setColor(new Color(80, 70, 60));
                g.fillRoundRect(boxX + padding - 5, y - 20, boxWidth - padding * 2 + 10, 50, 8, 8);
            }
            
            // 名称
            g.setColor(selected ? new Color(255, 215, 0) : 
                      canAfford ? Color.WHITE : new Color(100, 100, 100));
            g.drawString(items[i][0], boxX + padding, y);
            
            // 价格
            g.setColor(canAfford ? new Color(100, 255, 100) : new Color(255, 100, 100));
            String priceText = "$" + items[i][1];
            g.drawString(priceText, boxX + boxWidth - padding - 80, y);
            
            // 描述
            g.setColor(new Color(150, 150, 160));
            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            g.drawString(items[i][2], boxX + padding + 20, y + 18);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 18));
            
            y += 55;
        }
        
        // 提示
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        String hint = "↑↓ 选择  |  Enter 购买  |  B/ESC 关闭";
        fm = g.getFontMetrics();
        g.drawString(hint, boxX + (boxWidth - fm.stringWidth(hint)) / 2, boxY + boxHeight - 25);
    }
    
    /**
     * 渲染EMP效果
     */
    public void renderEMPEffect(Graphics2D g, double x, double y, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 500) return;
        
        float progress = elapsed / 500f;
        int radius = (int)(Constants.EMP_RADIUS * progress);
        int alpha = (int)(200 * (1 - progress));
        
        g.setColor(new Color(100, 150, 255, alpha));
        g.setStroke(new BasicStroke(3));
        g.drawOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
        
        g.setColor(new Color(150, 200, 255, alpha / 2));
        g.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
    }
    
    /**
     * 渲染相位移动轨迹
     */
    public void renderPhaseTrail(Graphics2D g, double fromX, double fromY, double toX, double toY, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > 300) return;
        
        float alpha = 1 - elapsed / 300f;
        
        g.setColor(new Color(100, 200, 255, (int)(alpha * 150)));
        g.setStroke(new BasicStroke(3));
        g.drawLine((int)fromX, (int)fromY, (int)toX, (int)toY);
        
        // 残影
        g.setColor(new Color(100, 200, 255, (int)(alpha * 100)));
        int size = Constants.TANK_SIZE;
        g.fillOval((int)(fromX - size/2), (int)(fromY - size/2), size, size);
    }
}
