package com.pinkcandy;

import com.pinkcandy.screenwolf.base.PetBase;

// 桌宠示例
public class Pet_Demo extends PetBase {

    // 创建自己的成员变量
    // protected boolean isPlaying;
    // ...

    // 【必须】构造方法 必须重写并调用父类构造
    // 【重要】类名是宠物的唯一标识，应该避免冲突。
    public Pet_Demo(Launcher launcher){super(launcher);}

    // 重写以实现自定义行为

    // 初始化
    @Override
    public void initPet(){
        super.initPet();
        // 调整成员变量
        // ...
    }
    // 鼠标事件
    @Override
    public void ready_addMouseAction(){
        super.ready_addMouseAction();
        // 更多鼠标监听处理......
    }
    // 宠物动画
    @Override
    public void auto_playAnimations(){
        super.auto_playAnimations();
        // 自定义动画......
    }

    // 可以阅读 PetBase 源码写更多定制化动作

}
