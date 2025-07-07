package com.pinkcandy;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import javax.swing.border.TitledBorder;

import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.GameTray;
import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.ModData;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.tools.Tool;
import com.pinkcandy.screenwolf.tools.GsonUtil;

// 启动器 - 按桌宠包组织
public class Launcher {
    private ArrayList<PetBase> petList;
    private ArrayList<JButton> petButtonList;
    private JPanel welcomePanel;
    private JPanel modPanel;
    private JButton playButton;
    private JButton clearButton;
    private JButton reloadButton;
    private JButton exitButton;
    private TransparentScreen screen;
    public WindowBase welcomeWindow;
    public GameTray gameTray;
    
    // 存储mod和对应的宠物按钮映射
    private Map<String, ArrayList<JButton>> modPetButtonsMap;
    
    public Launcher() {
        Tool.initFileDirs();
        Tool.initGlobalFont(new Font("SansSerif", Font.BOLD, Tool.DEFAULT_textSize));
        this.screen = new TransparentScreen(Tool.SCREEN_dimension);
        this.petList = new ArrayList<>();
        this.petButtonList = new ArrayList<>();
        this.modPetButtonsMap = new HashMap<>();
        initWelcomeWindow();
        this.welcomeWindow.updateWindow();
        this.gameTray = new GameTray(this);
        this.screen.setVisible(false);
    }

    // 初始化开始窗口
    public void initWelcomeWindow() {
        this.welcomePanel = new JPanel(new BorderLayout(10, 10));
        this.welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        welcomeWindow = new WindowBase("ScreenWolf", Tool.DEFAULT_windowSize);
        welcomeWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Tool.setWindowCenter(welcomeWindow);
        loadBasic();
        loadMods();
        welcomePanel.revalidate();
        welcomePanel.repaint();
        welcomeWindow.add(welcomePanel);
    }

    // 加载窗口固定项
    private void loadBasic() {
        ImageIcon logoImageIcon = new ImageIcon(Tool.GAME_workPath + "/assets/images/logo.png");
        logoImageIcon = Tool.scaleImageIcon(logoImageIcon, (int)(Tool.DEFAULT_windowSize.width * 0.75));
        
        // 标题
        JLabel titleLabel = new JLabel(logoImageIcon, SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, Tool.DEFAULT_textSize * 2));
        JPanel titlePanel = new JPanel();
        titlePanel.add(titleLabel);
        welcomePanel.add(titlePanel, BorderLayout.NORTH);
        
