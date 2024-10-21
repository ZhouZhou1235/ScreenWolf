package code.core;

import java.awt.Point;
import java.util.Map;
import java.util.Timer;
import javax.swing.JPanel;

/**
 * <h2>Wolf</h2>
 * <h3>狼的抽象</h3>
 * 一只桌面宠物狼的基本描述
 * 需要继承描述出来
 */
public abstract class Wolf extends JPanel {
    public Map<String,String> data;
    public WolfAnimation body;
    public int bodyWidth;
    public int bodyHeight;
    public int stateNum;
    public int speed;
    public int touchNum;
    public boolean canMove;
    public boolean moving;
    public boolean dragged;
    public boolean touched;
    public Timer autoTimer;
    // 生成狼
    public abstract void create(Map<String,String> theData);
    // 重置狼
    public abstract void resetWolf();
    // 活动事件
    public abstract void activeEvent();
    // 设置动作
    public abstract void setAction(int action);
    // 移动
    public abstract void move(Point pos);
    // 休息
    public abstract void rest();
    // 被聚焦
    public abstract void focusWolf();
    // 被抚摸
    public abstract void touchWolf();
}
