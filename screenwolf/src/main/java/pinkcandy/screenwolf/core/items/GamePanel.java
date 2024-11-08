package pinkcandy.screenwolf.core.items;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;
import pinkcandy.screenwolf.core.Wolf;

/**
 * 游戏面板
 */
public class GamePanel extends JPanel {
    Window window; // 游戏窗口
    Wolf myWolf; // 宠物狼
    Map<String,String> userInfo; // 玩家数据
    JLabel wolfInfo; // 面板内容
    public boolean showPanel; // 是否展示面板
    Timer updateTimer; // 帧刷新计时器
    public GamePanel(Window wi,Wolf wo){
        new GTools();
        window = wi;
        myWolf = wo;
        userInfo = GTools.decodeJsonObject(GTools.getContentFromFile(GArea.GAME_USER+"userData"));
        wolfInfo = new JLabel();add(wolfInfo);
        wolfInfo.setForeground(new Color(195,38,34));
        showPanel = true;
        updateTimer = new Timer(GArea.GAME_FREQUENY*10,e->{
            if(showPanel){if(!isVisible()){setVisible(true);}}
            else{if(isVisible()){setVisible(false);}}
            wolfInfo.setText(
                "<html>"+
                userInfo.get("name")+"的宠物狼："+myWolf.data.get("wolfname")+"<br>"+
                userInfo.get("info")+"<br>"+
                " 好感="+myWolf.favor+
                " 清洁="+myWolf.clean+
                " 饱食="+myWolf.food+
                " 精力="+myWolf.spirit+"<br>"+
                " 可移动-"+myWolf.canMove+
                " 正在移动-"+myWolf.moving+
                " 被拖拽-"+myWolf.dragged+
                " 被抚摸-"+myWolf.touched+
                " 被清洗-"+myWolf.cleaned+
                " 低状态-"+myWolf.lowState+
                " 跟随-"+myWolf.followMouse+
                "</html>"
            );
            this.updateUI();this.repaint();
        });updateTimer.start();
        // 点按面板隐藏
        this.addMouseListener(new MouseAdapter(){
            @Override public void mousePressed(MouseEvent e){
                showPanel = false;
            }
        });
        // 窗体设置
        setLocation(0,GTools.SCREEN_HEIGHT/10);
        setSize(GTools.SCREEN_WIDTH,GTools.SCREEN_HEIGHT/4);
        setBackground(new Color(0,0,0,0));
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setVisible(true);
    }
}
