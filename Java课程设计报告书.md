# Java课程设计报告书

## 项目：基于Java和Swing的坦克大战游戏

---

## 一、核心功能

### 1.1 项目概述

我设计并开发了一款基于Java Swing的坦克大战游戏，整个项目包含35个Java源文件，代码量约6400行。游戏采用面向对象的设计思想，将游戏逻辑拆分为实体(entity)、游戏(game)、输入(input)、系统(system)、界面(ui)、工具(util)和世界(world)七个模块包，实现了清晰的职责分离。

### 1.2 四种游戏模式

我为游戏设计了四种不同的玩法模式，以满足不同玩家的游戏需求：

**（1）经典模式（Classic）**

这是最传统的坦克大战玩法，玩家需要保护自己的基地（鹰标），同时消灭地图上出现的所有敌方坦克。游戏共设计了6个关卡，每关敌人数量递增，难度也会逐步提升。第6关为Boss关卡，需要击败特殊的Boss坦克才能获胜。

**（2）护送模式（Escort）**

这个模式下，玩家需要保护一辆物资车从起点安全抵达终点。物资车会沿着预设路径自动移动，玩家要在途中消灭来袭的敌人，一旦物资车被摧毁则游戏失败。

**（3）双人对战模式（PVP）**

支持本地双人同屏对战。玩家1使用WASD移动、空格射击，玩家2使用IJKL移动、回车射击。地图采用对称设计，确保公平竞技。

**（4）Boss挑战模式（Boss Rush）**

这是一个连续挑战Boss的模式，每关都会遇到强力的Boss坦克，考验玩家的操作技巧和策略应对能力。

### 1.3 多样化武器系统

我设计了四种不同特性的武器类型，玩家可以通过Tab键进行切换：

| 武器名称 | 功能描述 |
|---------|---------|
| 普通炮 | 标准弹药，无限使用 |
| 激光炮 | 穿透直线上的所有目标 |
| 散弹炮 | 扇形发射3发子弹 |
| 追踪导弹 | 自动追踪最近的敌人 |

武器弹药可以通过击杀敌人掉落的道具获取，也可以在商店中购买。

### 1.4 技能系统

玩家拥有三个主动技能，每个技能都有独立的冷却时间：

- **Q键 - 相位移动**：向当前朝向瞬间闪现一段距离，可以穿越某些障碍物，闪现后有短暂无敌时间
- **E键 - 电磁脉冲**：释放范围性电磁波，眩晕周围一定范围内的敌人
- **K键 - 护盾技能**：激活后获得3秒无敌护盾

### 1.5 Roguelite战术芯片系统

这是我设计的一个创新系统，每通过一关后，玩家可以从三个随机生成的"战术芯片"中选择一个，获得永久增益效果。我设计了8种不同的芯片：

| 芯片名称 | 效果描述 |
|---------|---------|
| 弹射芯片 | 子弹可穿透多个目标 |
| 吸血芯片 | 击杀敌人回复10点血量 |
| 埋雷芯片 | 移动时自动留下地雷 |
| 加速芯片 | 移动速度提升30% |
| 暴击芯片 | 伤害翻倍 |
| 护盾芯片 | 下次受到伤害减半 |
| 速射芯片 | 射击冷却减少50% |
| 大弹夹 | 获得大量特殊弹药 |

这个系统让每局游戏都有不同的成长路线，大大增加了游戏的可玩性。

### 1.6 精英敌人词缀系统

为了增加游戏难度和变化性，我给敌人设计了词缀系统。随着关卡推进，有一定概率会刷出带有特殊词缀的精英敌人：

- **自爆**：死亡时产生范围爆炸，对附近玩家造成伤害
- **刚毅**：免疫正面伤害，玩家需要绕到敌人后方才能造成伤害
- **吸血**：攻击命中玩家后会回复自身生命
- **迅捷**：移动速度翻倍，更难击中
- **护盾**：受到的伤害减半

### 1.7 动态天气系统

游戏中会随机切换天气状态，不同天气会带来不同的战斗体验：

