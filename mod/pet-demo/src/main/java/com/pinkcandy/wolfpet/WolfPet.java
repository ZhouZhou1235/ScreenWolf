package com.pinkcandy.wolfpet;

import javax.swing.event.MouseInputAdapter;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;

// 桌宠示例
public class WolfPet extends PetBase {

    // 创建自己的成员变量
    // protected boolean isPlaying;
    // ...

    // 【必须】构造方法 必须重写并调用父类构造
    // 【重要】类名是宠物的唯一标识，应该避免冲突。
    public WolfPet(Launcher launcher){super(launcher);}

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
        this.addMouseListener(new MouseInputAdapter(){
            // 更多鼠标监听处理......
        });
    }

    // 宠物动画
    @Override
    public void auto_playAnimations(){
        super.auto_playAnimations();
        // 自定义动画......
    }

    // 情绪表达
    @Override
    public void slowAuto_showEmotion(){
        super.slowAuto_showEmotion();
        // 怎么表达情绪（特殊动画、消息气泡、动作等）......
    }

    // 可以阅读 PetBase 源码写更多定制化动作

}
