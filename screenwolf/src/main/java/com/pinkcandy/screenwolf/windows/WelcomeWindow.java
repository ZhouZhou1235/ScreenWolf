package com.pinkcandy.screenwolf.windows;

import java.util.ArrayList;
import java.awt.*;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.*;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;
import com.pinkcandy.screenwolf.bean.GameInfoData;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;


/**
 * 开始窗口
 * 启动器完成加载后显示的窗口，能选择桌宠和控制游戏的状态。
 */
public class WelcomeWindow extends WindowBase {
    private ArrayList<JButton> petButtonsList = new ArrayList<>();
    private JPanel welcomePanel = new JPanel();
    private JButton playButton,clearButton,reloadButton,exitButton,infoButton,addPetButton;
    private Launcher launcher;
    private JPanel petsContentPanel;
    public WelcomeWindow(Launcher launcher){
        super("ScreenWolf",GUtil.DEFAULT_windowSize);
        this.launcher = launcher;
        initWelcomeWindow();
        this.setVisible(true);
    }
    // 初始化
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
        this.setSize(
            (int)(GUtil.SCREEN_dimension.width/1.5),
            (int)(GUtil.SCREEN_dimension.height/1.5)
        );
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
        int logoWidth = (int)(GUtil.DEFAULT_windowSize.width*0.5);
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
        playButton = GUtil.createIconButton("images/button_play.png","开始游戏 play",buttonSize);
        clearButton = GUtil.createIconButton("images/button_stop.png","结束游戏 stop",buttonSize);
        reloadButton = GUtil.createIconButton("images/button_reload.png","重新加载 reload",buttonSize);
        exitButton = GUtil.createIconButton("images/button_exit.png","退出程序 exit",buttonSize);
        infoButton = GUtil.createIconButton("images/button_info.png","游戏介绍 info",buttonSize);
        addPetButton = GUtil.createIconButton("images/button_add_pet.png","添加宠物 add pet",buttonSize);
        clearButton.setEnabled(false);
        playButton.addActionListener(e->{
            if(!launcher.getPetListCopy().isEmpty()){launcher.playGame();}
            else{JOptionPane.showMessageDialog(this,"没有选择宠物 no pet have selected");}
        });
        clearButton.addActionListener(e->launcher.stopGame());
        reloadButton.addActionListener(e->launcher.reloadLauncher());
        exitButton.addActionListener(e->System.exit(0));
        infoButton.addActionListener(e->launcher.infoWindow.setVisible(true));
        addPetButton.addActionListener(e->{
            int copiedCount = GUtil.copyFilesWithDialog(
                this, 
                "选择宠物JAR文件 select pet jar", 
                "JAR文件", 
                "jar",
                GUtil.GAME_petsPath
            );
            if(copiedCount>0){launcher.reloadLauncher();}
        });
        buttonPanel.add(playButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(infoButton);
        buttonPanel.add(addPetButton);
        // 底部操作和版本
        GameInfoData gameInfoData = GsonUtil.json2Bean(ResourceReader.readResourceAsString("screenwolf.json"),GameInfoData.class);
        JLabel versionLabel = new JLabel("版本 version:"+gameInfoData.getVersion()+" 作者 owner:"+gameInfoData.getOwner(),SwingConstants.CENTER);
        versionLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, (int)(GUtil.DEFAULT_textSize * 0.8)));
        versionLabel.setForeground(new Color(120, 120, 120));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(versionLabel, BorderLayout.SOUTH);
        welcomePanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    // 从JAR文件加载宠物
    private void loadPetsFromJars(){
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        petsContentPanel = new JPanel();
        petsContentPanel.setLayout(new BoxLayout(petsContentPanel, BoxLayout.Y_AXIS));
        petsContentPanel.setOpaque(false);
        petsContentPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        refreshPetsContent();
        scrollPane.setViewportView(petsContentPanel);
        welcomePanel.add(scrollPane, BorderLayout.CENTER);
    }
    // 刷新宠物内容面板
    public void refreshPetsContent() {
        petsContentPanel.removeAll();
        petButtonsList.clear();
        String[] petJars = GUtil.scanDir(GUtil.GAME_petsPath,".jar");
        if (petJars.length == 0){
            JLabel noPetsLabel = new JLabel("没有宠物，点击按钮添加。 No pets, click the button to add.", SwingConstants.CENTER);
            noPetsLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.ITALIC, GUtil.DEFAULT_textSize));
            noPetsLabel.setForeground(new Color(150, 150, 150));
            noPetsLabel.setBorder(BorderFactory.createEmptyBorder(50, 10, 50, 10));
            petsContentPanel.add(noPetsLabel);
        } else {
            for(String jarName:petJars){
                try{
                    String jarPath = GUtil.GAME_petsPath+jarName;
                    PetData petData = loadPetDataFromJar(jarPath);
                    if(petData!=null){
                        JPanel petEntryPanel = createPetEntryPanel(jarPath,jarName,petData);
                        petsContentPanel.add(petEntryPanel);
                        petsContentPanel.add(Box.createRigidArea(new Dimension(0, GUtil.DEFAULT_textSize)));
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        }
        petsContentPanel.add(Box.createVerticalGlue());
        petsContentPanel.revalidate();
        petsContentPanel.repaint();
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
        petEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200)); // 限制最大高度
        // 组件面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new BorderLayout(5,5));
        JPanel topPanel = new JPanel(new BorderLayout(10,0));
        // 宠物图标
        JLabel iconLabel = new JLabel();
        try{
            ImageIcon icon = new ImageIcon(JarFileUtil.readByteInJarFile(jarPath,"assets/icon.png"));
            if(icon!=null){
                icon = GUtil.scaleImageIcon(icon,GUtil.DEFAULT_textSize*3);
                iconLabel.setIcon(icon);
                iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        }catch(Exception e){
            // 使用默认图标
            ImageIcon defaultIcon = ResourceReader.getResourceAsImageIcon("images/icon.png");
            defaultIcon = GUtil.scaleImageIcon(defaultIcon, GUtil.DEFAULT_textSize*3);
            iconLabel.setIcon(defaultIcon);
        }
        leftPanel.add(iconLabel,BorderLayout.CENTER);
        // 宠物名称和选择按钮在同一行
        JLabel nameLabel = new JLabel(petData.getName());
        nameLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.BOLD, (int)(GUtil.DEFAULT_textSize*1.2)));
        nameLabel.setForeground(new Color(70, 70, 70));
        topPanel.add(nameLabel,BorderLayout.CENTER);
        // 选择按钮
        JButton selectButton = GUtil.createIconButton("images/button_import.png","选择 select",GUtil.DEFAULT_textSize*2);
        selectButton.addActionListener(e->{
            try{
                PetBase pet = loadPetFromJar(jarPath,petData.getMainClass(),launcher);
                if(pet!=null){
                    launcher.addPetToLauncher(pet);
                    selectButton.setEnabled(false);
                    selectButton.setToolTipText("已选择 selected");
                }
            }catch(Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "加载宠物失败 load pet failed: " + ex.getMessage(), "错误 error", JOptionPane.ERROR_MESSAGE);
            }
        });
        topPanel.add(selectButton,BorderLayout.EAST);
        rightPanel.add(topPanel,BorderLayout.NORTH);
        // 宠物描述
        JTextArea descArea = new JTextArea(petData.getInfo());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setOpaque(false);
        descArea.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, GUtil.DEFAULT_textSize));
        descArea.setForeground(new Color(100, 100, 100));
        descArea.setHighlighter(null);
        descArea.setFocusable(false);
        descArea.setMargin(new Insets(5,0,5,0));
        // 确保描述区域有合适的高度
        int lineCount = Math.max(1, descArea.getLineCount());
        int preferredHeight = Math.min(120, lineCount * descArea.getFontMetrics(descArea.getFont()).getHeight() + 10);
        descArea.setPreferredSize(new Dimension(0, preferredHeight));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        descScroll.setOpaque(false);
        descScroll.getViewport().setOpaque(false);
        descScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightPanel.add(descScroll,BorderLayout.CENTER);
        // 组装面板
        leftPanel.setPreferredSize(new Dimension(GUtil.DEFAULT_textSize*4, GUtil.DEFAULT_textSize*4));
        petEntryPanel.add(leftPanel,BorderLayout.WEST);
        petEntryPanel.add(rightPanel,BorderLayout.CENTER);
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
    private PetBase loadPetFromJar(String jarPath, String className, Launcher launcher) throws Exception {
        @SuppressWarnings("deprecation")
        URL jarUrl = new URL("file:" + jarPath);
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, getClass().getClassLoader());
        try{
            Class<?> petClass = classLoader.loadClass(className);
            PetBase pet = (PetBase)petClass.getConstructor(Launcher.class).newInstance(launcher);
            pet.setClassLoader(classLoader);
            return pet;
        }catch(Exception e){classLoader.close();throw e;}
    }
    // 更新窗口为开始游戏状态
    public void updateWindowToPlayState(){
        for(JButton petButton:petButtonsList){petButton.setEnabled(false);}
        playButton.setEnabled(false);
        clearButton.setEnabled(true);
        exitButton.setEnabled(false);
        reloadButton.setEnabled(false);
        addPetButton.setEnabled(false);
    }
    // 更新窗口为结束游戏状态
    public void updateWindowToStopState(){
        for(JButton petButton:petButtonsList){petButton.setEnabled(true);}
        playButton.setEnabled(true);
        clearButton.setEnabled(false);
        exitButton.setEnabled(true);
        reloadButton.setEnabled(true);
        addPetButton.setEnabled(true);
    }
    // 重新加载宠物列表
    public void reloadPets(){
        refreshPetsContent();
    }
}