- **晴朗**：正常战斗环境
- **暴雨**：地面变滑，坦克移动时会有惯性滑动效果
- **沙尘暴**：视野受限，所有坦克每秒受到1点伤害

### 1.8 丰富的地形系统

我设计了多种地形元素，让关卡设计更加多样化：

**（1）五级围墙系统**

围墙分为5个等级，玩家可以通过商店升级基地周围的围墙：
- 土墙（2血）→ 砖墙（4血）→ 石墙（6血）→ 铁墙（8血）→ 钢墙（10血）

**（2）特殊地形**
- **水域**：阻挡坦克移动，子弹可以飞过
- **草丛**：可以隐蔽，草丛会覆盖在坦克上方
- **冰面**：坦克经过时会产生滑动效果
- **油桶**：可被子弹引爆，产生范围伤害
- **传送门**：成对出现，进入一个传送门会从另一个传送出

### 1.9 商店系统

按B键可以打开游戏内商店，使用击杀敌人获得的分数购买各种道具：

| 商品 | 价格 | 效果 |
|-----|-----|-----|
| 自动哨戒炮 | 300分 | 在基地周围部署自动炮台（最多3座） |
| 修复围墙 | 50分 | 恢复基地周围围墙血量 |
| 升级围墙 | 150分 | 将围墙升级到下一等级 |
| 恢复生命 | 200分 | 恢复50点HP |
| 额外弹药 | 150分 | 获得各类武器弹药 |

### 1.10 道具掉落系统

击杀敌人有概率掉落8种不同的道具：

- 生命：恢复50点血量
- 护盾：获得2秒无敌
- 加速：移动速度提升
- 激光炮弹药：获得5发
- 散弹炮弹药：获得8发
- 追踪导弹：获得3发
- 额外生命：生命+1
- 积分奖励：获得200分

### 1.11 玩家生命系统

玩家初始有3条生命，被击杀后会在出生点复活并获得2秒无敌时间。生命用尽后游戏结束，可以选择重新开始或返回主菜单。

---

## 二、主要界面

### 2.1 主菜单界面

游戏启动后首先进入主菜单，界面采用深蓝色背景，顶部显示游戏标题"坦克大战"和副标题。菜单提供四个选项：开始游戏、模式选择、操作说明、退出游戏。玩家通过上下方向键选择，回车键确认。

### 2.2 模式选择界面

从主菜单进入模式选择后，会以卡片形式展示四种游戏模式。每张卡片显示模式名称和简要说明，当前选中的卡片会有高亮边框效果。

### 2.3 游戏主界面

游戏主界面分为两个区域：
- **左侧游戏区域（780×780像素）**：显示地图、坦克、子弹、特效等所有游戏元素
- **右侧HUD面板（220像素宽）**：显示当前关卡、剩余敌人数量、玩家血量、生命数、武器信息、技能冷却状态、得分、天气状况以及操作提示

### 2.4 暂停界面

按ESC或P键可以暂停游戏，屏幕中央会弹出半透明的暂停菜单，提供三个选项：继续游戏、重新开始、返回主菜单。

### 2.5 商店界面

按B键打开商店界面，以列表形式展示可购买的商品，每项显示名称、价格和说明。积分不足的商品会以灰色显示，已达上限的商品（如哨戒炮）会提示"已满"。

### 2.6 芯片选择界面

每关结束后进入芯片选择界面，以三张卡片的形式展示随机生成的芯片选项。每张卡片用不同颜色区分，显示芯片名称、图标和效果描述，玩家按数字键1/2/3选择。

### 2.7 游戏结束界面

游戏结束后会显示"游戏结束"或"胜利"字样，以及最终得分。玩家可以按回车重新开始，或按ESC返回主菜单。

---

## 三、关键代码

### 3.1 游戏主循环

游戏采用Swing Timer实现60FPS的固定帧率循环，这是整个游戏运行的核心：

