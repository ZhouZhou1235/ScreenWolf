package com.pinkcandy.screenwolf.base;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.AnimationSprite;
import com.pinkcandy.screenwolf.GArea;

// 桌面宠物
public class PetBase extends JPanel {
    private Map<String,String> animations; // 动画数据
    private AnimationSprite body; // 动画精灵
    private Timer animationUpdateTimer; // 自动动画更新计时器
    public PetBase(Dimension size,Map<String,String> animations){
        this.body = new AnimationSprite(size,animations);
        this.animations = animations;
        this.animationUpdateTimer = new Timer(GArea.GAME_renderTime,_->{
            this.auto_playAnimations();
        });
        this.animationUpdateTimer.start();
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
    }
    // 切换动画
    public void updateBodyAnimation(String animationName){
        if(animations.get(animationName)==null){return;}
        body.setAnimation(animationName);
    }
    // 载入新的动画数据
    public void setAnimations(Map<String,String> animations){
        this.animations = animations;
        this.body = new AnimationSprite(this.getSize(),animations);
    }
    // 获取播放的动画名称
    public String getAnimationName(){return this.body.animationName;}
    // 改变一次到指定动画
    public void updateAnimationOnce(String animationName){
        String theAnimationName = this.getAnimationName();
        if(theAnimationName!=animationName){this.updateBodyAnimation(animationName);}
    }
    // 重写 自动播放动画
    public void auto_playAnimations(){}
}
