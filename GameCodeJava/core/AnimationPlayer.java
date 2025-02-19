package pinkcandy.screenwolf.core;

import javax.swing.Timer;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * 帧动画播放机
 * 指定帧文件夹取帧播放
 */
public class AnimationPlayer extends JLabel {
    int animateSpeed = GArea.GAME_ANIMATION;
    ImageIcon[] frames; // 储存帧的数组
    int frameIndex; // 帧索引
    int frameCount; // 帧数量
    Timer playTimer; // 播放计时器
    int width; // 播放画面宽
    int height; // 播放画面高
    public AnimationPlayer(int w,int h){
        width=w;height=h;
        playTimer = new Timer(ABORT, null);
    }
    // 设置动画
    public void setAnimation(String dirUrl){
        setFrames(dirUrl);
        playTimer = new Timer(animateSpeed,e->{
            // 缩放图片
            ImageIcon image = GTools.scaleImageIcon(frames[frameIndex],width);
            this.setIcon(image);
            frameIndex++;if(frameIndex>=frameCount){frameIndex=0;}
        });
    }
    // 开始循环动画
    public void playAnimation(){playTimer.start();}
    // 停止动画
    public void stopAnimation(){playTimer.stop();}
    // 设置动画帧
    private void setFrames(String dirUrl){
        playTimer = new Timer(ABORT,null);
        File dir = new File(dirUrl);
        frameIndex = 0;
        frameCount = dir.list().length;
        frames = new ImageIcon[frameCount];
        for(int i=0;i<frameCount;i++){
            String url = dirUrl+"/"+i+".png";
            ImageIcon image = new ImageIcon(url);
            frames[i] = image;
        }
    }
}
