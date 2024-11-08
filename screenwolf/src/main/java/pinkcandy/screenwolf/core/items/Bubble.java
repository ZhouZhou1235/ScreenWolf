package pinkcandy.screenwolf.core.items;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.Window;

/**
 * 泡泡
 */
public class Bubble extends JPanel {
    public JLabel texture; // 泡泡贴图
    public int bubbleSpeed; // 泡泡速度
    public boolean broken; // 是否被戳破
    public Window window; // 游戏窗口
    Timer moveTimer; // 漂浮计时器
    public Bubble(Window w,Point pos,int speed){
        window = w;
        bubbleSpeed = speed;
        broken = false;
        moveTimer = new Timer();
        new GTools();
        texture = new JLabel(GTools.scaleImageIcon(new ImageIcon(GArea.TOY_BUBBLE),GTools.BODY_WIDTH/2));
        setLocation(pos);
        setSize(GTools.BODY_WIDTH/2,GTools.BODY_HEIGHT/2);
        setBackground(new Color(0,0,0,0));
        add(texture);
        setVisible(true);
        bubbleFloatUp();
        bubbleListenMouse();
    }
    // 泡泡漂浮
    private void bubbleFloatUp(){
        Point pos = GTools.getRandomPointOverScreenUp(GTools.BODY_HEIGHT/2);
        TimerTask move = new TimerTask(){
            @Override public void run(){
                boolean x = gotoPosition(pos);
                if(x || broken){
                    if(broken){ // 计数
                        window.bubbleCount++;
                        if(window.bubbleCount>=20){
                            window.bubbleGameState = 1;
                            window.bubbleCount = 0;
                        } // 游戏成功
                    }
                    else{
                        window.bubbleGameState = 2;
                    } // 游戏失败
                    doBroken();
                }
            }
        };
        moveTimer.scheduleAtFixedRate(move,0,GArea.GAME_FREQUENY);
    }
    // 泡泡向pos位置移动speed像素
    private boolean gotoPosition(Point pos){
        Point toyPos = this.getLocation();
        int differenceX = toyPos.x-pos.x;
        int differenceY = toyPos.y-pos.y;
        boolean done = true;
        if(Math.abs(differenceX)>bubbleSpeed){
            done = false;
            if(differenceX>0){toyPos.x-=bubbleSpeed;}
            else if(differenceX<0){toyPos.x+=bubbleSpeed;}
        }
        if(Math.abs(differenceY)>bubbleSpeed){
            done = false;
            if(differenceY>0){toyPos.y-=bubbleSpeed;}
            else if(differenceY<0){toyPos.y+=bubbleSpeed;}
        }
        this.setLocation(toyPos);
        return done;
    }
    // 泡泡监听鼠标事件
    private void bubbleListenMouse(){
        this.addMouseListener((MouseListener) new MouseAdapter(){
            @Override public void mousePressed(MouseEvent e){
                broken = true;
            }
        });
    }
    // 戳破泡泡
    private void doBroken(){
        this.setVisible(false);
        moveTimer.cancel();
        System.gc();
    }
}
