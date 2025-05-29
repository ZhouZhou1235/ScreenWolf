package com.pinkcandy.screenwolfpet;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.GWorkArea;
import com.pinkcandy.screenwolf.base.PetBase;

// 宠物 土豆
public class TudouUmbreon extends PetBase {
    public TudouUmbreon(){
        super(
            GArea.DEFAULT_bodySize,
            GWorkArea.loadPetAnimationMap("ScreenWolf_tudouUmbreon.json")
        );
    }
    @Override
    public void auto_playAnimations(){
        if(isFocus && !isPress){this.updateAnimationOnce("focus");}
        if(!isFocus && !isPress){this.updateAnimationOnce("default");}
        if(isPress){this.updateAnimationOnce("press");}
    }
}
