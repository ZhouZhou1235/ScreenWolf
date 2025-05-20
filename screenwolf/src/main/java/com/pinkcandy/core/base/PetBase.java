package com.pinkcandy.core.base;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import com.pinkcandy.core.AnimationSprite;

// 桌面宠物
public class PetBase extends JPanel {
    private AnimationSprite body = new AnimationSprite();
    public PetBase(Dimension size){
        body.setSpriteSize(size);
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
    };
}
