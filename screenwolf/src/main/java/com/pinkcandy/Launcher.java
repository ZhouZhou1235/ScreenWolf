package com.pinkcandy;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;

// 启动器
public class Launcher {
    private TransparentScreen screen;
    private WindowBase welcomeWindow;
    private JPanel welcomePanel;
    private ArrayList<PetBase> petsList;
    private JButton playButton;
    private JButton clearButton;
    private ArrayList<JButton> petButtonsList;
    public Launcher(){
        this.screen = new TransparentScreen(GArea.SCREEN_dimension);
        this.welcomePanel = new JPanel(new GridLayout(4,1,10,10));
        this.petsList = new ArrayList<>();
        this.petButtonsList = new ArrayList<>();
        GArea.initFileDirs();
        GArea.initGlobalFont(new Font("SansSerif",Font.BOLD,GArea.DEFAULT_textSize));
        initWelcomeWindow();
        loadPetButtons();
        this.welcomeWindow.updateWindow();
    };
    // 初始化开始窗口
    public void initWelcomeWindow(){
        welcomeWindow = new WindowBase("ScreenWolf",GArea.DEFAULT_windowSize);
        GArea.setWindowCenter(welcomeWindow);
        JLabel titleLabel = new JLabel("ScreenWolf");
        playButton = new JButton("play");
        clearButton = new JButton("clear pets");
        clearButton.setEnabled(false);
        playButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                for(PetBase pet:petsList){screen.add(pet);}
                for(JButton petButton:petButtonsList){petButton.setEnabled(false);}
                screen.setVisible(true); // 问题：这行没有就不显示宠物
                playButton.setEnabled(false);
                clearButton.setEnabled(true);
                // welcomeWindow.setVisible(false);
            }
        });
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                for(PetBase pet:petsList){screen.remove(pet);pet=null;}
                for(JButton petButton:petButtonsList){petButton.setEnabled(true);}
                petsList.clear();
                playButton.setEnabled(true);
                clearButton.setEnabled(false);
                System.gc();
            }
        });
        welcomePanel.add(titleLabel);
        welcomePanel.add(playButton);
        welcomePanel.add(clearButton);
        welcomeWindow.add(welcomePanel);
    }
    // 加载宠物按钮
    private void loadPetButtons(){
        String[] petsList = GArea.scanDir(GArea.GAME_petsPath);
        for(String petid:petsList){
            JButton petButton = new JButton(petid);
            petButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    addPetToList(petid);
                    petButton.setEnabled(false);
                }
            });
            welcomePanel.add(petButton);
            petButtonsList.add(petButton);
        }
    }
    // 添加桌宠到列表
    private void addPetToList(String petid){
        PetBase pet = (PetBase)GArea.loadObjFromJarByClass(
            GArea.GAME_petsPath+petid+"\\pet.jar",
            "com.pinkcandy."+petid
        );
        petsList.add(pet);
    }
}
