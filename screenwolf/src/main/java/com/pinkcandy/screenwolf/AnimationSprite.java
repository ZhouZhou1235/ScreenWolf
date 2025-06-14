package com.pinkcandy.screenwolf;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

// 动画精灵
public class AnimationSprite extends JLabel {
    private Dimension size = GArea.DEFAULT_bodySize;
    private Timer playTimer = new Timer(GArea.DEFAULT_animationPlaySpeed,null);
    private ImageIcon[] frames = new ImageIcon[GArea.GAME_maxFrameLength];
    private int frameIndex;
    private int frameLength;
    private Map<String,String> animations;
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
        String[] imageFiles = GArea.scanDir(path);
        for(String file:imageFiles){
            String imagePath = path+file;
            frames[index] = GArea.scaleImageIcon(new ImageIcon(imagePath),size.width);
            index++;
        }
        frameIndex = 0;
        frameLength = imageFiles.length;
        GArea.clearTimerListeners(playTimer);
        playTimer.addActionListener(e->{
            this.setIcon(frames[frameIndex]);
            frameIndex++;if(frameIndex>=frameLength){frameIndex=0;}
        });
    }
}
