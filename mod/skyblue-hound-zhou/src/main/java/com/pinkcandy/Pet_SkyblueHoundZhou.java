package com.pinkcandy;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;


import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.windows.PetOption;


// 小蓝狗周周
public class Pet_SkyblueHoundZhou extends PetBase {
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
            this.setBackground(new Color(173,216,230,200));
            this.setSize(new Dimension(
                (int)(GUtil.DEFAULT_bodySize.width*1.5),
                GUtil.DEFAULT_bodySize.height
            ));
        }
        @Override
        protected void readyToPaint(){
            updateStatusText();
            // loadButtonsToPanel();
            adjustWindowSize();
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
                    }
                );
            }
            catch(IOException e){}
        }
    }
    @Override
    public void ready_loadPlay(){
        super.ready_loadPlay();
        this.petOption = new MyOption(this);
    }
}
