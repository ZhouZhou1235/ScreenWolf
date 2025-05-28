package com.pinkcandy.pet;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pinkcandy.screenwolf.GWorkArea;
import com.pinkcandy.screenwolf.base.PetBase;

public class Zhou extends PetBase {
    private boolean isFocus = false; // 是否聚焦
    public Zhou(Dimension size){
        super(size,GWorkArea.loadPetAnimationMap("ScreenWolf_Zhou.json"));
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                super.mouseEntered(e);
                isFocus = true;
            }
            @Override
            public void mouseExited(MouseEvent e){
                super.mouseExited(e);
                isFocus = false;
            }  
        });
    }
    @Override
    public void auto_playAnimations(){
        if(isFocus){this.updateAnimationOnce("focus");}
        if(!isFocus){this.updateAnimationOnce("default");}
    }
}
