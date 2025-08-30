package com.pinkcandy.pet_skybluehound;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.utils.SwingTimerUtil;


// 冲刺的雪花
public class Snow extends ItemBase {
    private final Random random = new Random();
    private float xSpeed;
    private float ySpeed;
    private float currentX;
    private float currentY;
    private Timer moveTimer;
    public Snow(PetBase pet){
        super("");
        itemName = "Snow";
        canDrag = false;
        // 随机大小
        int randomSizeNum = 3 + random.nextInt(3);
        Dimension theSize = new Dimension(
            (int)(GUtil.DEFAULT_bodySize.width/randomSizeNum),
            (int)(GUtil.DEFAULT_bodySize.width/randomSizeNum)
        );
        // 加载图像
        ImageIcon imageIcon = new ImageIcon();
        try{
            imageIcon = GUtil.scaleImageIcon(
                GUtil.createImageIconFromBytes(
                    JarFileUtil.readByteInJarFile(
                        GUtil.GAME_petsPath+JarFileUtil.getCurrentJarName(this),
                        "assets/images/button_dash.png"
                    )
                ),
                theSize.width
            );
        }catch(IOException e){e.printStackTrace();}
        // 设置UI
        body = new JLabel(imageIcon);
        this.setSize(theSize);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        ready();
        // 初始位置
        Point petPos = pet.getPetPosition();
        int offsetX = -theSize.width/2 + random.nextInt(theSize.width);
        int offsetY = -theSize.height/2 + random.nextInt(theSize.height);
        this.setLocation(petPos.x + offsetX, petPos.y + offsetY);
        currentX = getX();
        currentY = getY();
        // 随机速度
        xSpeed = (random.nextFloat()-0.5f)*2.5f;
        ySpeed = 1.5f + random.nextFloat();
        // 添加到屏幕
        pet.getLauncher().getScreen().add(this);
        this.revalidate();
        this.repaint();
        // 飘落动画
        moveTimer = SwingTimerUtil.scheduleLoop(GUtil.GAME_renderTime,()->{
            currentX += xSpeed;
            currentY += ySpeed;
            setLocation((int)currentX,(int)currentY);
        });
        // 自动移除
        SwingTimerUtil.schedule(2000 + random.nextInt(2000),()->{
            pet.getLauncher().getScreen().remove(this);
            if(moveTimer!=null){
                moveTimer.stop();
                moveTimer = null;
            }
        });
    }
}
