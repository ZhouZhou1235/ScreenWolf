package com.pinkcandy.screenwolf.utils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;


/**
 * swing 定时任务工具类
 * 提供swing timer相关的方法
 */
public class SwingTimerUtil {
    // 执行延迟任务
    public static Timer schedule(int delayMs,Runnable task){
        Timer timer = new Timer(delayMs,e->{
            task.run();
            ((Timer)e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
        return timer;
    }
    // 执行循环延迟任务
    public static Timer scheduleLoop(int delayMs,Runnable task){
        Timer timer = new Timer(delayMs,e->{
            task.run();
        });
        timer.setRepeats(true);
        timer.start();
        return timer;
    }
    // 带参执行延迟任务
    public static <T> Timer schedule(int delayMs,Consumer<T> task,T arg){
        Timer timer = new Timer(delayMs,e->{
            task.accept(arg);
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
        return timer;
    }
    // 使用 ActionEvent 执行延迟任务
    public static Timer schedule(int delayMs,ActionListener listener){
        Timer timer = new Timer(delayMs, e->{
            listener.actionPerformed(e);
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
        return timer;
    }
}
