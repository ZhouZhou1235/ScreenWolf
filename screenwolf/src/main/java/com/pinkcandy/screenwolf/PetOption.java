package com.pinkcandy.screenwolf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pinkcandy.screenwolf.base.PetBase;
import com.pinkcandy.screenwolf.base.WindowBase;

// 宠物选项窗口
public class PetOption extends WindowBase {
    private PetBase pet;
    private JPanel mainPanel;
    public PetOption(PetBase thePet,Dimension size){
        super(thePet.getPetData().getId(),size);
        this.pet = thePet;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(false);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JLabel petNameLabel = new JLabel(thePet.getPetData().getName());
        petNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(petNameLabel);
        JLabel petInfoLabel = new JLabel(thePet.getPetData().getInfo());
        petInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petInfoLabel.setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,GArea.DEFAULT_textSize/2));
        mainPanel.add(petInfoLabel);
        addOption("休息",new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){pet.doRest();}}
        );
        addOption("截图",new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){pet.copyScreenImage();}}
        );
        this.add(mainPanel);
    }
    // 添加宠物选项
    public void addOption(String optionName,ActionListener listener){
        JButton someButton = new JButton(optionName);
        someButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        someButton.addActionListener(listener);
        mainPanel.add(someButton);
    }
}
