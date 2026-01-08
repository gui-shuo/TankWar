package com.tankwar.world;

import com.tankwar.game.GameMode;

/**
 * 关卡管理器
 */
public class LevelManager {
    private int currentLevel = 1;
    private int maxLevel = 6;  // 5个普通关 + 1个Boss关
    private GameMode mode;
    
    // 关卡数据 - 26x26
    // #: 砖墙, S: 钢墙, ~: 水, *: 草丛, I: 冰面, B: 基地, O: 油桶, a/b: 传送门, P: 玩家出生, E: 敌人出生
    private static final String[][] LEVELS = {
        // 第1关 - 入门（包含各种地形）
        {
            "E    SSSS  SSSS    E ",
            "  ###  ##  ##  ### ",
            "  ###  ##  ##  ### ",
            "     ~~~~  ~~~~    ",
            "###  ~~~~  ~~~~  ### ",
            "###              ### ",
            "     IIII  IIII     ",
            "     IIII  IIII     ",
            "                    ",
            "  ##    ****    ##  ",
            "  ##    ****    ##  ",
            "  SS    ****    SS  ",
            "        ****        ",
            "  ##    ~~~~    ##  ",
            "  ##    ~~~~    ##  ",
            "        IIII        ",
            "###     IIII    ### ",
            "###             ### ",
            "                    ",
            "  ##  SS    SS  ##  ",
            "  ##  SS    SS  ##  ",
            "                    ",
            "    ##  ####  ##    ",
            "    ##  #  #  ##    ",
            "  P     #B #     P  ",
            "        ####        "
        },
        // 第2关 - 水域与传送门
        {
            "E    #####  #####    E",
            "     #   #  #   #     ",
            "###  # a #  # b #  ###",
            "###  #####  #####  ###",
            "                      ",
            "  ~~~~          ~~~~  ",
            "  ~~~~  ######  ~~~~  ",
            "  ~~~~  #    #  ~~~~  ",
            "        #    #        ",
            "####    #    #    ####",
            "####              ####",
            "      O        O      ",
            "                      ",
            "####    ~~~~~~    ####",
            "####    ~~~~~~    ####",
            "        ~~~~~~        ",
            "                      ",
            "  ##  ##      ##  ##  ",
            "  ##  ##      ##  ##  ",
            "                      ",
            "    ##  SSSSSS  ##    ",
            "    ##  S    S  ##    ",
            "E       S    S       E",
            "        S B  S        ",
            "  P     SSSSSS     P  ",
            "                      "
        },
        // 第3关 - 迷宫与油桶
        {
            "E  #####E#####E#####  ",
            "   #   # # # # #   #  ",
            "## # # # # # # # # # #",
            "## # #   # # #   # # #",
            "   # ##### # ##### #  ",
            "   #       #       #  ",
            "#### ##### # ##### ###",
            "   #     # # #        ",
            "   # ### # # # ### ###",
            "## # #O#   #   #O# # #",
            "## # ###   #   ### # #",
            "   #       #       #  ",
            "   ####### # #######  ",
            "##       # # #       #",
            "## ##### # # # ##### #",
            "   #   # # # # #   #  ",
            "   # #   # # #   # #  ",
            "#### ##### # ##### ###",
            "           #          ",
            "  ##  **      **  ##  ",
            "  ##  **  ##  **  ##  ",
            "      **  ##  **      ",
            "          ##          ",
            "    ##   S  S   ##    ",
            "  P ##   SB S   ## P  ",
            "         SSSS         "
        },
        // 第4关 - 精英敌人关
        {
            "ESSSSS  E    E  SSSSSE",
            "S    S          S    S",
            "S  a S  ######  S b  S",
            "S    S  #    #  S    S",
            "SSSSSS  #    #  SSSSSS",
            "        #    #        ",
            "  ####  ######  ####  ",
            "  #  #          #  #  ",
            "  #  #  O    O  #  #  ",
            "  ####          ####  ",
            "                      ",
            "****    ######    ****",
            "****    #    #    ****",
            "****    #    #    ****",
            "        ######        ",
            "                      ",
            "  ####  ~~~~~~  ####  ",
            "  #  #  ~~~~~~  #  #  ",
            "  #  #  ~~~~~~  #  #  ",
            "  ####          ####  ",
            "                      ",
            "    SSSS      SSSS    ",
            "    S  S      S  S    ",
            "    S  S  ##  S  S    ",
            "  P SSSS #B # SSSS P  ",
            "         ####         "
        },
        // 第5关 - 最终关卡
        {
            "E###  ESSSSSE  ###E   ",
            " # #  S     S  # #    ",
            " # #  S  O  S  # #    ",
            " # #  S     S  # #    ",
            " ###  SSSSSSS  ###    ",
            "                      ",
            "****  ########  ****  ",
            "****  #      #  ****  ",
            "****  # a  b #  ****  ",
            "      #      #        ",
            "####  ########  ####  ",
            "#  #            #  #  ",
            "#  #  O      O  #  #  ",
            "####            ####  ",
            "      ~~~~~~~~        ",
            "      ~~~~~~~~        ",
            "####  ~~~~~~~~  ####  ",
            "#  #            #  #  ",
            "#  #  ########  #  #  ",
            "####  #      #  ####  ",
            "      # **** #        ",
            "SSSS  # **** #  SSSS  ",
            "S  S  # **** #  S  S  ",
            "S  S    ####    S  S  ",
            "SPSS    #B #    SSPS  ",
            "        ####          "
        },
        // 第6关 - Boss关
        {
            "                      ",
            "                      ",
            "  SSSSSSSSSSSSSSSSSS  ",
            "  S                S  ",
            "  S   E       E    S  ",
            "  S                S  ",
            "  S                S  ",
            "  SSSSSSSSSSSSSSSSSS  ",
            "                      ",
            "    O    O    O    O  ",
            "                      ",
            "  ****          ****  ",
            "  ****          ****  ",
            "  ****          ****  ",
            "                      ",
            "    ####    ####      ",
            "    #  #    #  #      ",
            "    #  #    #  #      ",
            "    ####    ####      ",
            "                      ",
            "  ~~~~    ~~~~    ~~~~",
            "  ~~~~    ~~~~    ~~~~",
            "                      ",
            "      SSSSSSSSSS      ",
            "  P   S   B    S   P  ",
            "      SSSSSSSSSS      "
        }
    };
    
