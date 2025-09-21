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
    private boolean isDebug = false;
    private boolean isListen = false;
    public GlobalInputListener(){}
    public int getKeyPressCount(){return keyPressCount;}
    public int getMousePressCount(){return mousePressCount;}
    public boolean isDebug(){return isDebug;}
    public boolean isListen(){return isListen;}
    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }
    public void setListen(boolean isListen) {
        this.isListen = isListen;
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
    // 重置计数
    public void resetCount(){
        keyPressCount = 0;
        mousePressCount = 0;
    }
    // 键盘事件
    public void nativeKeyPressed(NativeKeyEvent e){
        if(isListen){
            keyPressCount++;
            if(isDebug){System.out.println(NativeKeyEvent.getKeyText(e.getKeyCode()));}
        }
    }
    public void nativeKeyReleased(NativeKeyEvent e){
        if(isListen){
            if(isDebug){System.out.println(NativeKeyEvent.getKeyText(e.getKeyCode()));}
        }
    }
    // 鼠标事件
    public void nativeMouseClicked(NativeMouseEvent e){
        if(isListen){
            if(isDebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
        }
    }
    @Override
    public void nativeMousePressed(NativeMouseEvent e){
        if(isListen){
            mousePressCount++;
            if(isDebug){System.out.println(e.getButton());}
        }
    }
    @Override
    public void nativeMouseReleased(NativeMouseEvent e){
        if(isListen){
            if(isDebug){System.out.println(e.getButton());}
        }
    }
    @Override
    public void nativeMouseMoved(NativeMouseEvent e){
        if(isListen){
            if(isDebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
        }
    }
    @Override
    public void nativeMouseDragged(NativeMouseEvent e){
        if(isListen){
            if(isDebug){System.out.println(e.getButton()+"("+e.getX()+","+e.getY()+")");}
        }
    }
}
