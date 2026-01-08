package com.tankwar;

import com.tankwar.game.Game;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 坦克大战 - 创新版
 * 程序入口
 * 
 * @author TankWar Team
 * @version 1.0
 */
public class Main {
    
    public static void main(String[] args) {
        // 设置系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 使用默认外观
        }
        
        // 在EDT线程中启动游戏
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.start();
        });
    }
}
