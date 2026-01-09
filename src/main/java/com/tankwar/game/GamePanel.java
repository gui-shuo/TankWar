package com.tankwar.game;

import com.tankwar.entity.*;
import com.tankwar.input.InputHandler;
import com.tankwar.system.*;
import com.tankwar.ui.HUD;
import com.tankwar.ui.MenuPanel;
import com.tankwar.util.Constants;
import com.tankwar.util.Direction;
import com.tankwar.world.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 游戏主面板 - 负责游戏逻辑和渲染
 */
public class GamePanel extends JPanel {
    // 游戏状态
    private GameState gameState = GameState.MENU;
    private GameMode gameMode = GameMode.CLASSIC;
    
    // 输入处理
    private InputHandler input;
    
    // 游戏系统
    private GameMap gameMap;
    private LevelManager levelManager;
    private CollisionSystem collisionSystem;
    private WeatherSystem weatherSystem;
    private ChipSystem chipSystem;
    
    // UI
    private MenuPanel menuPanel;
    private HUD hud;
    private com.tankwar.ui.UIManager uiManager;
    
    // 实体列表
    private PlayerTank player1;
    private PlayerTank player2;  // PvP模式用
    private List<EnemyTank> enemies;
    private List<Bullet> bullets;
    private List<Explosion> explosions;
    private List<PowerUp> powerUps;
    private List<Turret> turrets;
    private List<Mine> mines;
    private BossTank boss;
    private SupplyTruck supplyTruck;  // 护送模式用
    
    // 游戏数据
    private int enemiesRemaining;
    private int enemiesKilledThisLevel;
    private long lastEnemySpawnTime;
    private long enemySpawnInterval = 3000;
    private int maxEnemiesOnScreen = 6;
    
    // UI状态
    private int pauseMenuIndex = 0;
    private int shopMenuIndex = 0;
    private int chipSelectIndex = 0;
    
    // 特效追踪
    private long empEffectStart = 0;
    private double empEffectX, empEffectY;
    private long phaseEffectStart = 0;
    private double phaseFromX, phaseFromY, phaseToX, phaseToY;
    
    // 双缓冲
    private BufferedImage buffer;
    
    // 沙尘暴伤害计时
    private long lastSandstormDamage = 0;
    
    // 商店限制
    private int turretsPurchased = 0;
    private static final int MAX_TURRETS = 5;  // 总购买上限
    private static final int MAX_TURRETS_ON_MAP = 3;  // 场上存活上限
    private int wallLevel = 1;  // 围墙等级 1-5
    private static final int MAX_WALL_LEVEL = 5;
    
    public GamePanel(InputHandler input) {
        this.input = input;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.COLOR_BG);
        setFocusable(true);
        setDoubleBuffered(true);
        
        // 禁用Tab键的焦点遍历功能，使其能被KeyListener捕获
        setFocusTraversalKeysEnabled(false);
        
