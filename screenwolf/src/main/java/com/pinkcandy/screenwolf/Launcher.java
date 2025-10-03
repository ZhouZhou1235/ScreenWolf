package com.pinkcandy.screenwolf;

import java.awt.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.part.GameTray;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GlobalInputListener;
import com.pinkcandy.screenwolf.windows.AboutWindow;
import com.pinkcandy.screenwolf.windows.TransparentScreen;
import com.pinkcandy.screenwolf.windows.WelcomeWindow;


/**
 * 启动器
 * 加载游戏所需的一切事物
 */
public class Launcher {
    private TransparentScreen screen;
    private ArrayList<PetBase> petList;
    private WelcomeWindow welcomeWindow;
    private AboutWindow infoWindow;
    private GameTray gameTray;
    private GlobalInputListener globalInputListener;
    private Timer updateTimer;
    private Timer slowUpdateTimer;
    public Launcher(){
        try{
            GUtil.initFileDirs();
            GUtil.initGlobalFont(GUtil.DEFAULT_font);
            this.screen = new TransparentScreen(GUtil.SCREEN_dimension);
            this.petList = new ArrayList<>();
            this.welcomeWindow = new WelcomeWindow(this);
            this.infoWindow = new AboutWindow();
            this.gameTray = new GameTray(this);
            this.globalInputListener = new GlobalInputListener();
            this.updateTimer = new Timer(GUtil.GAME_updateTime,e->{
                updatePetAutoLoop();
            });this.updateTimer.start();
            this.slowUpdateTimer = new Timer(GUtil.GAME_slowUpdateTime,e->{
                updatePetSlowAutoLoop();
            });this.slowUpdateTimer.start();
            globalInputListener.startListening();
            gameTray.addSeparator();
            gameTray.addMenuItem("stop game", e->stopGame());
            gameTray.addMenuItem("force exit game", e->System.exit(0));
        }
        catch(Exception e){
            System.err.println(e);
            JOptionPane.showMessageDialog(
                null,
                String.format("启动失败 launch failed:"+e),
                "ERROR",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
        }
    };
    public GameTray getGameTray() {
        return gameTray;
    }
    public GlobalInputListener getGlobalInputListener() {
        return globalInputListener;
    }
    public AboutWindow getInfoWindow() {
        return infoWindow;
    }
    public ArrayList<PetBase> getPetList() {
        return petList;
    }
    public TransparentScreen getScreen() {
        return screen;
    }
    public WelcomeWindow getWelcomeWindow() {
        return welcomeWindow;
    }
    // 开始游戏
    public void playGame(){
        for(PetBase pet:petList){screen.add(pet);}
        screen.setVisible(true);
        welcomeWindow.updateWindowToPlayState();
        welcomeWindow.setVisible(false);
        infoWindow.setVisible(false);
        globalInputListener.setListen(true);
    }
    // 结束游戏
    public void stopGame(){
        for(PetBase pet:petList){pet.dispose();pet=null;}
        petList.clear();
        welcomeWindow.updateWindowToStopState();
        screen.setVisible(false);
        welcomeWindow.setVisible(true);
        globalInputListener.setListen(false);
        globalInputListener.resetCount();
    }
    // 重新加载启动器
    public void reloadLauncher(){
        petList.clear();
        welcomeWindow.dispose();welcomeWindow=new WelcomeWindow(this);
        infoWindow.dispose();infoWindow=new AboutWindow();
    }
    // 获取宠物列表的副本
    public ArrayList<PetBase> getPetListCopy(){return new ArrayList<>(petList);}
    // 添加桌宠到启动器
    public void addPetToLauncher(PetBase pet){petList.add(pet);}
    // 清空屏幕组件（保留桌宠）
    public void clearScreenItems(){
        Component[] items = screen.getAllComponents();
        for(Component item:items){
            if(item instanceof PetBase){continue;}
            screen.remove(item);
        }
    }
    // 宠物的高速逻辑循环
    public void updatePetAutoLoop(){
        for(PetBase pet:petList){
            pet.autoLoop();
            pet.animationSprite.updateFrameDisplay();
        }
    }
    // 宠物的低速逻辑循环
    public void updatePetSlowAutoLoop(){
        for(PetBase pet:petList){
            pet.slowAutoLoop();
        }
    }
}
