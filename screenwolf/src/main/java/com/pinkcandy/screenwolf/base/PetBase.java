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

import com.alibaba.fastjson.JSON;
import com.pinkcandy.screenwolf.AnimationSprite;
import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.ImageSelection;
import com.pinkcandy.screenwolf.PetOption;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.bean.PlayPetData;

// 桌面宠物
public class PetBase extends JPanel {
    private String id; // 宠物号码
    private Map<String,String> animations; // 动画数据
    private AnimationSprite body; // 动画精灵
    private Point pressPetPoint; // 宠物点按处
    private Point autoMoveTarget; // 自动移动目标位置
    private Timer updateTimer; // 自动动画更新计时器
    private Timer responseTimer; // 反应计时器
    private int followDistanse = (int)GArea.DEFAULT_bodySize.getWidth(); // 跟随距离
    private int moveSpeed = (int)GArea.DEFAULT_bodySize.getWidth()/10; // 移动速度
    private PetData petData; // 宠物数据
    private PlayPetData playPetData; // 游玩数据
    private String savePath; // 数据保存地址
    private int touchNum = 0; // 抚摸值
    private int restNum = 0; // 休息值
    private int moveNum = 0; // 移动值
    private int moveThreshold = 30; // 移动阈值
    private int restThreshold = 60*10; // 休息阈值
    private int touchThreshold = 5; // 抚摸阈值
    private PetOption petOption; // 宠物选项窗口
    private Robot robot; // 自动机器
    public boolean isFollow = false; // 跟随
    public boolean isFocus = false; // 聚焦
    public boolean isPress = false; // 按住
    public boolean isMoving = false; // 移动
    public boolean isTouching = false; // 正在被摸
    public boolean isResting = false; // 正在休息
    public boolean isAutoMoving = false; // 正在自主移动
    public boolean isTargetAnimationPlaying = false; // 正在播放特定动画
    public PetBase(){
        Dimension size = GArea.DEFAULT_bodySize;
        this.id = this.getClass().getSimpleName();
        String jsonpetdata = GArea.readFile(GArea.GAME_petsPath+id+"/pet_data.json");
        this.petData = JSON.parseObject(jsonpetdata).toJavaObject(PetData.class);
        String[] animationNames = petData.getAnimationNames();
        HashMap<String,String> imageFrameHashmap = new HashMap<>();
        for(String animationName:animationNames){
            imageFrameHashmap.put(
                animationName,
                GArea.GAME_petsPath+id+"/"+"frames"+"/"+animationName+"/"
            );
        }
        this.animations = imageFrameHashmap;
        this.body = new AnimationSprite(size,animations);
        this.updateTimer = new Timer(GArea.GAME_petUpdateTime,e->{autoLoop();});this.updateTimer.start();
        this.responseTimer = new Timer(GArea.DEFAULT_petResponseTime,e->{responseAutoLoop();});this.responseTimer.start();
        this.savePath = GArea.GAME_dataPath+id+".json";
        this.petOption = new PetOption(
            this,
            new Dimension(GArea.DEFAULT_bodySize.width*2,GArea.DEFAULT_bodySize.height*3)
        );
        try{this.robot = new Robot();}catch(AWTException e){e.printStackTrace();}
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        this.ready();
    }
    // 释放宠物对象
    public void dispose(){
        savePetData();
        this.petOption.setVisible(false);
        this.body.stopAnimation();
        this.updateTimer.stop();
        this.responseTimer.stop();
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
        String jsonString = GArea.jsonEncode(playPetData);
        GArea.saveToFile(savePath,jsonString);
    }
    // 回应值置零
    public void ZeroingResponseNum(){
        restNum = 0;
        moveNum = 0;
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
        Rectangle rectangle = new Rectangle(GArea.SCREEN_dimension);
        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
        ImageSelection imageSelection = new ImageSelection(bufferedImage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(imageSelection,null);
        playTargetAnimation("copy_screen",1000);
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
    // 初始化完成时执行
    public void ready(){
        ready_loadPlayPetData();
        ready_addAutoSaveHonkOnExit();
        ready_addMouseAction();
    }
    // 加载游玩数据
    public void ready_loadPlayPetData(){
        if(GArea.createFile(savePath)==1){
            PlayPetData playPetData = new PlayPetData();
            GArea.saveToFile(savePath,GArea.jsonEncode(playPetData));
            this.playPetData = playPetData;
        }
        else{
            PlayPetData playPetData = JSON.parseObject(GArea.readFile(savePath)).toJavaObject(PlayPetData.class);
            this.playPetData = playPetData;
        }
    }
    // 退出时自动保存数据
    public void ready_addAutoSaveHonkOnExit(){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
            @Override
            public void run(){savePetData();}
        }));
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
                }
            }
        });
        petBase.addMouseWheelListener(new MouseWheelListener(){
            @Override
            public void mouseWheelMoved(MouseWheelEvent e){
                ZeroingResponseNum();
                touchNum += 1;
            }
        });
    }
    // 自动执行事件
    public void autoLoop(){
        auto_playAnimations();
        auto_followMouse();
        auto_move();
    }
    // 跟随鼠标
    public void auto_followMouse(){
        if(isFollow){
            Point mousePoint = GArea.getMousePoint();
            Point petPosition = this.getPetPosition();
            double distanse = GArea.getDistanse2Point(mousePoint,petPosition);
            if(distanse>followDistanse){
                gotoPoint(mousePoint);
                if(!isMoving){isMoving=true;}
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
            double distanse = GArea.getDistanse2Point(autoMoveTarget,petPosition);
            if(distanse>followDistanse){
                gotoPoint(autoMoveTarget);
                if(!isAutoMoving){isAutoMoving=true;}
            }
            else if(isAutoMoving){isAutoMoving=false;}
        }
    }
    // 反应自动执行事件
    public void responseAutoLoop(){
        responseAuto_touch();
        responseAuto_rest();
        responseAuto_move();
    };
    // 抚摸反应
    public void responseAuto_touch(){
        if(touchNum>=touchThreshold){isTouching=true;}else{isTouching=false;}
        if(touchNum>0){
            touchNum-=touchThreshold;
            if(touchNum<0){touchNum=0;}
        }
    }
    // 过久不操作休息
    public void responseAuto_rest(){
        if(!isResting && isFree()){
            if(restNum<restThreshold){restNum++;}
            else{isResting=true;restNum=0;}
        }
    }
    // 自主移动
    public void responseAuto_move(){
        if(!isAutoMoving && isFree()){
            if(moveNum<moveThreshold){moveNum++;}
            else{
                autoMoveTarget = GArea.getRandomPointOnScreen();
                isAutoMoving = true;
                moveNum = 0;
            }
        }
    }
}
