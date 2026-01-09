package com.tankwar.game;

import com.tankwar.input.InputHandler;
import com.tankwar.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 游戏主类 - 管理窗口和游戏循环
 */
public class Game extends JFrame {
    private GamePanel gamePanel;
    private InputHandler inputHandler;
    private Timer gameTimer;
    private boolean running = false;
    
    public Game() {
        initWindow();
        initComponents();
        initGameLoop();
    }
    
    private void initWindow() {
        setTitle(Constants.GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 窗口关闭时清理资源
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stop();
            }
        });
    }
    
    private void initComponents() {
        // 创建输入处理器
        inputHandler = new InputHandler();
        
        // 创建游戏面板
        gamePanel = new GamePanel(inputHandler);
        gamePanel.addKeyListener(inputHandler);
        
        // 添加到窗口
        add(gamePanel);
        pack();
        
        // 居中显示
        setLocationRelativeTo(null);
    }
    
    private void initGameLoop() {
        // 使用Swing Timer实现游戏循环（约60FPS）
        gameTimer = new Timer(Constants.FRAME_TIME, e -> {
            gamePanel.update();
            gamePanel.repaint();
        });
    }
    
    /**
     * 启动游戏
     */
    public void start() {
        if (!running) {
            running = true;
            setVisible(true);
            gamePanel.requestFocusInWindow();
            gameTimer.start();
            System.out.println("游戏已启动 - " + Constants.GAME_TITLE);
        }
    }
    
    /**
     * 停止游戏
     */
    public void stop() {
        if (running) {
            running = false;
            gameTimer.stop();
            System.out.println("游戏已停止");
        }
    }
    
    /**
     * 获取游戏面板
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    /**
     * 检查游戏是否在运行
     */
    public boolean isRunning() {
        return running;
    }
}
