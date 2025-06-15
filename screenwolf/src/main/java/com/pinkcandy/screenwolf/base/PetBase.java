package com.pinkcandy.screenwolf.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.alibaba.fastjson.JSON;
import com.pinkcandy.screenwolf.AnimationSprite;
import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.bean.PlayPetData;

// 桌面宠物
public class PetBase extends JPanel {
    private String id; // 宠物号码
    private Map<String,String> animations; // 动画数据
    private AnimationSprite body; // 动画精灵
    private Point pressPetPoint; // 宠物点按处
    private Timer updateTimer; // 自动动画更新计时器
    private int followDistanse = (int)GArea.DEFAULT_bodySize.getWidth(); // 跟随距离
    private int moveSpeed = (int)GArea.DEFAULT_bodySize.getWidth()/20; // 移动速度
    private PlayPetData playPetData; // 游玩数据
    private String savePath; // 数据保存地址
    public boolean isFollow = false; // 跟随
    public boolean isFocus = false; // 聚焦
    public boolean isPress = false; // 按住
    public boolean isMoving = false; // 移动
    public PetBase(Dimension size,String id){
        String jsonpetdata = GArea.readFile(GArea.GAME_petsPath+id+"\\"+id+".json");
        PetData petData = JSON.parseObject(jsonpetdata).toJavaObject(PetData.class);
        String[] animationNames = petData.getAnimationNames();
        HashMap<String,String> imageFrameHashmap = new HashMap<>();
        for(String animationName:animationNames){
            imageFrameHashmap.put(
                animationName,
                GArea.GAME_petsPath+id+"\\"+"frames"+"\\"+animationName+"\\"
            );
        }
        this.animations = imageFrameHashmap;
        this.id = petData.getId();
        this.body = new AnimationSprite(size,animations);
        this.updateTimer = new Timer(GArea.GAME_petUpdateTime,e->{autoLoop();});this.updateTimer.start();
        this.savePath = GArea.GAME_dataPath+"playpet_"+id+".json";
        this.setSize(size);
        this.setBackground(new Color(0,0,0,0));
        this.add(body);
        this.ready();
    }
    // 获取宠物号码
    public String getid(){return this.id;}
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
    public void gotoPoint(Point point){
        Point petPosition = this.getPetPosition();
        Point o = this.getLocation();
        int moveX = petPosition.x-point.x;
        int moveY = petPosition.y-point.y;
        Point nextPoint = new Point(o);
        if(moveX>moveSpeed){nextPoint.x-=moveSpeed;}
        else if(moveX<-moveSpeed){nextPoint.x+=moveSpeed;}
        if(moveY>moveSpeed){nextPoint.y-=moveSpeed;}
        else if(moveY<-moveSpeed){nextPoint.y+=moveSpeed;}
        this.setLocation(nextPoint);
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
        PetBase petBase = this;
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
            @Override
            public void run(){
                petBase.savePlayPetData();
            }
        }));
    }
    // 添加鼠标事件回应
    public void ready_addMouseAction(){
        PetBase petBase = this;
        petBase.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                super.mousePressed(e);
                isPress = true;
                pressPetPoint = e.getPoint();
                playPetData.setMouseClickNum(playPetData.getMouseClickNum()+1);
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
                isPress = false;
            }
            @Override
            public void mouseClicked(MouseEvent e){
                super.mouseClicked(e);
                int num = e.getClickCount();
                if(num>=2){isFollow = !isFollow;}
            }
        });
        petBase.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                super.mouseDragged(e);
                Point petPosition = petBase.getLocation();
                int x = petPosition.x+e.getX()-pressPetPoint.x;
                int y = petPosition.y+e.getY()-pressPetPoint.y;
                petBase.setLocation(x,y);
            }
        });
    }
    // 自动执行事件
    public void autoLoop(){
        auto_playAnimations();
        auto_followMouse();
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
    // 保存游玩数据
    public void savePlayPetData(){
        String jsonString = GArea.jsonEncode(playPetData);
        GArea.saveToFile(savePath,jsonString);
    }
    // 重写 播放动画
    public void auto_playAnimations(){}
}
