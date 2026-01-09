package com.tankwar.game;

/**
 * 游戏状态枚举
 */
public enum GameState {
    MENU,           // 主菜单
    MODE_SELECT,    // 模式选择
    PLAYING,        // 游戏进行中
    PAUSED,         // 暂停
    CHIP_SELECT,    // 芯片选择
    SHOP,           // 商店
    GAME_OVER,      // 游戏结束
    VICTORY,        // 胜利
    LEVEL_COMPLETE  // 关卡完成
}
