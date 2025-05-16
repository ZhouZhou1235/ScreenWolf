package code.wolves;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Robot;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import code.GArea;
import code.GAreaClass;
import code.core.Tools;
import code.core.Wolf;
import code.core.WolfAnimation;

/**
 * WolfZhou
 * 小蓝狗
 */
public class WolfZhou extends Wolf {
    // 小蓝狗的属性
    Robot robot;
    public WolfZhou(){
        Map<String,String> theData = new HashMap<>();
        theData.put("name","zhou");
        theData.put("act_default","assets/images/wolves/zhou/default");
        theData.put("act_focus","assets/images/wolves/zhou/focus");
        theData.put("act_move","assets/images/wolves/zhou/move");
        theData.put("act_press","assets/images/wolves/zhou/press");
        theData.put("act_rest","assets/images/wolves/zhou/rest");
        theData.put("act_touch","assets/images/wolves/zhou/touch");
        theData.put("actZhou_listenMusic","assets/images/wolves/zhou/listenMusic");
        create(theData);
    }
    // 小蓝狗的重载方法
    @Override public void create(Map<String,String> theData){
        GAreaClass gAreaClass = new GAreaClass();
        data = theData;
        bodyWidth = gAreaClass.BODY_WIDTH;
        bodyHeight = gAreaClass.BODY_HEIGHT;
        stateNum = GArea.STATE_normal;
        speed = 5;
        canMove = true;
        moving = false;
        dragged = false;
        touched = false;
        touchNum = 0;
        autoTimer = new Timer();
        body = new WolfAnimation();
        setAction(GArea.ACT_default);
        try {robot = new Robot();}
        catch(AWTException e){System.out.println(e);}
        activeEvent();
        int x = gAreaClass.SCREEN_WIDTH/2 - bodyWidth/2;
        int y = gAreaClass.SCREEN_HEIGHT/2 - bodyHeight/2;
        setLocation(x,y);
        setSize(bodyWidth,bodyHeight);
        setBackground(new Color(0,0,0,0));
        add(body);
    }
    @Override public void resetWolf(){
        System.out.println("resetWolf");
        GAreaClass gAreaClass = new GAreaClass();
        setAction(GArea.ACT_default);
        bodyWidth = gAreaClass.BODY_WIDTH;
        bodyHeight = gAreaClass.BODY_HEIGHT;
        stateNum = GArea.STATE_normal;
        canMove = true;
        moving = false;
        dragged = false;
        touched = false;
        touchNum = 0;
        autoTimer.cancel();autoTimer=new Timer();
        activeEvent();
    }
    @Override public void activeEvent(){
        TimerTask move = new TimerTask(){ // 移动
            @Override public void run(){if(Tools.randomTodo(4)){move(Tools.getRandomPoint());}}
        };
        TimerTask listenMusic = new TimerTask(){ // 听音乐
            @Override public void run(){if(Tools.randomTodo(6)){listenMusic();}}
        };
        TimerTask dragMouse = new TimerTask(){ // 拖拽鼠标
            @Override public void run(){if(Tools.randomTodo(6)){randomDragMouse();}}
        };
        autoTimer.scheduleAtFixedRate(move,100,5000);
        autoTimer.scheduleAtFixedRate(listenMusic,200,5000);
        autoTimer.scheduleAtFixedRate(dragMouse,300,10000);
    }
    @Override public void setAction(int action){
        String dirUrl = "";
        if(action==GArea.ACT_default){dirUrl=data.get("act_default");}
        else if(action==GArea.ACT_focus){dirUrl=data.get("act_focus");}
        else if(action==GArea.ACT_move){dirUrl=data.get("act_move");}
        else if(action==GArea.ACT_press){dirUrl=data.get("act_press");}
        else if(action==GArea.ACT_rest){dirUrl=data.get("act_rest");}
        else if(action==GArea.ACT_touch){dirUrl=data.get("act_touch");}
        else if(action==100){dirUrl=data.get("actZhou_listenMusic");}
        else{return;}
        body.stopAnimation();
        body.setAnimation(dirUrl);
        body.playAnimation();
    }
    @Override public void move(Point pos){
        if(!preDoAction()){return;}
        System.out.println("move");
        stateNum = GArea.STATE_doingAction;
        moving = true;
        setAction(GArea.ACT_move);
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){
                boolean x = gotoPosition(pos);
                if(x || dragged || !canMove){
                    moving = false;
                    stateNum = GArea.STATE_normal;
                    setAction(GArea.ACT_default);
                    autoTimer.cancel();autoTimer=new Timer();activeEvent();
                }
            }
        };
        autoTimer.scheduleAtFixedRate(timerTask,0,GArea.GAME_FREQUENY);
    }
    @Override public void rest(){
        canMove = false;
        autoTimer.cancel();
        autoTimer = new Timer();
        System.out.println("rest");
        stateNum = GArea.STATE_rest;
        setAction(GArea.ACT_rest);
    }
    @Override public void focusWolf(){
        canMove = false;
        setAction(GArea.ACT_focus);
    }
    @Override public void touchWolf(){
        if(touchNum<100){touchNum++;}
        else if(!touched){
            touched = true;
            canMove = false;
            setAction(GArea.ACT_touch);
            TimerTask timerTask = new TimerTask(){
                @Override public void run(){resetWolf();}
            };
            autoTimer.schedule(timerTask,5000);
        }
    }
    // 小蓝狗的方法
    // 活动事件 听音乐
    private void listenMusic(){
        if(!preDoAction()){return;}
        System.out.println("listenMusic");
        canMove = false;
        stateNum = GArea.STATE_doingAction;
        setAction(100);
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){
                canMove = true;
                stateNum = GArea.STATE_normal;
                setAction(GArea.ACT_default);
                autoTimer.cancel();autoTimer=new Timer();activeEvent();
            }
        };
        autoTimer.schedule(timerTask,10000);
    }
    // 活动事件 鼠标被小蓝狗拖拽
    private void randomDragMouse(){
        if(!preDoAction()){return;}
        System.out.println("randomDragMouse");
        moving = true;
        stateNum = GArea.STATE_doingAction;
        Point pos = Tools.getRandomPoint();
        setAction(GArea.ACT_move);
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){
                boolean x1 = gotoPosition(pos);
                mouseJumptoWolf();
                if(x1 || dragged){
                    moving = false;
                    stateNum = GArea.STATE_normal;
                    setAction(GArea.ACT_default);
                    autoTimer.cancel();autoTimer=new Timer();activeEvent();
                }
            }
        };
        autoTimer.scheduleAtFixedRate(timerTask,0,GArea.GAME_FREQUENY);
    }
    // 狼向pos位置移动speed像素
    private boolean gotoPosition(Point pos){
        Point wolfPos = this.getLocation();
        int differenceX = wolfPos.x-pos.x;
        int differenceY = wolfPos.y-pos.y;
        boolean done = true;
        if(Math.abs(differenceX)>speed){
            done = false;
            if(differenceX>0){wolfPos.x-=speed;}
            else if(differenceX<0){wolfPos.x+=speed;}
        }
        if(Math.abs(differenceY)>speed){
            done = false;
            if(differenceY>0){wolfPos.y-=speed;}
            else if(differenceY<0){wolfPos.y+=speed;}
        }
        this.setLocation(wolfPos);
        return done;
    }
    // 鼠标指到狼
    private void mouseJumptoWolf(){robot.mouseMove(this.getX()+bodyWidth/2,this.getY()+bodyHeight/2);}
    // 准备执行活动事件
    private boolean preDoAction(){
        if(!canMove || moving || dragged || touched){return false;}
        if(stateNum==GArea.STATE_doingAction){return false;}
        stateNum = GArea.STATE_doingAction;
        return true;
    }
}
