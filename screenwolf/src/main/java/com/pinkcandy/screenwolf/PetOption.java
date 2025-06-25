package com.pinkcandy.screenwolf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.PetData;
import com.pinkcandy.screenwolf.bean.PlayPetData;

// 宠物选项窗口
public class PetOption extends WindowBase {
    public PetOption(PetData petData,PlayPetData playPetData,Dimension size){
        super(petData.getId(),size);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JLabel petNameLabel = new JLabel(petData.getName());
        petNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(petNameLabel);
        JLabel petInfoLabel = new JLabel(petData.getInfo());
        petInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        petInfoLabel.setFont(new Font(Font.DIALOG,Font.CENTER_BASELINE,GArea.DEFAULT_textSize/2));
        mainPanel.add(petInfoLabel);
        
        // 选项......
        // JButton someButton = new JButton("操作");
        // someButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        // mainPanel.add(someButton);
        
        this.add(mainPanel);
    }
}
