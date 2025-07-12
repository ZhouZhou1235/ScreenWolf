package com.pinkcandy.screenwolf.windows;

import javax.swing.*;
import java.awt.*;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.GameInfoData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;

// 游戏介绍窗口
public class InfoWindow extends WindowBase {
    public InfoWindow(){
        super("Info",GUtil.DEFAULT_windowSize);
        initWindow();
    }
    private void initWindow(){
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(ResourceReader.getResourceAsImageIcon("images/icon.png").getImage());
        GUtil.setWindowCenter(this);
        GameInfoData gameInfoData = GsonUtil.json2Bean(ResourceReader.readResourceAsString("screenwolf.json"),GameInfoData.class);
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 250));
        // 标题
        ImageIcon logo = ResourceReader.getResourceAsImageIcon("images/logo.png");
        logo = GUtil.scaleImageIcon(logo, (int)(getWidth() * 0.5));
        JLabel titleLabel = new JLabel(logo, SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        // 内容
        String infoText = gameInfoData.getInfo();
        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN,GUtil.DEFAULT_textSize));
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN,GUtil.DEFAULT_textSize));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(infoLabel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane,BorderLayout.CENTER);
        JButton closeButton = new JButton("关闭");
        closeButton.setFont(GUtil.DEFAULT_font);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.addActionListener(e -> this.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel,BorderLayout.SOUTH);
        this.add(mainPanel);
        this.pack();
    }
}
