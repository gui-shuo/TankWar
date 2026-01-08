package com.tankwar.world;

/**
 * 地图块类型
 */
public enum TileType {
    EMPTY(false, false, "空地"),
    BRICK(true, true, "砖墙"),
    STEEL(true, false, "钢墙"),
    WATER(true, false, "水域"),
    GRASS(false, false, "草丛"),
    BASE(true, true, "基地"),
    BARREL(true, true, "油桶"),
    PORTAL_A(false, false, "传送门A"),
    PORTAL_B(false, false, "传送门B");
    
    public final boolean blocksMovement;  // 阻挡移动
    public final boolean destructible;     // 可被摧毁
    public final String name;
    
    TileType(boolean blocksMovement, boolean destructible, String name) {
        this.blocksMovement = blocksMovement;
        this.destructible = destructible;
        this.name = name;
    }
}