```java
public class GamePanel extends JPanel {
    private Timer gameTimer;
    
    private void init() {
        // 60FPS游戏循环
        gameTimer = new Timer(1000 / 60, e -> {
            update();    // 更新游戏逻辑
            repaint();   // 重绘画面
        });
        gameTimer.start();
    }
    
    private void update() {
        switch (gameState) {
            case PLAYING:
                updateGame();
                break;
            case PAUSED:
                updatePause();
                break;
            // ... 其他状态
        }
        input.update();  // 清除单帧按键状态
    }
}
```

### 3.2 碰撞检测系统

我实现了一个完整的碰撞检测系统，处理坦克与地形、子弹与目标等各种碰撞情况：

```java
public class CollisionSystem {
    // 检查坦克与地图的碰撞
    public boolean checkTankMapCollision(Tank tank, Rectangle bounds) {
        int startCol = bounds.x / Constants.TILE_SIZE;
        int endCol = (bounds.x + bounds.width) / Constants.TILE_SIZE;
        int startRow = bounds.y / Constants.TILE_SIZE;
        int endRow = (bounds.y + bounds.height) / Constants.TILE_SIZE;
        
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile != null && tile.blocksMovement()) {
                    if (bounds.intersects(tile.getBounds())) {
                        return true;  // 发生碰撞
                    }
                }
            }
        }
        return false;
    }
    
    // 检查子弹碰撞并处理
    public Tile checkBulletTileCollision(Bullet bullet) {
        Tile tile = gameMap.getTileAtPixel(bullet.getX(), bullet.getY());
        if (tile != null && tile.blocksMovement()) {
            if (tile.isDestructible()) {
                boolean destroyed = tile.takeDamage(bullet.getDamage());
                if (destroyed) {
                    gameMap.setTileType(tile.getRow(), tile.getCol(), 
                                        TileType.EMPTY);
                }
            }
            return tile;
        }
        return null;
    }
}
```

### 3.3 追踪导弹的实现

追踪导弹是我觉得比较有意思的一个功能，使用向量插值实现平滑的追踪转向：

```java
public class MissileBullet extends Bullet {
    private Entity target;
    private double vx, vy;
    private double turnRate = 0.15;  // 转向速率
    
    @Override
    public void update(double deltaTime) {
        // 每帧寻找最近目标
        findTarget();
        
        // 追踪目标
        if (target != null && target.isAlive()) {
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            
            if (dist > 0) {
                // 计算期望速度方向
                double targetVx = (dx / dist) * speed;
                double targetVy = (dy / dist) * speed;
                
                // 平滑转向（插值）
                vx += (targetVx - vx) * turnRate;
                vy += (targetVy - vy) * turnRate;
                
                // 保持速度恒定
                double currentSpeed = Math.sqrt(vx * vx + vy * vy);
                vx = (vx / currentSpeed) * speed;
                vy = (vy / currentSpeed) * speed;
            }
        }
        
        x += vx;
        y += vy;
    }
}
```

### 3.4 冰面滑动效果

冰面和雨天的滑动效果通过速度衰减来模拟物理惯性：

```java
private void updatePlayer() {
    // 检查是否在冰面或雨天
    Tile currentTile = gameMap.getTileAtPixel(player1.getX(), player1.getY());
    boolean onIce = currentTile != null && 
                    currentTile.getType() == TileType.ICE;
    boolean isRaining = weatherSystem.getCurrentWeather() == 
                        WeatherSystem.WeatherType.RAIN;
    boolean shouldSlide = onIce || isRaining;
    
    // 移动时累积滑动速度
    if (shouldSlide && newDir != null) {
        double slideMultiplier = isRaining ? 0.2 : 0.3;
        slideVelocityX += newDir.dx * ICE_SLIDE_SPEED * slideMultiplier;
        slideVelocityY += newDir.dy * ICE_SLIDE_SPEED * slideMultiplier;
    }
    
    // 滑动效果（松开按键后继续滑动）
    if (shouldSlide && (Math.abs(slideVelocityX) > 0.1 || 
                        Math.abs(slideVelocityY) > 0.1)) {
        double newX = player1.getX() + slideVelocityX;
        double newY = player1.getY() + slideVelocityY;
        
        // 检查碰撞
        if (!blocked) {
            player1.setX(newX);
            player1.setY(newY);
        } else {
            slideVelocityX = 0;
            slideVelocityY = 0;
        }
        
        // 应用摩擦力减速
        double friction = isRaining ? 0.88 : 0.92;
        slideVelocityX *= friction;
        slideVelocityY *= friction;
    }
}
```

