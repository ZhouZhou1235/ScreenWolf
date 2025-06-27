package com.pinkcandy.screenwolf;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.pinkcandy.Launcher;

// 系统托盘
public class GameTray {
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private PopupMenu popupMenu;
    private Launcher launcher;
    public GameTray(Launcher theLauncher){
        Image iconImage = new ImageIcon(GArea.GAME_workPath+"/assets/images/icon.png").getImage();
        String tooltip = "ScreenWolf - 屏幕有狼";
        this.systemTray = SystemTray.getSystemTray();
        this.popupMenu = new PopupMenu();
        this.trayIcon = new TrayIcon(iconImage,tooltip,popupMenu);
        this.launcher = theLauncher;
        this.trayIcon.setImageAutoSize(true);
        // 点击托盘事件
        trayIcon.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getButton()==MouseEvent.BUTTON1){launcher.welcomeWindow.setVisible(true);}
            }
        });
        try{systemTray.add(trayIcon);}catch(AWTException e){throw new RuntimeException(e);}
        // 托盘选项
        addMenuItem("显示开始",new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                launcher.welcomeWindow.setVisible(true);
            }
        });
        // addSeparator();
        // ...
    }
    // 添加托盘选项
    public void addMenuItem(String label,ActionListener listener){
        MenuItem menuItem = new MenuItem(label);
        menuItem.addActionListener(listener);
        popupMenu.add(menuItem);
    }
    // 添加分隔线
    public void addSeparator(){popupMenu.addSeparator();}
}
