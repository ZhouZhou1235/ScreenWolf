package com.pinkcandy;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.base.PetBase;

// 测试宠物
public class Test {
    public static void main(String[] args){
        TransparentScreen screen = new TransparentScreen(GArea.SCREEN_dimension);
        PetBase pet = new ScreenWolf_Demo();
        screen.add(pet);
    }
}
