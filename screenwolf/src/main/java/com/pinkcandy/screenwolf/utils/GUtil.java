package com.pinkcandy.screenwolf.utils;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

// 全局通用类
public class GUtil {
    // 最大帧长度
    static public int GAME_maxFrameLength = 1024;
    // 渲染时钟间隔
    static public int GAME_renderTime = 32;
    // 高速循环间隔
    static public int GAME_updateTime = 16;
    // 低速循环间隔
    static public int GAME_slowUpdateTime = 1024;
    // 程序工作地址
    static public String GAME_workPath = System.getProperty("user.dir").replace("\\", "/");
    // 游戏数据保存位置
    static public String GAME_dataPath = GUtil.GAME_workPath+"/data/";
    // 桌宠资源保存位置
    static public String GAME_petsPath = GUtil.GAME_workPath+"/pets/";
    // 屏幕大小
    static public Dimension SCREEN_dimension = Toolkit.getDefaultToolkit().getScreenSize();
    // 默认动画播放间隔
    static public int DEFAULT_animationPlaySpeed = 96;
    // 默认窗口大小
    static public Dimension DEFAULT_windowSize = new Dimension(SCREEN_dimension.width/2,SCREEN_dimension.height/2);
    // 默认字体大小
    static public int DEFAULT_textSize = SCREEN_dimension.width/54;
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
    // 创建文件
    static public int createFile(String path){
        File file = new File(path);
        if(!file.exists()){
        try{
            file.createNewFile();
            return 1;
        }catch(IOException e){e.printStackTrace();}}
        return 0;
    }
    // 读取文件
    static public String readFile(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)),"UTF-8");
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
    // 在jar包中指定类 返回一个实例
    static public Object loadObjFromJarByClass(String jarPath,String jarClass,Object... constructorArgs){
        try{
            File jarFile = new File(jarPath);
            URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(jarClass);
            Class<?>[] paramTypes = new Class<?>[constructorArgs.length];
            for(int i=0;i<constructorArgs.length;i++){
                paramTypes[i] = constructorArgs[i].getClass();
            }
            Constructor<?> constructor = loadedClass.getDeclaredConstructor(paramTypes);
            Object instance = constructor.newInstance(constructorArgs);
            classLoader.close();
            return instance;
        }catch(Exception e){System.out.println(e);return null;}
    }
    // 构建游戏文件结构
    static public void initFileDirs(){
        File assetsDir = new File(GAME_workPath+"/assets/");
        File dataDir = new File(GAME_workPath+"/data/");
        File petsDir = new File(GAME_workPath+"/pets/");
        File[] fileList = {assetsDir,dataDir,petsDir};
        for(File file:fileList){if(!file.exists()){file.mkdir();}}
    }
    // awt 翻转图像
    static public Image flipImage(Image image){
        BufferedImage bufferedImage = new BufferedImage(
            image.getWidth(null), 
            image.getHeight(null), 
            BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image,0, 0,null);
        g2d.dispose();
        AffineTransform tx = AffineTransform.getScaleInstance(-1,1);
        tx.translate(-bufferedImage.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(bufferedImage, null);
    }
    // 全局字体
	static public void initGlobalFont(Font font){
		FontUIResource fontRes = new FontUIResource(font);
		for(Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements();){
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource){
                UIManager.put(key,fontRes);
            }
		}
	}
    // 随机在屏幕上取点
    static public Point getRandomPointOnScreen(){
        int width = SCREEN_dimension.width;
        int height = SCREEN_dimension.height;
        int w = (int)(width*Math.random());
        int h = (int)(height*Math.random());
        return new Point(w,h);
    }
}
