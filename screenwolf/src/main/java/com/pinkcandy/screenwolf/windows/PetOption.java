package com.pinkcandy.screenwolf.windows;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;


/**
 * 宠物选项面板
 * 每只宠物都有一个专属面板 继承以定制更多内容
 */
public class PetOption extends WindowBase {
    protected PetBase pet;
    protected JLabel statusLabel;
    protected Timer updateTimer;
    protected Point dragOffset;
    protected JPanel buttonPanel;
    protected GridBagConstraints buttonGrid;
    protected int buttonsPerRow = 4; // 每行按钮数
    public PetOption(PetBase thePet){
        super(
            thePet.getPetData().getName(),
            new Dimension(
                (int)(GUtil.DEFAULT_bodySize.width*2),
                (int)(GUtil.DEFAULT_bodySize.height*1.5)
            )
        );
        this.pet = thePet;
        initWindow();
        readyToPaint();
    }
    // 初始化窗口
    protected void initWindow(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setBackground(new Color(0, 0, 0, 200));
        this.setLayout(new BorderLayout(5, 5));
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        JPanel dragPanel = new JPanel();
        dragPanel.setOpaque(false);
        dragPanel.setPreferredSize(new Dimension(0, 30));
        JLabel nameLabel = new JLabel(pet.getPetData().getName());
        nameLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.BOLD, GUtil.DEFAULT_textSize));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(240,240,240));
        dragPanel.add(nameLabel);
        contentPanel.add(dragPanel);
        statusLabel = new JLabel();
        statusLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, (int)(GUtil.DEFAULT_textSize * 0.8)));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(200, 200, 200));
        contentPanel.add(statusLabel);
        contentPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonGrid = new GridBagConstraints();
        buttonGrid.gridx = 0;
        buttonGrid.gridy = 0;
        buttonGrid.insets = new Insets(5,5,5,5);
        buttonGrid.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane);
        this.add(contentPanel,BorderLayout.CENTER);
    }
    // 开始渲染
    protected void readyToPaint(){
        updateStatusText();
        loadButtonsToPanel();
        adjustWindowSize();
        setupDragBehavior();
        startStatusUpdate();
    }
    // 加载按钮到面板
    protected void loadButtonsToPanel(){
        buttonPanel.removeAll();
        addButton("images/button_follow.png", "跟随 follow",e->pet.followMouse());
        addButton("images/button_rest.png", "休息 rest",e->pet.doRest());
        addButton("images/button_copy_text.png", "复制文本 copy text",e->pet.copyTextFromClipboard());
        addButton("images/button_read.png", "阅读 read",e->pet.readMessageList());
        addButton("images/button_screenshot.png", "截图 screenshot",e->{
            pet.copyScreenImage();
            pet.showMessage("截图已复制到剪贴板");
        });
        addButton("images/button_close.png", "关闭 close",e->closeWindow());
        // 重写加载更多......
    }
    // 调整窗口大小
    public void adjustWindowSize(){
        Dimension preferredSize = this.getPreferredSize();
        int width = Math.min(Math.max(preferredSize.width, GUtil.DEFAULT_bodySize.width * 2), GUtil.DEFAULT_bodySize.width * 2);
        int height = Math.min(Math.max(preferredSize.height, GUtil.DEFAULT_bodySize.height), GUtil.DEFAULT_bodySize.height * 2);
        this.setSize(width, height);
        this.revalidate();
    }
    // 设置拖动行为
    public void setupDragBehavior(){
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
    public void updateStatusText(){
        if(pet.getPlayPetData()==null){
            statusLabel.setText("");
            return;
        }
        String statusText = String.format("%d - %d/%d | %s",
            pet.getPlayPetData().getAffectionLevel(),
            pet.getPlayPetData().getAffectionPoints(),
            pet.affectLevelUp,
            pet.animationSprite.currentAnimation);
        statusLabel.setText(statusText);
    }
    // 启动状态更新定时器
    public void startStatusUpdate(){
        updateTimer = new Timer(GUtil.GAME_renderTime,e->{
            updateStatusText();
            if(!pet.isVisible()){
                closeWindow();
                updateTimer.stop();
            }
        });
        updateTimer.start();
    }
    // 关闭窗口
    public void closeWindow(){
        if (updateTimer != null){
            updateTimer.stop();
        }
        this.setVisible(false);
        this.dispose();
    }
    // 显示窗口
    public void showWindow(){
        Point petLocation = pet.getLocationOnScreen();
        Point windowLocation = new Point(petLocation.x + pet.getWidth() + 5, petLocation.y);
        if (windowLocation.x + this.getWidth() > GUtil.SCREEN_dimension.width){
            windowLocation.x = petLocation.x - this.getWidth() - 5;
        }
        if (windowLocation.y + this.getHeight() > GUtil.SCREEN_dimension.height){
            windowLocation.y = GUtil.SCREEN_dimension.height - this.getHeight();
        }
        this.setLocation(windowLocation);
        this.setVisible(true);
        if(!updateTimer.isRunning()){updateTimer.start();}
    }
    // 添加按钮 从主程序
    protected void addButton(String iconPath,String tooltip,ActionListener listener){
        JButton button = GUtil.createIconButton(iconPath,tooltip,GUtil.DEFAULT_textSize*2);
        button.addActionListener(listener);
        buttonPanel.add(button,buttonGrid);
        buttonGrid.gridx++;
        if(buttonGrid.gridx>buttonsPerRow-1){
            buttonGrid.gridx = 0;
            buttonGrid.gridy++;
        }
    }
    // 添加按钮 从桌宠包
    protected void addButton(ImageIcon icon,String tooltip,ActionListener listener){
        JButton button = GUtil.createIconButton(icon,tooltip,GUtil.DEFAULT_textSize*2);
        button.addActionListener(listener);
        buttonPanel.add(button,buttonGrid);
        buttonGrid.gridx++;
        if(buttonGrid.gridx>buttonsPerRow-1){
            buttonGrid.gridx = 0;
            buttonGrid.gridy++;
        }
    }
}
