package code.core;

import code.GArea;
import java.awt.AWTException;
import java.awt.TrayIcon;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import javax.swing.ImageIcon;

/**
 * Tray
 * 托盘
 */
public class Tray {
    public Tray(Window wi,Wolf wo){init(wi,wo);}
    Window window;
    Wolf wolf;
    // 托盘初始化
    private void init(Window wi,Wolf wo){
        window = wi;
        wolf = wo;
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
        // 显示狼
        MenuItem item1 = new MenuItem("show wolf");
        item1.addActionListener(e->{window.setVisible(true);wolf.resetWolf();});
        menu.add(item1);
        // 隐藏狼
        MenuItem item2 = new MenuItem("hide wolf");
        item2.addActionListener(e->{window.setVisible(false);wolf.rest();});
        menu.add(item2);
        // 重置狼
        MenuItem item3 = new MenuItem("reset wolf");
        item3.addActionListener(e->{wolf.resetWolf();});
        menu.add(item3);
        // 让狼睡觉
        MenuItem item4 = new MenuItem("rest wolf");
        item4.addActionListener(e->{wolf.rest();});
        menu.add(item4);
        // 退出
        MenuItem item5 = new MenuItem("exit game");
        item5.addActionListener(e->System.exit(0));
        menu.add(item5);
        return menu;
    }
}
