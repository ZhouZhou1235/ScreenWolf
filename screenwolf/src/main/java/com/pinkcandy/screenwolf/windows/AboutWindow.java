package com.pinkcandy.screenwolf.windows;

import javax.swing.*;
import java.awt.*;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.GameInfoData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;


/**
 * 关于
 * 介绍游戏
 */
public class AboutWindow extends WindowBase {
    public AboutWindow() {
        super("Info",GUtil.DEFAULT_windowSize);
        initWindow();
    }
    private void initWindow(){
        GameInfoData data = GsonUtil.json2Bean(ResourceReader.readResourceAsString("screenwolf.json"),GameInfoData.class);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ImageIcon logo = ResourceReader.getResourceAsImageIcon("images/logo.png");
        logo = GUtil.scaleImageIcon(logo, (int)(getWidth()*0.5));
        panel.add(new JLabel(logo, SwingConstants.CENTER), BorderLayout.NORTH);
        JTextArea infoText = new JTextArea(data.getInfo());
        infoText.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN, GUtil.DEFAULT_textSize));
        infoText.setWrapStyleWord(true);
        infoText.setLineWrap(true);
        infoText.setEditable(false);
        infoText.setOpaque(false);
        infoText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));        
        JScrollPane scroll = new JScrollPane(infoText);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll, BorderLayout.CENTER);
        this.add(panel);
        this.pack();
        this.setSize(GUtil.DEFAULT_windowSize);
        GUtil.setWindowCenter(this);
    }
}
