package com.pinkcandy.screenwolf.base;

import java.awt.Dimension;
import javax.swing.JFrame;

// 窗口
public class WindowBase extends JFrame {
    public WindowBase(){
        this.setTitle("window");
        this.setSize(new Dimension(256,256));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public WindowBase(String title,Dimension size){
        this.setTitle(title);
        this.setSize(size);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    // 刷新窗口
    public void updateWindow(){
        this.revalidate();
        this.update(getGraphics());
        this.repaint();
    }
}
