package com.pinkcandy.screenwolf.windows;

import java.awt.*;
import javax.swing.*;
import com.pinkcandy.screenwolf.PetMessageBubble;
import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.utils.GUtil;


// 宠物选项窗口
public class PetOption extends WindowBase {
    private PetBase pet;
    private static PetOption currentInstance;
    private JLabel statusLabel;
    private Timer updateTimer;
    public PetOption(PetBase thePet,Dimension size){
        super(thePet.getId(),size);
        this.pet=thePet;
        initWindow();
        startStatusUpdate();
    }
    // 初始化窗口
    private void initWindow(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setBackground(new Color(245,245,250,240));
        this.setLayout(new BorderLayout(5,5));
        JPanel contentPanel=new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        JLabel nameLabel=new JLabel(pet.getPetData().getName());
        nameLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.BOLD,GUtil.DEFAULT_textSize));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        contentPanel.add(nameLabel);
        statusLabel=new JLabel();
        statusLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN,(int)(GUtil.DEFAULT_textSize*0.8)));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(100,100,100));
        updateStatusText();
        contentPanel.add(statusLabel);
        contentPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        contentPanel.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel buttonPanel=new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
        buttonPanel.setOpaque(false);
        JButton restButton=GUtil.createIconButton("images/button_stop.png","休息",GUtil.DEFAULT_textSize*2);
        restButton.addActionListener(e->{
            pet.doRest();
            closeWindow();
        });
        buttonPanel.add(restButton);
        JButton screenshotButton=GUtil.createIconButton("images/button_add_pet.png","截图",GUtil.DEFAULT_textSize*2);
        screenshotButton.addActionListener(e->{
            pet.copyScreenImage();
            showMessage("截图已复制到剪贴板");
            closeWindow();
        });
        buttonPanel.add(screenshotButton);
        JButton closeButton=GUtil.createIconButton("images/button_exit.png","关闭",GUtil.DEFAULT_textSize*2);
        closeButton.addActionListener(e->closeWindow());
        buttonPanel.add(closeButton);
        contentPanel.add(buttonPanel);
        this.add(contentPanel,BorderLayout.CENTER);
    }
    // 更新状态文本
    private void updateStatusText(){
        if(pet.getPlayPetData()==null){
            statusLabel.setText("");
            return;
        }
        String statusText=String.format("等级:%d 经验:%d/%d | 动画:%s",
            pet.getPlayPetData().getAffectionLevel(),
            pet.getPlayPetData().getAffectionPoints(),
            pet.affectLevelUp,
            pet.animationSprite.currentAnimation);
        statusLabel.setText(statusText);
    }
    // 启动状态更新定时器
    private void startStatusUpdate(){
        updateTimer=new Timer(500,e->updateStatusText());
        updateTimer.start();
    }
    // 显示消息气泡
    private void showMessage(String message){
        PetMessageBubble bubble=new PetMessageBubble(message);
        bubble.setLocation(pet.getLocation());
        pet.getParent().add(bubble);
        bubble.revalidate();
        bubble.repaint();
    }
    // 关闭窗口
    private void closeWindow(){
        if(updateTimer!=null){
            updateTimer.stop();
        }
        this.setVisible(false);
        this.dispose();
        currentInstance=null;
    }
    // 显示宠物选项窗口
    public static void showForPet(PetBase pet){
        if(currentInstance!=null){
            currentInstance.closeWindow();
        }
        Dimension size=new Dimension(240,160);
        currentInstance=new PetOption(pet,size);
        Point petLocation=pet.getLocationOnScreen();
        Point windowLocation=new Point(petLocation.x+pet.getWidth()+5,petLocation.y);
        if(windowLocation.x+size.width>GUtil.SCREEN_dimension.width){
            windowLocation.x=petLocation.x-size.width-5;
        }
        if(windowLocation.y+size.height>GUtil.SCREEN_dimension.height){
            windowLocation.y=GUtil.SCREEN_dimension.height-size.height;
        }
        currentInstance.setLocation(windowLocation);
        currentInstance.setVisible(true);
    }
}
