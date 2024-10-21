package code.core;

import code.GArea;
import code.GAreaClass;
import code.wolves.WolfZhou;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Launcher
 * 启动器
 */
public class Launcher extends JFrame {
    JPanel rootPanel;
    public Launcher(){
        initGlobalFont(new Font("SansSerif",Font.BOLD,24));
        rootPanel = new JPanel();
        this.setContentPane(rootPanel);
        GAreaClass gAreaClass = new GAreaClass();
        this.setSize(gAreaClass.SCREEN_WIDTH/2,gAreaClass.SCREEN_HEIGHT/2);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle(GArea.GAME_TITLE);
        this.setResizable(false);
        this.setVisible(true);
        loadInfo();
    }
    // 开始运行桌宠
    public void begin(int wolfID){
        Window window = new Window();
        // ===载入狼===
        if(wolfID==1){
            Wolf wolf = new WolfZhou();
            window.listenWolf(wolf);
            window.add(wolf);
            new Tray(window,wolf);
        }
        else{System.out.println("can not find wolf");System.exit(0);}
        this.setVisible(false);
    }
    // 加载开始界面
    private void loadInfo(){
        rootPanel.add(new JLabel(Tools.scaleImageIcon(new ImageIcon(GArea.GAME_LOGO),this.getWidth())));
        // ===添加狼按钮===
        putWolfButton("小蓝狗",1);
        this.repaint();
        rootPanel.updateUI();
    }
    // 全局字体
	private void initGlobalFont(Font font){
		FontUIResource fontRes = new FontUIResource(font);
		for(Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements();){
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof FontUIResource){
                UIManager.put(key,fontRes);
            }
		}
	}
    private void putWolfButton(String text,int wolfID){
        JButton button = new JButton(text);
        button.addActionListener(new ActionListener(){
            @Override public void actionPerformed(ActionEvent e){begin(wolfID);}
        });
        rootPanel.add(button);
    }
}
