package com.pinkcandy.screenwolf.base;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pinkcandy.screenwolf.GArea;

// 物体基类
public class ItemBase extends JPanel {
    private String itemName; // 物体名称
    private Dimension size; // 物体大小
    private JLabel body; // 物体贴图
    public ItemBase(){
        size = GArea.DEFAULT_bodySize;
        body = new JLabel("ItemBase");
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
    }
    public ItemBase(String theItemName,ImageIcon imageIcon){
        itemName = theItemName;
        size = GArea.DEFAULT_bodySize;
        body = new JLabel(imageIcon);
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
    }
    public ItemBase(String theItemName,ImageIcon imageIcon,Dimension theSize){
        itemName = theItemName;
        size = theSize;
        imageIcon = GArea.scaleImageIcon(imageIcon,theSize.width);
        body = new JLabel(imageIcon);
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
    }
    public String getItmeName(){return itemName;}
}
