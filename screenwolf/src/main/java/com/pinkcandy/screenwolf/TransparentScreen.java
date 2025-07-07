package com.pinkcandy.screenwolf;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.utils.GUtil;

// 屏幕透明窗体
public class TransparentScreen extends JFrame {
    private Timer renderTimer; // 渲染时钟
    public TransparentScreen(Dimension size){
        this.setTitle("ScreenWolf TransparentScreen");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width,size.height);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
        this.setLayout(null);
        this.setType(JFrame.Type.UTILITY);
        this.setVisible(true);
        renderTimer = new Timer(GUtil.GAME_renderTime,e->{
            this.repaint();
            this.update(getGraphics());
        });
        renderTimer.start();
    }
}
