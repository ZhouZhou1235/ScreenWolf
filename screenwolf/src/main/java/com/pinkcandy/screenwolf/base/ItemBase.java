package com.pinkcandy.screenwolf.base;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import com.pinkcandy.screenwolf.GArea;

// 物体基类
public class ItemBase extends JPanel {
    private String itemName;
    private Dimension size;
    private JLabel body;
    private Timer updateTimer;
    private Point pressPoint = new Point();
    public boolean isPress = false;
    public boolean canDrag = true;
    public ItemBase(){
        size = GArea.DEFAULT_bodySize;
        body = new JLabel("ItemBase");
        initItem();
    }
    public ItemBase(String text){
        size = GArea.DEFAULT_bodySize;
        body = new JLabel(text);
        initItem();
    }
    public ItemBase(String theItemName,ImageIcon imageIcon){
        itemName = theItemName;
        size = new Dimension(imageIcon.getIconWidth(),imageIcon.getIconHeight());
        body = new JLabel(imageIcon);
        initItem();
    }
    public ItemBase(String theItemName,ImageIcon imageIcon,Dimension theSize){
        itemName = theItemName;
        size = theSize;
        imageIcon = GArea.scaleImageIcon(imageIcon,theSize.width);
        body = new JLabel(imageIcon);
        initItem();
    }
    // 初始化物体
    public void initItem(){
        updateTimer = new Timer(GArea.GAME_updateTime,null);
        updateTimer.start();
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        ready();
    }
    // 获取物体名称
    public String getItmeName(){return itemName;}
    // 就绪时调用
    public void ready(){
        ItemBase itemBase = this;
        itemBase.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                if(e.getButton()==MouseEvent.BUTTON1){
                    if(canDrag){
                        isPress = true;
                        pressPoint = e.getPoint();
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                if(e.getButton()==MouseEvent.BUTTON1){
                    if(canDrag){
                        isPress = false;
                    }
                }
            }
        });
        itemBase.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                super.mouseDragged(e);
                if(isPress && canDrag){
                    Point position = itemBase.getLocation();
                    int x = position.x+e.getX()-pressPoint.x;
                    int y =position.y+e.getY()-pressPoint.y;
                    itemBase.setLocation(x,y);
                }
            }
        });
    }
}
