package com.tankwar.system;

import com.tankwar.entity.PlayerTank;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 战术芯片系统 - Roguelite成长
 */
public class ChipSystem {
    
    /**
     * 芯片类型
     */
    public enum ChipType {
        RICOCHET("弹射芯片", "子弹可穿透多个目标", new Color(100, 200, 255)),
        VAMPIRE("吸血芯片", "击杀敌人回复10点血量", new Color(200, 50, 80)),
        MINE_DROP("埋雷芯片", "移动时自动留下地雷", new Color(150, 150, 50)),
        SPEED_BOOST("加速芯片", "移动速度提升30%", new Color(100, 255, 150)),
        DOUBLE_DAMAGE("暴击芯片", "伤害翻倍", new Color(255, 100, 100)),
        SHIELD("护盾芯片", "下次受到伤害减半", new Color(100, 150, 255)),
        RAPID_FIRE("速射芯片", "射击冷却减少50%", new Color(255, 200, 50)),
        LARGE_AMMO("大弹夹", "获得大量特殊弹药", new Color(200, 150, 100));
        
        public final String name;
        public final String description;
        public final Color color;
        
        ChipType(String name, String description, Color color) {
            this.name = name;
            this.description = description;
            this.color = color;
        }
    }
    
    private List<ChipType> acquiredChips = new ArrayList<>();
    private ChipType[] currentChoices = new ChipType[3];
    private boolean selectionPending = false;
    
    public ChipSystem() {}
    
    /**
     * 生成3个随机芯片选项
     */
    public void generateChoices() {
        List<ChipType> available = new ArrayList<>();
        for (ChipType type : ChipType.values()) {
            // 排除已获得的芯片（部分可叠加）
            if (!acquiredChips.contains(type) || canStack(type)) {
                available.add(type);
            }
        }
        
        // 随机打乱
        Collections.shuffle(available);
        
        // 取前3个
        for (int i = 0; i < 3; i++) {
            if (i < available.size()) {
                currentChoices[i] = available.get(i);
            } else {
                currentChoices[i] = ChipType.values()[(int)(Math.random() * ChipType.values().length)];
            }
        }
        
        selectionPending = true;
    }
    
    private boolean canStack(ChipType type) {
        // 某些芯片可以叠加
        return type == ChipType.LARGE_AMMO || type == ChipType.SHIELD;
    }
    
    /**
     * 选择芯片
     */
    public void selectChip(int index, PlayerTank player) {
        if (index < 0 || index >= 3 || !selectionPending) return;
        
        ChipType selected = currentChoices[index];
        applyChip(selected, player);
        acquiredChips.add(selected);
        selectionPending = false;
    }
    
    /**
     * 应用芯片效果
     */
    private void applyChip(ChipType chip, PlayerTank player) {
        switch (chip) {
            case RICOCHET:
                player.setHasRicochet(true);
                break;
            case VAMPIRE:
                player.setHasVampire(true);
                break;
            case MINE_DROP:
                player.setHasMineDrop(true);
                break;
            case SPEED_BOOST:
                player.setHasSpeedBoost(true);
                break;
            case DOUBLE_DAMAGE:
                player.setHasDoubleDamage(true);
                break;
            case SHIELD:
                player.setHasShield(true);
                break;
            case RAPID_FIRE:
                // 直接修改射击冷却（需要在Tank类中支持）
                break;
            case LARGE_AMMO:
                player.addAmmo(com.tankwar.entity.WeaponType.LASER, 10);
                player.addAmmo(com.tankwar.entity.WeaponType.SHOTGUN, 15);
                player.addAmmo(com.tankwar.entity.WeaponType.MISSILE, 5);
                break;
        }
    }
    
    /**
     * 渲染芯片选择界面
     */
    public void renderSelection(Graphics2D g, int selectedIndex) {
        if (!selectionPending) return;
        
        int width = 600;
        int height = 300;
        int x = (780 - width) / 2;
        int y = (780 - height) / 2;
        
        // 背景
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(x - 20, y - 60, width + 40, height + 80, 20, 20);
        
        // 标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 24));
        String title = "选择战术芯片";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, x + (width - fm.stringWidth(title)) / 2, y - 20);
        
        // 芯片卡片
        int cardWidth = 180;
        int cardHeight = 240;
        int gap = 20;
        int startX = x + (width - 3 * cardWidth - 2 * gap) / 2;
        
        for (int i = 0; i < 3; i++) {
            ChipType chip = currentChoices[i];
            int cx = startX + i * (cardWidth + gap);
            int cy = y;
            
            // 选中高亮
            if (i == selectedIndex) {
                g.setColor(new Color(255, 255, 100, 100));
                g.fillRoundRect(cx - 5, cy - 5, cardWidth + 10, cardHeight + 10, 15, 15);
            }
            
            // 卡片背景
            g.setColor(new Color(chip.color.getRed(), chip.color.getGreen(), 
                                chip.color.getBlue(), 180));
            g.fillRoundRect(cx, cy, cardWidth, cardHeight, 10, 10);
            
            // 边框
            g.setColor(chip.color.brighter());
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(cx, cy, cardWidth, cardHeight, 10, 10);
            
            // 编号
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString(String.valueOf(i + 1), cx + 10, cy + 35);
            
            // 芯片图标区域
            g.setColor(chip.color.darker());
            g.fillOval(cx + cardWidth/2 - 30, cy + 50, 60, 60);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String icon = chip.name.substring(0, 1);
            g.drawString(icon, cx + cardWidth/2 - 8, cy + 88);
            
            // 名称
            g.setFont(new Font("微软雅黑", Font.BOLD, 16));
            fm = g.getFontMetrics();
            g.drawString(chip.name, cx + (cardWidth - fm.stringWidth(chip.name)) / 2, cy + 140);
            
            // 描述
            g.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            fm = g.getFontMetrics();
            // 自动换行
            String desc = chip.description;
            int maxWidth = cardWidth - 20;
            int descY = cy + 165;
            
            while (!desc.isEmpty()) {
                String line = desc;
                while (fm.stringWidth(line) > maxWidth && line.length() > 1) {
                    line = line.substring(0, line.length() - 1);
                }
                g.drawString(line, cx + 10, descY);
                desc = desc.substring(line.length());
                descY += 16;
            }
        }
        
        // 提示
        g.setColor(Color.GRAY);
        g.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        String hint = "按 1/2/3 选择芯片";
        fm = g.getFontMetrics();
        g.drawString(hint, x + (width - fm.stringWidth(hint)) / 2, y + height + 10);
    }
    
    public boolean isSelectionPending() { return selectionPending; }
    public ChipType[] getCurrentChoices() { return currentChoices; }
    public List<ChipType> getAcquiredChips() { return acquiredChips; }
    
    public void reset() {
        acquiredChips.clear();
        selectionPending = false;
    }
}
