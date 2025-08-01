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
import java.util.TimerTask;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.AnimationSprite;
import com.pinkcandy.screenwolf.PetMessageBubble;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.bean.PlayPetData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.ImageSelection;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.windows.PetOption;

// 桌面宠物
// TODO 检查方法以及默认行为的规范
// TODO 完善好感系统的运用
public class PetBase extends JPanel {
    // === 组成 ===
    protected Robot robot; // 自动机器
    protected Launcher launcher; // 启动器的引用
    public AnimationSprite animationSprite; // 动画精灵
    // === 数值 ===
    protected int followDistanse = (int)GUtil.DEFAULT_bodySize.getWidth(); // 跟随距离
    protected int moveSpeed = (int)GUtil.DEFAULT_bodySize.getWidth()/10; // 移动速度
    // === 数据 ===
    protected String id; // 宠物号码
    protected PetData petData; // 宠物数据
    protected String savePath; // 数据保存地址
    protected PlayPetData playPetData; // 游玩数据
    protected Point pressPetPoint; // 宠物点按处
    protected Point autoMoveTarget; // 自动移动目标位置
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
    public int affectLevelUp = 100; // 好感升级所需值
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
    protected Timer updateTimer; // 高速循环计时器
    protected Timer lowUpdateTimer; // 低速循环计时器
    public PetBase(Launcher theLauncher){
        this.launcher = theLauncher;
        initPet();
    }
    // 初始化桌宠
    public void initPet(){
        // 基本属性
        Dimension size = GUtil.DEFAULT_bodySize;
        this.id = this.getClass().getSimpleName();
        this.savePath = GUtil.GAME_savePath+id+".json";
        // 宠物数据
        ClassLoader classLoader = this.getClass().getClassLoader();
        try(InputStream is = classLoader.getResourceAsStream("META-INF/pet_data.json")){
            if(is!=null){
                String jsonpetdata = new String(is.readAllBytes(),StandardCharsets.UTF_8);
                this.petData = GsonUtil.json2Bean(jsonpetdata,PetData.class);
            }else{throw new RuntimeException("PINKCANDY: pet_data not found");}
        }catch(IOException e){
            throw new RuntimeException("PINKCANDY: Failed to load pet data",e);
        }
        // 动画资源
        HashMap<String, String> imageFrameHashmap = new HashMap<>();
        for(String animationName:JarFileUtil.listJarDirNamesByPath(
            GUtil.GAME_petsPath+petData.getJarName(),
            "assets/animations"
        )){
            imageFrameHashmap.put(
                animationName,
                "assets/animations/"+animationName+"/"
            );
        }
        this.animationSprite = new AnimationSprite(
            size,
            imageFrameHashmap,
            GUtil.GAME_petsPath+petData.getJarName()
        );
        // 定时器
        this.updateTimer = new Timer(GUtil.GAME_updateTime,e->autoLoop());
        this.updateTimer.start();
        this.lowUpdateTimer = new Timer(GUtil.GAME_slowUpdateTime,e->slowAutoLoop());
        this.lowUpdateTimer.start();        
        // 自动机器
        try{this.robot = new Robot();}
        catch(AWTException e){e.printStackTrace();}
        // 面板属性
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(animationSprite);
        // 完毕
        this.ready();
    }
    // 释放宠物对象
    public void dispose(){
        savePetData();
        this.animationSprite.stopAnimation();
        this.updateTimer.stop();
        this.lowUpdateTimer.stop();
        this.removeAll();
        this.getParent().remove(this);
        System.gc();
    }

    // === 获取 ===
    // 获取号码
    public String getId(){return id;}
    // 获取宠物数据
    public PetData getPetData(){return this.petData;}
    // 获取游玩数据
    public PlayPetData getPlayPetData(){return this.playPetData;}
    // 获取宠物中心位置
    public Point getPetPosition(){
        Point o = this.getLocation();
        Dimension size = this.getSize();
        int x = o.x+size.width/2;
        int y = o.y+size.height/2;
        return new Point(x,y);
    }