        init();
    }
    
    private void init() {
        // 初始化系统
        gameMap = new GameMap(Constants.MAP_ROWS, Constants.MAP_COLS);
        levelManager = new LevelManager();
        collisionSystem = new CollisionSystem(gameMap);
        weatherSystem = new WeatherSystem();
        chipSystem = new ChipSystem();
        
        // 初始化UI
        menuPanel = new MenuPanel();
        hud = new HUD();
        uiManager = new com.tankwar.ui.UIManager();
        
        // 初始化实体列表
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        turrets = new ArrayList<>();
        mines = new ArrayList<>();
        
        // 创建缓冲
        buffer = new BufferedImage(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, 
                                   BufferedImage.TYPE_INT_ARGB);
    }
    
    /**
     * 游戏主循环更新
     */
    public void update() {
        switch (gameState) {
            case MENU:
                updateMenu();
                break;
            case PLAYING:
                updateGame();
                break;
            case PAUSED:
                updatePause();
                break;
            case CHIP_SELECT:
                updateChipSelect();
                break;
            case SHOP:
                updateShop();
                break;
            case GAME_OVER:
            case VICTORY:
                updateGameOver();
                break;
            case LEVEL_COMPLETE:
                // 自动进入芯片选择
                chipSystem.generateChoices();
                gameState = GameState.CHIP_SELECT;
                break;
        }
        
        input.update();
    }
    
    private void updateMenu() {
        menuPanel.update();
        
        if (input.isKeyJustPressed(KeyEvent.VK_UP) || input.isKeyJustPressed(KeyEvent.VK_W)) {
            menuPanel.moveUp();
        }
        if (input.isKeyJustPressed(KeyEvent.VK_DOWN) || input.isKeyJustPressed(KeyEvent.VK_S)) {
            menuPanel.moveDown();
        }
        if (input.isKeyJustPressed(KeyEvent.VK_LEFT) || input.isKeyJustPressed(KeyEvent.VK_A)) {
            menuPanel.moveLeft();
        }
        if (input.isKeyJustPressed(KeyEvent.VK_RIGHT) || input.isKeyJustPressed(KeyEvent.VK_D)) {
            menuPanel.moveRight();
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            if (!menuPanel.back()) {
                // 已经在主菜单，不做处理
            }
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ENTER) || input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            int action = menuPanel.confirm();
            if (action == 0) {
                // 开始游戏
                gameMode = menuPanel.getSelectedMode();
                startNewGame();
            } else if (action == 3) {
                // 退出
                System.exit(0);
            }
        }
    }
    
    private void updateGame() {
        // 暂停
        if (input.isPausePressed()) {
            gameState = GameState.PAUSED;
            pauseMenuIndex = 0;
            return;
        }
        
        // 商店
        if (input.isKeyJustPressed(KeyEvent.VK_B)) {
            gameState = GameState.SHOP;
            shopMenuIndex = 0;
            return;
        }
        
        // 更新天气
        weatherSystem.update();
        
        // 沙尘暴伤害
        if (weatherSystem.getCurrentWeather() == WeatherSystem.WeatherType.SANDSTORM) {
            long now = System.currentTimeMillis();
            if (now - lastSandstormDamage > 1000) {  // 每秒1点伤害
                if (player1 != null && player1.isAlive()) {
                    boolean died = player1.takeDamage(1);
                    if (died) {
                        handlePlayerDeath();
                    }
                }
                lastSandstormDamage = now;
            }
        }
        
        // 更新玩家
        updatePlayer();
        
        // 更新敌人
        updateEnemies();
        
        // 更新Boss
        if (boss != null && boss.isAlive()) {
            updateBoss();
        }
        
        // 更新物资车（护送模式）
        if (supplyTruck != null && supplyTruck.isAlive()) {
            supplyTruck.update(0);
            if (supplyTruck.hasReachedEnd()) {
                // 护送成功
                handleLevelComplete();
            }
        }
        
        // 更新子弹
        updateBullets();
        
        // 更新炮塔
        updateTurrets();
        
        // 更新地雷
        updateMines();
        
        // 更新爆炸
        updateExplosions();
        
        // 更新道具
        updatePowerUps();
        
        // 生成敌人
        spawnEnemies();
        
        // 检查胜负条件
        checkWinLoseConditions();
    }
    
    // 冰面滑动相关
    private Direction lastMoveDirection = null;
    private double slideVelocityX = 0;
    private double slideVelocityY = 0;
    private static final double ICE_FRICTION = 0.92;  // 冰面摩擦力（越接近1滑动越久）
    private static final double ICE_SLIDE_SPEED = 2.5; // 冰面滑动速度
    
    private void updatePlayer() {
        if (player1 == null || !player1.isAlive()) return;
        
        // 检查是否在冰面上
        Tile currentTile = gameMap.getTileAtPixel(player1.getX(), player1.getY());
        boolean onIce = currentTile != null && currentTile.getType() == TileType.ICE;
        
        // 检查雨天效果（雨天也会滑动）
        boolean isRaining = weatherSystem.getCurrentWeather() == WeatherSystem.WeatherType.RAIN;
        boolean shouldSlide = onIce || isRaining;
        
        // 移动
        int[] dir = input.getPlayer1Direction();
        if ((dir[0] != 0 || dir[1] != 0) && !player1.isStunned()) {
            Direction newDir = null;
            if (dir[1] < 0) newDir = Direction.UP;
            else if (dir[1] > 0) newDir = Direction.DOWN;
            else if (dir[0] < 0) newDir = Direction.LEFT;
            else if (dir[0] > 0) newDir = Direction.RIGHT;
            
            if (newDir != null) {
                player1.setDirection(newDir);
                lastMoveDirection = newDir;
                
                // 在冰面上或雨天增加滑动速度
                if (shouldSlide) {
                    double slideMultiplier = isRaining ? 0.2 : 0.3;  // 雨天滑动稍微弱一些
                    slideVelocityX += newDir.dx * ICE_SLIDE_SPEED * slideMultiplier;
                    slideVelocityY += newDir.dy * ICE_SLIDE_SPEED * slideMultiplier;
                }
                
                // 检查碰撞后移动
                Rectangle nextBounds = player1.getNextBounds(newDir, player1.getSpeed());
                boolean blocked = collisionSystem.checkTankMapCollision(player1, nextBounds);
                blocked = blocked || collisionSystem.checkTankTankCollision(player1, nextBounds, enemies);
                
                if (!blocked) {
                    player1.move();
                    player1.clampToMap();
                    
                    // 移动留雷
                    if (player1.canDropMine()) {
                        mines.add(new Mine(player1.getX(), player1.getY(), player1));
                        player1.markMineDropped();
                    }
                }
            }
        }
        
        // 冰面/雨天滑动效果（即使没有按键也继续滑动）
        if (shouldSlide && (Math.abs(slideVelocityX) > 0.1 || Math.abs(slideVelocityY) > 0.1)) {
            // 计算滑动后的位置
            double newX = player1.getX() + slideVelocityX;
            double newY = player1.getY() + slideVelocityY;
            
            Rectangle slideBounds = new Rectangle(
                (int)(newX - player1.getWidth()/2),
                (int)(newY - player1.getHeight()/2),
                player1.getWidth(), player1.getHeight()
            );
            
            boolean slideBlocked = collisionSystem.checkTankMapCollision(player1, slideBounds);
            slideBlocked = slideBlocked || collisionSystem.checkTankTankCollision(player1, slideBounds, enemies);
            
            if (!slideBlocked) {
                player1.setX(newX);
                player1.setY(newY);
                player1.clampToMap();
            } else {
                // 撞墙停止滑动
                slideVelocityX = 0;
                slideVelocityY = 0;
            }
            
            // 应用摩擦力减速
            double friction = isRaining ? 0.88 : ICE_FRICTION;  // 雨天摩擦力稍大
            slideVelocityX *= friction;
            slideVelocityY *= friction;
        } else if (!shouldSlide) {
            // 离开滑动区域快速停止
            slideVelocityX *= 0.5;
            slideVelocityY *= 0.5;
            if (Math.abs(slideVelocityX) < 0.1) slideVelocityX = 0;
            if (Math.abs(slideVelocityY) < 0.1) slideVelocityY = 0;
        }
        
        // 检查传送门
        if (player1.canTeleport()) {
            Point teleportDest = collisionSystem.checkPortalCollision(player1);
            if (teleportDest != null) {
                player1.setPosition(teleportDest.x, teleportDest.y);
                player1.markTeleported();
            }
        }
        
        // 射击
        if (input.isPlayer1Shooting()) {
            List<Bullet> newBullets = player1.shoot(enemies);
            if (newBullets != null) {
                bullets.addAll(newBullets);
            }
        }
        
        // 技能1：相位移动
        if (input.isPlayer1Skill1()) {
            if (player1.usePhaseShift()) {
                double targetX = player1.getPhaseTargetX();
                double targetY = player1.getPhaseTargetY();
                
                // 检查目标位置是否有障碍物，逐步回退找到安全位置
                double safeX = targetX;
                double safeY = targetY;
                double startX = player1.getX();
                double startY = player1.getY();
                
                // 从目标位置向起点回退检查
                for (int i = 10; i >= 0; i--) {
                    double ratio = i / 10.0;
                    double checkX = startX + (targetX - startX) * ratio;
                    double checkY = startY + (targetY - startY) * ratio;
                    
                    Rectangle checkBounds = new Rectangle(
                        (int)(checkX - player1.getWidth()/2),
                        (int)(checkY - player1.getHeight()/2),
                        player1.getWidth(), player1.getHeight()
                    );
                    
                    if (!gameMap.isAreaBlocked(checkBounds)) {
                        safeX = checkX;
                        safeY = checkY;
                        break;
                    }
                }
                
                phaseFromX = player1.getX();
                phaseFromY = player1.getY();
                player1.confirmPhaseShift(safeX, safeY);
                phaseToX = player1.getX();
                phaseToY = player1.getY();
                phaseEffectStart = System.currentTimeMillis();
            }
        }
        
        // 技能2：电磁脉冲
        if (input.isPlayer1Skill2()) {
            if (player1.useEMP()) {
                empEffectX = player1.getX();
                empEffectY = player1.getY();
                empEffectStart = System.currentTimeMillis();
                
                // 眩晕范围内敌人
                List<EnemyTank> affected = collisionSystem.getEnemiesInRange(
                    player1.getX(), player1.getY(), Constants.EMP_RADIUS, enemies);
                for (EnemyTank enemy : affected) {
                    enemy.stun(Constants.EMP_STUN_DURATION);
                }
                if (boss != null && boss.isAlive() && 
                    boss.distanceTo(player1) <= Constants.EMP_RADIUS) {
                    boss.stun(Constants.EMP_STUN_DURATION / 2);  // Boss眩晕时间减半
                }
            }
        }
        
        // 技能3：K键主动技能（护盾/时停）
        if (input.isPlayer1Skill3()) {
            if (player1.useActiveSkill()) {
                // 激活护盾，短暂无敌
                player1.setInvincible(3000);  // 3秒无敌护盾
            }
        }
        
        // 切换武器（Tab键）
        if (input.isSwitchWeapon()) {
            player1.switchWeapon();
        }
        
        player1.update(0);
        
        // 拾取道具
        PowerUp pickedUp = collisionSystem.checkPowerUpCollision(player1, powerUps);
        if (pickedUp != null) {
            pickedUp.apply(player1);
        }
    }
    
    private void updateEnemies() {
        for (Iterator<EnemyTank> it = enemies.iterator(); it.hasNext(); ) {
            EnemyTank enemy = it.next();
            
            if (!enemy.isAlive()) {
                it.remove();
                continue;
            }
            
            enemy.setTarget(player1);
            enemy.setBaseTarget(gameMap.getBasePosition());
            enemy.update(0);
            
            if (!enemy.isStunned()) {
                // 移动
                Rectangle nextBounds = enemy.getNextBounds();
                boolean blocked = collisionSystem.checkTankMapCollision(enemy, nextBounds);
                blocked = blocked || collisionSystem.checkTankTankCollision(enemy, nextBounds, enemies);
                if (player1 != null) {
                    List<Tank> players = new ArrayList<>();
                    players.add(player1);
                    blocked = blocked || collisionSystem.checkTankTankCollision(enemy, nextBounds, players);
                }
                
                if (!blocked) {
                    enemy.move();
                    enemy.clampToMap();
                } else {
                    enemy.onBlocked();
                }
                
                // 射击
                if (enemy.shouldShoot(player1) && Math.random() < 0.03) {
                    Bullet bullet = enemy.tryShoot();
                    if (bullet != null) {
                        bullets.add(bullet);
                    }
                }
            }
        }
    }
    
    private void updateBoss() {
        boss.update(0);
        
        if (boss.isStunned()) return;
        
        // Boss射击
        Bullet bullet = boss.shoot();
        if (bullet != null) {
            bullets.add(bullet);
        }
        
        // 弹幕攻击（阶段2+）
        if (boss.getPhase() >= 2 && Math.random() < 0.02) {
            List<Bullet> barrage = boss.shootBarrage();
            bullets.addAll(barrage);
        }
    }
    
    private void updateBullets() {
        for (Iterator<Bullet> it = bullets.iterator(); it.hasNext(); ) {
            Bullet bullet = it.next();
            bullet.update(0);
            
            if (!bullet.isAlive()) {
                it.remove();
                continue;
            }
            
            // 判断是否为友军子弹（不伤害己方基地）
            Entity bulletOwner = bullet.getOwner();
            boolean isFriendlyBullet = (bulletOwner instanceof PlayerTank) || (bulletOwner instanceof Turret);
            
            // 子弹与地图碰撞
            List<Tile> hitTiles = collisionSystem.checkBulletMapCollision(bullet);
            for (Tile tile : hitTiles) {
                if (tile.getType() == TileType.WATER || tile.getType() == TileType.GRASS) {
                    continue;
                }
                
                // 油桶爆炸
                if (tile.getType() == TileType.BARREL) {
                    handleBarrelExplosion(tile);
                    gameMap.setTileType(tile.getRow(), tile.getCol(), TileType.EMPTY);
                    if (!bullet.canPierce()) {
                        bullet.destroy();
                    } else {
                        bullet.pierce();
                    }
                    continue;
                }
                
                // 友军子弹不伤害基地
                if (tile.getType() == TileType.BASE && isFriendlyBullet) {
                    continue;  // 跳过，不伤害己方基地
                }
                
                // 可破坏物
                if (tile.isDestructible()) {
                    if (tile.takeDamage(1)) {
                        if (tile.getType() == TileType.BASE) {
                            // 基地被摧毁
                            handleBaseDestroyed();
                        }
                        gameMap.setTileType(tile.getRow(), tile.getCol(), TileType.EMPTY);
                    }
                }
                
                if (!bullet.canPierce()) {
                    bullet.destroy();
                } else {
                    bullet.pierce();
                }
                break;
            }
            
            if (!bullet.isAlive()) {
                it.remove();
                continue;
            }
            
            // 子弹与玩家碰撞（排除友军火力 - 使用已定义的变量）
            if (player1 != null && player1.isAlive() && !isFriendlyBullet) {
                if (bullet.collidesWith(player1)) {
                    boolean died = player1.takeDamage(bullet.getDamage());
                    explosions.add(new Explosion(bullet.getX(), bullet.getY(), false));
                    if (died) {
                        handlePlayerDeath();
                    }
                    bullet.destroy();
                    it.remove();
                    continue;
                }
            }
            
            // 子弹与敌人碰撞
            if (bullet.getOwner() instanceof PlayerTank || bullet.getOwner() instanceof Turret) {
                // 检查敌人
                for (EnemyTank enemy : enemies) {
                    if (enemy.isAlive() && bullet.collidesWith(enemy)) {
                        // 刚毅词缀检查
                        boolean fromBehind = enemy.isHitFromBehind(bullet.getDirection());
                        if (enemy.getAffix() == EnemyAffix.TOUGH && !fromBehind) {
                            // 正面免疫
                            explosions.add(new Explosion(bullet.getX(), bullet.getY(), false));
                        } else {
                            boolean died = enemy.takeDamage(bullet.getDamage());
                            explosions.add(new Explosion(bullet.getX(), bullet.getY(), false));
                            
                            if (died) {
                                handleEnemyDeath(enemy);
                            } else if (enemy.getAffix() == EnemyAffix.VAMPIRE) {
                                // 吸血效果（这里简化，实际应该是敌人攻击命中时回血）
                            }
                        }
                        
                        if (!bullet.canPierce()) {
                            bullet.destroy();
                        } else {
                            bullet.pierce();
                        }
                        break;
                    }
                }
                
                // 检查Boss
                if (boss != null && boss.isAlive() && bullet.collidesWith(boss)) {
                    boolean died = boss.takeDamage(bullet.getDamage());
                    explosions.add(new Explosion(bullet.getX(), bullet.getY(), true));
                    
                    if (died) {
                        handleBossDeath();
                    }
                    
                    if (!bullet.canPierce()) {
                        bullet.destroy();
                    } else {
                        bullet.pierce();
                    }
                }
            }
            
            // 子弹与物资车碰撞（护送模式）
            if (supplyTruck != null && supplyTruck.isAlive() && 
                !(bullet.getOwner() instanceof PlayerTank) && 
                bullet.collidesWith(supplyTruck)) {
                supplyTruck.takeDamage(bullet.getDamage());
                explosions.add(new Explosion(bullet.getX(), bullet.getY(), false));
                bullet.destroy();
            }
            
            // 敌方子弹与哨戒炮碰撞
            if (!isFriendlyBullet) {
                for (Turret turret : turrets) {
                    if (turret.isAlive() && bullet.collidesWith(turret)) {
                        boolean destroyed = turret.takeDamage(bullet.getDamage());
                        explosions.add(new Explosion(bullet.getX(), bullet.getY(), false));
                        if (destroyed) {
                            explosions.add(new Explosion(turret.getX(), turret.getY(), true));
                        }
                        bullet.destroy();
                        break;
                    }
                }
            }
            
            if (!bullet.isAlive()) {
                it.remove();
            }
        }
    }
    
    private void updateTurrets() {
        for (Iterator<Turret> it = turrets.iterator(); it.hasNext(); ) {
            Turret turret = it.next();
            if (!turret.isAlive()) {
                it.remove();
                continue;
            }
            
            Bullet bullet = turret.updateAndShoot(enemies);
            if (bullet != null) {
                bullets.add(bullet);
            }
        }
    }
    
    private void updateMines() {
        for (Iterator<Mine> it = mines.iterator(); it.hasNext(); ) {
            Mine mine = it.next();
            mine.update(0);
            
            if (!mine.isAlive()) {
                it.remove();
                continue;
            }
            
            if (!mine.isArmed()) continue;
            
            // 检查敌人触发
            for (EnemyTank enemy : enemies) {
                if (enemy.isAlive() && mine.checkTrigger(enemy)) {
                    enemy.takeDamage(mine.getDamage());
                    explosions.add(new Explosion(mine.getX(), mine.getY(), true));
                    mine.destroy();
                    
                    if (!enemy.isAlive()) {
                        handleEnemyDeath(enemy);
                    }
                    break;
                }
            }
            
            // 检查Boss触发
            if (boss != null && boss.isAlive() && mine.checkTrigger(boss)) {
                boss.takeDamage(mine.getDamage());
                explosions.add(new Explosion(mine.getX(), mine.getY(), true));
                mine.destroy();
            }
            
            if (!mine.isAlive()) {
                it.remove();
            }
        }
    }
    
    private void updateExplosions() {
        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion exp = it.next();
            exp.update(0);
            if (!exp.isAlive()) {
                it.remove();
            }
        }
    }
    
    private void updatePowerUps() {
        for (Iterator<PowerUp> it = powerUps.iterator(); it.hasNext(); ) {
            PowerUp powerUp = it.next();
            powerUp.update(0);
            if (!powerUp.isAlive()) {
                it.remove();
            }
        }
    }
    
    private void spawnEnemies() {
        if (gameMode == GameMode.PVP) return;
        if (boss != null && boss.isAlive()) return;  // Boss战不刷敌人
        
        if (enemiesRemaining <= 0) return;
        if (enemies.size() >= maxEnemiesOnScreen) return;
        
        long now = System.currentTimeMillis();
        if (now - lastEnemySpawnTime < enemySpawnInterval) return;
        
        List<Point> spawns = gameMap.getEnemySpawns();
        if (spawns.isEmpty()) return;
        
        Point spawn = spawns.get((int)(Math.random() * spawns.size()));
        
        // 检查生成点是否被占用
        Rectangle spawnArea = new Rectangle(spawn.x - 15, spawn.y - 15, 30, 30);
        boolean occupied = false;
        for (EnemyTank e : enemies) {
            if (spawnArea.intersects(e.getBounds())) {
                occupied = true;
                break;
            }
        }
        
        if (!occupied) {
            boolean elite = Math.random() * 100 < levelManager.getEliteChance();
            EnemyTank enemy = new EnemyTank(spawn.x, spawn.y, elite);
            enemies.add(enemy);
            enemiesRemaining--;
            lastEnemySpawnTime = now;
        }
    }
    
    private void handleBarrelExplosion(Tile barrel) {
        double bx = barrel.getX() + Constants.TILE_SIZE / 2.0;
        double by = barrel.getY() + Constants.TILE_SIZE / 2.0;
        
        explosions.add(new Explosion(bx, by, true));
        
        // 伤害范围内实体
        List<Entity> allEntities = new ArrayList<>();
        if (player1 != null && player1.isAlive()) allEntities.add(player1);
        allEntities.addAll(enemies);
        if (boss != null && boss.isAlive()) allEntities.add(boss);
        
        List<Entity> affected = collisionSystem.handleBarrelExplosion(barrel, allEntities);
        for (Entity e : affected) {
            if (e instanceof Tank) {
                ((Tank) e).takeDamage(40);
            }
        }
    }
    
    private void handleEnemyDeath(EnemyTank enemy) {
        explosions.add(new Explosion(enemy.getX(), enemy.getY(), true));
        player1.onEnemyKill(enemy.getScoreValue());
        enemiesKilledThisLevel++;
        
        // 自爆词缀
        if (enemy.getAffix() == EnemyAffix.EXPLOSIVE) {
            explosions.add(new Explosion(enemy.getX(), enemy.getY(), true));
            // 范围伤害
            if (player1 != null && player1.isAlive() && 
                player1.distanceTo(enemy) < 60) {
                boolean died = player1.takeDamage(30);
                if (died) {
                    handlePlayerDeath();
                }
            }
        }
        
        // 掉落道具
        if (Math.random() < 0.3) {
            powerUps.add(new PowerUp(enemy.getX(), enemy.getY(), PowerUpType.random()));
        }
    }
    
    private void handleBossDeath() {
        explosions.add(new Explosion(boss.getX(), boss.getY(), true));
        player1.addScore(boss.getScoreValue());
        boss = null;
        
        // 多个爆炸效果
        for (int i = 0; i < 5; i++) {
            double ex = boss != null ? boss.getX() : player1.getX();
            double ey = boss != null ? boss.getY() : player1.getY();
            explosions.add(new Explosion(
                ex + (Math.random() - 0.5) * 60,
                ey + (Math.random() - 0.5) * 60,
                true
            ));
        }
        
        // Boss模式或最后一关
        if (gameMode == GameMode.BOSS_RUSH || levelManager.isBossLevel()) {
            gameState = GameState.VICTORY;
        } else {
            handleLevelComplete();
        }
    }
    
    private void handlePlayerDeath() {
        explosions.add(new Explosion(player1.getX(), player1.getY(), true));
        
        if (player1.getLives() > 0) {
            // 还有生命，重生
            List<Point> spawns = gameMap.getPlayerSpawns();
            Point spawn = spawns.isEmpty() ? new Point(400, 700) : spawns.get(0);
            player1.respawn(spawn.x, spawn.y);
        } else {
            // 生命用尽，游戏结束
            player1.setAlive(false);
            gameState = GameState.GAME_OVER;
        }
    }
    
    private void handleBaseDestroyed() {
        gameState = GameState.GAME_OVER;
    }
    
    private void handleLevelComplete() {
        if (levelManager.hasNextLevel()) {
            gameState = GameState.LEVEL_COMPLETE;
        } else {
            gameState = GameState.VICTORY;
        }
    }
    
    private void checkWinLoseConditions() {
        if (gameState != GameState.PLAYING) return;
        
        switch (gameMode) {
            case CLASSIC:
                // 所有敌人消灭
                if (enemiesRemaining <= 0 && enemies.isEmpty() && 
                    (boss == null || !boss.isAlive())) {
                    if (!levelManager.isBossLevel() || boss == null) {
                        handleLevelComplete();
                    }
                }
                break;
                
            case ESCORT:
                // 物资车到达或被摧毁
                if (supplyTruck != null && !supplyTruck.isAlive()) {
                    gameState = GameState.GAME_OVER;
                }
                break;
                
            case BOSS_RUSH:
                // Boss被消灭
                if (boss != null && !boss.isAlive()) {
                    handleLevelComplete();
                }
                break;
                
            case PVP:
                // 双人模式特殊处理
                break;
        }
    }
    
    private void updatePause() {
        if (input.isKeyJustPressed(KeyEvent.VK_UP) || input.isKeyJustPressed(KeyEvent.VK_W)) {
            pauseMenuIndex = (pauseMenuIndex - 1 + 3) % 3;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_DOWN) || input.isKeyJustPressed(KeyEvent.VK_S)) {
            pauseMenuIndex = (pauseMenuIndex + 1) % 3;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ENTER) || input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            switch (pauseMenuIndex) {
                case 0: // 继续
                    gameState = GameState.PLAYING;
                    break;
                case 1: // 重新开始
                    startNewGame();
                    break;
                case 2: // 返回主菜单
                    gameState = GameState.MENU;
                    break;
            }
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            gameState = GameState.PLAYING;
        }
    }
    
    private void updateChipSelect() {
        if (input.getNumberPressed() >= 1 && input.getNumberPressed() <= 3) {
            chipSystem.selectChip(input.getNumberPressed() - 1, player1);
            startNextLevel();
        }
        
        // 也可以用方向键选择
        if (input.isKeyJustPressed(KeyEvent.VK_LEFT)) {
            chipSelectIndex = (chipSelectIndex - 1 + 3) % 3;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_RIGHT)) {
            chipSelectIndex = (chipSelectIndex + 1) % 3;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ENTER)) {
            chipSystem.selectChip(chipSelectIndex, player1);
            startNextLevel();
        }
    }
    
    private void updateShop() {
        if (input.isKeyJustPressed(KeyEvent.VK_UP) || input.isKeyJustPressed(KeyEvent.VK_W)) {
            shopMenuIndex = (shopMenuIndex - 1 + 5) % 5;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_DOWN) || input.isKeyJustPressed(KeyEvent.VK_S)) {
            shopMenuIndex = (shopMenuIndex + 1) % 5;
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ENTER) || input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            handleShopPurchase(shopMenuIndex);
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ESCAPE) || input.isKeyJustPressed(KeyEvent.VK_B)) {
            gameState = GameState.PLAYING;
        }
    }
    
    private void handleShopPurchase(int index) {
        int score = player1.getScore();
        int[] prices = {Constants.PRICE_TURRET, Constants.PRICE_REPAIR_WALL, 
                       Constants.PRICE_UPGRADE_WALL, 200, 150};
        
        if (score < prices[index]) return;
        
        switch (index) {
            case 0:  // 哨戒炮（场上最多3个）
                // 检查场上存活的哨戒炮数量
                int aliveTurrets = 0;
                for (Turret t : turrets) {
                    if (t.isAlive()) aliveTurrets++;
                }
                if (aliveTurrets >= MAX_TURRETS_ON_MAP) {
                    return;  // 场上已有太多哨戒炮
                }
                // 在玩家附近放置
                turrets.add(new Turret(player1.getX() + 40, player1.getY(), player1));
                turretsPurchased++;
                player1.addScore(-prices[0]);
                break;
            case 1:  // 修复围墙（每次修复一定血量）
                if (repairWallsNearBase()) {
                    player1.addScore(-prices[1]);
                }
                break;
            case 2:  // 升级围墙（5个等级）
                if (wallLevel >= MAX_WALL_LEVEL) {
                    return;  // 已达最高等级
                }
                upgradeWallsNearBase();
                wallLevel++;
                player1.addScore(-prices[2]);
                break;
            case 3:  // 恢复生命
                player1.heal(50);
                player1.addScore(-prices[3]);
                break;
            case 4:  // 弹药
                player1.addAmmo(WeaponType.LASER, 3);
                player1.addAmmo(WeaponType.SHOTGUN, 5);
                player1.addAmmo(WeaponType.MISSILE, 2);
                player1.addScore(-prices[4]);
                break;
        }
    }
    
    /**
     * 修复围墙（每次修复一定血量，返回是否有修复）
     */
    private boolean repairWallsNearBase() {
        Point base = gameMap.getBasePosition();
        if (base == null) return false;
        
        int baseRow = base.y / Constants.TILE_SIZE;
        int baseCol = base.x / Constants.TILE_SIZE;
        boolean repaired = false;
        
        for (int r = baseRow - 2; r <= baseRow + 2; r++) {
            for (int c = baseCol - 2; c <= baseCol + 2; c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile != null) {
                    // 如果是空地，放置1级围墙（土墙）
                    if (tile.getType() == TileType.EMPTY) {
                        gameMap.setTileType(r, c, TileType.WALL_LV1);
                        repaired = true;
                    }
                    // 如果是可摧毁的围墙，恢复血量（每次+2）
                    else if (tile.getType().isWall() && tile.isDestructible()) {
                        int healed = tile.heal(2);
                        if (healed > 0) {
                            repaired = true;
                        }
                    }
                }
            }
        }
        return repaired;
    }
    
    /**
     * 升级围墙（升级到下一等级类型）
     */
    private void upgradeWallsNearBase() {
        Point base = gameMap.getBasePosition();
        if (base == null) return;
        
        int baseRow = base.y / Constants.TILE_SIZE;
        int baseCol = base.x / Constants.TILE_SIZE;
        
        for (int r = baseRow - 2; r <= baseRow + 2; r++) {
            for (int c = baseCol - 2; c <= baseCol + 2; c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile != null && tile.getType().isWall()) {
                    // 升级围墙到下一等级类型
                    tile.upgradeWall();
                }
            }
        }
    }
    
    private void updateGameOver() {
        if (input.isKeyJustPressed(KeyEvent.VK_ENTER) || input.isKeyJustPressed(KeyEvent.VK_SPACE)) {
            startNewGame();
        }
        if (input.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            gameState = GameState.MENU;
        }
    }
    
    private void startNewGame() {
        levelManager.setMode(gameMode);
        levelManager.reset();
        chipSystem.reset();
        
        // 重置商店限制
        turretsPurchased = 0;
        wallLevel = 1;
        
        startLevel();
    }
    
    private void startLevel() {
        // 清空实体
        enemies.clear();
        bullets.clear();
        explosions.clear();
        powerUps.clear();
        turrets.clear();
        mines.clear();
        boss = null;
        supplyTruck = null;
        
        // 加载地图
        gameMap.loadLevel(levelManager.getCurrentLevelData());
        collisionSystem.setMap(gameMap);
        
        // 创建玩家
        List<Point> playerSpawns = gameMap.getPlayerSpawns();
        Point spawn = playerSpawns.isEmpty() ? new Point(400, 700) : playerSpawns.get(0);
        
        if (player1 == null) {
            player1 = new PlayerTank(spawn.x, spawn.y, 1);
        } else {
            player1.setPosition(spawn.x, spawn.y);
            player1.setHp(player1.getMaxHp());
            player1.setDirection(Direction.UP);
        }
        
        if (levelManager.getCurrentLevel() == 1) {
            player1.fullReset(spawn.x, spawn.y);
        }
        
        // 设置敌人数量
        enemiesRemaining = levelManager.getEnemyCount();
        enemiesKilledThisLevel = 0;
        lastEnemySpawnTime = System.currentTimeMillis();
        
        // Boss关
        if (levelManager.isBossLevel() || gameMode == GameMode.BOSS_RUSH) {
            List<Point> enemySpawns = gameMap.getEnemySpawns();
            Point bossSpawn = enemySpawns.isEmpty() ? 
                new Point(Constants.GAME_WIDTH / 2, 100) : enemySpawns.get(0);
            boss = new BossTank(bossSpawn.x, bossSpawn.y);
            enemiesRemaining = 0;  // Boss战不刷普通敌人
        }
        
        // 护送模式
        if (gameMode == GameMode.ESCORT) {
            supplyTruck = new SupplyTruck(Constants.GAME_WIDTH / 2, 100);
            supplyTruck.generateDefaultPath();
        }
        
        // PvP模式
        if (gameMode == GameMode.PVP && playerSpawns.size() >= 2) {
            Point spawn2 = playerSpawns.get(1);
            player2 = new PlayerTank(spawn2.x, spawn2.y, 2);
        }
        
        // 重置天气
        weatherSystem.setWeather(WeatherSystem.WeatherType.CLEAR);
        
        gameState = GameState.PLAYING;
    }
    
    private void startNextLevel() {
        levelManager.nextLevel();
        startLevel();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 双缓冲绘制
        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 清空
        g2.setColor(Constants.COLOR_BG);
        g2.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        switch (gameState) {
            case MENU:
                menuPanel.render(g2);
                break;
                
            case PLAYING:
            case PAUSED:
            case SHOP:
            case CHIP_SELECT:
            case LEVEL_COMPLETE:
                renderGame(g2);
                
                if (gameState == GameState.PAUSED) {
                    uiManager.renderPause(g2, pauseMenuIndex);
                } else if (gameState == GameState.SHOP) {
                    // 计算场上存活的哨戒炮数量
                    int aliveTurrets = 0;
                    for (Turret t : turrets) {
                        if (t.isAlive()) aliveTurrets++;
                    }
                    uiManager.renderShop(g2, player1.getScore(), shopMenuIndex, 
                                        aliveTurrets, MAX_TURRETS_ON_MAP, wallLevel, MAX_WALL_LEVEL);
                } else if (gameState == GameState.CHIP_SELECT || gameState == GameState.LEVEL_COMPLETE) {
                    uiManager.renderLevelComplete(g2, levelManager.getCurrentLevel(), 
                                                  player1.getScore(), enemiesKilledThisLevel);
                    chipSystem.renderSelection(g2, chipSelectIndex);
                }
                break;
                
            case GAME_OVER:
            case VICTORY:
                renderGame(g2);
                uiManager.renderGameOver(g2, player1.getScore(), 
                                        levelManager.getCurrentLevel(), 
                                        gameState == GameState.VICTORY);
                break;
        }
        
        g2.dispose();
        g.drawImage(buffer, 0, 0, null);
    }
    
    private void renderGame(Graphics2D g) {
        // 游戏区域裁剪
        g.setClip(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
        
        // 渲染地图（不含草丛）
        gameMap.render(g);
        
        // 渲染地雷
        for (Mine mine : mines) {
            mine.render(g);
        }
        
        // 渲染炮塔
        for (Turret turret : turrets) {
            turret.render(g);
        }
        
        // 渲染道具
        for (PowerUp powerUp : powerUps) {
            powerUp.render(g);
        }
        
        // 渲染物资车
        if (supplyTruck != null && supplyTruck.isAlive()) {
            supplyTruck.render(g);
        }
        
        // 渲染敌人
        for (EnemyTank enemy : enemies) {
            enemy.render(g);
        }
        
        // 渲染Boss
        if (boss != null && boss.isAlive()) {
            boss.render(g);
        }
        
        // 渲染玩家
        if (player1 != null && player1.isAlive()) {
            player1.render(g);
        }
        if (player2 != null && player2.isAlive()) {
            player2.render(g);
        }
        
        // 渲染草丛（在坦克之上，遮挡坦克）
        gameMap.renderGrass(g);
        
        // 渲染子弹
        for (Bullet bullet : bullets) {
            bullet.render(g);
        }
        
        // 渲染爆炸
        for (Explosion exp : explosions) {
            exp.render(g);
        }
        
        // 渲染特效
        if (System.currentTimeMillis() - empEffectStart < 500) {
            uiManager.renderEMPEffect(g, empEffectX, empEffectY, empEffectStart);
        }
        if (System.currentTimeMillis() - phaseEffectStart < 300) {
            uiManager.renderPhaseTrail(g, phaseFromX, phaseFromY, phaseToX, phaseToY, phaseEffectStart);
        }
        
        // 渲染天气
        weatherSystem.render(g, player1 != null ? player1.getX() : 400, 
                            player1 != null ? player1.getY() : 400);
        
        // 取消裁剪
        g.setClip(null);
        
        // 渲染HUD
        hud.render(g, player1, levelManager.getCurrentLevel(), 
                  enemiesRemaining + enemies.size() + (boss != null && boss.isAlive() ? 1 : 0),
                  player1 != null ? player1.getScore() : 0, 
                  gameMode,
                  weatherSystem.getCurrentWeather().name);
        
        // 渲染已获得芯片
        hud.renderChips(g, chipSystem, 500);
    }
}
