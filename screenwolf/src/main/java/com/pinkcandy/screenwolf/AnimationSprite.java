package com.pinkcandy.screenwolf;

import java.awt.Dimension;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;

import java.util.HashMap;

// 动画精灵类
public class AnimationSprite extends JLabel {
    private ImageIcon[] frames;
    private int frameIndex;
    private int frameCount;
    private Timer animationTimer;
    private String jarPath;
    public Map<String,String> animations;
    public boolean flip_h;
    public String currentAnimation;
    public AnimationSprite(Dimension size,Map<String,String> animations,String theJarPath){
        this.animations = animations;
        this.frames = new ImageIcon[GUtil.GAME_maxFrameLength];
        this.animationTimer = new Timer(GUtil.DEFAULT_animationPlaySpeed,e->{
            if(frameCount>0){
                updateFrameDisplay();
                frameIndex=(frameIndex+1)%frameCount;
            }
        });
        this.jarPath = theJarPath;
        setSize(size);
        if(!animations.isEmpty()){
            setAndPlayAnimation(animations.keySet().iterator().next());
        }
    }
    public int getFrameCount(){return frameCount;}
    // 设置和播放动画
    public void setAndPlayAnimation(String animationName){
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
        playAnimation();
    }
    // 更新当前帧显示
    private void updateFrameDisplay(){
        ImageIcon currentFrame = frames[frameIndex];
        if(flip_h){currentFrame = new ImageIcon(GUtil.flipImage(currentFrame.getImage()));}
        setIcon(currentFrame);
    }
    // 开始播放
    public void playAnimation(){
        if(!animationTimer.isRunning()&&frameCount>0){
            animationTimer.start();
        }
    }
    // 停止播放
    public void stopAnimation(){
        if(animationTimer.isRunning()){
            animationTimer.stop();
        }
    }
}
