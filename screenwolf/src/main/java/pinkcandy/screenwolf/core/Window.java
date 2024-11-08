package pinkcandy.screenwolf.core;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.MouseEvent;
import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.items.Bubble;
import pinkcandy.screenwolf.core.items.Food;
import pinkcandy.screenwolf.core.items.GamePanel;
import pinkcandy.screenwolf.core.items.MouseTip;
import pinkcandy.screenwolf.core.items.ToyBone;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

/**
 * 游戏空间窗口
 * 所有窗体都放到窗口
 */
public class Window extends JFrame {
    // - 鼠标
    Point screenPoint; // 鼠标的屏幕绝对位置
    int mouseState; // 鼠标模式
    MouseTip mouseTip; //鼠标提示
    Point point; // 鼠标相对点
    // - 计时器
    Timer updateTimer; // Swing Timer 帧刷新计时器
    Timer bubbleSuccessTimer; // Swing Timer 泡泡游戏胜利结算计时器
    Timer bubbleTimer; // Swing Timer 泡泡游戏计时器
    // - 宠物狼
    public Wolf myWolf; // 桌面宠物狼
    // - 物品
    Map<String,String> foodMap; // 食物贴图
    ToyBone toyBone; // 骨头玩具
    GamePanel gamePanel; // 游戏面板
    // - 记录值
    public int bubbleCount; // 戳泡泡得分
    public int bubbleGameState; // 戳泡泡游戏 -1未开始 0进行中 1成功 2失败
    // 构造游戏窗口
    public Window(){
        // - 鼠标
        screenPoint = new Point();
        mouseState = GArea.MOUSE_normal;
        mouseTip=new MouseTip(screenPoint);this.add(mouseTip);
        point = new Point();
        // - 计时器
        // 游戏始终帧刷新
        updateTimer = new Timer(GArea.GAME_FREQUENY,e->{
            this.repaint();
            this.update(getGraphics());
            rootPane.repaint();
            rootPane.update(getGraphics());
            rootPane.updateUI();
            // 不断更新鼠标绝对位置
            screenPoint = MouseInfo.getPointerInfo().getLocation();
        });updateTimer.start();
        // 戳泡泡游戏
        bubbleTimer = new Timer(GArea.WOLF_period/5,e->{
            myWolf.setAction(GArea.ACT_playBubble);
            if(bubbleGameState!=0){ // 游戏结束
                if(bubbleGameState==1){
                    myWolf.setAction(GArea.ACT_gameSuccess);
                    bubbleSuccessTimer.start();
                    myWolf.favor+=GArea.WOLF_addNum*5;
                }
                if(bubbleGameState==2){
                    myWolf.setAction(GArea.ACT_gameFailed);
                    myWolf.favor+=GArea.WOLF_addNum;
                }
                bubbleTimer.stop();
            }
            // 狼吹泡泡时移动
            if(GTools.randomTodo(6)){myWolf.wolfMove(GTools.getRandomPoint());}
            // 随机吹泡泡 1-2个
            int x = (int)(Math.random()*1+1);
            for(int i=0;i<1+x;i++){
                Bubble bubble = new Bubble(this,myWolf.getLocation(),GArea.WOLF_speed);
                this.add(bubble,1);
            }
        });
        // 戳泡泡胜利结算
        bubbleSuccessTimer = new Timer(GArea.GAME_FREQUENY*20,e->{
            if(!myWolf.lowState){
                Bubble bubble = new Bubble(this,myWolf.getLocation(),GArea.WOLF_speed);
                this.add(bubble,1);
            }
            else{bubbleSuccessTimer.stop();}
        });
        // - 宠物狼
        // 外部调用 loadWolf
        // - 物品
        foodMap = GTools.decodeJsonObject(GTools.getContentFromFile(GArea.GAME_FOODS));
        toyBone = new ToyBone();this.add(toyBone);toyBone.setVisible(false);listenToyBone();
        // gamePanel = new GamePanel(this,myWolf);this.add(gamePanel);
        // - 记录值
        bubbleCount = 0;
        bubbleGameState = -1;
        // 监听键盘
        listenKeyBoard();
        // 窗体设置
        this.setTitle(GArea.GAME_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,GTools.SCREEN_WIDTH,GTools.SCREEN_HEIGHT);
        this.setSize(GTools.SCREEN_WIDTH,GTools.SCREEN_HEIGHT);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
        this.setLayout(null);
        this.setType(JFrame.Type.UTILITY);
        this.setVisible(true);
    }
    // 加载狼并加载面板
    public void loadWolf(Wolf theWolf){
        myWolf = theWolf;
        gamePanel = new GamePanel(this,myWolf);this.add(gamePanel);
    }
    // 监听狼鼠标事件
    public void listenWolf(Wolf wolf){
        // 鼠标监听器
        wolf.addMouseListener(new MouseAdapter(){
            // 鼠标按下时
            @Override public void mousePressed(MouseEvent e){
                // 右键切换鼠标模式
                if(e.getButton()==MouseEvent.BUTTON3){
                    gamePanel.showPanel = true;
                    changeMouseMode();
                }
                if(mouseState==GArea.MOUSE_normal){ // 正常模式下
                    // 让狼起来
                    if(wolf.stateNum==GArea.STATE_rest){wolf.resetWolf();}
                    // 狼被按下
                    if(e.getButton()==MouseEvent.BUTTON1){
                        point.x = e.getX();
                        point.y = e.getY();
                        wolf.setAction(GArea.ACT_press);
                        wolf.dragged = true;
                    }
                }
                if(mouseState==GArea.MOUSE_play){
                    // 停止泡泡游戏胜利结算
                    if(bubbleSuccessTimer.isRunning()){bubbleSuccessTimer.stop();}
                    // 抱紧鼠标
                    if(myWolf.followMouse){if(GTools.randomTodo(4)){
                        myWolf.setAction(GArea.ACT_holdMouse);
                    }}
                }
            }
            // 鼠标释放时
            @Override public void mouseReleased(MouseEvent e){
                if(mouseState==GArea.MOUSE_normal){
                    wolf.dragged = false;
                    wolf.setAction(GArea.ACT_default);
                }
            }
            // 鼠标进入时
            @Override public void mouseEntered(MouseEvent e){
                mouseTip.setVisible(true); // 鼠标提示
                if(mouseState==GArea.MOUSE_normal){
                    // 狼被聚焦
                    if(wolf.stateNum==GArea.STATE_normal && !wolf.dragged){wolf.focusWolf();}
                }
            }
            // 鼠标离开时
            @Override public void mouseExited(MouseEvent e){
                mouseTip.setVisible(false);
                if(mouseState==GArea.MOUSE_normal){
                    // 狼回复默认状态
                    if(wolf.stateNum==GArea.STATE_normal && !wolf.dragged){
                        wolf.setAction(GArea.ACT_default);
                        wolf.canMove = true;
                    }
                }
            }
        });
        // 鼠标动作监听器
        wolf.addMouseMotionListener(new MouseMotionAdapter(){
            // 鼠标拖拽时
            @Override public void mouseDragged(MouseEvent e){
                if(mouseState==GArea.MOUSE_normal){
                    // 响应拖拽
                    if(wolf.dragged){
                        Point p = wolf.getLocation();
                        // 窗口位置 + 鼠标在窗口位置 - 鼠标按下时在窗口位置
                        wolf.setLocation(p.x+e.getX()-point.x,p.y+e.getY()-point.y);    
                    }
                }
            }
            // 鼠标移动时
            @Override public void mouseMoved(MouseEvent e){
                screenPoint = MouseInfo.getPointerInfo().getLocation();
                mouseTip.setLocation(screenPoint.x-GTools.BODY_WIDTH/4,screenPoint.y-GTools.BODY_HEIGHT/4);
                if(mouseState==GArea.MOUSE_normal){
                    // 抚摸狼
                    if(wolf.stateNum==GArea.STATE_normal){wolf.touchWolf();}
                }
                if(mouseState==GArea.MOUSE_clean){
                    // 清洗狼
                    if(wolf.stateNum==GArea.STATE_cleaning){wolf.cleanWolf();}
                }
            }
        });
    }
    // 监听骨头玩具鼠标事件
    private void listenToyBone(){
        toyBone.addMouseListener(new MouseAdapter(){
            // 鼠标按下时
            @Override public void mousePressed(MouseEvent e){
                if(!toyBone.followMouse){ // 拿玩具
                    toyBone.toyFollowMouse();
                    if(
                        myWolf.stateNum==GArea.STATE_playing &&
                        !myWolf.lowState
                    ){myWolf.setAction(GArea.ACT_focusToy);}
                }
                else{ // 扔玩具
                    Point pos = GTools.getRandomPoint();
                    toyBone.toyStopFollow();
                    toyBone.toyMove(pos);
                    if(
                        myWolf.stateNum==GArea.STATE_playing &&
                        !myWolf.lowState
                    ){
                        Point wolfPos = new Point(
                            pos.x-myWolf.bodyWidth/2,
                            pos.y-myWolf.bodyHeight/2
                        );
                        myWolf.wolfMove(wolfPos);
                    }
                }
            }
        });
    }    
    // 监听键盘事件
    private void listenKeyBoard(){
        // 键盘监听器
        this.addKeyListener(new KeyAdapter(){
            // 按键被按下时
            @Override public void keyPressed(KeyEvent e){
                // Esc直接重置游戏
                if(e.getKeyCode()==27){
                    myWolf.resetWolf();
                    mouseState = GArea.MOUSE_normal;
                    mouseTip.setTip(GArea.MOUSE_normal);
                    clearAllFood();
                    toyBone.setVisible(false);
                    gamePanel.showPanel = false;
                }
                // 停止泡泡游戏胜利结算
                if(bubbleSuccessTimer.isRunning()){bubbleSuccessTimer.stop();}
                // 喂食模式下
                if(myWolf.stateNum==GArea.STATE_eating){
                    // 空格32 回车10 Esc 27
                    if(
                        e.getKeyCode()==32 ||
                        e.getKeyCode()==10
                    ){putFoodOnWindow();}
                    if(e.getKeyCode()==27){
                        clearAllFood();
                    }
                }
                // 玩耍模式下
                if(myWolf.stateNum==GArea.STATE_playing){
                    // F1 112 F2 113 F3 114 1主 49 2主 50 3主51 1小 97 2小98 3小 99
                    // 玩耍1 跟随鼠标
                    if(
                        e.getKeyCode()==112 ||
                        e.getKeyCode()==49 ||
                        e.getKeyCode()==97
                    ){
                        if(myWolf.followMouse){
                            myWolf.setAction(GArea.ACT_default);
                            myWolf.followMouse = false;
                            myWolf.speed = GArea.WOLF_speed;
                        }
                        else{
                            myWolf.setAction(GArea.ACT_followMouse);
                            myWolf.followMouse = true;
                            myWolf.speed = GArea.WOLF_speed*2;
                        }
                    }
                    // 玩耍2 扔骨头玩具
                    if(
                        e.getKeyCode()==113 ||
                        e.getKeyCode()==50 ||
                        e.getKeyCode()==98    
                    ){
                        // 显示和隐藏
                        if(toyBone.isVisible()){toyBone.setVisible(false);}
                        else{toyBone.setVisible(true);}
                    }
                    // 玩耍3 戳泡泡游戏
                    if(
                        e.getKeyCode()==114 ||
                        e.getKeyCode()==51 ||
                        e.getKeyCode()==99
                    ){if(!myWolf.lowState){playBubbleGame();}}
                }
            }
        });
    }
    // 切换鼠标模式
    public void changeMouseMode(){
        // 如果狼在睡觉则中止
        if(myWolf.stateNum==GArea.STATE_rest){
            mouseState = GArea.MOUSE_normal;
            mouseTip.setTip(GArea.MOUSE_normal);
            return;
        }
        myWolf.setAction(GArea.ACT_default);
        if(mouseState==GArea.MOUSE_normal){
            mouseState = GArea.MOUSE_clean;
            mouseTip.setTip(GArea.MOUSE_clean);
            myWolf.stopActiveEvent();
            myWolf.stateNum = GArea.STATE_cleaning;
        }
        else if(mouseState==GArea.MOUSE_clean){
            mouseState = GArea.MOUSE_food;
            mouseTip.setTip(GArea.MOUSE_food);
            myWolf.stateNum = GArea.STATE_eating;
        }
        else if(mouseState==GArea.MOUSE_food){
            mouseState=GArea.MOUSE_play;
            mouseTip.setTip(GArea.MOUSE_play);
            myWolf.stateNum = GArea.STATE_playing;
            myWolf.canMove = true;
            myWolf.speed = GArea.WOLF_speed*2;
        }
        else if(mouseState==GArea.MOUSE_play){
            mouseState=GArea.MOUSE_normal;
            mouseTip.setTip(GArea.MOUSE_normal);
            myWolf.startActiveEvent();
            myWolf.stateNum = GArea.STATE_normal;
            myWolf.speed = GArea.WOLF_speed;
        }
    }
    // 在屏幕上随机放一种食物
    private void putFoodOnWindow(){
        if(myWolf.foodList.size()>GArea.WOLF_maxStateNum/10){return;} // 不能放太多
        Object[] foodArray = foodMap.keySet().toArray();
        int x = (int)(Math.random()*foodMap.size());
        String key = (String)foodArray[x];
        String url = foodMap.get(key);
        Food theFood = new Food(key,url,GTools.BODY_WIDTH/4,GTools.getRandomPoint());
        myWolf.foodList.add(theFood);
        this.add(theFood,1);
    }
    // 清除所有食物
    public void clearAllFood(){
        for(int i=0;i<myWolf.foodList.size();i++){
            Food theFood = myWolf.foodList.get(i);
            theFood.eatIt();
        }
        myWolf.foodList.clear();
    }
    // 玩戳泡泡游戏
    private void playBubbleGame(){
        if(
            bubbleGameState==0 ||
            myWolf.stateNum!=GArea.STATE_playing ||
            myWolf.lowState
        ){return;}
        bubbleGameState = 0;
        bubbleCount = 0;
        if(bubbleSuccessTimer.isRunning()){bubbleSuccessTimer.stop();}
        bubbleTimer.start();
    }
}
