package code.core;

import javax.swing.JFrame;
import javax.swing.Timer;

import java.awt.event.MouseEvent;
import code.GArea;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseAdapter;
import code.GAreaClass;

/**
 * Window
 * 程序总窗口
 */
public class Window extends JFrame {
    Timer updateTimer; // 窗口刷新计时器
    int gameFrequency = GArea.GAME_FREQUENY;
    Point point = new Point(); // 鼠标点
    public Window(){
        init();
        updateTimer = new Timer(gameFrequency,e->{
            repaint();
            update(getGraphics());
        });
        updateTimer.start();
    }
    // 窗口初始化
    private void init(){
        GAreaClass gAreaClass = new GAreaClass();
        this.setTitle(GArea.GAME_TITLE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,gAreaClass.SCREEN_WIDTH,gAreaClass.SCREEN_HEIGHT);
        this.setSize(gAreaClass.SCREEN_WIDTH,gAreaClass.SCREEN_HEIGHT);
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLayout(null);
        this.setType(JFrame.Type.UTILITY);
        this.setVisible(true);
    }
    // 总窗口对狼的事件监听
    public void listenWolf(Wolf wolf){
        // 鼠标监听器
        wolf.addMouseListener(new MouseAdapter(){
            // 鼠标按下时
            @Override public void mousePressed(MouseEvent e){
                if(wolf.stateNum==GArea.STATE_rest){wolf.resetWolf();}
                if(e.getButton()==MouseEvent.BUTTON1){
                    point.x = e.getX();
                    point.y = e.getY();
                    wolf.setAction(GArea.ACT_press);
                    wolf.dragged = true;
                }
            }
            // 鼠标点击时
            @Override public void mouseClicked(MouseEvent e){}
            // 鼠标释放时
            @Override public void mouseReleased(MouseEvent e){
                wolf.dragged = false;
                wolf.setAction(GArea.ACT_default);
            }
            // 鼠标进入时
            @Override public void mouseEntered(MouseEvent e){
                if(wolf.stateNum==GArea.STATE_normal && !wolf.dragged){wolf.focusWolf();}
            }
            // 鼠标离开时
            @Override public void mouseExited(MouseEvent e){
                if(wolf.stateNum==GArea.STATE_normal && !wolf.dragged){
                    wolf.setAction(GArea.ACT_default);
                    wolf.canMove=true;
                }
            }
        });
        // 鼠标动作监听器
        wolf.addMouseMotionListener(new MouseMotionAdapter(){
            // 鼠标拖拽时
            @Override
            public void mouseDragged(MouseEvent e){
                if(wolf.dragged){
                    Point p = wolf.getLocation();
                    // 窗口位置 + 鼠标在窗口位置 - 鼠标按下时在窗口位置
                    wolf.setLocation(p.x+e.getX()-point.x,p.y+e.getY()-point.y);    
                }
            }
            // 鼠标移动时
            @Override
            public void mouseMoved(MouseEvent e){
                if(wolf.stateNum==GArea.STATE_normal){wolf.touchWolf();}
            }
        });
    }
}
