package com.pinkcandy.screenwolfpet;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.GWorkArea;
import com.pinkcandy.screenwolf.base.PetBase;

// 宠物 小蓝狗
public class Zhou extends PetBase {
    public Zhou(){
        super(
            GArea.DEFAULT_bodySize,
            GWorkArea.loadPetAnimationMap("ScreenWolf_Zhou.json")
        );
    }
    @Override
    public void auto_playAnimations(){
        if(isFocus && !isPress){this.updateAnimationOnce("focus");}
        if(!isFocus && !isPress){this.updateAnimationOnce("default");}
        if(isPress){this.updateAnimationOnce("press");}
    }
}
