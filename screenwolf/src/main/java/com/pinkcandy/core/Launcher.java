package com.pinkcandy.core;

import java.awt.Dimension;
import java.util.HashMap;

import com.pinkcandy.GArea;
import com.pinkcandy.core.base.PetBase;
import com.pinkcandy.core.base.WindowBase;

// 启动器
public class Launcher {
    private WindowBase beginWindow;
    private TransparentScreen screen;
    public Launcher(){
        // 开始窗口
        beginWindow = new WindowBase("ScreenWolf",new Dimension(GArea.SCREEN_dimension.width/4,GArea.SCREEN_dimension.height/4));
        screen = new TransparentScreen(GArea.SCREEN_dimension);
        GArea.setWindowCenter(beginWindow);

        // 开始游戏
        Dimension petSize = new Dimension(GArea.SCREEN_dimension.width/10,GArea.SCREEN_dimension.width/10);
        HashMap<String,String> animations = new HashMap<>();
        String petFrames = GArea.GAME_workPath+"/screenwolf/src/main/assets/images/PetFrames";
        animations.put("default", petFrames+"/zhou/default/");
        PetBase wolf1 = new PetBase(petSize,animations);
        screen.add(wolf1);
    }
}
