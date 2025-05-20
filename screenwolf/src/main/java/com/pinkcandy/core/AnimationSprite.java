package com.pinkcandy.core;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import com.pinkcandy.GArea;

// 动画精灵
public class AnimationSprite extends JLabel {
    private Dimension size = GArea.DEFAULT_bodySize;
    private Timer playTimer = new Timer(GArea.DEFAULT_animationPlaySpeed,null);
    private ImageIcon[] frames = null;
    private int frameIndex = 0;
    private int frameLength = 0;
    // 播放动画
    public void playAnimation(){playTimer.start();}
    // 停止动画
    public void stopAnimation(){playTimer.stop();}
    // 设置动画
    public void setAnimation(String path){
        int index = 0;
        String[] imageFiles = GArea.scanDir(path);
        for(String file:imageFiles){
            String imagePath = path+file;
            frames[index] = GArea.scaleImageIcon(new ImageIcon(imagePath),size.width);
            index++;
        }
        frameIndex = 0;
        frameLength = frames.length;
        GArea.clearTimerListeners(playTimer);
        playTimer.addActionListener(_->{
            this.setIcon(frames[frameIndex]);
            frameIndex++;if(frameIndex>=frameLength){frameIndex=0;}
        });
    }
    // 设置动画精灵大小
    public void setSpriteSize(Dimension size){this.size=size;}
}
