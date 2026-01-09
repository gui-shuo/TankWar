package com.tankwar.world;

/**
 * 地图块类型
 */
public enum TileType {
    EMPTY(false, false, "空地", 0, 0),
    
    // 5级围墙系统
    WALL_LV1(true, true, "土墙", 1, 2),      // 等级1: 土墙, 2血
    WALL_LV2(true, true, "砖墙", 2, 4),      // 等级2: 砖墙, 4血
    WALL_LV3(true, true, "石墙", 3, 6),      // 等级3: 石墙, 6血
    WALL_LV4(true, true, "铁墙", 4, 8),      // 等级4: 铁墙, 8血
    WALL_LV5(true, true, "钢墙", 5, 10),     // 等级5: 钢墙, 10血, 可摧毁
    
    // 保留旧类型作为别名（兼容）
    BRICK(true, true, "砖墙", 2, 4),         // 等同于WALL_LV2
    STEEL(true, true, "钢墙", 5, 10),        // 等同于WALL_LV5, 可摧毁
    
    WATER(true, false, "水域", 0, 0),
    GRASS(false, false, "草丛", 0, 0),
    ICE(false, false, "冰面", 0, 0),
    BASE(true, true, "基地", 0, 1),
    BARREL(true, true, "油桶", 0, 1),
    PORTAL_A(false, false, "传送门A", 0, 0),
    PORTAL_B(false, false, "传送门B", 0, 0);
    
    public final boolean blocksMovement;  // 阻挡移动
    public final boolean destructible;     // 可被摧毁
    public final String name;
    public final int wallLevel;            // 围墙等级 (1-5, 0表示非围墙)
    public final int defaultHp;            // 默认血量 (-1表示不可摧毁)
    
    TileType(boolean blocksMovement, boolean destructible, String name, int wallLevel, int defaultHp) {
        this.blocksMovement = blocksMovement;
        this.destructible = destructible;
        this.name = name;
        this.wallLevel = wallLevel;
        this.defaultHp = defaultHp;
    }
    
    /**
     * 是否是围墙类型
     */
    public boolean isWall() {
        return wallLevel > 0;
    }
    
    /**
     * 根据等级获取对应的围墙类型
     */
    public static TileType getWallByLevel(int level) {
        switch (level) {
            case 1: return WALL_LV1;
            case 2: return WALL_LV2;
            case 3: return WALL_LV3;
            case 4: return WALL_LV4;
            case 5: return WALL_LV5;
            default: return WALL_LV1;
        }
    }
    
    /**
     * 获取下一级围墙类型（用于升级）
     */
    public TileType getNextWallLevel() {
        if (!isWall() || wallLevel >= 5) return this;
        return getWallByLevel(wallLevel + 1);
    }
}
