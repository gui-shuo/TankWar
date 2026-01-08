package com.tankwar.world;

import com.tankwar.util.Constants;
import java.awt.*;

/**
 * 地图块
 */
public class Tile {
    private TileType type;
    private int hp;
    private final int row;
    private final int col;
    
    public Tile(TileType type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.hp = getMaxHp();
    }
    
    private int getMaxHp() {
        switch (type) {
            case BRICK: return 2;
            case BASE: return 1;
            case BARREL: return 1;
            default: return 1;
        }
    }
    
    public TileType getType() { return type; }
    
    public void setType(TileType type) {
        this.type = type;
        this.hp = getMaxHp();
    }
    
    public boolean blocksMovement() {
        return type.blocksMovement;
    }
    
    public boolean isDestructible() {
        return type.destructible;
    }
    
    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public int getX() { return col * Constants.TILE_SIZE; }
    public int getY() { return row * Constants.TILE_SIZE; }
    
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), Constants.TILE_SIZE, Constants.TILE_SIZE);
    }
    
    /**
     * 受到伤害
     * @return 是否被摧毁
     */
    public boolean takeDamage(int damage) {
        if (!isDestructible()) return false;
        hp -= damage;
        if (hp <= 0) {
            return true;
        }
        return false;
    }
    
    public void render(Graphics2D g) {
        int x = getX();
        int y = getY();
        int size = Constants.TILE_SIZE;
        
        switch (type) {
            case EMPTY:
                // 不绘制
                break;
                
            case BRICK:
                g.setColor(Constants.COLOR_BRICK);
                g.fillRect(x, y, size, size);
                // 砖纹
                g.setColor(Constants.COLOR_BRICK.darker());
                g.drawLine(x, y + size/2, x + size, y + size/2);
                g.drawLine(x + size/2, y, x + size/2, y + size/2);
                g.drawLine(x, y + size/2, x, y + size);
                g.drawLine(x + size, y + size/2, x + size, y + size);
                break;
                
            case STEEL:
                g.setColor(Constants.COLOR_STEEL);
                g.fillRect(x, y, size, size);
                // 金属光泽
                g.setColor(Constants.COLOR_STEEL.brighter());
                g.drawLine(x + 2, y + 2, x + size - 4, y + 2);
                g.drawLine(x + 2, y + 2, x + 2, y + size - 4);
                g.setColor(Constants.COLOR_STEEL.darker());
                g.drawRect(x, y, size - 1, size - 1);
                break;
                
            case WATER:
                g.setColor(Constants.COLOR_WATER);
                g.fillRect(x, y, size, size);
                // 波纹
                g.setColor(Constants.COLOR_WATER.brighter());
                long time = System.currentTimeMillis();
                int offset = (int)((time / 200) % 10);
                g.drawArc(x - 5 + offset, y + 5, 15, 10, 0, 180);
                g.drawArc(x + 10 - offset, y + 15, 15, 10, 0, 180);
                break;
                
            case GRASS:
                g.setColor(Constants.COLOR_GRASS);
                g.fillRect(x, y, size, size);
                // 草纹
                g.setColor(Constants.COLOR_GRASS.brighter());
                for (int i = 0; i < 5; i++) {
                    int gx = x + 3 + (i * 6);
                    g.drawLine(gx, y + size - 5, gx - 2, y + 5);
                    g.drawLine(gx, y + size - 5, gx + 2, y + 8);
                }
                break;
                
            case BASE:
                // 基地（鹰标）
                g.setColor(Constants.COLOR_BASE);
                g.fillRect(x + 2, y + 2, size - 4, size - 4);
                g.setColor(Color.BLACK);
                g.drawRect(x + 2, y + 2, size - 5, size - 5);
                // 鹰形状简化
                g.setColor(new Color(139, 69, 19));
                int cx = x + size/2;
                int cy = y + size/2;
                int[] xPoints = {cx, cx - 8, cx - 4, cx, cx + 4, cx + 8};
                int[] yPoints = {cy - 8, cy + 5, cy, cy + 8, cy, cy + 5};
                g.fillPolygon(xPoints, yPoints, 6);
                break;
                
            case BARREL:
                // 油桶
                g.setColor(Constants.COLOR_BARREL);
                g.fillOval(x + 4, y + 2, size - 8, size - 4);
                g.setColor(Color.ORANGE);
                g.drawOval(x + 4, y + 2, size - 8, size - 4);
                // 危险标志
                g.setColor(Color.YELLOW);
                g.fillRect(x + size/2 - 3, y + size/2 - 5, 6, 10);
                g.setColor(Color.BLACK);
                g.drawString("!", x + size/2 - 2, y + size/2 + 4);
                break;
                
            case PORTAL_A:
            case PORTAL_B:
                // 传送门
                g.setColor(Constants.COLOR_PORTAL);
                float alpha = (float)(0.5 + 0.3 * Math.sin(System.currentTimeMillis() / 200.0));
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.fillOval(x + 2, y + 2, size - 4, size - 4);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g.setColor(Color.WHITE);
                g.drawOval(x + 5, y + 5, size - 10, size - 10);
                break;
        }
    }
}
