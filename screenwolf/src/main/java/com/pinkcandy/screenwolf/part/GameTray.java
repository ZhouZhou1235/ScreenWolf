package com.pinkcandy.screenwolf.part;

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

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.utils.ResourceReader;


/**
 * 游戏托盘
 * 游戏会在用户电脑系统中创建托盘以控制游戏进程
 */
public class GameTray {
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private PopupMenu popupMenu;
    private Launcher launcher;
    public GameTray(Launcher theLauncher){
        Image iconImage = ResourceReader.getResourceAsImageIcon("images/icon.png").getImage();
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
                if(e.getButton()==MouseEvent.BUTTON1){launcher.getWelcomeWindow().setVisible(true);}
            }
        });
        try{systemTray.add(trayIcon);}catch(AWTException e){throw new RuntimeException(e);}
        // 托盘选项
        addMenuItem("show begin window",new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                launcher.getWelcomeWindow().setVisible(true);
            }
        });
        addMenuItem("clear screen",new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                launcher.clearScreenItems();                
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
