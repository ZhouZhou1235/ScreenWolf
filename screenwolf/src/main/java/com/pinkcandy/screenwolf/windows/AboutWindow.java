package com.pinkcandy.screenwolf.windows;

import javax.swing.*;
import java.awt.*;
import com.pinkcandy.screenwolf.base.WindowBase;
import com.pinkcandy.screenwolf.bean.GameInfoData;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.GsonUtil;
import com.pinkcandy.screenwolf.utils.ResourceReader;

// 关于
// TODO 游戏介绍窗口
public class AboutWindow extends WindowBase {
    public AboutWindow(){
        super("Info",GUtil.DEFAULT_windowSize);
        initWindow();
    }
    private void initWindow(){
        GameInfoData data = GsonUtil.json2Bean(ResourceReader.readResourceAsString("screenwolf.json"),GameInfoData.class);
        JPanel panel=new JPanel(new BorderLayout());
        
        ImageIcon logo=ResourceReader.getResourceAsImageIcon("images/logo.png");
        logo=GUtil.scaleImageIcon(logo,(int)(getWidth()*0.5));
        panel.add(new JLabel(logo,SwingConstants.CENTER),BorderLayout.NORTH);
        
        JLabel infoLabel=new JLabel(data.getInfo());
        infoLabel.setFont(GUtil.DEFAULT_font.deriveFont(Font.PLAIN,GUtil.DEFAULT_textSize));
        
        JScrollPane scroll=new JScrollPane(infoLabel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll,BorderLayout.CENTER);
        
        this.add(panel);
        this.pack();
        GUtil.setWindowCenter(this);
    }
}
