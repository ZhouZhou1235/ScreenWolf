package com.pinkcandy.screenwolfpet;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.GWorkArea;
import com.pinkcandy.screenwolf.base.PetBase;

// 宠物 小蓝狗
public class Zhou extends PetBase {
    private int followDistanse = 100;
    private int moveSpeed = 10;
    private boolean isFollow = false;
    private Timer myUpdateTimer;
    public Zhou(){
        super(
            GArea.DEFAULT_bodySize,
            GWorkArea.loadPetAnimationMap("ScreenWolf_Zhou.json")
        );
        this.addMouseListener(new MouseInputAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                int num = e.getClickCount();
                if(num>=2){isFollow = !isFollow;}
            }
        });
        myUpdateTimer = new Timer(GArea.GAME_renderTime,_->{
            followMouse();
        });
        myUpdateTimer.start();
    }
    @Override
    public void auto_playAnimations(){
        if(isFocus && !isPress){this.updateAnimationOnce("focus");}
        if(!isFocus && !isPress){this.updateAnimationOnce("default");}
        if(isPress){this.updateAnimationOnce("press");}
    }
    public void followMouse(){
        if(isFollow){
            Point mousePoint = GArea.getMousePoint();
            Point petPostion = this.getPetPosition();
            Point o = this.getLocation();
            double distanse = GArea.getDistanse2Point(mousePoint,petPostion);
            if(distanse>followDistanse){
                int moveX = petPostion.x-mousePoint.x;
                int moveY = petPostion.y-mousePoint.y;
                Point nextPoint = new Point(o);
                if(moveX>moveSpeed){nextPoint.x-=moveSpeed;}
                else if(moveX<-moveSpeed){nextPoint.x+=moveSpeed;}
                if(moveY>moveSpeed){nextPoint.y-=moveSpeed;}
                else if(moveY<-moveSpeed){nextPoint.y+=moveSpeed;}
                this.setLocation(nextPoint);
            }
        }
    }
}
