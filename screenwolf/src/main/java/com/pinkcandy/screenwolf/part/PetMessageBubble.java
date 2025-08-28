package com.pinkcandy.screenwolf.part;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import com.pinkcandy.screenwolf.base.ItemBase;
import com.pinkcandy.screenwolf.utils.GUtil;

/**
 * 宠物消息气泡
 * 在屏幕上显示文字的容器
 */
public class PetMessageBubble extends ItemBase {
    private Timer autoHideTimer;
    private int displayTime = 5000;
    public PetMessageBubble(String text) {
        super(text);
        JLabel textLabel = getBody();
        textLabel.setText(text);
        textLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, GUtil.DEFAULT_textSize));
        textLabel.setForeground(Color.WHITE);
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        textLabel.setVerticalAlignment(SwingConstants.CENTER);
        this.setBackground(new Color(0,0,0,200));
        this.setOpaque(true);
        this.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        adjustSizeToText(text);
        autoHideTimer = new Timer(displayTime, e -> {
            if (this.getParent() != null) {
                this.getParent().remove(this);
            }
            autoHideTimer.stop();
        });
        autoHideTimer.setRepeats(false);
        autoHideTimer.start();
    }
    // 调整大小适应文本
    private void adjustSizeToText(String text) {
        JLabel textLabel = getBody();
        int width = textLabel.getFontMetrics(textLabel.getFont()).stringWidth(text)+30;
        int height = textLabel.getFontMetrics(textLabel.getFont()).getHeight()+20;
        this.setSize(width, height);
        textLabel.setSize(width, height);
    }
    // 显示时间设置
    public void setDisplayTime(int milliseconds) {
        this.displayTime = milliseconds;
        if (autoHideTimer != null) {
            autoHideTimer.setInitialDelay(milliseconds);
        }
    }
    @Override
    public void ready(){
        super.ready();
        PetMessageBubble bubble = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                autoHideTimer.stop();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                autoHideTimer.restart();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (bubble.getParent() != null) {
                        bubble.getParent().remove(bubble);
                    }
                    autoHideTimer.stop();
                }
            }
        });
    }
}
