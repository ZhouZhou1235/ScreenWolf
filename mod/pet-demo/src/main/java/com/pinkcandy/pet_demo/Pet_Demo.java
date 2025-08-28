package com.pinkcandy.pet_demo;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;

// 桌宠示例
// 这是屏幕有狼游戏标准的最简宠物实现
// 开发参考 PetBase.java
public class Pet_Demo extends PetBase {

    // 【可选开发】创建这只宠物自己的成员变量
    // ...

    // 【必须】显式定义该构造方法，类名是宠物的唯一标识，应该避免冲突。
    public Pet_Demo(Launcher launcher){super(launcher);}

    // 【可选开发】定义这只宠物的行为
    // ......
}
