package com.tankwar.system;

import com.tankwar.util.Constants;
import java.awt.*;

/**
 * 天气系统
 */
public class WeatherSystem {
    
    public enum WeatherType {
        CLEAR("晴朗", "正常战斗环境"),
        RAIN("暴雨", "移动变滑，可能产生积水"),
        SANDSTORM("沙尘暴", "持续轻微扣血");
        
        public final String name;
        public final String description;
        
        WeatherType(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
    
    private WeatherType currentWeather = WeatherType.CLEAR;
    private long weatherStartTime;
    private long weatherDuration = 30000;  // 30秒
    private boolean weatherEnabled = true;
    
    // 雨滴粒子
    private double[] rainX = new double[100];
    private double[] rainY = new double[100];
    private double[] rainSpeed = new double[100];
    
    // 沙尘粒子
    private double[] sandX = new double[50];
    private double[] sandY = new double[50];
    
    // 雾效果参数
    private int fogRadius = 150;
    
    public WeatherSystem() {
        initParticles();
        weatherStartTime = System.currentTimeMillis();
    }
    
    private void initParticles() {
        for (int i = 0; i < rainX.length; i++) {
            rainX[i] = Math.random() * Constants.GAME_WIDTH;
            rainY[i] = Math.random() * Constants.GAME_HEIGHT;
            rainSpeed[i] = 8 + Math.random() * 6;
        }
        
        for (int i = 0; i < sandX.length; i++) {
            sandX[i] = Math.random() * Constants.GAME_WIDTH;
            sandY[i] = Math.random() * Constants.GAME_HEIGHT;
        }
    }
    
    public void update() {
        if (!weatherEnabled) return;
        
        // 天气持续时间检查
        if (System.currentTimeMillis() - weatherStartTime > weatherDuration) {
            changeWeather();
        }
        
        // 更新粒子
        switch (currentWeather) {
            case RAIN:
                updateRain();
                break;
            case SANDSTORM:
                updateSand();
                break;
            default:
                break;
        }
    }
    
    private void updateRain() {
        for (int i = 0; i < rainX.length; i++) {
            rainY[i] += rainSpeed[i];
            rainX[i] += 2;  // 斜向下
            
            if (rainY[i] > Constants.GAME_HEIGHT) {
                rainY[i] = 0;
                rainX[i] = Math.random() * Constants.GAME_WIDTH;
            }
            if (rainX[i] > Constants.GAME_WIDTH) {
                rainX[i] = 0;
            }
        }
    }
    
    private void updateSand() {
        for (int i = 0; i < sandX.length; i++) {
            sandX[i] += 3 + Math.random() * 2;
            sandY[i] += Math.sin(System.currentTimeMillis() / 100.0 + i) * 0.5;
            
            if (sandX[i] > Constants.GAME_WIDTH) {
                sandX[i] = 0;
                sandY[i] = Math.random() * Constants.GAME_HEIGHT;
            }
        }
    }
    
    public void changeWeather() {
        WeatherType[] types = WeatherType.values();
        currentWeather = types[(int)(Math.random() * types.length)];
        weatherStartTime = System.currentTimeMillis();
        
        // 调整持续时间
        weatherDuration = 20000 + (long)(Math.random() * 20000);
    }
    
    public void setWeather(WeatherType type) {
        currentWeather = type;
        weatherStartTime = System.currentTimeMillis();
    }
    
    /**
     * 渲染天气效果（在所有实体之后调用）
     */
    public void render(Graphics2D g, double playerX, double playerY) {
        switch (currentWeather) {
            case RAIN:
                renderRain(g);
                break;
            case SANDSTORM:
                renderSandstorm(g);
                break;
            default:
                break;
        }
    }
    
    private void renderRain(Graphics2D g) {
        g.setColor(new Color(150, 200, 255, 150));
        g.setStroke(new BasicStroke(1));
        
        for (int i = 0; i < rainX.length; i++) {
            int x1 = (int)rainX[i];
            int y1 = (int)rainY[i];
            int x2 = x1 + 4;
            int y2 = y1 + 12;
            g.drawLine(x1, y1, x2, y2);
        }
        
        // 整体蓝色覆盖
        g.setColor(new Color(100, 150, 200, 30));
        g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
    }
    
    private void renderSandstorm(Graphics2D g) {
        // 沙尘颗粒
        g.setColor(new Color(200, 170, 100, 180));
        for (int i = 0; i < sandX.length; i++) {
            int size = 2 + (int)(Math.random() * 3);
            g.fillOval((int)sandX[i], (int)sandY[i], size, size);
        }
        
        // 整体黄色覆盖
        g.setColor(new Color(180, 150, 80, 60));
        g.fillRect(0, 0, Constants.GAME_WIDTH, Constants.GAME_HEIGHT);
    }
    
    /**
     * 获取移动速度修正（暴雨时滑动）
     */
    public double getSpeedModifier() {
        if (currentWeather == WeatherType.RAIN) {
            return 1.2;  // 滑动时略快但难以控制
        }
        return 1.0;
    }
    
    /**
     * 获取沙尘暴伤害
     */
    public int getSandstormDamage() {
        if (currentWeather == WeatherType.SANDSTORM) {
            return 1;  // 每次调用扣1点血
        }
        return 0;
    }
    
    public WeatherType getCurrentWeather() { return currentWeather; }
    public void setEnabled(boolean enabled) { this.weatherEnabled = enabled; }
    public boolean isEnabled() { return weatherEnabled; }
}
