package com.pinkcandy.screenwolf.part;

import java.awt.Dimension;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;

import java.util.HashMap;


/**
 * 动画精灵
 * 播放序列帧动画的Swing标签
 */
public class AnimationSprite extends JLabel {
    private ImageIcon[] frames;
    private int frameIndex;
    private int frameCount;
    private String jarPath;
    private boolean isPlaying = true;
    public Map<String,String> animations;
    public boolean flip_h;
    public String currentAnimation;
    public AnimationSprite(Dimension size,Map<String,String> animations,String theJarPath){
        this.animations = animations;
        this.frames = new ImageIcon[GUtil.GAME_maxFrameLength];
        this.jarPath = theJarPath;
        setSize(size);
        if(!animations.isEmpty()){
            setAnimation(animations.keySet().iterator().next());
        }
    }
    public int getFrameCount(){return frameCount;}
    public boolean isPlaying(){return isPlaying;}
    public void setPlaying(boolean play){isPlaying=play;}
    // 设置和播放动画
    public void setAnimation(String animationName){
        if(animationName.equals(currentAnimation)){return;}
        String framePath = animations.get(animationName);
        if(framePath==null){return;}
        currentAnimation = animationName;
        frameIndex = 0;
        frameCount = 0;
        HashMap<String,byte[]> hashMap = JarFileUtil.listJarFileMapByPath(jarPath,framePath);
        for(String name:hashMap.keySet()){
            byte[] byteData = hashMap.get(name);
            ImageIcon frame = GUtil.scaleImageIcon(new ImageIcon(byteData),getSize().width);
            frames[frameIndex] = frame;
            frameIndex++;frameCount++;
        }
        frameIndex = 0;
    }
    // 更新当前帧显示
    public void updateFrameDisplay(){
        if(!isPlaying){return;}
        if(frameCount>0){
            ImageIcon currentFrame = frames[frameIndex];
            if(flip_h){currentFrame = new ImageIcon(GUtil.flipImage(currentFrame.getImage()));}
            setIcon(currentFrame);
            frameIndex=(frameIndex+1)%frameCount;
        }
    }
}