### 3.5 输入处理系统

为了区分"按住"和"单击"两种输入，我设计了一套按键状态管理机制：

```java
public class InputHandler implements KeyListener {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> justPressedKeys = new HashSet<>();
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // 只有第一次按下时才加入justPressedKeys
        if (!pressedKeys.contains(code)) {
            justPressedKeys.add(code);
        }
        pressedKeys.add(code);
    }
    
    // 持续按住检测（用于移动和射击）
    public boolean isKeyDown(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
    
    // 单次触发检测（用于技能和菜单操作）
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }
    
    // 每帧结束清除单击状态
    public void update() {
        justPressedKeys.clear();
    }
}
```

### 3.6 战术芯片选择界面渲染

芯片选择界面使用Graphics2D绘制，包含卡片、图标、文字等元素：

```java
public void renderSelection(Graphics2D g, int selectedIndex) {
    int cardWidth = 180;
    int cardHeight = 240;
    
    for (int i = 0; i < 3; i++) {
        ChipType chip = currentChoices[i];
        int cx = startX + i * (cardWidth + gap);
        
        // 选中高亮
        if (i == selectedIndex) {
            g.setColor(new Color(255, 255, 100, 100));
            g.fillRoundRect(cx - 5, cy - 5, cardWidth + 10, 
                           cardHeight + 10, 15, 15);
        }
        
        // 卡片背景（使用芯片颜色）
        g.setColor(new Color(chip.color.getRed(), 
                             chip.color.getGreen(),
                             chip.color.getBlue(), 180));
        g.fillRoundRect(cx, cy, cardWidth, cardHeight, 10, 10);
        
        // 绘制芯片名称和描述
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g.drawString(chip.name, cx + 10, cy + 140);
        
        g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        g.drawString(chip.description, cx + 10, cy + 165);
    }
}
```

---

## 四、功能展望

### 4.1 网络对战功能

目前游戏只支持本地双人对战，未来可以考虑加入网络联机功能。可以使用Java Socket实现客户端-服务器架构，让两个远程玩家能够在线对战。主要技术难点在于网络延迟补偿和状态同步。

### 4.2 地图编辑器

可以开发一个可视化的地图编辑器，让玩家能够自己设计关卡。使用鼠标点击放置不同类型的地形块，设置敌人出生点和玩家出生点，然后导出为地图文件。

### 4.3 存档系统

目前游戏没有存档功能，关闭后进度会丢失。可以使用Java序列化或JSON文件来保存游戏进度，包括当前关卡、分数、已获得的芯片等信息。

### 4.4 更多敌人类型

可以设计更多种类的敌人坦克，比如双管坦克、火焰坦克、隐形坦克等，每种有不同的攻击方式和弱点，增加游戏的策略深度。

### 4.5 成就系统

可以加入成就系统，记录玩家的各种游戏成就，比如"击杀100个敌人"、"一局不受伤通关"、"收集所有芯片"等，增加游戏的挑战性和收集要素。

### 4.6 音效和背景音乐

目前游戏没有音效，可以使用Java Sound API添加射击声、爆炸声、背景音乐等，让游戏体验更加沉浸。

### 4.7 更丰富的视觉效果

可以考虑使用图片资源替代纯代码绘制，加入更多粒子效果（如尾烟、火花），让游戏画面更加精美。

---

## 五、开发心得

刚开始做这个项目的时候，说实话我是有点发愁的。坦克大战这个游戏看起来简单，但真正动手写的时候才发现里面有很多细节需要考虑。比如碰撞检测怎么做才不会穿墙、子弹和坦克的判定范围怎么算、多个系统之间怎么协调配合……这些问题一个接一个地冒出来。

