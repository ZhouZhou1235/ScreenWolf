package pinkcandy.screenwolf.core;

import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.items.Food;

import java.awt.Color;
import java.awt.MouseInfo;

/**
 * 原型狼
 * 此类是狼的定义
 * 具有桌面宠物的基础功能
 * 不应该实例化 因为没有具体的方法和资源
 */
public class Wolf extends JPanel {
    /**
     * 宠物狼的基本属性
     */
    // 固定数据
    public Map<String,String> data; // 狼数据
    public AnimationPlayer body; // 狼贴图动画
    public int role; // 角色
    public int bodyWidth; // 身体宽
    public int bodyHeight; // 身体高
    // 活动数据
    public int stateNum; // 状态数
    public int speed; // 速度
    public int favor; // 好感度
    public int clean; // 清洁度
    public int food; // 饱食度
    public int spirit; // 精力
    public int level; // 等级
    public int touchNum; // 抚摸值
    public int cleanNum; // 清洗值
    public ArrayList<Food> foodList; // 周围的食物数组
    // 布尔控制
    public boolean canMove; // 可移动
    public boolean moving; // 正在移动
    public boolean dragged; // 被拖拽
    public boolean touched; // 被抚摸
    public boolean cleaned; // 被清洗
    public boolean lowState; // 低状态
    public boolean followMouse; // 跟随
    // 自主活动
    public Timer actionTimer; // 动作计时器
    public Timer saveTimer; // 数据保存计时器
    public Timer lifeTimer; // 狼身体计时器
    public Timer restTimer; // 休息计时器
    public Timer eatTimer; // 进食计时器
    public Timer followTimer; // 跟随计时器
    public Robot robot; // 自动操控计算机
    /**
     * 宠物狼的基本方法
     */
    // 构造方法 生成狼
    public Wolf(Map<String,String> theData){
        new GTools();
        // 属性设置
        data = theData;
        body = new AnimationPlayer(GTools.BODY_WIDTH,GTools.BODY_HEIGHT);
        role = Integer.parseInt(data.get("role"));
        bodyWidth = GTools.BODY_WIDTH;
        bodyHeight = GTools.BODY_HEIGHT;
        stateNum = GArea.STATE_normal;
        speed = GArea.WOLF_speed;
        favor = Integer.parseInt(data.get("favor"));
        clean = Integer.parseInt(data.get("clean"));
        food = Integer.parseInt(data.get("food"));
        spirit = Integer.parseInt(data.get("spirit"));
        level = Integer.parseInt(data.get("level"));
        touchNum = 0;
        cleanNum = 0;
        foodList = new ArrayList<>();
        canMove = true;
        moving = false;
        dragged = false;
        touched = false;
        cleaned = false;
        lowState = false;
        followMouse = false;
        actionTimer = new Timer();
        saveTimer = new Timer();
        lifeTimer = new Timer();
        restTimer = new Timer();
        eatTimer = new Timer();
        followTimer = new Timer();
        try{robot = new Robot();}
        catch(Exception e){e.printStackTrace();}
        // 开始的动作
        setAction(GArea.ACT_default);
        startActiveEvent();
        setWolfLife();
        eatingFood();
        followMouseToPlay();
        TimerTask gamesave = new TimerTask(){@Override public void run(){saveDataToServer();}};
        saveTimer.scheduleAtFixedRate(gamesave,0,GArea.GAME_SAVE);
        // 窗体设置
        int x = GTools.SCREEN_WIDTH/2 - bodyWidth/2;
        int y = GTools.SCREEN_HEIGHT/2 - bodyHeight/2;
        setLocation(x,y);
        setSize(bodyWidth,bodyHeight);
        setBackground(new Color(0,0,0,0));
        add(body);
    };
    // 重置狼
    public void resetWolf(){
        stateNum = GArea.STATE_normal;
        speed = GArea.WOLF_speed;
        touchNum = 0;
        cleanNum = 0;
        // foodList = new ArrayList<>();
        canMove = true;
        moving = false;
        dragged = false;
        touched = false;
        cleaned = false;
        lowState = false;
        followMouse = false;
        try{robot = new Robot();}
        catch(Exception e){e.printStackTrace();}
        reloadWolfTimers();
        setAction(GArea.ACT_default);
    };
    // 移动
    public void wolfMove(Point pos){
        if(!isDefaultAction()){return;}
        if(spirit<GArea.WOLF_lowStateNum){return;}
        if(GTools.randomTodo(4)){spirit--;if(spirit<0){spirit=0;}}
        if(GTools.randomTodo(4)){clean--;if(clean<0){clean=0;}}
        moving = true;
        setAction(GArea.ACT_move);
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){
                boolean x = gotoPosition(pos);
                if(x || dragged || !canMove){
                    moving = false;
                    setAction(GArea.ACT_default);
                    stopActiveEvent();startActiveEvent();
                }
            }
        };
        actionTimer.scheduleAtFixedRate(timerTask,0,GArea.GAME_FREQUENY);
    };
    // 休息
    public void rest(){
        stateNum = GArea.STATE_rest;
        canMove = false;
        moving = false;
        dragged = false;
        touched = false;
        actionTimer.cancel();actionTimer=new Timer();
        lifeTimer.cancel();lifeTimer=new Timer();
        restTimer.cancel();restTimer=new Timer();
        TimerTask recover = new TimerTask(){
            @Override public void run(){
                food--;if(food<0){food=0;}
                spirit+=GArea.WOLF_addNum*2;if(spirit>GArea.WOLF_maxStateNum){spirit=GArea.WOLF_maxStateNum;resetWolf();}
            }
        };
        restTimer.scheduleAtFixedRate(recover,GArea.WOLF_lifePeriod,GArea.WOLF_lifePeriod/2);
        setAction(GArea.ACT_rest);
    };
    // 被聚焦
    public void focusWolf(){
        if(lowState){
            setAction(GArea.ACT_low);
            return;
        }
        canMove = false;
        setAction(GArea.ACT_focus);
    };
    // 被抚摸
    public void touchWolf(){
        if(lowState){return;}
        int num1 = GArea.WOLF_reachTouch; // 舒服阈值
        int num2 = GArea.WOLF_duration; // 持续时间
        if(touchNum<num1){touchNum++;}
        else if(!touched){
            favor+=GArea.WOLF_addNum;if(favor>GArea.WOLF_maxStateNum){favor=GArea.WOLF_maxStateNum;}
            touched = true;
            canMove = false;
            setAction(GArea.ACT_touch);
            TimerTask timerTask = new TimerTask(){
                @Override public void run(){resetWolf();}
            };
            actionTimer.schedule(timerTask,num2);
        }
    };
    // 被清洗
    public void cleanWolf(){
        int num1 = GArea.WOLF_reachTouch;
        int num2 = GArea.WOLF_duration*2;
        cleanNum++;
        if(cleanNum<num1){return;}
        if(cleanNum%GArea.WOLF_maxStateNum==0){
            // 洗澡加清洁和好感
            clean+=GArea.WOLF_addNum*2;
            favor+=GArea.WOLF_addNum;if(favor>GArea.WOLF_maxStateNum){favor=GArea.WOLF_maxStateNum;}
            if(clean>GArea.WOLF_maxStateNum){
                // 如果已经洗干净了还洗会烦 掉好感度
                clean=GArea.WOLF_maxStateNum;
                if(favor>GArea.WOLF_basicFavor){favor-=GArea.WOLF_addNum;}
            }
        }
        if(!cleaned){
            cleaned = true;
            setAction(GArea.ACT_clean);
            TimerTask timerTask = new TimerTask(){
                @Override public void run(){
                    cleaned = false;
                    cleanNum = 0;
                    setAction(GArea.ACT_default);
                }
            };
            actionTimer.schedule(timerTask,num2);
        }
    }
    // 设置动作
    public void setAction(int action){
        String url = "";
        switch(action){
            case GArea.ACT_default:url=data.get("ACT_default");break;
            case GArea.ACT_focus:url=data.get("ACT_focus");break;
            case GArea.ACT_move:url=data.get("ACT_move");break;
            case GArea.ACT_press:url=data.get("ACT_press");break;
            case GArea.ACT_rest:url=data.get("ACT_rest");break;
            case GArea.ACT_touch:url=data.get("ACT_touch");break;
            case GArea.ACT_clean:url=data.get("ACT_clean");break;
            case GArea.ACT_sad:url=data.get("ACT_sad");break;
            case GArea.ACT_dirty:url=data.get("ACT_dirty");break;
            case GArea.ACT_hungry:url=data.get("ACT_hungry");break;
            case GArea.ACT_tired:url=data.get("ACT_tired");break;
            case GArea.ACT_low:url=data.get("ACT_low");break;
            case GArea.ACT_eat:url=data.get("ACT_eat");break;
            case GArea.ACT_focusToy:url=data.get("ACT_focusToy");break;
            case GArea.ACT_playBubble:url=data.get("ACT_playBubble");break;
            case GArea.ACT_gameSuccess:url=data.get("ACT_gameSuccess");break;
            case GArea.ACT_gameFailed:url=data.get("ACT_gameFailed");break;
            case GArea.ACT_eatFull:url=data.get("ACT_eatFull");break;
            case GArea.ACT_followMouse:url=data.get("ACT_followMouse");break;
            case GArea.ACT_holdMouse:url=data.get("ACT_holdMouse");break;
            default:return;
        }
        if(body.playTimer.isRunning()){body.stopAnimation();}
        body.setAnimation(url);
        body.playAnimation();
    };
    // 开始活动事件 标记重载
    public void startActiveEvent(){
        TimerTask wolfMove = new TimerTask(){ // 自主的随机移动
            @Override public void run(){
                if(GTools.randomTodo(4) && preDoAction()){wolfMove(GTools.getRandomPoint());}
            }
        };
        actionTimer.scheduleAtFixedRate(wolfMove,GArea.WOLF_maxStateNum,GArea.WOLF_period);
    };
    // 停止活动事件
    public void stopActiveEvent(){actionTimer.cancel();actionTimer=new Timer();}
    // 恢复狼的所有计时器到构造时的状态
    public void reloadWolfTimers(){
        actionTimer.cancel();actionTimer=new Timer();
        lifeTimer.cancel();lifeTimer=new Timer();
        restTimer.cancel();restTimer=new Timer();
        eatTimer.cancel();eatTimer=new Timer();
        followTimer.cancel();followTimer=new Timer();
        startActiveEvent();
        setWolfLife();
        eatingFood();
        followMouseToPlay();
    }
    // 保存数据至服务器
    public void saveDataToServer(){
        String sessionID = GTools.getContentFromFile(GArea.GAME_USER+"PINKCANDY");
        Map<String,String> bodyMap = new HashMap<String,String>();
        bodyMap.put("action","saveDataToServer");
        bodyMap.put("PINKCANDY",sessionID);
        bodyMap.put("favor",String.valueOf(favor));
        bodyMap.put("clean",String.valueOf(clean));
        bodyMap.put("food",String.valueOf(food));
        bodyMap.put("spirit",String.valueOf(spirit));
        bodyMap.put("level",String.valueOf(level));
        GTools.postSender(GTools.GAME_NET,null,bodyMap);
    }
    // 得到狼的位置
    public Point getWolfLocation(){return this.getLocation();}
    // 设置狼的位置
    public void setWolfLocation(Point pos){this.setLocation(pos);}    
    // 狼向pos位置移动speed像素
    public boolean gotoPosition(Point pos){
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
    // 准备执行活动事件
    public boolean preDoAction(){
        if(
            stateNum!=GArea.STATE_normal ||
            !canMove ||
            moving ||
            dragged ||
            touched ||
            cleaned ||
            lowState ||
            followMouse
        ){return false;}
        else{return true;}
    }
    // 是否无动作状态
    public boolean isDefaultAction(){
        if(
            moving ||
            dragged ||
            touched ||
            cleaned ||
            followMouse
        ){return false;}
        else{return true;}
    }
    // 设置宠物狼身体运作
    public void setWolfLife(){
        // 饿
        TimerTask hunger = new TimerTask(){
            // 太饿将限制互动方法且掉好感度
            @Override public void run(){
                food--;if(food<0){food=0;}
                if(food<GArea.WOLF_lowStateNum){
                    favor--;if(favor<0){favor=0;}
                }
            }
        };
        // 疲倦
        TimerTask tired = new TimerTask(){
            // 太累将限制互动方法
            @Override public void run(){
                // 不是休息状态才会掉精力
                if(stateNum!=GArea.STATE_rest){
                    // 如果太累了会自己睡觉
                    spirit--;if(spirit<0){spirit=0;rest();}
                }
            }
        };
        // 变脏
        TimerTask dirty = new TimerTask(){
            // 变得太脏会掉好感度
            @Override public void run(){
                clean--;if(clean<0){clean=0;}
                if(clean<GArea.WOLF_lowStateNum){
                    favor--;if(favor<0){favor=0;}
                }
            }
        };
        // 好感度自动降低
        TimerTask falldown = new TimerTask(){
            // 下降到水平值停止
            @Override public void run(){if(favor>GArea.WOLF_basicFavor){favor--;}}
        };
        // 数值过低做出反应动作
        TimerTask lowStateAct = new TimerTask(){
            @Override public void run(){lowStateAction();}
        };
        lifeTimer.scheduleAtFixedRate(hunger,GArea.WOLF_lifePeriod,GArea.WOLF_lifePeriod);
        lifeTimer.scheduleAtFixedRate(tired,GArea.WOLF_lifePeriod,GArea.WOLF_lifePeriod);
        lifeTimer.scheduleAtFixedRate(dirty,GArea.WOLF_lifePeriod,GArea.WOLF_lifePeriod);
        lifeTimer.scheduleAtFixedRate(falldown,GArea.WOLF_lifePeriod,GArea.WOLF_lifePeriod);
        lifeTimer.scheduleAtFixedRate(lowStateAct,0,GArea.WOLF_lifePeriod/10);
    }
    // 身体数值过低做出的动作
    public void lowStateAction(){
        // 是否低状态
        if(
            favor<GArea.WOLF_lowStateNum ||
            clean<GArea.WOLF_lowStateNum ||
            food<GArea.WOLF_lowStateNum ||
            spirit<GArea.WOLF_lowStateNum
        ){lowState=true;}else{lowState=false;return;}
        if(!isDefaultAction()){return;}
        if(favor<GArea.WOLF_lowStateNum){if(GTools.randomTodo(2)){setAction(GArea.ACT_sad);}}
        if(clean<GArea.WOLF_lowStateNum){if(GTools.randomTodo(2)){setAction(GArea.ACT_dirty);}}
        if(food<GArea.WOLF_lowStateNum){if(GTools.randomTodo(2)){setAction(GArea.ACT_hungry);}}
        if(spirit<GArea.WOLF_lowStateNum){if(GTools.randomTodo(2)){setAction(GArea.ACT_tired);}}
    }
    // 狼想吃东西
    public void eatingFood(){
        TimerTask eat = new TimerTask(){
            @Override public void run(){
                // 扫描食物数组
                for(int i=0;i<foodList.size();i++){
                    Food theFood = foodList.get(i);
                    // 玩家把食物喂给狼
                    Point wolfPoint = getWolfLocation();
                    wolfPoint.x+=bodyWidth/2;wolfPoint.y+=bodyHeight/2;
                    double len = GTools.get2PosLength(wolfPoint,theFood.getLocation());
                    if(len<GTools.BODY_WIDTH/2){
                        boolean x = eatAFood(theFood);
                        if(x){
                            foodList.remove(i);
                            theFood.eatIt();
                        }
                    }
                }
            }
        };
        eatTimer.scheduleAtFixedRate(eat,0,GArea.GAME_FREQUENY*10);
    }
    // 狼吃了一个食物
    public boolean eatAFood(Food f){
        // 太饱了就停止
        if(food>GArea.WOLF_basicFood){
            if(preDoAction()){setAction(GArea.ACT_eatFull);}
            return false;
        }
        String likelyFood = f.foodName;
        food+=GArea.WOLF_addNum*2;
        if(likelyFood==""){favor+=GArea.WOLF_addNum*2;} // 吃到喜欢的
        if(food==GArea.WOLF_maxStateNum){food=GArea.WOLF_maxStateNum;}
        setAction(GArea.ACT_eat);
        TimerTask timerTask = new TimerTask(){
            @Override public void run(){setAction(GArea.ACT_default);}
        };
        actionTimer.schedule(timerTask,GArea.WOLF_period);
        return true;
    }
    // 玩耍模式下的跟随鼠标
    public void followMouseToPlay(){
        TimerTask followmove = new TimerTask(){
            @Override public void run(){
                if(followMouse){
                    Point pos = MouseInfo.getPointerInfo().getLocation();
                    pos.x-=bodyWidth/2;pos.y-=bodyHeight/2;
                    setWolfLocation(pos);
                }
            }
        };
        followTimer.scheduleAtFixedRate(followmove,0,GArea.GAME_FREQUENY*2);
    }
}
