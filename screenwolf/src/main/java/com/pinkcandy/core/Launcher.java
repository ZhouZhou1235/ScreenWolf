package com.pinkcandy.core;

import java.awt.Dimension;

import com.pinkcandy.GArea;
import com.pinkcandy.core.base.WindowBase;

// 启动器
public class Launcher {
    public Launcher(){
        Dimension welcomeSize = new Dimension(GArea.SCREEN_dimension.width/4,GArea.SCREEN_dimension.height/4);
        WindowBase welcome = new WindowBase("ScreenWolf",welcomeSize);
        GArea.setWindowCenter(welcome);
    }
}
