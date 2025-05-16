package code.core;

import javax.swing.Timer;
import code.GAreaClass;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * WolfAnimation
 * 狼的帧动画
 */
public class WolfAnimation extends JLabel {
    int animateSpeed = 100;
    ImageIcon[] frames;
    int frameIndex;
    int frameCount;
    Timer playTimer;
    int bodyWidth;
    int bodyHeight;
    public WolfAnimation(){
        GAreaClass gAreaClass = new GAreaClass();
        bodyWidth = gAreaClass.BODY_WIDTH;
        bodyHeight = gAreaClass.BODY_HEIGHT;
        playTimer = new Timer(ABORT, null);
    }
    // 设置动画
    public void setAnimation(String dirUrl){
        setFrames(dirUrl);
        playTimer = new Timer(animateSpeed,e->{
            // 缩放图片
            ImageIcon image = Tools.scaleImageIcon(frames[frameIndex],bodyWidth);
            this.setIcon(image);
            frameIndex++;if(frameIndex>=frameCount){frameIndex=0;}
        });
    }
    // 开始循环动画
    public void playAnimation(){playTimer.start();}
    // 中止动画
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
