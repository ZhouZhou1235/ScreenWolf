package com.pinkcandy.screenwolf.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;


/**
 * Windows全局输入事件监听器
 * 监听电脑全局的键盘和鼠标事件
 */
public class GlobalInputListener implements NativeKeyListener,NativeMouseListener,NativeMouseMotionListener {
    private int keyPressCount = 0;
    private int mousePressCount = 0;
    public boolean isdebug = false;
    public GlobalInputListener(){}
    public int getKeyPressCount() {
        return keyPressCount;
    }
    public int getMousePressCount() {
        return mousePressCount;
    }
    // 开始监听
    public void startListening(){
        try{
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeMouseMotionListener(this);
        }catch(NativeHookException e){
            System.err.println(e.getMessage());
        }
    }
    // 停止监听
    public void stopListening(){
        try{
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.removeNativeMouseListener(this);
            GlobalScreen.removeNativeMouseMotionListener(this);
            GlobalScreen.unregisterNativeHook();
        }catch(NativeHookException e){
            System.err.println(e.getMessage());
        }
    }
    // 键盘事件
    public void nativeKeyPressed(NativeKeyEvent e){
        keyPressCount++;
        if(isdebug){System.out.println(NativeKeyEvent.getKeyText(e.getKeyCode()));}
    }
    public void nativeKeyReleased(NativeKeyEvent e){
        if(isdebug){System.out.println(NativeKeyEvent.getKeyText(e.getKeyCode()));}
    }
    // 鼠标事件
    public void nativeMouseClicked(NativeMouseEvent e){
        if(isdebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
    }
    @Override
    public void nativeMousePressed(NativeMouseEvent e){
        mousePressCount++;
        if(isdebug){System.out.println(e.getButton());}
    }
    @Override
    public void nativeMouseReleased(NativeMouseEvent e){
        if(isdebug){System.out.println(e.getButton());}
    }
    @Override
    public void nativeMouseMoved(NativeMouseEvent e){
        if(isdebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
    }
    @Override
    public void nativeMouseDragged(NativeMouseEvent e){
        if(isdebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
    }
}
