package com.pinkcandy.screenwolf.windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;

// 宠物选项窗口
public class PetOption extends WindowBase {
    private PetBase pet;
    private static PetOption currentInstance;
    private JLabel statusLabel;
    private Timer updateTimer;
    private Point dragOffset;
    private JPanel buttonPanel;
    public PetOption(PetBase thePet){
        super(
            thePet.getPetData().getName(),
            new Dimension(
                GUtil.DEFAULT_bodySize.width*2,
                GUtil.DEFAULT_bodySize.height
            )
        );
        this.pet = thePet;
        initWindow();
        setupDragBehavior();
        startStatusUpdate();
    }
    // 初始化窗口
    private void initWindow(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setBackground(new Color(245,245,250,200));
        this.setLayout(new BorderLayout(5,5));
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        JPanel dragPanel = new JPanel();
        dragPanel.setOpaque(false);
        dragPanel.setPreferredSize(new Dimension(0, 30));
        JLabel nameLabel = new JLabel(pet.getPetData().getName());
        nameLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.BOLD, GUtil.DEFAULT_textSize));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        dragPanel.add(nameLabel);
        contentPanel.add(dragPanel);
        statusLabel = new JLabel();
        statusLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, (int)(GUtil.DEFAULT_textSize*0.8)));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(100,100,100));
        updateStatusText();
        contentPanel.add(statusLabel);
        contentPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        contentPanel.add(Box.createRigidArea(new Dimension(0,10)));
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        addButtonsToPanel();
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane);
        this.add(contentPanel, BorderLayout.CENTER);
        adjustWindowSize();
    }
    // 添加按钮到面板
    private void addButtonsToPanel(){
        buttonPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // 添加按钮
        addButton(buttonPanel, gbc, "images/button_rest.png", "休息", e->pet.doRest());
        addButton(buttonPanel, gbc, "images/button_screenshot.png", "截图", e->{
            pet.copyScreenImage();
            pet.showMessage("截图已复制到剪贴板");
        });
        addButton(buttonPanel, gbc, "images/button_copy_text.png", "复制文本", e->pet.copyTextFromClipboard());
        addButton(buttonPanel, gbc, "images/button_follow.png", "跟随", e->pet.followMouse());
        addButton(buttonPanel, gbc, "images/button_close.png", "关闭", e->closeWindow());
        // addButton...
    }
    // 添加单个按钮
    private void addButton(JPanel panel, GridBagConstraints gbc, String iconPath, String tooltip, ActionListener listener){
        JButton button = GUtil.createIconButton(iconPath, tooltip, GUtil.DEFAULT_textSize*2);
        button.addActionListener(listener);
        
        panel.add(button, gbc);
        gbc.gridx++;
        if(gbc.gridx>4){
            gbc.gridx = 0;
            gbc.gridy++;
        }
    }
    // 调整窗口大小
    private void adjustWindowSize(){
        Dimension preferredSize = this.getPreferredSize();
        int width = Math.min(Math.max(preferredSize.width,GUtil.DEFAULT_bodySize.width*2),GUtil.DEFAULT_bodySize.width*2);
        int height = Math.min(Math.max(preferredSize.height,GUtil.DEFAULT_bodySize.height),GUtil.DEFAULT_bodySize.height*2);
        this.setSize(width,height);
        this.revalidate();
    }
    // 设置拖动行为
    private void setupDragBehavior(){
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                dragOffset = e.getPoint();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                Point newLocation = e.getLocationOnScreen();
                newLocation.x -= dragOffset.x;
                newLocation.y -= dragOffset.y;
                setLocation(newLocation);
            }
        });
    }
    // 更新状态文本
    private void updateStatusText(){
        if(pet.getPlayPetData() == null){
            statusLabel.setText("");
            return;
        }
        String statusText = String.format("等级:%d 经验:%d/%d | 动画:%s",
            pet.getPlayPetData().getAffectionLevel(),
            pet.getPlayPetData().getAffectionPoints(),
            pet.affectLevelUp,
            pet.animationSprite.currentAnimation);
        statusLabel.setText(statusText);
    }
    // 启动状态更新定时器
    private void startStatusUpdate(){
        updateTimer = new Timer(GUtil.GAME_renderTime, e->{
            updateStatusText();
            if(!pet.isVisible()){
                closeWindow();
                updateTimer.stop();
            }
        });
        updateTimer.start();
    }
    // 关闭窗口
    private void closeWindow(){
        if(updateTimer != null){
            updateTimer.stop();
        }
        this.setVisible(false);
        this.dispose();
        currentInstance = null;
    }
    // 显示宠物选项窗口
    public static void showForPet(PetBase pet){
        if(currentInstance != null){
            currentInstance.closeWindow();
        }
        currentInstance = new PetOption(pet);
        Point petLocation = pet.getLocationOnScreen();
        Point windowLocation = new Point(petLocation.x + pet.getWidth() + 5, petLocation.y);
        if(windowLocation.x + currentInstance.getWidth() > GUtil.SCREEN_dimension.width){
            windowLocation.x = petLocation.x - currentInstance.getWidth() - 5;
        }
        if(windowLocation.y + currentInstance.getHeight() > GUtil.SCREEN_dimension.height){
            windowLocation.y = GUtil.SCREEN_dimension.height - currentInstance.getHeight();
        }
        currentInstance.setLocation(windowLocation);
        currentInstance.setVisible(true);
    }
    // 添加新按钮的公共方法
    public void addOptionButton(String iconPath, String tooltip, ActionListener listener){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = ((GridBagLayout)buttonPanel.getLayout()).getLayoutDimensions()[0].length;
        gbc.gridy = ((GridBagLayout)buttonPanel.getLayout()).getLayoutDimensions()[1].length;
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addButton(buttonPanel, gbc, iconPath, tooltip, listener);
        adjustWindowSize();
    }
}
