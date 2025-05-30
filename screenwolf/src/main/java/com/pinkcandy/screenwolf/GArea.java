package com.pinkcandy.screenwolf;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.alibaba.fastjson.JSON;

// 全局通用类
public class GArea {
    // 动画播放间隔
    static public int DEFAULT_animationPlaySpeed = 96;
    // 最大帧长度
    static public int GAME_maxFrameLength = 1024;
    // 渲染时钟间隔
    static public int GAME_renderTime = 32;
    // 程序工作地址
    static public String GAME_workPath = System.getProperty("user.dir");
    // 屏幕大小
    static public Dimension SCREEN_dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // 默认宠物大小
    static public Dimension DEFAULT_bodySize = new Dimension((int)(SCREEN_dimension.getWidth()/10),(int)(SCREEN_dimension.getWidth()/10));
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
    // 编码json
    static public String jsonEncode(Object object){
        return JSON.toJSONString(object);
    }
    // 读取文件
    static public String readFile(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }
    }
    // 写入文件
    static public void saveToFile(String path,String content){
        File file = new File(path);
        try {
            if(!file.exists()){file.createNewFile();}
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    // 获取鼠标在屏幕位置
    static public Point getMousePoint(){return MouseInfo.getPointerInfo().getLocation();}
    // 获取两点之间距离
    static public double getDistanse2Point(Point point1,Point point2){
        double x = Math.abs(point1.x-point2.x);
        double y = Math.abs(point1.y-point2.y);
        double distanse = Math.pow(Math.pow(x,2)+Math.pow(y,2),0.5);
        return distanse;
    }
}
