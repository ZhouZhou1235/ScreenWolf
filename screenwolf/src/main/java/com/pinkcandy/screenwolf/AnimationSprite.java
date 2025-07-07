package com.pinkcandy.screenwolf;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.tools.Tool;

// 动画精灵
public class AnimationSprite extends JLabel {
    private Dimension size = Tool.DEFAULT_bodySize;
    private Timer playTimer = new Timer(Tool.DEFAULT_animationPlaySpeed,null);
    private ImageIcon[] frames = new ImageIcon[Tool.GAME_maxFrameLength];
    private int frameIndex;
    private int frameLength;
    private Map<String,String> animations;
    public boolean filp_h = false;
    public String animationName;
    public AnimationSprite(Dimension size,Map<String,String> animations){
        this.animations = animations;
        this.size = size;
        if(animations!=null){
            Object[] animationKeys = animations.keySet().toArray();
            if(animationKeys.length>0){
                setAnimation(animationKeys[0].toString());
                playAnimation();
            }
        }
    }
    // 播放动画
    public void playAnimation(){playTimer.start();}
    // 停止动画
    public void stopAnimation(){playTimer.stop();}
    // 设置动画
    public void setAnimation(String animationName){
        String path = animations.get(animationName);if(path==null){return;}
        this.animationName = animationName;
        int index = 0;
        String[] imageFiles = Tool.scanDir(path);
        for(String file:imageFiles){
            String imagePath = path+file;
            ImageIcon image = new ImageIcon(imagePath);
            if(filp_h){image=new ImageIcon(Tool.flipImage(image.getImage()));}
            frames[index] = Tool.scaleImageIcon(image,size.width);
            index++;
        }
        frameIndex = 0;
        frameLength = imageFiles.length;
        Tool.clearTimerListeners(playTimer);
        playTimer.addActionListener(e->{
            ImageIcon imageIcon = frames[frameIndex];
            if(filp_h){imageIcon=new ImageIcon(Tool.flipImage(imageIcon.getImage()));}
            this.setIcon(imageIcon);
            frameIndex++;if(frameIndex>=frameLength){frameIndex=0;}
        });
    }
}
