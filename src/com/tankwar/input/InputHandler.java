package com.tankwar.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * 键盘输入处理器
 */
public class InputHandler implements KeyListener {
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> justPressedKeys = new HashSet<>();
    private final Set<Integer> justReleasedKeys = new HashSet<>();
    
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (!pressedKeys.contains(code)) {
            justPressedKeys.add(code);
        }
        pressedKeys.add(code);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        pressedKeys.remove(code);
        justReleasedKeys.add(code);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // 不处理
    }
    
    /**
     * 检查按键是否正在被按住
     */
    public boolean isKeyDown(int keyCode) {
        return pressedKeys.contains(keyCode);
    }
    
    /**
     * 检查按键是否刚刚被按下（只触发一次）
     */
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }
    
    /**
     * 检查按键是否刚刚被释放
     */
    public boolean isKeyJustReleased(int keyCode) {
        return justReleasedKeys.contains(keyCode);
    }
    
    /**
     * 每帧结束时清除一次性按键状态
     */
    public void update() {
        justPressedKeys.clear();
        justReleasedKeys.clear();
    }
    
    /**
     * 获取玩家1方向输入
     */
    public int[] getPlayer1Direction() {
        int dx = 0, dy = 0;
        if (isKeyDown(KeyEvent.VK_W) || isKeyDown(KeyEvent.VK_UP)) dy = -1;
        if (isKeyDown(KeyEvent.VK_S) || isKeyDown(KeyEvent.VK_DOWN)) dy = 1;
        if (isKeyDown(KeyEvent.VK_A) || isKeyDown(KeyEvent.VK_LEFT)) dx = -1;
        if (isKeyDown(KeyEvent.VK_D) || isKeyDown(KeyEvent.VK_RIGHT)) dx = 1;
        return new int[]{dx, dy};
    }
    
    /**
     * 获取玩家2方向输入（IJKL）
     */
    public int[] getPlayer2Direction() {
        int dx = 0, dy = 0;
        if (isKeyDown(KeyEvent.VK_I)) dy = -1;
        if (isKeyDown(KeyEvent.VK_K)) dy = 1;
        if (isKeyDown(KeyEvent.VK_J)) dx = -1;
        if (isKeyDown(KeyEvent.VK_L)) dx = 1;
        return new int[]{dx, dy};
    }
    
    /**
     * 玩家1射击键
     */
    public boolean isPlayer1Shooting() {
        return isKeyDown(KeyEvent.VK_SPACE);
    }
    
    /**
     * 玩家2射击键
     */
    public boolean isPlayer2Shooting() {
        return isKeyDown(KeyEvent.VK_ENTER);
    }
    
    /**
     * 玩家1技能1（相位移动）
     */
    public boolean isPlayer1Skill1() {
        return isKeyJustPressed(KeyEvent.VK_Q);
    }
    
    /**
     * 玩家1技能2（电磁脉冲）
     */
    public boolean isPlayer1Skill2() {
        return isKeyJustPressed(KeyEvent.VK_E);
    }
    
    /**
     * 切换武器
     */
    public boolean isSwitchWeapon() {
        return isKeyJustPressed(KeyEvent.VK_TAB);
    }
    
    /**
     * 暂停
     */
    public boolean isPausePressed() {
        return isKeyJustPressed(KeyEvent.VK_ESCAPE) || isKeyJustPressed(KeyEvent.VK_P);
    }
    
    /**
     * 确认
     */
    public boolean isConfirmPressed() {
        return isKeyJustPressed(KeyEvent.VK_ENTER) || isKeyJustPressed(KeyEvent.VK_SPACE);
    }
    
    /**
     * 数字键1-3（用于选择）
     */
    public int getNumberPressed() {
        for (int i = 1; i <= 9; i++) {
            if (isKeyJustPressed(KeyEvent.VK_0 + i)) {
                return i;
            }
        }
        return -1;
    }
}
