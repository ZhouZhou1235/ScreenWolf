package com.pinkcandy.core.base;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JPanel;
import com.pinkcandy.core.AnimationSprite;

// 桌面宠物
public class PetBase extends JPanel {
    private AnimationSprite body;
    private Map<String,String> animations;
    public PetBase(Dimension size,Map<String,String> animations){
        body = new AnimationSprite(size,animations);
        this.animations = animations;
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
    };
    public void updateBodyAnimation(String animationName){
        if(animations.get(animationName)==null){return;}
        body.setAnimation(animationName);
    }
}
