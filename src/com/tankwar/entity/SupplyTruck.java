package com.tankwar.entity;

import com.tankwar.util.Constants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 护送模式 - 物资车
 */
public class SupplyTruck extends Entity {
    private int hp = 200;
    private int maxHp = 200;
    private List<Point> path;
    private int currentPathIndex = 0;
    private boolean reachedEnd = false;
    
    public SupplyTruck(double x, double y) {
        super(x, y, 36, 28);
        this.speed = 0.8;
        this.path = new ArrayList<>();
    }
    
    public void setPath(List<Point> path) {
        this.path = path;
        this.currentPathIndex = 0;
    }
    
    /**
     * 生成默认路径（从上到下）
     */
    public void generateDefaultPath() {
        path.clear();
        int cols = Constants.MAP_COLS;
        int tileSize = Constants.TILE_SIZE;
        
        // 简单的S形路径
        int startY = 3 * tileSize;
        int endY = (Constants.MAP_ROWS - 2) * tileSize;
        int midX = cols / 2 * tileSize;
        
        // 起点
        path.add(new Point((int)x, (int)y));
        
        // 向下移动
        for (int y = startY; y < endY; y += tileSize * 3) {
            path.add(new Point(midX, y));
            path.add(new Point(midX + tileSize * 4, y + tileSize));
            path.add(new Point(midX - tileSize * 4, y + tileSize * 2));
            path.add(new Point(midX, y + tileSize * 3));
        }
        
        // 终点
        path.add(new Point(midX, endY));
    }
    
    @Override
    public void update(double deltaTime) {
        if (reachedEnd || path.isEmpty()) return;
        
        Point target = path.get(currentPathIndex);
        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist < 5) {
            // 到达当前路径点
            currentPathIndex++;
            if (currentPathIndex >= path.size()) {
                reachedEnd = true;
            }
        } else {
            // 移动向目标
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        int w = width;
        int h = height;
        int px = (int)(x - w/2);
        int py = (int)(y - h/2);
        
        // 车身
        g.setColor(new Color(80, 120, 80));
        g.fillRoundRect(px, py, w, h, 6, 6);
        
        // 车厢
        g.setColor(new Color(100, 80, 60));
        g.fillRect(px + 8, py + 2, w - 12, h - 4);
        
        // 货物
        g.setColor(new Color(200, 180, 100));
        g.fillRect(px + 12, py + 5, w - 20, h - 10);
        
        // 轮子
        g.setColor(Color.BLACK);
        g.fillOval(px + 2, py + h - 6, 8, 8);
        g.fillOval(px + w - 10, py + h - 6, 8, 8);
        
        // 进度指示
        float progress = path.isEmpty() ? 0 : (float) currentPathIndex / path.size();
        
        // 血条
        int barWidth = w;
        int barHeight = 6;
        int barY = py - 10;
        
        g.setColor(Constants.COLOR_HP_BG);
        g.fillRect(px, barY, barWidth, barHeight);
        
        float hpPercent = (float) hp / maxHp;
        g.setColor(hpPercent > 0.5 ? Constants.COLOR_HP_BAR : 
                  hpPercent > 0.25 ? Color.YELLOW : Color.RED);
        g.fillRect(px, barY, (int)(barWidth * hpPercent), barHeight);
        
        g.setColor(Color.WHITE);
        g.drawRect(px, barY, barWidth, barHeight);
        
        // 标签
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("物资车", px, barY - 3);
    }
    
    public boolean takeDamage(int damage) {
        hp -= damage;
        if (hp <= 0) {
            hp = 0;
            destroy();
            return true;
        }
        return false;
    }
    
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean hasReachedEnd() { return reachedEnd; }
    public float getProgress() {
        return path.isEmpty() ? 0 : (float) currentPathIndex / path.size();
    }
}
