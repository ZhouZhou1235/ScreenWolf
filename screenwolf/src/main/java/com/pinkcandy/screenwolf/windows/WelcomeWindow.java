package com.pinkcandy.screenwolf.windows;

import java.util.ArrayList;
import java.awt.*;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.*;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;
import com.pinkcandy.screenwolf.bean.GameInfoData;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;

// 开始窗口
public class WelcomeWindow extends WindowBase {
    private ArrayList<JButton> petButtonsList = new ArrayList<>();
    private JPanel welcomePanel = new JPanel();
    private JButton playButton,clearButton,reloadButton,exitButton,infoButton,createorButton,addPetButton;
    private Launcher launcher;
    public WelcomeWindow(Launcher launcher){
        super("ScreenWolf",GUtil.DEFAULT_windowSize);
        this.launcher = launcher;
        initWelcomeWindow();
        this.setVisible(true);
    }
    private void initWelcomeWindow(){
        this.welcomePanel = new JPanel(new BorderLayout(5,5));
        this.welcomePanel.setBackground(new Color(245,245,250));
        this.welcomePanel.setBorder(BorderFactory.createEmptyBorder(
            GUtil.DEFAULT_textSize/2, 
            GUtil.DEFAULT_textSize/2,
            GUtil.DEFAULT_textSize/2,
            GUtil.DEFAULT_textSize/2
        ));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(ResourceReader.getResourceAsImageIcon("images/icon.png").getImage());
        GUtil.setWindowCenter(this);
        loadBasic();
        loadPetsFromJars();
        this.add(welcomePanel);
        this.updateWindow();
    }
    // 加载窗口固定项
    private void loadBasic(){
        // 标题
        ImageIcon logo = ResourceReader.getResourceAsImageIcon("images/logo.png");
        int logoWidth = (int)(GUtil.DEFAULT_windowSize.width*0.6);
        logo = GUtil.scaleImageIcon(logo, logoWidth);
        JLabel titleLabel = new JLabel(logo,SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(
            GUtil.DEFAULT_textSize, 
            0, 
            GUtil.DEFAULT_textSize, 
            0
        ));
        welcomePanel.add(titleLabel,BorderLayout.NORTH);

        // 组件面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,15,5));
        
        int buttonSize = GUtil.DEFAULT_textSize * 2;
        playButton = createIconButton("images/button_play.png","开始游戏",buttonSize);
        clearButton = createIconButton("images/button_stop.png","结束游戏",buttonSize);
        reloadButton = createIconButton("images/button_reload.png","重新加载",buttonSize);
        exitButton = createIconButton("images/button_exit.png","退出程序",buttonSize);
        infoButton = createIconButton("images/button_info.png","游戏介绍",buttonSize);
        createorButton = createIconButton("images/button_creator.png","宠物制作器",buttonSize);
        addPetButton = createIconButton("images/button_add_pet.png","添加宠物",buttonSize);

        clearButton.setEnabled(false);
        playButton.addActionListener(e->{
            if(!launcher.getPetListCopy().isEmpty()){launcher.playGame();}
            else{JOptionPane.showMessageDialog(this,"没有选择宠物");}
        });
        clearButton.addActionListener(e->launcher.stopGame());
        reloadButton.addActionListener(e->launcher.reloadLauncher());
        exitButton.addActionListener(e->System.exit(0));
        infoButton.addActionListener(e->launcher.infoWindow.setVisible(true));
        createorButton.addActionListener(e->launcher.petCreateorWindow.setVisible(true));
        addPetButton.addActionListener(e->launcher.jarFileImporterWindow.setVisible(true));
        buttonPanel.add(playButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(infoButton);
        buttonPanel.add(createorButton);
        buttonPanel.add(addPetButton);
        
        // 底部操作和版本
        GameInfoData gameInfoData = GsonUtil.json2Bean(ResourceReader.readResourceAsString("screenwolf.json"),GameInfoData.class);
        JLabel versionLabel = new JLabel("version "+gameInfoData.getVersion()+" by "+gameInfoData.getOwner(), SwingConstants.CENTER);
        versionLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, (int)(GUtil.DEFAULT_textSize * 0.8)));
        versionLabel.setForeground(new Color(120, 120, 120));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(versionLabel, BorderLayout.SOUTH);
        welcomePanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    // 创建图标按钮
    private JButton createIconButton(String path,String tooltip,int size){
        ImageIcon icon = ResourceReader.getResourceAsImageIcon(path);
        icon = GUtil.scaleImageIcon(icon,size);
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    // 从JAR文件加载宠物
    private void loadPetsFromJars(){
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        String[] petJars = GUtil.scanDir(GUtil.GAME_petsPath,".jar");
        for(String jarName:petJars){
            try{
                String jarPath = GUtil.GAME_petsPath+jarName;
                PetData petData = loadPetDataFromJar(jarPath);
                if(petData!=null){
                    JPanel petEntryPanel = createPetEntryPanel(jarPath,jarName,petData);
                    contentPanel.add(petEntryPanel);
                    contentPanel.add(Box.createRigidArea(new Dimension(0, GUtil.DEFAULT_textSize)));
                }
            }catch(Exception e){e.printStackTrace();}
        }
        contentPanel.add(Box.createVerticalGlue());
        scrollPane.setViewportView(contentPanel);
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
    }
    // 创建宠物的面板
    private JPanel createPetEntryPanel(String jarPath,String jarName,PetData petData){
        // 主面板
        JPanel petEntryPanel = new JPanel(new BorderLayout(10,5));
        petEntryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,230),1),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        petEntryPanel.setBackground(Color.WHITE);
        
