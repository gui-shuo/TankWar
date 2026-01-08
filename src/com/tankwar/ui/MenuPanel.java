package com.tankwar.ui;

import com.tankwar.game.GameMode;
import com.tankwar.util.Constants;
import java.awt.*;

/**
 * 主菜单面板
 */
public class MenuPanel {
    private int selectedIndex = 0;
    private String[] menuItems = {"开始游戏", "模式选择", "操作说明", "退出"};
    
    // 模式选择
    private boolean showModeSelect = false;
    private int selectedModeIndex = 0;
    private GameMode[] modes = GameMode.values();
    
    // 帮助
    private boolean showHelp = false;
    
    // 动画
    private long animTime = 0;
    
    public void update() {
        animTime = System.currentTimeMillis();
    }
    
    public void render(Graphics2D g) {
        int width = Constants.WINDOW_WIDTH;
        int height = Constants.WINDOW_HEIGHT;
        
        // 背景
        g.setColor(Constants.COLOR_BG);
        g.fillRect(0, 0, width, height);
        
        // 背景装饰
        renderBackground(g, width, height);
        
        if (showHelp) {
            renderHelp(g, width, height);
            return;
        }
        
        if (showModeSelect) {
            renderModeSelect(g, width, height);
            return;
        }
        
        // 标题
        renderTitle(g, width);
        
        // 菜单项
        int menuY = 350;
        int itemHeight = 60;
        
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        
        for (int i = 0; i < menuItems.length; i++) {
            int y = menuY + i * itemHeight;
            boolean selected = (i == selectedIndex);
            
            if (selected) {
                // 选中效果
                float pulse = (float)(0.8 + 0.2 * Math.sin(animTime / 150.0));
                g.setColor(new Color(255, 215, 0, (int)(255 * pulse)));
                
                // 箭头
                g.fillPolygon(
                    new int[]{width/2 - 150, width/2 - 130, width/2 - 150},
                    new int[]{y - 10, y, y + 10},
                    3
                );
                g.fillPolygon(
                    new int[]{width/2 + 150, width/2 + 130, width/2 + 150},
                    new int[]{y - 10, y, y + 10},
                    3
                );
            } else {
                g.setColor(new Color(180, 180, 180));
            }
            
            FontMetrics fm = g.getFontMetrics();
            int textX = (width - fm.stringWidth(menuItems[i])) / 2;
            g.drawString(menuItems[i], textX, y);
        }
        
        // 底部提示
        g.setColor(new Color(100, 100, 110));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        String hint = "↑↓ 选择  |  Enter 确认";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hint, (width - fm.stringWidth(hint)) / 2, height - 50);
        
