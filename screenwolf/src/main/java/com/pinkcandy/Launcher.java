package com.pinkcandy;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;

// 启动器
public class Launcher {
    private TransparentScreen screen; // 全屏透明窗体
    private WindowBase welcomeWindow;
    private JPanel welcomePanel;
    public Launcher(){
        this.screen = new TransparentScreen(GArea.SCREEN_dimension);
        this.welcomePanel = new JPanel(new GridLayout(4,1,10,10));
        GArea.initFileDirs();
        initGlobalFont(new Font("SansSerif",Font.BOLD,GArea.DEFAULT_textSize));
        initWelcomeWindow();
        loadPetButtons();
        this.welcomeWindow.updateWindow();
    };
    // 初始化开始窗口
    public void initWelcomeWindow(){
        welcomeWindow = new WindowBase("ScreenWolf",GArea.DEFAULT_windowSize);
        GArea.setWindowCenter(welcomeWindow);
        JLabel titleLabel = new JLabel("屏幕有狼");
        welcomePanel.add(titleLabel);
        welcomeWindow.add(welcomePanel);
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
    // 加载宠物按钮
    private void loadPetButtons(){
        String[] petsList = GArea.scanDir(GArea.GAME_petsPath);
        for(String petid:petsList){
            JButton petButton = new JButton(petid);
            petButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    createPet(petid);
                }
            });
            welcomePanel.add(petButton);
        }
    }
    // 创建桌宠
    private void createPet(String petid){
        PetBase pet = (PetBase)GArea.loadObjFromJarByClass(
            GArea.GAME_petsPath+petid+"\\"+petid+".jar",
            "com.pinkcandy."+petid
        );
        screen.add(pet);
    }
}
