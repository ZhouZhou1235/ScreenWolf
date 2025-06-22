package com.pinkcandy;

// import java.awt.AWTException;
// import java.awt.Robot;
// import java.awt.event.InputEvent;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.base.PetBase;

// 宠物 小蓝狗
public class ScreenWolf_Zhou extends PetBase {
    // private Robot petRobot;
    public ScreenWolf_Zhou(){
        super(
            GArea.DEFAULT_bodySize,
            "ScreenWolf_Zhou"
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
