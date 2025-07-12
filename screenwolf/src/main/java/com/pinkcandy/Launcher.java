package com.pinkcandy;

import java.awt.*;
import java.util.*;

import com.pinkcandy.screenwolf.GameTray;
import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.windows.InfoWindow;
import com.pinkcandy.screenwolf.windows.PetCreateorWindow;
import com.pinkcandy.screenwolf.windows.TransparentScreen;
import com.pinkcandy.screenwolf.windows.WelcomeWindow;

// 启动器
public class Launcher {
    private TransparentScreen screen;
    private ArrayList<PetBase> petList;
    public WelcomeWindow welcomeWindow;
    public InfoWindow infoWindow;
    public PetCreateorWindow petCreateorWindow;
    public GameTray gameTray;
    public Launcher(){
        GUtil.initFileDirs();
        GUtil.initGlobalFont(GUtil.DEFAULT_font);
        this.screen = new TransparentScreen(GUtil.SCREEN_dimension);
        this.petList = new ArrayList<>();
        this.gameTray = new GameTray(this);
        this.screen.setVisible(false);
        this.welcomeWindow = new WelcomeWindow(this);
        this.infoWindow = new InfoWindow();
        this.infoWindow.setVisible(false);
        this.petCreateorWindow = new PetCreateorWindow();
        this.petCreateorWindow.setVisible(false);
    };

    // === 启动器公开方法 ===
    // 开始游戏
    public void playGame(){
        for(PetBase pet:petList){screen.add(pet);}
        screen.setVisible(true);
        welcomeWindow.updateWindowToPlayState();
        welcomeWindow.setVisible(false);
        infoWindow.setVisible(false);
        petCreateorWindow.setVisible(false);
    }
    // 结束游戏
    public void stopGame(){
        for(PetBase pet:petList){pet.dispose();pet=null;}
        petList.clear();
        welcomeWindow.updateWindowToStopState();
        screen.setVisible(false);
        welcomeWindow.setVisible(true);
    }
    // 重新加载启动器
    public void reloadLauncher(){
        petList.clear();
        welcomeWindow.dispose();welcomeWindow=new WelcomeWindow(this);
        infoWindow.dispose();infoWindow=new InfoWindow();infoWindow.setVisible(false);
    }
    // 获取宠物列表的副本
    public ArrayList<PetBase> getPetListCopy(){return new ArrayList<>(petList);}
    // 添加桌宠到启动器
    public void addPetToLauncher(PetBase pet){petList.add(pet);}
    // 添加组件到屏幕
    public void addItemToScreen(ItemBase item){screen.add(item);}
    // 清空屏幕组件（保留桌宠）
    public void clearScreenItems(){
        Component[] items = screen.getComponents();
        for(Component item:items){
            if(item instanceof PetBase){continue;}
            screen.remove(item);
        }
    }
}
