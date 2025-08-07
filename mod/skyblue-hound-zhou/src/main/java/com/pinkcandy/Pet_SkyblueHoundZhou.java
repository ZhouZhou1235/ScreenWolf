package com.pinkcandy;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.utils.SwingTimerUtil;
import com.pinkcandy.screenwolf.windows.PetOption;


// 小蓝狗周周
public class Pet_SkyblueHoundZhou extends PetBase {
    private boolean isDashing = false;
    public Pet_SkyblueHoundZhou(Launcher theLauncher){
        super(theLauncher);
        // 调整数值
        this.moveSpeed = (int)(GUtil.DEFAULT_bodySize.getWidth()/8);
        this.followDistanse = (int)(GUtil.DEFAULT_bodySize.getWidth()*1.2);
        this.emotionThreshold = 60;
        this.moveThreshold = 60;
    }
    // 蓝狗的面板
    private class MyOption extends PetOption {
        public MyOption(PetBase pet){
            super(pet);
            this.buttonsPerRow = 3;
            loadButtonsToPanel();
            adjustWindowSize();
            this.setBackground(new Color(173,216,230,200));
        }
        @Override
        protected void readyToPaint(){
            updateStatusText();
            setupDragBehavior();
            startStatusUpdate();
        }
        @Override
        public void loadButtonsToPanel(){
            super.loadButtonsToPanel();
            try{
                byte[] data = JarFileUtil.readByteInJarFile(
                    GUtil.GAME_petsPath+pet.getPetData().getJarName(),
                    "assets/images/button_dash.png"
                );
                this.addButton(
                    GUtil.createImageIconFromBytes(data),
                    "冲刺",
                    e->{
                        // TODO 冲刺技能
                        // 点击冲刺按钮蓝狗向鼠标方向冲刺，播放冲刺动画。轨迹生成雪花。
                        // 雪花随机大小生成，简单加个向下飘落的动画。
                        snowDash();
                    }
                );
            }
            catch(IOException e){System.err.println(e);}
        }
    }
    // 冲刺的雪花
    private class Snow extends ItemBase {
        private Snow(PetBase pet){
            itemName = "Snow";
            canDrag = false;
            Dimension theSize = new Dimension(
                (int)(GUtil.DEFAULT_bodySize.width/4),
                (int)(GUtil.DEFAULT_bodySize.width/4)
            );
            ImageIcon imageIcon;
            try {
                // TODO 解决问题
                // 这里报错，此处不能使用 petData，因为实例化未完成。
                imageIcon = GUtil.createImageIconFromBytes(
                    JarFileUtil.readByteInJarFile(
                        GUtil.GAME_petsPath+pet.getPetData().getJarName(),
                        "assets/images/button_dash.png"
                    )
                );
            }catch(IOException e){
                e.printStackTrace();
                imageIcon = new ImageIcon();
            }
            imageIcon = GUtil.scaleImageIcon(imageIcon,theSize.width);
            body = new JLabel(imageIcon);
            this.setSize(theSize);
            this.setBackground(new Color(0,0,0,0));
            this.setLocation(pet.getPetPosition());
            this.add(body);
            ready();
            SwingTimerUtil.schedule(2000,(Snow snow)->{
                snow.getParent().remove(snow);
            },this);
        }
    }
    // 冲刺
    public void snowDash(){
        if(isDashing){return;}
        isDashing = true;
        moveSpeed *= 2;
        int dashTime = 1000;
        playTargetAnimation("dash",dashTime);
        SwingTimerUtil.schedule(dashTime,()->{
            isDashing = false;
            moveSpeed /= 2;
        });
    }
    @Override
    public void ready_loadPlay(){
        super.ready_loadPlay();
        this.petOption = new MyOption(this);
    }
    @Override
    public void autoLoop(){
        super.autoLoop();
        if(isDashing){
            // 生成雪花
            new Snow(this);
        }
    }
}
