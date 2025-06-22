package com.pinkcandy;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.base.PetBase;

public class ScreenWolf_DianDian extends PetBase {
    public ScreenWolf_DianDian(){
        super(GArea.DEFAULT_bodySize,"ScreenWolf_DianDian");
    }
    @Override
    public void auto_playAnimations(){
        this.updateAnimationOnce("default");
    }
}
