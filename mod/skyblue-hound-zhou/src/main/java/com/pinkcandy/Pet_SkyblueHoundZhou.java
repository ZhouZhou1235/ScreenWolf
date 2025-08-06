package com.pinkcandy;

import java.awt.Color;
import java.io.IOException;


import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.windows.PetOption;


// 小蓝狗周周
public class Pet_SkyblueHoundZhou extends PetBase {
    public Pet_SkyblueHoundZhou(Launcher theLauncher){
        super(theLauncher);
        this.moveSpeed = (int)(GUtil.DEFAULT_bodySize.getWidth()/8);
        this.followDistanse = (int)(GUtil.DEFAULT_bodySize.getWidth()*1.2);
        this.emotionThreshold = 60;
        this.moveThreshold = 60;
    }
    private class MyOption extends PetOption {
        public MyOption(PetBase pet){
            super(pet);
            this.buttonsPerRow = 3;
            this.setBackground(new Color(173,216,230,200));
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
        this.petOption.loadButtonsToPanel();
    }
}
