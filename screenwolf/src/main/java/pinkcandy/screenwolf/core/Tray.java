package pinkcandy.screenwolf.core;

import pinkcandy.screenwolf.GArea;
import java.awt.AWTException;
import java.awt.TrayIcon;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import javax.swing.ImageIcon;

/**
 * 托盘
 */
public class Tray {
    Window window; // 游戏窗口
    Wolf wolf; // 宠物狼
    public Tray(Window wi){
        window = wi;
        wolf = wi.myWolf;
        loadTray();
    }
    // 加载系统托盘
    private void loadTray(){
        if(!SystemTray.isSupported()){System.exit(0);}
        SystemTray tray = SystemTray.getSystemTray();
        PopupMenu menu = menuContent(window);
        ImageIcon icon = new ImageIcon(GArea.GAME_ICON);
        Image image = icon.getImage().getScaledInstance(16,16,Image.SCALE_DEFAULT);
        TrayIcon trayIcon = new TrayIcon(image,GArea.GAME_TITLE,menu);
        try{tray.add(trayIcon);}
        catch(AWTException e){e.printStackTrace();}
    }
    // 托盘菜单
    private PopupMenu menuContent(Window window){
        PopupMenu menu = new PopupMenu();
        // 显示游戏窗口
        MenuItem item1 = new MenuItem("show window");
        item1.addActionListener(e->{window.setVisible(true);});
        // 隐藏游戏窗口
        MenuItem item2 = new MenuItem("hide window");
        item2.addActionListener(e->{window.setVisible(false);});
        // 重置狼
        MenuItem item3 = new MenuItem("reset wolf");
        item3.addActionListener(e->{wolf.resetWolf();wolf.saveDataToServer();});
        // 让狼休息
        MenuItem item4 = new MenuItem("rest wolf");
        item4.addActionListener(e->{wolf.rest();wolf.saveDataToServer();});
        // 切换鼠标模式
        MenuItem item5 = new MenuItem("change mouse mode");
        item5.addActionListener(e->{window.changeMouseMode();});
        // 清除和隐藏所有物品
        MenuItem item6 = new MenuItem("clear and hide items");
        item6.addActionListener(e->{
            window.clearAllFood();
            window.toyBone.setVisible(false);
            window.gamePanel.showPanel = false;
        });
        // 显示面板
        MenuItem item7 = new MenuItem("show game panel");
        item7.addActionListener(e->{
            window.gamePanel.showPanel = true;
        });
        // 停止活动事件
        MenuItem item8 = new MenuItem("stop active event");
        item8.addActionListener(e->{wolf.stopActiveEvent();});
        // 退出
        MenuItem itemExit = new MenuItem("exit game");
        itemExit.addActionListener(e->{
            wolf.saveDataToServer();
            System.exit(0);
        });
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(item4);
        menu.add(item5);
        menu.add(item6);
        menu.add(item7);
        menu.add(item8);
        menu.add(itemExit);
        return menu;
    }
}
