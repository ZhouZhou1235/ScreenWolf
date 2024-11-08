package pinkcandy.screenwolf.core.items;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pinkcandy.screenwolf.GTools;

/**
 * 食物
 */
public class Food extends JPanel {
    public String foodName; // 食物名
    JLabel texture; // 贴图
    public int foodSize; //食物贴图大小
    public Food(String name,String url,int size,Point pos){
        foodName = name;
        ImageIcon image = GTools.scaleImageIcon(new ImageIcon(url),size);
        texture = new JLabel(image);
        foodSize = size;
        setLocation(pos);
        setSize(size,size);
        setBackground(new Color(0,0,0,0));
        add(texture);
        setVisible(true);
        this.addMouseMotionListener(new MouseMotionAdapter(){
            @Override public void mouseDragged(MouseEvent e){
                setPosToMouse();
            }
        });
    }
    // 设置食物位置到鼠标
    private void setPosToMouse(){
        Point thePoint = MouseInfo.getPointerInfo().getLocation();
        thePoint.x-=foodSize/2;thePoint.y-=foodSize/2;
        this.setLocation(thePoint);
    }
    // 吃掉食物
    public void eatIt(){setVisible(false);System.gc();}
}
