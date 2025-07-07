package com.pinkcandy.screenwolf.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pinkcandy.screenwolf.tools.Tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

// 物体基类
public class ItemBase extends JPanel {
    private String itemName;
    private JLabel body;
    private Point pressPoint = new Point();
    public boolean isPress = false;
    public boolean canDrag = true;
    public ItemBase(){this("ItemBase");}
    public ItemBase(String text){
        body = new JLabel(text);
        setFontAdjustText(text);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        ready();
    }
    public ItemBase(String theItemName,ImageIcon imageIcon){
        itemName = theItemName;
        body = new JLabel(imageIcon);
        this.setSize(new Dimension(imageIcon.getIconWidth(),imageIcon.getIconHeight()));
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        ready();
    }
    public ItemBase(String theItemName,ImageIcon imageIcon,Dimension theSize){
        itemName = theItemName;
        imageIcon = Tool.scaleImageIcon(imageIcon,theSize.width);
        body = new JLabel(imageIcon);
        this.setSize(theSize);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        ready();
    }
    // 获取物体名称
    public String getItmeName(){return itemName;}
    // 获取标签组件
    public JLabel getBody(){return body;}
    // 设定组件大小适应文本
    public void setFontAdjustText(String text){
        FontMetrics fm = body.getFontMetrics(body.getFont());
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        this.setSize(new Dimension(textWidth,textHeight));
    }
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
