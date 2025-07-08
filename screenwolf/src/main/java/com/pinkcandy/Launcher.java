package com.pinkcandy;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import javax.swing.*;

import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.GameTray;
import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;

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
        GUtil.initFileDirs();
        GUtil.initGlobalFont(new Font("SansSerif",Font.BOLD,GUtil.DEFAULT_textSize));
        this.screen = new TransparentScreen(GUtil.SCREEN_dimension);
        this.petList = new ArrayList<>();
        this.petButtonsList = new ArrayList<>();
        initWelcomeWindow();
        this.welcomeWindow.updateWindow();
        this.gameTray = new GameTray(this);
        this.screen.setVisible(false);
    };
    // 初始化开始窗口
    public void initWelcomeWindow() {
        this.welcomePanel = new JPanel(new BorderLayout(10, 10));
        this.welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        welcomeWindow = new WindowBase("ScreenWolf",GUtil.DEFAULT_windowSize);
        welcomeWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        welcomeWindow.setIconImage(ResourceReader.getResourceAsImageIcon("images/icon.png").getImage());
        GUtil.setWindowCenter(welcomeWindow);
        loadBasic();
        loadPetsFromJars();
        welcomePanel.revalidate();
        welcomePanel.repaint();
        welcomeWindow.add(welcomePanel);
    }
    // 加载窗口固定项
    private void loadBasic(){
        ImageIcon logoImageIcon = ResourceReader.getResourceAsImageIcon("images/logo.png");
        logoImageIcon = GUtil.scaleImageIcon(logoImageIcon,(int)(GUtil.DEFAULT_windowSize.width*0.75));
        // 标题
        JLabel titleLabel = new JLabel(logoImageIcon,SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(),Font.BOLD,GUtil.DEFAULT_textSize*2));
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
    // 从JAR文件加载宠物
    private void loadPetsFromJars() {
        petSelectionPanel = new JPanel();
        petSelectionPanel.setLayout(new BoxLayout(petSelectionPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(petSelectionPanel);
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
        String[] petJars = GUtil.scanDir(GUtil.GAME_petsPath,".jar");
        for(String jarName:petJars){
            try{
                String jarPath = GUtil.GAME_petsPath+jarName;
                PetData petData = loadPetDataFromJar(jarPath);
                if(petData!=null){
                    JPanel petEntryPanel = createPetEntryPanel(jarPath,jarName,petData);
                    petSelectionPanel.add(petEntryPanel);
                }
            }catch(Exception e){e.printStackTrace();}
        }
        welcomeWindow.updateWindow();
    }
    // 创建宠物的面板
    private JPanel createPetEntryPanel(String jarPath,String jarName,PetData petData){
        JPanel petEntryPanel = new JPanel(new BorderLayout(10,5));
        petEntryPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JButton petButton = new JButton(petData.getName());
        petButtonsList.add(petButton);
        // 宠物图标
        try {
            ImageIcon icon = new ImageIcon(JarFileUtil.readByteInJarFile(jarPath,"assets/icon.png"));
            if(icon != null){
                icon = GUtil.scaleImageIcon(icon,(int)(GUtil.DEFAULT_bodySize.width*0.75));
                petButton.setIcon(icon);
            }
        }catch(Exception e){e.printStackTrace();}
        petButton.addActionListener(e -> {
            try{
                PetBase pet = loadPetFromJar(jarPath,petData.getMainClass(),this);
                if (pet!=null){
                    petList.add(pet);
                    petButton.setEnabled(false);
                }
            }catch(Exception ex){ex.printStackTrace();}
        });
        JTextArea petInfo = new JTextArea(petData.getInfo());
        petInfo.setEditable(false);
        petInfo.setLineWrap(true);
        petInfo.setWrapStyleWord(true);
        petInfo.setBackground(petEntryPanel.getBackground());
        petEntryPanel.add(petButton, BorderLayout.WEST);
        petEntryPanel.add(petInfo, BorderLayout.CENTER);
        return petEntryPanel;
    }
    // 从JAR中加载宠物数据
    private PetData loadPetDataFromJar(String jarPath) throws Exception{
        String json = new String(JarFileUtil.readByteInJarFile(
            jarPath,
            "META-INF/pet_data.json"
        ));
        return GsonUtil.json2Bean(json, PetData.class);
    }
    // 从JAR加载宠物实现类
    private PetBase loadPetFromJar(String jarPath,String className,Launcher launcher) throws Exception{
        @SuppressWarnings("deprecation")
        URL jarUrl = new URL("file:"+jarPath);
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl})){
            Class<?> petClass = classLoader.loadClass(className);
            PetBase pet = (PetBase)petClass.getConstructor(Launcher.class).newInstance(launcher);
            return pet;
        }
    }

    // === 公开方法 ===
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