        // 组件面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());

        // 宠物图标
        JLabel iconLabel = new JLabel();
        try{
            ImageIcon icon = new ImageIcon(JarFileUtil.readByteInJarFile(jarPath,"assets/icon.png"));
            if(icon!=null){
                icon = GUtil.scaleImageIcon(icon,GUtil.DEFAULT_textSize*4);
                iconLabel.setIcon(icon);
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }catch(Exception e){e.printStackTrace();}
        leftPanel.add(iconLabel,BorderLayout.CENTER);
        
        // 宠物名称
        JLabel nameLabel = new JLabel(petData.getName());
        nameLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.BOLD, (int)(GUtil.DEFAULT_textSize*1.2)));
        nameLabel.setForeground(new Color(70, 70, 70));
        topPanel.add(nameLabel,BorderLayout.WEST);
        
        // 选择按钮
        JButton selectButton = createIconButton("images/button_import.png",petData.getName(),GUtil.DEFAULT_textSize*2);
        selectButton.addActionListener(e->{
            try{
                PetBase pet = loadPetFromJar(jarPath,petData.getMainClass(),launcher);
                if(pet!=null){
                    launcher.addPetToLauncher(pet);
                    selectButton.setEnabled(false);
                }
            }catch(Exception ex){ex.printStackTrace();}
        });
        topPanel.add(selectButton,BorderLayout.EAST);
        
        rightPanel.add(topPanel,BorderLayout.NORTH);
        
        // 宠物描述
        JTextArea descArea = new JTextArea(petData.getInfo());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, (int)(GUtil.DEFAULT_textSize * 0.9)));
        descArea.setForeground(new Color(100, 100, 100));
        descArea.setHighlighter(null);
        descArea.setFocusable(false);
        descArea.setMargin(new Insets(0,0,0,0));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(null);
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(descScroll,BorderLayout.CENTER);
        
        // 组装面板
        leftPanel.setPreferredSize(new Dimension(GUtil.DEFAULT_textSize*4,GUtil.DEFAULT_textSize*4));
        petEntryPanel.add(leftPanel,BorderLayout.WEST);
        petEntryPanel.add(rightPanel,BorderLayout.CENTER);
        
        // 动态高度
        int lineCount = descArea.getLineCount();
        int preferredHeight = Math.max(
            GUtil.DEFAULT_textSize*4,
            (int)(GUtil.DEFAULT_textSize*(1.5 + lineCount*0.8))
        );
        petEntryPanel.setPreferredSize(new Dimension(
            (int)(GUtil.DEFAULT_windowSize.width * 0.9)-30,
            preferredHeight
        ));
        
        petButtonsList.add(selectButton);
        return petEntryPanel;
    }
    // 从JAR中加载宠物数据
    private PetData loadPetDataFromJar(String jarPath) throws Exception{
        String json = new String(JarFileUtil.readByteInJarFile(
            jarPath,
            "META-INF/pet_data.json"
        ));
        return GsonUtil.json2Bean(json,PetData.class);
    }
    // 从JAR加载宠物实现类
    private PetBase loadPetFromJar(String jarPath,String className,Launcher launcher) throws Exception{
        @SuppressWarnings("deprecation")
        URL jarUrl = new URL("file:"+jarPath);
        try(URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl})){
            Class<?> petClass = classLoader.loadClass(className);
            PetBase pet = (PetBase)petClass.getConstructor(Launcher.class).newInstance(launcher);
            return pet;
        }
    }
    // 更新窗口为开始游戏状态
    public void updateWindowToPlayState(){
        for(JButton petButton:petButtonsList){petButton.setEnabled(false);}
        playButton.setEnabled(false);
        clearButton.setEnabled(true);
        exitButton.setEnabled(false);
        reloadButton.setEnabled(false);
        createorButton.setEnabled(false);
        addPetButton.setEnabled(false);
    }
    // 更新窗口为结束游戏状态
    public void updateWindowToStopState(){
        for(JButton petButton:petButtonsList){petButton.setEnabled(true);}
        playButton.setEnabled(true);
        clearButton.setEnabled(false);
        exitButton.setEnabled(true);
        reloadButton.setEnabled(true);
        createorButton.setEnabled(true);
        addPetButton.setEnabled(true);
    }
}
