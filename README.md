# 坦克大战 - 创新版 (Tank War Innovation)

一款基于经典坦克大战的创新升级版，使用 Java Swing 开发。

## 📋 功能特性

### 核心创新玩法
- **🎯 Roguelite 成长系统**：每通关可选择战术芯片（子弹弹射、击杀回血、移动留雷等）
- **⚡ 主动技能系统**：相位移动（穿墙闪现）、电磁脉冲（范围眩晕）
- **🔫 多样化武器库**：普通炮、激光炮（穿透）、散弹炮（扇形）、追踪导弹

### 策略与互动
- **🏗️ 基地建设与塔防**：购买自动哨戒炮、修复/升级围墙
- **💥 环境互动元素**：油桶爆炸、传送门
- **🌧️ 动态天气系统**：暴雨（滑动）、浓雾（战争迷雾）、沙尘暴（持续扣血）

### 游戏模式
- **🎮 经典模式**：保护基地，消灭所有敌人
- **🚚 护送模式**：保护物资车安全抵达终点
- **👥 双人对战**：本地双人竞技对战
- **👹 Boss挑战**：连续挑战多阶段Boss

### 精英敌人系统
- **💀 自爆**：死亡时产生爆炸
- **🛡️ 刚毅**：免疫正面伤害，需绕后攻击
- **🧛 吸血**：攻击命中回复生命
- **⚡ 迅捷**：移动速度翻倍

## 🎮 操作说明

| 按键 | 功能 |
|------|------|
| W / ↑ | 向上移动 |
| S / ↓ | 向下移动 |
| A / ← | 向左移动 |
| D / → | 向右移动 |
| 空格 | 发射子弹 |
| Q | 相位移动（穿墙闪现，5秒冷却） |
| E | 电磁脉冲（范围眩晕，10秒冷却） |
| TAB | 切换武器 |
| B | 打开/关闭商店 |
| P / ESC | 暂停游戏 |
| 1/2/3 | 选择芯片（过关时） |

### 双人模式（玩家2）
| 按键 | 功能 |
|------|------|
| I/J/K/L | 移动 |
| Enter | 射击 |

## 🚀 运行方式

### 方式一：命令行编译运行
```bash
# 进入项目目录
cd TankWar

# 编译
javac -encoding UTF-8 -d out src/com/tankwar/**/*.java src/com/tankwar/*.java

# 运行
java -cp out com.tankwar.Main
```

### 方式二：使用提供的脚本
```bash
# Linux/Mac
./run.sh

# Windows
run.bat
```

### 方式三：IDE运行
1. 用 IntelliJ IDEA / Eclipse 打开项目
2. 将 `src` 设为源代码根目录
3. 运行 `com.tankwar.Main` 类

## 📁 项目结构

```
TankWar/
├── src/
│   └── com/tankwar/
│       ├── Main.java              # 程序入口
│       ├── game/                  # 游戏核心
│       │   ├── Game.java          # 游戏主类（窗口管理）
│       │   ├── GamePanel.java     # 游戏面板（逻辑+渲染）
│       │   ├── GameState.java     # 游戏状态枚举
│       │   └── GameMode.java      # 游戏模式枚举
│       ├── entity/                # 游戏实体
│       │   ├── Entity.java        # 实体基类
│       │   ├── Tank.java          # 坦克基类
│       │   ├── PlayerTank.java    # 玩家坦克
│       │   ├── EnemyTank.java     # 敌人坦克
│       │   ├── BossTank.java      # Boss坦克
│       │   ├── Bullet.java        # 子弹基类
│       │   ├── LaserBullet.java   # 激光子弹
│       │   ├── ShotgunBullet.java # 散弹
│       │   ├── MissileBullet.java # 追踪导弹
│       │   ├── Explosion.java     # 爆炸特效
│       │   ├── PowerUp.java       # 道具
│       │   ├── Turret.java        # 自动哨戒炮
│       │   ├── Mine.java          # 地雷
│       │   └── SupplyTruck.java   # 护送物资车
│       ├── world/                 # 世界与地图
│       │   ├── GameMap.java       # 游戏地图
│       │   ├── Tile.java          # 地图块
│       │   ├── TileType.java      # 地图块类型
│       │   └── LevelManager.java  # 关卡管理
│       ├── system/                # 游戏系统
│       │   ├── CollisionSystem.java # 碰撞检测
│       │   ├── WeatherSystem.java   # 天气系统
│       │   └── ChipSystem.java      # 战术芯片系统
│       ├── ui/                    # 用户界面
│       │   ├── MenuPanel.java     # 主菜单
│       │   ├── HUD.java           # 游戏内HUD
│       │   └── UIManager.java     # UI管理器
│       ├── input/
│       │   └── InputHandler.java  # 键盘输入处理
│       └── util/
│           ├── Constants.java     # 常量定义
│           └── Direction.java     # 方向枚举
├── run.sh                         # Linux/Mac运行脚本
├── run.bat                        # Windows运行脚本
└── README.md                      # 本文件
```

## 🎯 游戏目标

### 经典模式
- 保护基地（金色鹰形图标）不被摧毁
- 消灭所有敌人坦克
- 收集道具强化自己
- 通过6个关卡（含Boss战）

### 护送模式
- 保护物资车安全到达终点
- 消灭沿途敌人

### Boss挑战
- 连续击败多阶段Boss
- Boss有特殊弹幕攻击

## 🔧 技术说明

- **语言**: Java 8+
- **GUI框架**: Swing (JFrame + JPanel)
- **渲染**: 双缓冲 + paintComponent自绘
- **游戏循环**: javax.swing.Timer (60 FPS)
- **碰撞检测**: AABB矩形碰撞
- **图形**: 纯代码绘制几何图形（无外部资源依赖）

## 📝 开发说明

本项目完全使用Java标准库开发，无需任何第三方依赖。

### 设计模式
- **继承**: Entity → Tank → PlayerTank/EnemyTank
- **策略模式**: 不同武器类型的子弹行为
- **状态机**: 游戏状态管理

### 扩展点
- 添加新武器：继承 `Bullet` 类
- 添加新敌人词缀：修改 `EnemyAffix` 枚举
- 添加新关卡：编辑 `LevelManager` 中的关卡数据
- 添加新芯片：修改 `ChipSystem` 中的芯片类型

## 🎮 游戏截图

游戏使用代码绘制图形，界面简洁清晰：
- 绿色坦克：玩家
- 红色坦克：敌人
- 紫色坦克：精英敌人
- 橙色大型坦克：Boss
- 砖红色方块：砖墙（可破坏）
- 银灰色方块：钢墙（不可破坏）
- 蓝色方块：水域（阻挡通行）
- 绿色方块：草丛（可穿行）
- 金色方块：基地（需保护）
- 红色圆形：油桶（击中爆炸）
- 紫色圆形：传送门

## 📄 许可证

本项目仅供学习交流使用。

---

**Enjoy the game! 🎮**
