package com.pinkcandy.pet_demo;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;

// 桌宠示例
public class Pet_Demo extends PetBase {

    // 创建自己的成员变量
    // ...

    // 【必须】构造方法 必须重写并调用父类构造
    // 【重要】类名是宠物的唯一标识，应该避免冲突。
    public Pet_Demo(Launcher launcher){super(launcher);}

    // 重写以实现自定义行为
    // 阅读 PetBase 源码写更多定制化动作
}
