package com.pinkcandy.pet_skybluehound;

import java.awt.Color;
import java.io.IOException;

import javax.swing.Timer;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.utils.SwingTimerUtil;
import com.pinkcandy.screenwolf.windows.PetOption;

// 小蓝狗周周
public class Pet_SkyblueHound extends PetBase {
    private boolean isDashing = false;
    public Pet_SkyblueHound(Launcher theLauncher){
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
                    e->{snowDash();}
                );
            }
            catch(IOException e){System.err.println(e);}
        }
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
        final int snowCount = 30;
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
    @Override
    public void ready_loadPlay(){
        super.ready_loadPlay();
        this.petOption = new MyOption(this);
    }
}
