package com.tankwar.world;

import com.tankwar.util.Constants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 游戏地图
 */
public class GameMap {
    private Tile[][] tiles;
    private int rows;
    private int cols;
    private Point portalA;
    private Point portalB;
    private Point basePosition;
    private List<Point> playerSpawns;
    private List<Point> enemySpawns;
    
    public GameMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new Tile[rows][cols];
        this.playerSpawns = new ArrayList<>();
        this.enemySpawns = new ArrayList<>();
        initEmpty();
    }
    
    private void initEmpty() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c] = new Tile(TileType.EMPTY, r, c);
            }
        }
    }
    
    public void loadLevel(String[] levelData) {
        initEmpty();
        portalA = null;
        portalB = null;
        basePosition = null;
        playerSpawns.clear();
        enemySpawns.clear();
        
        for (int r = 0; r < Math.min(levelData.length, rows); r++) {
            String row = levelData[r];
            for (int c = 0; c < Math.min(row.length(), cols); c++) {
                char ch = row.charAt(c);
                TileType type = charToTile(ch);
                tiles[r][c] = new Tile(type, r, c);
                
                // 记录特殊位置
                if (type == TileType.BASE) {
                    basePosition = new Point(c * Constants.TILE_SIZE + Constants.TILE_SIZE/2,
                                            r * Constants.TILE_SIZE + Constants.TILE_SIZE/2);
                } else if (type == TileType.PORTAL_A) {
                    portalA = new Point(c, r);
                } else if (type == TileType.PORTAL_B) {
                    portalB = new Point(c, r);
                }
                
                // P: 玩家出生点, E: 敌人出生点
                if (ch == 'P') {
                    playerSpawns.add(new Point(c * Constants.TILE_SIZE + Constants.TILE_SIZE/2,
                                              r * Constants.TILE_SIZE + Constants.TILE_SIZE/2));
                } else if (ch == 'E') {
                    enemySpawns.add(new Point(c * Constants.TILE_SIZE + Constants.TILE_SIZE/2,
                                             r * Constants.TILE_SIZE + Constants.TILE_SIZE/2));
                }
            }
        }
        
        // 默认出生点
        if (playerSpawns.isEmpty()) {
            playerSpawns.add(new Point(4 * Constants.TILE_SIZE, (rows - 2) * Constants.TILE_SIZE));
            playerSpawns.add(new Point(12 * Constants.TILE_SIZE, (rows - 2) * Constants.TILE_SIZE));
        }
        if (enemySpawns.isEmpty()) {
            enemySpawns.add(new Point(0 * Constants.TILE_SIZE + Constants.TILE_SIZE/2, Constants.TILE_SIZE/2));
            enemySpawns.add(new Point(12 * Constants.TILE_SIZE + Constants.TILE_SIZE/2, Constants.TILE_SIZE/2));
            enemySpawns.add(new Point((cols-1) * Constants.TILE_SIZE + Constants.TILE_SIZE/2, Constants.TILE_SIZE/2));
        }
    }
    
    private TileType charToTile(char ch) {
        switch (ch) {
            case '#': return TileType.BRICK;
            case 'S': return TileType.STEEL;
            case '~': return TileType.WATER;
            case '*': return TileType.GRASS;
            case 'B': return TileType.BASE;
            case 'O': return TileType.BARREL;
            case 'a': return TileType.PORTAL_A;
            case 'b': return TileType.PORTAL_B;
            default: return TileType.EMPTY;
        }
    }
    
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return null;
        }
        return tiles[row][col];
    }
    
    public Tile getTileAtPixel(double x, double y) {
        int col = (int)(x / Constants.TILE_SIZE);
        int row = (int)(y / Constants.TILE_SIZE);
        return getTile(row, col);
    }
    
    public void setTileType(int row, int col, TileType type) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            tiles[row][col].setType(type);
        }
    }
    
    public boolean isBlocked(double x, double y) {
        Tile tile = getTileAtPixel(x, y);
        return tile != null && tile.blocksMovement();
    }
    
    public boolean isInBounds(double x, double y) {
        return x >= 0 && x < cols * Constants.TILE_SIZE && 
               y >= 0 && y < rows * Constants.TILE_SIZE;
    }
    
    /**
     * 检查矩形区域是否有阻挡
     */
    public boolean isAreaBlocked(Rectangle rect) {
        int startCol = Math.max(0, rect.x / Constants.TILE_SIZE);
        int endCol = Math.min(cols - 1, (rect.x + rect.width) / Constants.TILE_SIZE);
        int startRow = Math.max(0, rect.y / Constants.TILE_SIZE);
        int endRow = Math.min(rows - 1, (rect.y + rect.height) / Constants.TILE_SIZE);
        
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (tiles[r][c].blocksMovement()) {
                    if (tiles[r][c].getBounds().intersects(rect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 获取与矩形碰撞的地图块列表
     */
    public List<Tile> getCollidingTiles(Rectangle rect) {
        List<Tile> result = new ArrayList<>();
        int startCol = Math.max(0, rect.x / Constants.TILE_SIZE);
        int endCol = Math.min(cols - 1, (rect.x + rect.width) / Constants.TILE_SIZE);
        int startRow = Math.max(0, rect.y / Constants.TILE_SIZE);
        int endRow = Math.min(rows - 1, (rect.y + rect.height) / Constants.TILE_SIZE);
        
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (tiles[r][c].getBounds().intersects(rect)) {
                    result.add(tiles[r][c]);
                }
            }
        }
        return result;
    }
    
    public Point getPortalA() { return portalA; }
    public Point getPortalB() { return portalB; }
    public Point getBasePosition() { return basePosition; }
    public List<Point> getPlayerSpawns() { return playerSpawns; }
    public List<Point> getEnemySpawns() { return enemySpawns; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    
    /**
     * 传送门传送
     */
    public Point teleport(int row, int col) {
        if (portalA != null && portalA.x == col && portalA.y == row && portalB != null) {
            return new Point(portalB.x * Constants.TILE_SIZE + Constants.TILE_SIZE/2,
                           portalB.y * Constants.TILE_SIZE + Constants.TILE_SIZE/2);
        }
        if (portalB != null && portalB.x == col && portalB.y == row && portalA != null) {
            return new Point(portalA.x * Constants.TILE_SIZE + Constants.TILE_SIZE/2,
                           portalA.y * Constants.TILE_SIZE + Constants.TILE_SIZE/2);
        }
        return null;
    }
    
    public void render(Graphics2D g) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                tiles[r][c].render(g);
            }
        }
    }
    
    /**
     * 渲染草丛（在坦克之上）
     */
    public void renderGrass(Graphics2D g) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (tiles[r][c].getType() == TileType.GRASS) {
                    tiles[r][c].render(g);
                }
            }
        }
    }
}