        // 版本
        g.setColor(new Color(80, 80, 90));
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("v1.0 - Java Swing Edition", 20, height - 20);
    }
    
    private void renderTitle(Graphics2D g, int width) {
        // 标题阴影
        g.setFont(new Font("微软雅黑", Font.BOLD, 64));
        g.setColor(new Color(0, 0, 0, 100));
        String title = "坦克大战";
        FontMetrics fm = g.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g.drawString(title, titleX + 4, 154);
        
        // 标题渐变
        GradientPaint gradient = new GradientPaint(
            titleX, 100, new Color(255, 200, 50),
            titleX, 150, new Color(255, 100, 50)
        );
        g.setPaint(gradient);
        g.drawString(title, titleX, 150);
        
        // 副标题
        g.setColor(new Color(150, 200, 255));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        String subtitle = "创新版";
        fm = g.getFontMetrics();
        g.drawString(subtitle, (width - fm.stringWidth(subtitle)) / 2, 200);
    }
    
    private void renderBackground(Graphics2D g, int width, int height) {
        // 网格
        g.setColor(new Color(40, 45, 50));
        int gridSize = 50;
        for (int x = 0; x < width; x += gridSize) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += gridSize) {
            g.drawLine(0, y, width, y);
        }
        
        // 装饰坦克
        float offset = (float)Math.sin(animTime / 500.0) * 10;
        drawDecoTank(g, 100, 600 + offset, Constants.COLOR_PLAYER, 0);
        drawDecoTank(g, width - 100, 600 - offset, Constants.COLOR_ENEMY, 180);
    }
    
    private void drawDecoTank(Graphics2D g, int x, float y, Color color, int rotation) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(x, y);
        g2.rotate(Math.toRadians(rotation));
        
        int size = 40;
        
        // 履带
        g2.setColor(color.darker().darker());
        g2.fillRect(-size/2, -size/2, 8, size);
        g2.fillRect(size/2 - 8, -size/2, 8, size);
        
        // 主体
        g2.setColor(color);
        g2.fillRoundRect(-size/2 + 5, -size/2 + 5, size - 10, size - 10, 5, 5);
        
        // 炮塔
        g2.setColor(color.brighter());
        g2.fillOval(-10, -10, 20, 20);
        
        // 炮管
        g2.setColor(color.darker());
        g2.fillRect(-4, -size/2 - 15, 8, 20);
        
        g2.dispose();
    }
    
    private void renderModeSelect(Graphics2D g, int width, int height) {
        // 半透明背景
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, width, height);
        
        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 36));
        String title = "选择游戏模式";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (width - fm.stringWidth(title)) / 2, 150);
        
        // 模式卡片
        int cardWidth = 200;
        int cardHeight = 280;
        int gap = 30;
        int totalWidth = modes.length * cardWidth + (modes.length - 1) * gap;
        int startX = (width - totalWidth) / 2;
        int cardY = 200;
        
        for (int i = 0; i < modes.length; i++) {
            GameMode mode = modes[i];
            int cx = startX + i * (cardWidth + gap);
            boolean selected = (i == selectedModeIndex);
            
            // 选中效果
            if (selected) {
                float pulse = (float)(0.5 + 0.5 * Math.sin(animTime / 150.0));
                g.setColor(new Color(255, 215, 0, (int)(100 * pulse)));
                g.fillRoundRect(cx - 5, cardY - 5, cardWidth + 10, cardHeight + 10, 15, 15);
            }
            
            // 卡片背景
            g.setColor(selected ? new Color(60, 70, 80) : new Color(40, 45, 50));
            g.fillRoundRect(cx, cardY, cardWidth, cardHeight, 10, 10);
            
            // 边框
            g.setColor(selected ? new Color(255, 215, 0) : new Color(80, 80, 90));
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(cx, cardY, cardWidth, cardHeight, 10, 10);
            
            // 模式编号
            g.setColor(new Color(100, 100, 110));
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString(String.valueOf(i + 1), cx + 15, cardY + 55);
            
            // 模式名称
            g.setColor(Color.WHITE);
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            fm = g.getFontMetrics();
            g.drawString(mode.name, cx + (cardWidth - fm.stringWidth(mode.name)) / 2, cardY + 120);
            
            // 描述
            g.setColor(new Color(180, 180, 190));
            g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            String desc = mode.description;
            int maxWidth = cardWidth - 20;
            fm = g.getFontMetrics();
            int descY = cardY + 160;
            
            // 简单换行
            while (!desc.isEmpty() && descY < cardY + cardHeight - 20) {
                String line = desc;
                while (fm.stringWidth(line) > maxWidth && line.length() > 1) {
                    line = line.substring(0, line.length() - 1);
                }
                g.drawString(line, cx + 10, descY);
                desc = desc.substring(line.length());
                descY += 20;
            }
        }
        
        // 底部提示
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        String hint = "←→ 选择  |  Enter 确认  |  ESC 返回";
        fm = g.getFontMetrics();
        g.drawString(hint, (width - fm.stringWidth(hint)) / 2, height - 80);
    }
    
    private void renderHelp(Graphics2D g, int width, int height) {
        // 半透明背景
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);
        
        int boxWidth = 600;
        int boxHeight = 500;
        int boxX = (width - boxWidth) / 2;
        int boxY = (height - boxHeight) / 2;
        
        // 内容框
        g.setColor(new Color(40, 45, 50));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        g.setColor(new Color(100, 100, 110));
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        int y = boxY + 50;
        int padding = 40;
        
        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 28));
        g.drawString("操作说明", boxX + padding, y);
        
        y += 50;
        
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        g.setColor(new Color(200, 200, 210));
        
        String[][] controls = {
            {"W / ↑", "向上移动"},
            {"S / ↓", "向下移动"},
            {"A / ←", "向左移动"},
            {"D / →", "向右移动"},
            {"空格", "发射子弹"},
            {"Q", "相位移动（穿墙闪现）"},
            {"E", "电磁脉冲（范围眩晕）"},
            {"TAB", "切换武器"},
            {"B", "打开商店"},
            {"P / ESC", "暂停游戏"}
        };
        
        for (String[] control : controls) {
            g.setColor(new Color(255, 215, 0));
            g.drawString(control[0], boxX + padding, y);
            g.setColor(new Color(200, 200, 210));
            g.drawString(control[1], boxX + padding + 150, y);
            y += 30;
        }
        
        // 返回提示
        g.setColor(new Color(150, 150, 160));
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        String hint = "按 ESC 或 Enter 返回";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hint, boxX + (boxWidth - fm.stringWidth(hint)) / 2, boxY + boxHeight - 30);
    }
    
    public void moveUp() {
        if (showModeSelect) return;
        selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
    }
    
    public void moveDown() {
        if (showModeSelect) return;
        selectedIndex = (selectedIndex + 1) % menuItems.length;
    }
    
    public void moveLeft() {
        if (showModeSelect) {
            selectedModeIndex = (selectedModeIndex - 1 + modes.length) % modes.length;
        }
    }
    
    public void moveRight() {
        if (showModeSelect) {
            selectedModeIndex = (selectedModeIndex + 1) % modes.length;
        }
    }
    
    /**
     * @return 选中的操作：0=开始游戏, 1=模式选择, 2=帮助, 3=退出, -1=无操作
     */
    public int confirm() {
        if (showHelp) {
            showHelp = false;
            return -1;
        }
        
        if (showModeSelect) {
            showModeSelect = false;
            return 0;  // 选择完模式后开始游戏
        }
        
        switch (selectedIndex) {
            case 0: return 0;  // 开始游戏
            case 1: 
                showModeSelect = true;
                return -1;
            case 2:
                showHelp = true;
                return -1;
            case 3: return 3;  // 退出
            default: return -1;
        }
    }
    
    public boolean back() {
        if (showHelp) {
            showHelp = false;
            return true;
        }
        if (showModeSelect) {
            showModeSelect = false;
            return true;
        }
        return false;
    }
    
    public GameMode getSelectedMode() {
        return modes[selectedModeIndex];
    }
    
    public boolean isInSubmenu() {
        return showModeSelect || showHelp;
    }
}
