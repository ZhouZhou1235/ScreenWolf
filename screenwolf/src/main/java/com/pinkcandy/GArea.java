package com.pinkcandy;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

// 游戏全局类
public class GArea {
    static public int DEFAULT_animationPlaySpeed = 100;
    static public Dimension DEFAULT_bodySize = new Dimension(256,256);
    static public Dimension SCREEN_dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // 扫描文件夹内容
    static public String[] scanDir(String path){return (new File(path)).list();}
    // swing 等比例缩放图片标签
    static public ImageIcon scaleImageIcon(ImageIcon icon,int l){
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(l,(int)l*h/w,Image.SCALE_DEFAULT);
        return new ImageIcon(newImage);
    }
    // swing 清除计时器的所有事件
    static public void clearTimerListeners(Timer timer){
        ActionListener[] listeners = timer.getActionListeners();
        for(ActionListener listener:listeners){
            timer.removeActionListener(listener);
        }
    }
    // swing 使窗体居中
    static public void setWindowCenter(JFrame jFrame){
        Dimension size = jFrame.getSize();
        int x = (SCREEN_dimension.width/2)-(size.width/2);
        int y = (SCREEN_dimension.height/2)-(size.height/2);
        jFrame.setLocation(x,y);
    }
}
