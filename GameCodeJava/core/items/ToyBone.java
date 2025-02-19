package pinkcandy.screenwolf.core.items;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;

/**
 * 骨头玩具
 * 鼠标扔玩具 宠物狼捡玩具
 */
public class ToyBone extends JPanel {
    public JLabel texture; // 贴图
    public int speed; // 扔出速度
    public boolean moving; // 正在移动
    public boolean followMouse; // 是否捡起
    public Timer throwTimer; // 丢出计时器
    public Timer followTimer; // 捡起计时器
    public ToyBone(){
        new GTools();
        speed = GArea.WOLF_speed*3;
        moving = false;
        followMouse = false;
        throwTimer = new Timer();
        followTimer = new Timer();
        texture = new JLabel(GTools.scaleImageIcon(new ImageIcon(GArea.TOY_BONE),GTools.BODY_WIDTH/4));
        setLocation(MouseInfo.getPointerInfo().getLocation());
        setSize(GTools.BODY_WIDTH/4,GTools.BODY_HEIGHT/4);
        setBackground(new Color(0,0,0,0));
        add(texture);
        setVisible(true);
    }
    // 骨头玩具移动
    public void toyMove(Point pos){
        if(moving){return;}
        followMouse = false;
        moving=true;
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){
                boolean x = gotoPosition(pos);
                if(x || !moving || followMouse){
                    moving = false;
                    followMouse = false;
                    throwTimer.cancel();throwTimer=new Timer();
                }
            }
        };
        throwTimer.scheduleAtFixedRate(timerTask,0,GArea.GAME_FREQUENY);
    }
    // 骨头玩具向pos位置移动speed像素
    private boolean gotoPosition(Point pos){
        Point toyPos = this.getLocation();
        int differenceX = toyPos.x-pos.x;
        int differenceY = toyPos.y-pos.y;
        boolean done = true;
        if(Math.abs(differenceX)>speed){
            done = false;
            if(differenceX>0){toyPos.x-=speed;}
            else if(differenceX<0){toyPos.x+=speed;}
        }
        if(Math.abs(differenceY)>speed){
            done = false;
            if(differenceY>0){toyPos.y-=speed;}
            else if(differenceY<0){toyPos.y+=speed;}
        }
        this.setLocation(toyPos);
        return done;
    }
    // 跟随鼠标
    public void toyFollowMouse(){
        followMouse = true;
        moving = false;
        followTimer.cancel();followTimer=new Timer();
        TimerTask task = new TimerTask(){
            @Override public void run(){
                setToyPosToMouse();
            }
        };
        followTimer.scheduleAtFixedRate(task,0,GArea.GAME_FREQUENY);
    }
    // 停止跟随鼠标
    public void toyStopFollow(){
        followMouse = false;
        followTimer.cancel();followTimer=new Timer();
    }
    // 设置玩具位置到鼠标
    public void setToyPosToMouse(){
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        Point pos = new Point(
            mousePos.x-GTools.BODY_WIDTH/8,
            mousePos.y-GTools.BODY_HEIGHT/8
        );
        this.setLocation(pos);
    }
}
