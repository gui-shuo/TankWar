package com.tankwar.entity;

import java.awt.*;

/**
 * 爆炸特效
 */
public class Explosion extends Entity {
    private int frame = 0;
    private int maxFrames = 15;
    private int maxRadius;
    private Color[] colors;
    private long lastFrame;
    private int frameInterval = 50;  // 每帧间隔(ms)
    private boolean isBig;
    
    public Explosion(double x, double y, boolean big) {
        super(x, y, 0, 0);
        this.isBig = big;
        this.maxRadius = big ? 40 : 25;
        this.maxFrames = big ? 20 : 12;
        this.lastFrame = System.currentTimeMillis();
        
        if (big) {
            colors = new Color[]{
                new Color(255, 200, 50),
                new Color(255, 150, 30),
                new Color(255, 100, 20),
                new Color(200, 50, 10),
                new Color(100, 30, 10)
            };
        } else {
            colors = new Color[]{
                new Color(255, 255, 100),
                new Color(255, 200, 50),
                new Color(255, 100, 20),
                new Color(150, 50, 10)
            };
        }
    }
    
    @Override
    public void update(double deltaTime) {
        long now = System.currentTimeMillis();
        if (now - lastFrame >= frameInterval) {
            frame++;
            lastFrame = now;
            if (frame >= maxFrames) {
                destroy();
            }
        }
    }
    
    @Override
    public void render(Graphics2D g) {
        if (!alive) return;
        
        float progress = (float) frame / maxFrames;
        int colorIndex = Math.min((int)(progress * colors.length), colors.length - 1);
        
        // 扩散半径
        int radius = (int)(maxRadius * (0.3 + 0.7 * progress));
        
        // 透明度渐变
        float alpha = 1.0f - progress * 0.8f;
        
        // 绘制多层爆炸
        for (int i = 0; i < 3; i++) {
            int r = radius - i * (radius / 4);
            if (r > 0) {
                int ci = Math.max(0, colorIndex - i);
                Color c = colors[ci];
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 
                                   (int)(alpha * 255 * (1 - i * 0.2))));
                g.fillOval((int)x - r, (int)y - r, r * 2, r * 2);
            }
        }
        
        // 火花
        if (isBig && frame < maxFrames / 2) {
            g.setColor(new Color(255, 255, 200, (int)(alpha * 200)));
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4 + frame * 0.1;
                int sparkLen = (int)(radius * 0.8);
                int sx = (int)(x + Math.cos(angle) * radius * 0.6);
                int sy = (int)(y + Math.sin(angle) * radius * 0.6);
                int ex = (int)(x + Math.cos(angle) * (radius * 0.6 + sparkLen * (1 - progress)));
                int ey = (int)(y + Math.sin(angle) * (radius * 0.6 + sparkLen * (1 - progress)));
                g.drawLine(sx, sy, ex, ey);
            }
        }
    }
    
    public boolean isFinished() {
        return !alive;
    }
}
