package com.pinkcandy.screenwolfgame;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolfpet.Zhou;

// 启动器
public class Launcher {
    public Launcher(){
        TransparentScreen screen = new TransparentScreen(GArea.SCREEN_dimension);
        PetBase petBase1 = new Zhou();
        screen.add(petBase1);
        new WindowBase();
    };
}
