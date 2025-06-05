package com.pinkcandy.screenwolfpet;

// import java.awt.AWTException;
// import java.awt.Robot;
// import java.awt.event.InputEvent;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.GWorkArea;
import com.pinkcandy.screenwolf.base.PetBase;

// 宠物 小蓝狗
public class Zhou extends PetBase {
    // private Robot petRobot;
    public Zhou(){
        super(
            GArea.DEFAULT_bodySize,
            GWorkArea.loadPetData("ScreenWolf_Zhou.json")
        );
        // try{
        //     petRobot = new Robot();
        // }catch(AWTException e){e.printStackTrace();}
    }
    @Override
    public void auto_playAnimations(){
        if(isMoving){this.updateAnimationOnce("move");}
        else{
            if(isFocus && !isPress){this.updateAnimationOnce("focus");}
            if(!isFocus && !isPress){this.updateAnimationOnce("default");}
            if(isPress){this.updateAnimationOnce("press");}
        }
    }
}
