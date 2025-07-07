package com.pinkcandy.screenwolf;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import com.pinkcandy.screenwolf.base.ItemBase;

// 宠物消息气泡
public class PetMessageBubble extends ItemBase {
    private Timer autoHideTimer;
    private int displayTime = 5000;
    public PetMessageBubble(String text){
        super(text);
        getBody().setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,GArea.DEFAULT_textSize));
        getBody().setForeground(Color.WHITE);
        this.setBackground(new Color(0,0,0,100));
        this.setOpaque(true);
        setFontAdjustText(text);
        autoHideTimer = new Timer(displayTime,e->{
            if(this.getParent()!=null){this.getParent().remove(this);}
            autoHideTimer.stop();
        });
        autoHideTimer.setRepeats(false);
    }
    // 显示时间
    public void setDisplayTime(int milliseconds){
        this.displayTime = milliseconds;
        if(autoHideTimer!=null){autoHideTimer.setInitialDelay(milliseconds);}
    }
    @Override
    public void ready(){
        super.ready();
        PetMessageBubble bubble = this;
        // 鼠标悬停暂停消失
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent e){
                autoHideTimer.stop();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                autoHideTimer.restart();
            }
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getButton()==MouseEvent.BUTTON3){
                    if(bubble.getParent()!=null){bubble.getParent().remove(bubble);}
                    autoHideTimer.stop();
                }
            }
        });
    }
}
