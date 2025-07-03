package com.pinkcandy;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.alibaba.fastjson.JSON;
import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.GameTray;
import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.PetData;

// 启动器
public class Launcher {
    private ArrayList<PetBase> petList;
    private ArrayList<JButton> petButtonsList;
    private JPanel welcomePanel;
    private JPanel petSelectionPanel;
    private JButton playButton;
    private JButton clearButton;
    private JButton reloadButton;
    private JButton exitButton;
    private TransparentScreen screen;
    public WindowBase welcomeWindow;
    public GameTray gameTray;
    public Launcher(){
        GArea.initFileDirs();
        GArea.initGlobalFont(new Font("SansSerif",Font.BOLD,GArea.DEFAULT_textSize));
        this.screen = new TransparentScreen(GArea.SCREEN_dimension);
        this.petList = new ArrayList<>();
        this.petButtonsList = new ArrayList<>();
        initWelcomeWindow();
        this.welcomeWindow.updateWindow();
        this.gameTray = new GameTray(this);
        this.screen.setVisible(false);
    };
    // 初始化开始窗口
    public void initWelcomeWindow(){
        this.welcomePanel = new JPanel(new BorderLayout(10,10));
        this.welcomePanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        welcomeWindow = new WindowBase("ScreenWolf", GArea.DEFAULT_windowSize);
        welcomeWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GArea.setWindowCenter(welcomeWindow);
        loadBasic();
        loadPets();
        // 自动布局 渲染刷新
        welcomePanel.revalidate();
        welcomePanel.repaint();
        // 添加主面板
        welcomeWindow.add(welcomePanel);
    }
    // 加载窗口固定项
    private void loadBasic(){
        ImageIcon logoImageIcon = new ImageIcon(GArea.GAME_workPath+"/assets/images/logo.png");
        logoImageIcon = GArea.scaleImageIcon(logoImageIcon,(int)(GArea.DEFAULT_windowSize.width*0.75));
        // 标题
        JLabel titleLabel = new JLabel(logoImageIcon,SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(),Font.BOLD,GArea.DEFAULT_textSize*2));
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        welcomePanel.add(titlePanel,BorderLayout.NORTH);
        // 选项
        playButton = new JButton("开始游戏");
        clearButton = new JButton("结束游戏");
        reloadButton = new JButton("重新加载");
        exitButton = new JButton("退出程序");
        JPanel buttonPanel = new JPanel(new GridLayout(1,4,10,0));
        buttonPanel.add(playButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(exitButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        clearButton.setEnabled(false);
        playButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){playGame();}
        });
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){stopGame();}
        });
        reloadButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){reloadWelcomeWindow();}
        });
        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    // 加载桌宠
    private void loadPets(){
        petSelectionPanel = new JPanel();
        petSelectionPanel.setLayout(new BoxLayout(petSelectionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(petSelectionPanel);
        welcomePanel.add(scrollPane,BorderLayout.CENTER);
        String[] petsidList = GArea.scanDir(GArea.GAME_petsPath);
        for(String petid:petsidList){
            String jsonpetdata = GArea.readFile(GArea.GAME_petsPath + petid + "/pet_data.json");
            PetData petData = JSON.parseObject(jsonpetdata).toJavaObject(PetData.class);
            JPanel petEntryPanel = new JPanel(new BorderLayout(10,5));
            petEntryPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            JButton petButton = new JButton(petData.getName());
            petButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    PetBase pet = (PetBase)GArea.loadObjFromJarByClass(
                        GArea.GAME_petsPath+petid+"/pet.jar",
                        "com.pinkcandy."+petid,
                        Launcher.this // this 和 class.this 不一样！此处为 class.this 类的对象
                    );
                    petList.add(pet);
                    petButton.setEnabled(false);
                }
            });
            JTextArea petInfo = new JTextArea(petData.getInfo());
            petInfo.setEditable(false);
            petInfo.setLineWrap(true);
            petInfo.setWrapStyleWord(true);
            petInfo.setBackground(petEntryPanel.getBackground());
            petEntryPanel.add(petButton,BorderLayout.WEST);
            petEntryPanel.add(petInfo,BorderLayout.CENTER);
            petSelectionPanel.add(petEntryPanel);
            petButtonsList.add(petButton);
        }
        welcomeWindow.updateWindow();
    }
    // 开始游戏
    public void playGame(){
        for(PetBase pet:petList){screen.add(pet);}
        for(JButton petButton:petButtonsList){petButton.setEnabled(false);}
        playButton.setEnabled(false);
        clearButton.setEnabled(true);
        exitButton.setEnabled(false);
        reloadButton.setEnabled(false);
        screen.setVisible(true);
        welcomeWindow.setVisible(false);
    }
    // 结束游戏
    public void stopGame(){
        for(PetBase pet:petList){pet.dispose();pet=null;}
        for(JButton petButton:petButtonsList){petButton.setEnabled(true);}
        petList.clear();
        playButton.setEnabled(true);
        clearButton.setEnabled(false);
        exitButton.setEnabled(true);
        reloadButton.setEnabled(true);
        screen.setVisible(false);
        welcomeWindow.setVisible(true);
    }
    // 重新加载开始窗口
    public void reloadWelcomeWindow(){
        welcomePanel.removeAll();
        welcomePanel = null;
        welcomeWindow.removeAll();
        welcomeWindow.setVisible(false);
        welcomeWindow = null;
        petList.clear();
        petButtonsList.clear();
        System.gc();
        initWelcomeWindow();
    }
    // 添加组件到屏幕
    public void addItemToScreen(ItemBase item){screen.add(item);}
    // 清空屏幕组件（保留桌宠）
    public void clearScreenItems(){
        Component[] items = screen.getComponents();
        for(Component item:items){
            if(item instanceof PetBase){continue;}
            screen.remove(item);
        }
    }
}
