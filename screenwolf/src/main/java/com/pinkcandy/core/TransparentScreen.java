package com.pinkcandy.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.Timer;

import com.pinkcandy.GArea;
import com.pinkcandy.core.base.PetBase;

// 屏幕透明窗体
public class TransparentScreen extends JFrame {
    private Timer renderTimer; // 渲染时钟
    private Point mousePoint; // 鼠标点
    private Point pressPetPoint; // 宠物点按处
    public TransparentScreen(Dimension size){
        renderTimer = new Timer(GArea.GAME_renderTime,_->{
            mousePoint = MouseInfo.getPointerInfo().getLocation();
            this.repaint();
            this.update(getGraphics());
        });
        renderTimer.start();
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
    }
    // 为桌宠添加鼠标事件回应
    public void petAddMouseActionEcho(PetBase petBase){
        petBase.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                pressPetPoint = e.getPoint();
            }
        });
        petBase.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                super.mouseDragged(e);
                Point petPosition = petBase.getLocation();
                int x = petPosition.x+e.getX()-pressPetPoint.x;
                int y = petPosition.y+e.getY()-pressPetPoint.y;
                petBase.setLocation(x,y);
            }
        });
    }
}
