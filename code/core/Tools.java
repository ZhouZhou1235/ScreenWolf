package code.core;

import java.awt.Image;
import java.awt.Point;
import java.util.Random;

import javax.swing.ImageIcon;

import code.GAreaClass;

/**
 * Tools
 * 工具类
 */
public class Tools {
    // 概率触发
    // num越大概率越小
    public static boolean randomTodo(int num){
        int x = (int)(Math.random()*num)+1;
        System.out.println(x);
        // Random random = new Random();
        // int x = random.nextInt(0,num);
        return x==1?true:false;
    }
    // 获取一个屏幕随机点
    public static Point getRandomPoint(){
        GAreaClass gAreaClass = new GAreaClass();
        Random random = new Random();
        int x = random.nextInt(gAreaClass.BODY_WIDTH,gAreaClass.SCREEN_WIDTH-gAreaClass.BODY_WIDTH);
        int y = random.nextInt(gAreaClass.BODY_HEIGHT,gAreaClass.SCREEN_HEIGHT-gAreaClass.BODY_HEIGHT);
        Point pos = new Point(x,y);
        return pos;
    }
    // 等比例缩放图片标签
    public static ImageIcon scaleImageIcon(ImageIcon icon,int l){
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(l,(int)l*h/w,Image.SCALE_DEFAULT);
        return new ImageIcon(newImage);
    }
}
