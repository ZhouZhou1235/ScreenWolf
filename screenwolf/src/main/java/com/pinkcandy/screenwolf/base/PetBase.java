package com.pinkcandy.screenwolf.base;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.AnimationSprite;
import com.pinkcandy.screenwolf.PetMessageBubble;
import com.pinkcandy.screenwolf.PetOption;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.bean.PlayPetData;
import com.pinkcandy.screenwolf.tools.Tool;
import com.pinkcandy.screenwolf.tools.GsonUtil;
import com.pinkcandy.screenwolf.tools.ImageSelection;

// 桌面宠物
public class PetBase extends JPanel {
    // === 组成 ===
    private AnimationSprite body; // 动画精灵
    private PetOption petOption; // 宠物选项窗口
    private Robot robot; // 自动机器
    private Launcher launcher; // 启动器的引用
    // === 数值 ===
    private int followDistanse = (int)Tool.DEFAULT_bodySize.getWidth(); // 跟随距离
    private int moveSpeed = (int)Tool.DEFAULT_bodySize.getWidth()/10; // 移动速度
    // === 数据 ===
    private String id; // 宠物号码
    private Map<String,String> animations; // 动画数据
    private PetData petData; // 宠物数据
    private String savePath; // 数据保存地址
    private PlayPetData playPetData; // 游玩数据
    private Point pressPetPoint; // 宠物点按处
    private Point autoMoveTarget; // 自动移动目标位置
    // === 反应 ===
    protected int touchNum = 0; // 抚摸值
    protected int restNum = 0; // 休息值
    protected int moveNum = 0; // 移动值
    protected int talkNum = 0; // 说话值
    protected int emotionNum = 0; // 情绪表达值
    protected int touchThreshold = 5; // 抚摸阈值
    protected int restThreshold = 60*10; // 休息阈值
    protected int moveThreshold = 30; // 移动阈值
    protected int talkThreshold = 60*2; // 说话阈值
    protected int affectLevelUp = 100; // 好感升级所需值
    protected int affectTopLevel = 100; // 好感最高等级
    protected int emotionThreshold = 30; // 情绪表达阈值
    // === 状态 ===
    public boolean isFollow = false; // 跟随
    public boolean isFocus = false; // 聚焦
    public boolean isPress = false; // 按住
    public boolean isMoving = false; // 移动
    public boolean isTouching = false; // 正在被摸
    public boolean isResting = false; // 正在休息
    public boolean isAutoMoving = false; // 正在自主移动
    public boolean isTargetAnimationPlaying = false; // 正在播放特定动画
    // 计时器
    private Timer updateTimer; // 高速循环计时器
    private Timer lowUpdateTimer; // 低速循环计时器
    public PetBase(Launcher theLauncher){
        this.launcher = theLauncher;
        initPet();
    }
    // 空参构造 - 仅测试
    public PetBase(){initPet();}
    // 初始化桌宠
    public void initPet(){
        // 基本属性
        Dimension size = Tool.DEFAULT_bodySize;
        this.id = this.getClass().getSimpleName();
        this.savePath = Tool.GAME_dataPath+id+".json";
        // 宠物数据
        String jsonpetdata = Tool.readFile(Tool.GAME_modPath + id + "/pet_data.json");
        this.petData = GsonUtil.json2Bean(jsonpetdata,PetData.class);
        // 动画
        String[] animationNames = petData.getAnimationNames();
        HashMap<String, String> imageFrameHashmap = new HashMap<>();
        for(String animationName:animationNames){
            imageFrameHashmap.put(
                animationName,
                Tool.GAME_modPath+id+"/frames/"+animationName+"/"
            );
        }
        this.animations = imageFrameHashmap;
        this.body = new AnimationSprite(size,animations);
        // 定时器
        this.updateTimer = new Timer(Tool.GAME_updateTime,e->autoLoop());
        this.updateTimer.start();
        this.lowUpdateTimer = new Timer(Tool.GAME_slowUpdateTime,e->slowAutoLoop());
        this.lowUpdateTimer.start();        
        // 宠物选项
        this.petOption = new PetOption(
            this,
            new Dimension(size.width*2,size.width*2)
        );
        // 自动机器
        try{this.robot = new Robot();}
        catch(AWTException e){e.printStackTrace();}
        // 面板属性
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        // 完毕
        this.ready();
    }
    // 释放宠物对象
    public void dispose(){
        savePetData();
        this.petOption.setVisible(false);
        this.body.stopAnimation();
        this.updateTimer.stop();
        this.lowUpdateTimer.stop();
        this.removeAll();
        this.getParent().remove(this);
        System.gc();
    }
    // 获取宠物号码
    public String getid(){return this.id;}
    // 获取宠物数据
    public PetData getPetData(){return this.petData;}
    // 获取游玩数据
    public PlayPetData getPlayPetData(){return this.playPetData;}
    // 切换动画
    public void updateBodyAnimation(String animationName){
        if(animations.get(animationName)==null){return;}
        body.setAnimation(animationName);
    }
    // 载入新的动画数据
    public void setAnimations(Map<String,String> animations){
        this.animations = animations;
        this.body = new AnimationSprite(this.getSize(),animations);
    }
    // 获取播放的动画名称
    public String getAnimationName(){return this.body.animationName;}
    // 改变一次到指定动画
    public void updateAnimationOnce(String animationName){
        String theAnimationName = this.getAnimationName();
        if(theAnimationName!=animationName){this.updateBodyAnimation(animationName);}
    }
    // 获取宠物中心位置
    public Point getPetPosition(){
        Point o = this.getLocation();
        Dimension size = this.getSize();
        int x = o.x+size.width/2;
        int y = o.y+size.height/2;
        return new Point(x,y);
    }
    // 向目标点移动一次
    public void gotoPoint(Point target) {
        Point currentPos = this.getLocation();
        int dx = target.x-currentPos.x;
        int dy = target.y-currentPos.y;
        double distance = Math.sqrt(dx*dx+dy*dy);
        if(distance<=moveSpeed){this.setLocation(target);return;}
        double directionX = dx/distance;
        double directionY = dy/distance;
        int nextX = currentPos.x+(int)(directionX*moveSpeed);
        int nextY = currentPos.y+(int)(directionY*moveSpeed);
        this.setLocation(new Point(nextX, nextY));
        if(dx>0&&!body.filp_h){body.filp_h=true;}
        else if(dx<0&&body.filp_h){body.filp_h=false;}
    }
    // 保存桌宠游玩数据
    public void savePetData(){
        String jsonString = GsonUtil.bean2Json(playPetData);
        Tool.saveToFile(savePath,jsonString);
    }
    // 反应值置零
    public void ZeroingResponseNum(){
        touchNum = 0;
        restNum = 0;
        moveNum = 0;
        talkNum = 0;
        // emotionNum = 0; 情绪不中断
        isResting = false;
        isAutoMoving = false;
        isTargetAnimationPlaying = false;
    }
    // 是否空闲
    public boolean isFree(){
        return
        isFocus || isFollow || isMoving || isPress || isResting || isTargetAnimationPlaying
        ?false:true;
    }
    // 命令休息
    public void doRest(){
        ZeroingResponseNum();
        isResting = true;
    }
    // 屏幕截图
    public BufferedImage copyScreenImage(){
        Rectangle rectangle = new Rectangle(Tool.SCREEN_dimension);
        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
        ImageSelection imageSelection = new ImageSelection(bufferedImage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(imageSelection,null);
        return bufferedImage;
    }
    // 指定播放目标动画
    public void playTargetAnimation(String animationName,int time){
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                isTargetAnimationPlaying = true;
                updateAnimationOnce(animationName);
            }
        },50); // 保留微小延迟确保调用
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                isTargetAnimationPlaying = false;
                updateAnimationOnce(animationName);
                timer.cancel();
            }
        },time+50);
    }
    // 增加好感
    public void addAffectPoint(int affectPointNum){
        if(playPetData.getAffectionLevel()<affectTopLevel){
            playPetData.setAffectionPoints(playPetData.getAffectionPoints()+affectPointNum);
            if(playPetData.getAffectionPoints()>affectLevelUp){
                playPetData.setAffectionLevel(playPetData.getAffectionLevel()+1);
                playPetData.setAffectionPoints(playPetData.getAffectionPoints()-affectLevelUp);
            }
        }
        else{
            playPetData.setAffectionLevel(affectTopLevel);
            playPetData.setAffectionPoints(affectLevelUp);
        }
    }
    // 减少好感
    public void reduceAffectPoint(int affectPointNum){
        if(playPetData.getAffectionLevel()>0){
            playPetData.setAffectionPoints(playPetData.getAffectionLevel()-affectPointNum);
            if(playPetData.getAffectionPoints()<=0){
                playPetData.setAffectionLevel(playPetData.getAffectionLevel()-1);
                int num = Math.abs(playPetData.getAffectionPoints());
                playPetData.setAffectionPoints(affectLevelUp-num);
            }
        }
        else{
            playPetData.setAffectionLevel(0);
            playPetData.setAffectionPoints(0);
        }
    }
    // === 实例化完成调用 ===
    // 初始化完成时执行
    public void ready(){
        ready_loadPlayPetData();
        ready_addMouseAction();
    }
    // 加载游玩数据
    public void ready_loadPlayPetData(){
        if(Tool.createFile(savePath)==1){
            PlayPetData playPetData = new PlayPetData();
            Tool.saveToFile(savePath,GsonUtil.bean2Json(playPetData));
            this.playPetData = playPetData;
        }
        else{
            PlayPetData playPetData = GsonUtil.json2Bean(Tool.readFile(savePath),PlayPetData.class);
            this.playPetData = playPetData;
        }
    }
    // 添加鼠标事件回应
    public void ready_addMouseAction(){
        PetBase petBase = this;
        petBase.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                ZeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    isPress = true;
                    pressPetPoint = e.getPoint();
                    playPetData.setMouseClickNum(playPetData.getMouseClickNum()+1);
                }
                else if(e.getButton()==MouseEvent.BUTTON2){
                    copyScreenImage();
                }
                else if(e.getButton()==MouseEvent.BUTTON3){
                    petOption.setLocation(petBase.getPetPosition());
                    petOption.setVisible(true);
                }
            }
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
            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                ZeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    isPress = false;
                }
            }
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                ZeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    int num = e.getClickCount();
                    if(num>=2){isFollow = !isFollow;}
                }
            }
        });
        petBase.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                super.mouseDragged(e);
                ZeroingResponseNum();
                if(isPress){
                    Point petPosition = petBase.getLocation();
                    int x = petPosition.x+e.getX()-pressPetPoint.x;
                    int y = petPosition.y+e.getY()-pressPetPoint.y;
                    petBase.setLocation(x,y);
                    // 等级过低的拖拽减少好感
                    if(playPetData.getAffectionLevel()<20){
                        playPetData.setAffectionPoints(playPetData.getAffectionPoints()-1);
                    }
                }
            }
        });
        petBase.addMouseWheelListener(new MouseWheelListener(){
            @Override
            public void mouseWheelMoved(MouseWheelEvent e){
                touchNum += 1;
                addAffectPoint(1);
            }
        });
    }
    
    // === 高速循环 ===
    // 高速循环执行
    public void autoLoop(){
        auto_playAnimations();
        auto_followMouse();
        auto_move();
    }
    // 跟随鼠标
    public void auto_followMouse(){
        if(isFollow){
            Point mousePoint = Tool.getMousePoint();
            Point petPosition = this.getPetPosition();
            double distanse = Tool.getDistanse2Point(mousePoint,petPosition);
            if(distanse>followDistanse){
                // 等级过低概率不跟随
                if(
                    playPetData.getAffectionLevel()<20
                    &&
                    Math.random()<0.75
                ){return;}
                else if(
                    playPetData.getAffectionLevel()<40
                    &&
                    Math.random()<0.5
                ){return;}
                else if(
                    playPetData.getAffectionLevel()<60
                    &&
                    Math.random()<0.25
                ){return;}
                if(!isMoving){isMoving=true;}
                gotoPoint(mousePoint);
                playPetData.setFollowMouseDistance(playPetData.getFollowMouseDistance()+1);
            }
            else if(isMoving){isMoving=false;}
        }
    }
    // 播放动画
    public void auto_playAnimations(){
        if(!isResting){
            if(!isTargetAnimationPlaying){
                if(isPress){this.updateAnimationOnce("press");}
                else if(isMoving || isAutoMoving){this.updateAnimationOnce("move");}
                else if(isFocus){
                    if(isTouching){this.updateAnimationOnce("touch");}
                    else{this.updateAnimationOnce("focus");}
                }
                else{this.updateAnimationOnce("default");}
            }
        }
        else{this.updateAnimationOnce("rest");}
    }
    // 执行自主移动
    public void auto_move(){
        if(isAutoMoving){
            Point petPosition = this.getPetPosition();
            double distanse = Tool.getDistanse2Point(autoMoveTarget,petPosition);
            if(distanse>followDistanse){
                gotoPoint(autoMoveTarget);
                if(!isAutoMoving){isAutoMoving=true;}
            }
            else if(isAutoMoving){isAutoMoving=false;}
        }
    }
    
    // === 低速循环 ===
    // 低速循环执行
    public void slowAutoLoop(){
        slowAuto_touch();
        slowAuto_rest();
        slowAuto_move();
        slowAuto_talk();
        slowAuto_showEmotion();
    };
    // 抚摸反应
    public void slowAuto_touch(){
        if(touchNum>=touchThreshold){isTouching=true;}else{isTouching=false;}
        if(touchNum>0){
            touchNum-=touchThreshold;
            if(touchNum<0 || isFree()){touchNum=0;}
        }
    }
    // 过久不操作休息
    public void slowAuto_rest(){
        if(!isResting && isFree()){
            if(restNum<restThreshold){restNum++;}
            else{isResting=true;restNum=0;}
        }
    }
    // 自主移动
    public void slowAuto_move(){
        if(!isAutoMoving && isFree()){
            if(moveNum<moveThreshold){moveNum++;}
            else{
                autoMoveTarget = Tool.getRandomPointOnScreen();
                isAutoMoving = true;
                moveNum = 0;
            }
        }
    }
    // 说话
    public void slowAuto_talk(){
        if(isFree()){
            if(talkNum<talkThreshold){talkNum++;}
            else{
                if(launcher!=null){
                    String[] messageBubbleList = petData.getMessageBubbleList();
                    String message = messageBubbleList[(int)Math.round(Math.random()*(messageBubbleList.length-1))];
                    PetMessageBubble messageBubble = new PetMessageBubble(message);
                    Point bubblePos = new Point(getPetPosition().x-body.getSize().width/2,getPetPosition().y-body.getSize().height/2);
                    messageBubble.setLocation(bubblePos);
                    launcher.addItemToScreen(messageBubble);
                    messageBubble.revalidate();
                    messageBubble.repaint();
                }
                talkNum = 0;
            }
        }
    }
    // 根据好感等级表达情绪
    public void slowAuto_showEmotion(){
        int level = playPetData.getAffectionLevel();
        if(level<20){
            // ... 伤心
        }
        else if(level<40){
            // ... 不高兴
        }
        else if(level<60){
            // ... 一般
        }
        else if(level<80){
            // ... 高兴
        }
        else{
            // ... 开心
        }
    }
}
