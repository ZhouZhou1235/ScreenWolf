package com.pinkcandy.screenwolf_demo;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;

// 桌宠示例
public class Pet extends PetBase {
    // 【必须】显式定义构造方法
    public Pet(Launcher launcher){super(launcher);}
    public Pet(Launcher launcher,String jarPath){super(launcher,jarPath);}

    // 【开发】定义这只宠物的行为
    // ......
}