    // === 实例化完成调用 ===
    // 初始化完成时执行
    public void ready(){
        ready_loadPlayPetData();
        ready_addMouseAction();
    }
    // 加载游玩数据
    public void ready_loadPlayPetData(){
        if(GUtil.createFile(savePath)==1){
            PlayPetData playPetData = new PlayPetData();
            GUtil.saveToFile(savePath,GsonUtil.bean2Json(playPetData));
            this.playPetData = playPetData;
        }
        else{
            PlayPetData playPetData = GsonUtil.json2Bean(GUtil.readFile(savePath),PlayPetData.class);
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
                zeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    isPress = true;
                    pressPetPoint = e.getPoint();
                    playPetData.setMouseClickNum(playPetData.getMouseClickNum()+1);
                }
                else if(e.getButton()==MouseEvent.BUTTON2){
                    copyScreenImage();
                }
                else if(e.getButton()==MouseEvent.BUTTON3){
                    PetOption.showForPet(petBase);
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
                zeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    isPress = false;
                }
            }
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                zeroingResponseNum();
                if(e.getButton()==MouseEvent.BUTTON1){
                    int num = e.getClickCount();
                    if(num>=2){
                        // 等级过低概率不跟随
                        if(!isFollow){
                            double randomFollowNum = Math.random();
                            if(
                                playPetData.getAffectionLevel()<20
                                &&
                                randomFollowNum<0.75
                            ){return;}
                            else if(
                                playPetData.getAffectionLevel()<40
                                &&
                                randomFollowNum<0.5
                            ){return;}
                            else if(
                                playPetData.getAffectionLevel()<60
                                &&
                                randomFollowNum<0.25
                            ){return;}
                        }
                        isFollow = !isFollow;
                    }
                }
            }
        });
        petBase.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                super.mouseDragged(e);
                zeroingResponseNum();
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
            Point mousePoint = GUtil.getMousePoint();
            Point petPosition = this.getPetPosition();
            double distanse = GUtil.getDistanse2Point(mousePoint,petPosition);
            if(distanse>followDistanse){
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
                if(isPress){animationSprite.setAndPlayAnimation("press");}
                else if(isMoving || isAutoMoving){animationSprite.setAndPlayAnimation("move");}
                else if(isFocus){
                    if(isTouching){animationSprite.setAndPlayAnimation("touch");}
                    else{animationSprite.setAndPlayAnimation("focus");}
                }
                else{animationSprite.setAndPlayAnimation("default");}
            }
        }
        else{animationSprite.setAndPlayAnimation("rest");}
    }
    // 执行自主移动
    public void auto_move(){
        if(isAutoMoving){
            Point petPosition = this.getPetPosition();
            double distanse = GUtil.getDistanse2Point(autoMoveTarget,petPosition);
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
                autoMoveTarget = GUtil.getRandomPointOnScreen();
                isAutoMoving = true;
                moveNum = 0;
            }
        }
    }
    // 说话
    public void slowAuto_talk(){
        // TODO 修改消息气泡内容
        if(isFree()){
            if(talkNum<talkThreshold){talkNum++;}
            else{
                if(launcher!=null){
                    String[] messageBubbleList = playPetData.getMessageBubbleList();
                    if(messageBubbleList.length>0){
                        String message = messageBubbleList[(int)Math.round(Math.random()*(messageBubbleList.length-1))];
                        PetMessageBubble messageBubble = new PetMessageBubble(message);
                        Point bubblePos = new Point(
                            getPetPosition().x-animationSprite.getSize().width/2,
                            getPetPosition().y-animationSprite.getSize().height/2
                        );
                        messageBubble.setLocation(bubblePos);
                        launcher.addItemToScreen(messageBubble);
                        messageBubble.revalidate();
                        messageBubble.repaint();
                    }
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
        if(dx>0&&!animationSprite.flip_h){animationSprite.flip_h=true;}
        else if(dx<0&&animationSprite.flip_h){animationSprite.flip_h=false;}
    }
    // 保存桌宠游玩数据
    public void savePetData(){
        String jsonString = GsonUtil.bean2Json(playPetData);
        GUtil.saveToFile(savePath,jsonString);
    }
    // 反应值置零
    public void zeroingResponseNum(){
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
        zeroingResponseNum();
        isResting = true;
    }
    // 屏幕截图
    public BufferedImage copyScreenImage(){
        Rectangle rectangle = new Rectangle(GUtil.SCREEN_dimension);
        BufferedImage bufferedImage = robot.createScreenCapture(rectangle);
        ImageSelection imageSelection = new ImageSelection(bufferedImage);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(imageSelection,null);
        return bufferedImage;
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
    // 指定播放目标动画
    public void playTargetAnimation(String animationName,int time){
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                isTargetAnimationPlaying = true;
                animationSprite.setAndPlayAnimation(animationName);
            }
        },50); // 保留微小延迟确保调用
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                isTargetAnimationPlaying = false;
                animationSprite.setAndPlayAnimation(animationName);
                timer.cancel();
            }
        },time+50);
    }
}
