package com.pinkcandy.screenwolf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;


/**
 * 屏幕透明窗体
 * 在屏幕上覆盖一个透明窗体，所有物体都会添加到这个窗体上。
 */
public class TransparentScreen extends WindowBase {
    private Timer renderTimer;
    private JPanel contentPane;
    public TransparentScreen(Dimension size){
        this.setTitle("ScreenWolf TransparentScreen");
        this.setBounds(0,0,size.width,size.height);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setBackground(new Color(0,0,0,0));
        this.setLayout(null);
        this.setType(JFrame.Type.UTILITY);
        this.setVisible(true);
        setWindowTopMost(this);
        renderTimer = new Timer(GUtil.GAME_renderTime,e->{
            this.repaint();
            this.update(getGraphics());
        });
        renderTimer.start();
        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setOpaque(false);
        this.setContentPane(contentPane);
    }
    private void setWindowTopMost(JFrame window){
        User32 user32 = User32.INSTANCE;
        WinDef.HWND hwnd = new WinDef.HWND(Native.getComponentPointer(window));
        user32.SetWindowPos(
            hwnd,
            new WinDef.HWND(new Pointer(-1)),
            0,0,0,0,
            WinUser.SWP_NOMOVE | WinUser.SWP_NOSIZE | WinUser.SWP_NOACTIVATE
        );
    }
    public Component[] getAllComponents(){
        return contentPane.getComponents();
    }
    @Override
    public Component add(Component comp){
        return contentPane.add(comp);
    }
}
