package com.pinkcandy.screenwolf_demo;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.utils.GUtil;


// 宠物测试
public class Test {
    public static void main(String[] args){
        // 指定jar包载入资源
        Launcher launcher = new Launcher();
        Pet pet = new Pet(
            launcher,
            GUtil.GAME_petsPath+"screenwolf_demo-1.0.0.jar"
        );
        launcher.getScreen().add(pet);
    }
}
