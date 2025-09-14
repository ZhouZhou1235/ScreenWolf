package com.pinkcandy.screenwolf_zhou;

import javax.swing.Timer;
import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.SwingTimerUtil;


// 小蓝狗周周
public class Pet extends PetBase {
    private boolean isDashing = false;
    public Pet(Launcher theLauncher){
        super(theLauncher);
        this.moveSpeed = (int)(GUtil.DEFAULT_bodySize.getWidth()/8);
        this.followDistanse = (int)(GUtil.DEFAULT_bodySize.getWidth()*1.2);
        this.emotionThreshold = 60;
        this.moveThreshold = 60;
    }
    @Override
    public void ready_loadPlay(){
        super.ready_loadPlay();
        this.petOption = new MyOption(this);
    }
    // 冲刺
    public void snowDash(){
        if(playPetData.getAffectionLevel()<40){
            showMessage("等级过低");
            return;
        }
        if(isDashing){
            reduceAffectPoint(20);
            return;
        }
        addAffectPoint(10);
        isDashing = true;
        isFollow = true;
        moveSpeed *= 2;
        int dashTime = 2000;
        playTargetAnimation("dash",dashTime);
        final int snowCount = 10;
        final int interval = dashTime / snowCount;
        Timer snowTimer = SwingTimerUtil.scheduleLoop(interval,()->{
            new Snow(this);
        });
        SwingTimerUtil.schedule(dashTime,()->{
            isDashing = false;
            isFollow = false;
            moveSpeed /= 2;
            zeroingResponseNum();
            snowTimer.stop();
        });
    }
}