    // 护送模式地图
    private static final String[] ESCORT_MAP = {
        "E         E         E ",
        "  ###  ###  ###  ### ",
        "  ###  ###  ###  ### ",
        "        C             ", // C标记路线起点
        "###  ####  ####  ### ",
        "                     ",
        "     ######  ####    ",
        "                     ",
        "  ##    ****    ##   ",
        "  ##    ****    ##   ",
        "                     ",
        "        ****         ",
        "  ##            ##   ",
        "                     ",
        "        ####         ",
        "                     ",
        "###              ### ",
        "###              ### ",
        "                     ",
        "  ##  ##    ##  ##   ",
        "  ##  ##    ##  ##   ",
        "                     ",
        "    ##  ####  ##     ",
        "                     ",
        "  P              P   ", // 终点
        "                     "
    };
    
    // PVP地图（对称）
    private static final String[] PVP_MAP = {
        "P    ###  ###  ###   P",
        "     # #  # #  # #    ",
        "###  # #  # #  # #  ##",
        "#    ###  ###  ###   #",
        "#                    #",
        "###  ****    ****  ###",
        "     ****    ****     ",
        "     ****    ****     ",
        "                      ",
        "  ######    ######    ",
        "  #    #    #    #    ",
        "  #  O #    # O  #    ",
        "  #    #    #    #    ",
        "  ######    ######    ",
        "                      ",
        "     ****    ****     ",
        "     ****    ****     ",
        "###  ****    ****  ###",
        "#                    #",
        "#    ###  ###  ###   #",
        "###  # #  # #  # #  ##",
        "     # #  # #  # #    ",
        "     ###  ###  ###    ",
        "                      ",
        "  B               B   ",
        "P                   P "
    };
    
    public LevelManager() {
        this.mode = GameMode.CLASSIC;
    }
    
    public void setMode(GameMode mode) {
        this.mode = mode;
        this.currentLevel = 1;
    }
    
    public GameMode getMode() {
        return mode;
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }
    
    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(1, Math.min(level, maxLevel));
    }
    
    public boolean hasNextLevel() {
        return currentLevel < maxLevel;
    }
    
    public void nextLevel() {
        if (hasNextLevel()) {
            currentLevel++;
        }
    }
    
    public boolean isBossLevel() {
        return mode == GameMode.CLASSIC && currentLevel == maxLevel;
    }
    
    public String[] getCurrentLevelData() {
        switch (mode) {
            case ESCORT:
                return ESCORT_MAP;
            case PVP:
                return PVP_MAP;
            case BOSS_RUSH:
                return LEVELS[5]; // Boss地图
            case CLASSIC:
            default:
                int index = Math.min(currentLevel - 1, LEVELS.length - 1);
                return LEVELS[index];
        }
    }
    
    public int getEnemyCount() {
        switch (mode) {
            case ESCORT:
                return 15 + currentLevel * 3;
            case PVP:
                return 0;
            case BOSS_RUSH:
                return 1 + currentLevel;
            case CLASSIC:
            default:
                if (isBossLevel()) {
                    return 5;
                }
                return 10 + currentLevel * 5;
        }
    }
    
    public int getEliteChance() {
        // 精英敌人出现概率（百分比）
        return Math.min(10 + currentLevel * 5, 40);
    }
    
    public void reset() {
        currentLevel = 1;
    }
    
    public int getMaxLevel() {
        return maxLevel;
    }
}