我在开发过程中体会最深的是模块化设计的重要性。一开始我把所有代码都写在一个类里面，结果代码越来越乱，改一个地方经常会影响其他地方。后来我花了一些时间重构代码，把不同功能拆分到不同的包里面——实体归实体、系统归系统、界面归界面，这样每个类的职责就清晰了，改起来也方便多了。

调试游戏也挺折腾的。有一个bug让我印象特别深：按Tab键切换武器一直没反应。我检查了好久，最后才发现是Tab键在Swing里默认是焦点切换键，会被系统截获而不会触发KeyListener。知道原因后解决倒是很简单，加一行`setFocusTraversalKeysEnabled(false)`就好了。这件事让我明白，遇到bug不能只盯着自己的代码看，有时候问题出在框架或者系统层面。

做追踪导弹功能的时候也挺有意思。我一开始想得很简单，就让子弹一直往目标方向飞就行了。结果做出来发现导弹转向太生硬了，看起来不自然。后来我查了一些资料，用向量插值的方法实现了平滑转向，效果就好多了。这让我意识到，有些功能表面上看起来简单，但要做得"感觉对"还是需要花心思的。

整个项目做下来，我对Java的面向对象编程理解更深了。以前学的时候总觉得继承、多态这些概念很抽象，但在这个项目里用起来感觉很自然。比如所有的子弹都继承自Bullet基类，不同类型的子弹只需要重写自己特殊的行为就行了；敌人和玩家都是Tank的子类，共用一套血量和碰撞的逻辑。这种设计确实能减少很多重复代码。

另外，做游戏让我体会到"用户体验"这个东西。有些功能你觉得已经实现了，但玩起来就是不舒服。比如冰面滑动，我第一版做出来滑得太厉害了，根本控制不住；调了好几次参数，才找到一个既有滑动感又不至于太难操控的平衡点。游戏开发确实不只是把功能做出来就完了，还得让玩家玩得舒服。

总的来说，这个项目让我收获挺大的。不光是Java技术层面的提升，更重要的是学会了怎么把一个相对复杂的项目拆分成小块去完成，怎么在遇到问题时耐心地排查定位。这些经验我觉得以后做其他项目也用得上。

---

## 六、教师点评

该同学完成的坦克大战游戏项目整体质量较高，能够看出在设计和开发过程中下了不少功夫。

从代码结构来看，项目采用了较为规范的包组织方式，将实体、系统、界面等模块分离，体现了一定的软件工程思维。35个类文件、6000余行代码的工作量对于课程设计而言是比较扎实的。类的继承层次设计合理，比如Tank作为基类派生出PlayerTank和EnemyTank，Bullet基类派生出多种特殊子弹类型，这些都说明该同学对面向对象的核心概念有较好的理解和应用能力。

功能实现方面，该项目不只是简单复刻经典坦克大战，而是加入了不少创新元素。战术芯片系统借鉴了Roguelite游戏的成长机制，敌人词缀系统增加了战斗的多样性，多种武器和技能的设计也让游戏玩法更加丰富。这些设计思路值得肯定。

碰撞检测、追踪导弹的向量插值转向、冰面滑动的物理模拟等技术点处理得当，说明该同学具备一定的问题分析和解决能力。特别是在开发心得中提到的Tab键被系统截获的问题，能够跳出自身代码去思考框架层面的原因，这种调试思路是很好的。

当然，项目也存在一些可以改进的地方。目前游戏没有音效支持，对于游戏类项目来说体验上会有所欠缺。另外，所有图形都是代码绘制的，虽然展示了Graphics2D的使用能力，但如果能结合图片资源会让画面更加精致。网络对战功能如果能实现，将会是一个很好的技术亮点。

综合来看，该项目较好地完成了课程设计的要求，功能完整、代码规范、有一定的创新性。建议该同学在后续学习中可以进一步了解游戏开发的其他方面，如资源管理、性能优化、网络编程等。

**评定等级：优秀**
