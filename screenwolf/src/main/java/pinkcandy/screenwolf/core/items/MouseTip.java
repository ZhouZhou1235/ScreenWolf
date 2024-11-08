package pinkcandy.screenwolf.core.items;

import java.awt.Color;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pinkcandy.screenwolf.GArea;
import pinkcandy.screenwolf.GTools;

/**
 * 鼠标提示
 * 提示玩家鼠标当前的模式
 */
public class MouseTip extends JPanel {
    public JLabel texture; // 鼠标提示贴图
    public MouseTip(Point point){
        new GTools();
        texture = new JLabel();
        setTip(GArea.MOUSE_normal);
        setLocation(point);
        setSize(GTools.BODY_WIDTH/4,GTools.BODY_HEIGHT/4);
        setBackground(new Color(0,0,0,0));
        setVisible(true);
        add(texture);
    }
    // 设置提示图标
    public void setTip(int tip){
        String url = "";
        switch(tip){
            case GArea.MOUSE_normal:
                texture.setIcon(null);
                repaint();update(getGraphics());
                return;
            case GArea.MOUSE_clean:url=GArea.MOUSETIP_clean;break;
            case GArea.MOUSE_food:url=GArea.MOUSTIP_food;break;
            case GArea.MOUSE_play:url=GArea.MOUSETIP_play;break;
            default:return;
        }
        ImageIcon image = GTools.scaleImageIcon(new ImageIcon(url),GTools.BODY_WIDTH/4);
        texture.setIcon(image);
        repaint();update(getGraphics());
    }
}
