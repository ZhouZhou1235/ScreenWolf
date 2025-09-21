package com.pinkcandy.screenwolf.bean;


/**
 * 桌宠游玩数据bean
 * 储存与这只宠物游玩产生的数据
 */
public class PlayPetData {
    private int mouseClickNum = 0; // 鼠标点击记数
    private int followMouseDistance = 0; // 跟随鼠标距离
    private int affectionLevel = 50; // 好感等级
    private int affectionPoints = 0; // 好感度点数
    private String[] messageBubbleList = {}; // 消息气泡
    private int globalMouseClickCount = 0; // 全局鼠标点击
    private int globalKeyPressCount = 0; // 全局键盘按下
    public int getMouseClickNum() {
        return mouseClickNum;
    }
    public int getFollowMouseDistance() {
        return followMouseDistance;
    }
    public int getAffectionLevel() {
        return affectionLevel;
    }
    public int getAffectionPoints() {
        return affectionPoints;
    }
    public String[] getMessageBubbleList() {
        return messageBubbleList;
    }
    public int getGlobalMouseClickCount() {
        return globalMouseClickCount;
    }
    public int getGlobalKeyPressCount() {
        return globalKeyPressCount;
    }
    public void setMouseClickNum(int mouseClickNum) {
        this.mouseClickNum = mouseClickNum;
    }
    public void setFollowMouseDistance(int followMouseDistance) {
        this.followMouseDistance = followMouseDistance;
    }
    public void setAffectionLevel(int affectionLevel) {
        this.affectionLevel = affectionLevel;
    }
    public void setAffectionPoints(int affectionPoints) {
        this.affectionPoints = affectionPoints;
    }
    public void setMessageBubbleList(String[] messageBubbleList) {
        this.messageBubbleList = messageBubbleList;
    }
    public void setGlobalMouseClickCount(int globalMouseClickCount) {
        this.globalMouseClickCount = globalMouseClickCount;
    }
    public void setGlobalKeyPressCount(int globalKeyPressCount) {
        this.globalKeyPressCount = globalKeyPressCount;
    }
}
