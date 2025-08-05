package com.pinkcandy;

import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;


// 小蓝狗周周
// TODO 冲刺 专属选项面板
public class Pet_SkyblueHoundZhou extends PetBase {
    public Pet_SkyblueHoundZhou(Launcher theLauncher){
        super(theLauncher);
        this.moveSpeed = (int)(GUtil.DEFAULT_bodySize.getWidth()/8);
        this.followDistanse = (int)(GUtil.DEFAULT_bodySize.getWidth()*1.2);
        this.emotionThreshold = 60;
        this.moveThreshold = 60;
    }
}