        // 选项按钮
        playButton = new JButton("开始游戏");
        clearButton = new JButton("结束游戏");
        reloadButton = new JButton("重新加载");
        exitButton = new JButton("退出程序");
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.add(playButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(exitButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        clearButton.setEnabled(false);
        
        playButton.addActionListener(e -> playGame());
        clearButton.addActionListener(e -> stopGame());
        reloadButton.addActionListener(e -> reloadWelcomeWindow());
        exitButton.addActionListener(e -> System.exit(0));
        
        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    // 加载桌宠包
    private void loadMods() {
        modPanel = new JPanel();
        modPanel.setLayout(new BoxLayout(modPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(modPanel);
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
        
        // 扫描mod目录
        for (String modDirName : Tool.scanDir(Tool.GAME_modPath)) {
            String modPath = Tool.GAME_modPath + modDirName + "/";
            ModData modData = GsonUtil.json2Bean(Tool.readFile(modPath + "mod.json"), ModData.class);
            
            // 为每个mod创建面板
            JPanel modGroupPanel = createModGroupPanel(modData);
            modPanel.add(modGroupPanel);
            
            // 加载该mod下的所有宠物
            loadPetsForMod(modData, modGroupPanel);
        }
        
        welcomeWindow.updateWindow();
    }

    // 创建mod分组面板
    private JPanel createModGroupPanel(ModData modData) {
        JPanel modGroupPanel = new JPanel();
        modGroupPanel.setLayout(new BoxLayout(modGroupPanel, BoxLayout.Y_AXIS));
        
        // 设置带标题的边框
        TitledBorder border = BorderFactory.createTitledBorder(modData.getmodName());
        border.setTitleFont(new Font("SansSerif", Font.BOLD, Tool.DEFAULT_textSize));
        modGroupPanel.setBorder(border);
        
        // 添加mod描述
        JTextArea modInfo = new JTextArea(modData.getmodInfo());
        modInfo.setEditable(false);
        modInfo.setLineWrap(true);
        modInfo.setWrapStyleWord(true);
        modInfo.setBackground(modGroupPanel.getBackground());
        modGroupPanel.add(modInfo);
        
        return modGroupPanel;
    }

    // 加载单个mod下的所有宠物
    private void loadPetsForMod(ModData modData, JPanel modGroupPanel) {
        String modid = modData.getId();
        ArrayList<JButton> petButtons = new ArrayList<>();
        
        for (String petid : modData.getPetidList()) {
            String petPath = Tool.GAME_modPath + modid + "/pets/" + petid;
            String jsonpetdata = Tool.readFile(petPath + "/pet.json");
            PetData petData = GsonUtil.json2Bean(jsonpetdata, PetData.class);
            
            // 创建宠物选择面板
            JPanel petSelectPanel = createPetSelectPanel(modid, petid, petData);
            modGroupPanel.add(petSelectPanel);
            
            // 将按钮添加到映射中
            petButtons.add((JButton) petSelectPanel.getComponent(0));
        }
        
        // 存储mod和宠物按钮的映射
        modPetButtonsMap.put(modid, petButtons);
    }

    // 创建宠物选择面板
    private JPanel createPetSelectPanel(String modid, String petid, PetData petData) {
        JPanel petSelectPanel = new JPanel(new BorderLayout(10, 5));
        petSelectPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton petButton = new JButton(petData.getName());
        petButton.addActionListener(e -> {
            // 加载宠物实例
            PetBase pet = (PetBase) Tool.loadObjFromJarByClass(
                Tool.GAME_modPath + modid + "/pets/" + petid + "/pet.jar",
                "com.pinkcandy." + modid + "." + petid,
                this
            );
            
            if (pet != null) {
                petList.add(pet);
                petButton.setEnabled(false);
            }
        });
        
        JTextArea petInfo = new JTextArea(petData.getInfo());
        petInfo.setEditable(false);
        petInfo.setLineWrap(true);
        petInfo.setWrapStyleWord(true);
        petInfo.setBackground(petSelectPanel.getBackground());
        
        petSelectPanel.add(petButton, BorderLayout.WEST);
        petSelectPanel.add(petInfo, BorderLayout.CENTER);
        
        // 添加到全局按钮列表
        petButtonList.add(petButton);
        
        return petSelectPanel;
    }

    // 开始游戏
    public void playGame() {
        for (PetBase pet : petList) {
            screen.add(pet);
        }
        
        // 禁用所有宠物按钮
        for (JButton petButton : petButtonList) {
            petButton.setEnabled(false);
        }
        
        playButton.setEnabled(false);
        clearButton.setEnabled(true);
        exitButton.setEnabled(false);
        reloadButton.setEnabled(false);
        screen.setVisible(true);
        welcomeWindow.setVisible(false);
    }

    // 结束游戏
    public void stopGame() {
        for (PetBase pet : petList) {
            pet.dispose();
            pet = null;
        }
        
        // 启用所有宠物按钮
        for (JButton petButton : petButtonList) {
            petButton.setEnabled(true);
        }
        
        petList.clear();
        playButton.setEnabled(true);
        clearButton.setEnabled(false);
        exitButton.setEnabled(true);
        reloadButton.setEnabled(true);
        screen.setVisible(false);
        welcomeWindow.setVisible(true);
    }

    // 重新加载开始窗口
    public void reloadWelcomeWindow() {
        welcomePanel.removeAll();
        welcomePanel = null;
        welcomeWindow.removeAll();
        welcomeWindow.setVisible(false);
        welcomeWindow = null;
        petList.clear();
        petButtonList.clear();
        modPetButtonsMap.clear();
        System.gc();
        initWelcomeWindow();
    }

    // 添加组件到屏幕
    public void addItemToScreen(ItemBase item) {
        screen.add(item);
    }

    // 清空屏幕组件（保留桌宠）
    public void clearScreenItems() {
        Component[] items = screen.getComponents();
        for (Component item : items) {
            if (item instanceof PetBase) {
                continue;
            }
            screen.remove(item);
        }
    }
}