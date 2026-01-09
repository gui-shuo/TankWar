package com.tankwar.system;

import com.tankwar.entity.*;
import com.tankwar.world.GameMap;
import com.tankwar.world.Tile;
import com.tankwar.world.TileType;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 碰撞检测系统
 */
public class CollisionSystem {
    private GameMap map;
    
    public CollisionSystem(GameMap map) {
        this.map = map;
    }
    
    public void setMap(GameMap map) {
        this.map = map;
    }
    
    /**
     * 检测坦克与地图的碰撞
     * @return 是否发生碰撞
     */
    public boolean checkTankMapCollision(Tank tank, Rectangle nextBounds) {
        return map.isAreaBlocked(nextBounds);
    }
    
    /**
     * 检测子弹与地图的碰撞
     * @return 碰撞的地图块列表
     */
    public List<Tile> checkBulletMapCollision(Bullet bullet) {
        List<Tile> result = new ArrayList<>();
        List<Tile> collidingTiles = map.getCollidingTiles(bullet.getBounds());
        
        for (Tile tile : collidingTiles) {
            if (tile.getType() != TileType.EMPTY && 
                tile.getType() != TileType.GRASS) {
                result.add(tile);
            }
        }
        return result;
    }
    
    /**
     * 检测子弹与坦克的碰撞
     */
    public Tank checkBulletTankCollision(Bullet bullet, List<? extends Tank> tanks) {
        for (Tank tank : tanks) {
            if (tank != null && tank.isAlive() && tank != bullet.getOwner()) {
                if (bullet.collidesWith(tank)) {
                    return tank;
                }
            }
        }
        return null;
    }
    
    /**
     * 检测坦克与坦克的碰撞
     */
    public boolean checkTankTankCollision(Tank tank, Rectangle nextBounds, List<? extends Tank> otherTanks) {
        for (Tank other : otherTanks) {
            if (other != null && other != tank && other.isAlive()) {
                if (nextBounds.intersects(other.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 检测玩家拾取道具
     */
    public PowerUp checkPowerUpCollision(PlayerTank player, List<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp != null && powerUp.isAlive() && player.collidesWith(powerUp)) {
                return powerUp;
            }
        }
        return null;
    }
    
    /**
     * 检测地雷触发
     */
    public Mine checkMineCollision(Entity entity, List<Mine> mines) {
        for (Mine mine : mines) {
            if (mine != null && mine.isAlive() && mine.checkTrigger(entity)) {
                return mine;
            }
        }
        return null;
    }
    
    /**
     * 检测传送门
     */
    public Point checkPortalCollision(Tank tank) {
        Tile tile = map.getTileAtPixel(tank.getX(), tank.getY());
        if (tile != null && (tile.getType() == TileType.PORTAL_A || 
                            tile.getType() == TileType.PORTAL_B)) {
            return map.teleport(tile.getRow(), tile.getCol());
        }
        return null;
    }
    
    /**
     * 范围内的敌人（用于电磁脉冲）
     */
    public List<EnemyTank> getEnemiesInRange(double cx, double cy, double radius, List<EnemyTank> enemies) {
        List<EnemyTank> result = new ArrayList<>();
        for (EnemyTank enemy : enemies) {
            if (enemy != null && enemy.isAlive()) {
                double dist = Math.sqrt(Math.pow(enemy.getX() - cx, 2) + Math.pow(enemy.getY() - cy, 2));
                if (dist <= radius) {
                    result.add(enemy);
                }
            }
        }
        return result;
    }
    
    /**
     * 检测是否在草丛中
     */
    public boolean isInGrass(Entity entity) {
        Tile tile = map.getTileAtPixel(entity.getX(), entity.getY());
        return tile != null && tile.getType() == TileType.GRASS;
    }
    
    /**
     * 处理油桶爆炸
     * @return 受影响的实体列表
     */
    public List<Entity> handleBarrelExplosion(Tile barrel, List<? extends Entity> entities) {
        List<Entity> affected = new ArrayList<>();
        double explosionRadius = 60;
        double bx = barrel.getX() + 15;
        double by = barrel.getY() + 15;
        
        for (Entity e : entities) {
            if (e != null && e.isAlive()) {
                double dist = Math.sqrt(Math.pow(e.getX() - bx, 2) + Math.pow(e.getY() - by, 2));
                if (dist <= explosionRadius) {
                    affected.add(e);
                }
            }
        }
        return affected;
    }
}
