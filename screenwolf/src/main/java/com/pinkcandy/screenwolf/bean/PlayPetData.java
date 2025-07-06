package com.pinkcandy.screenwolf.bean;

// 与桌宠游玩的数据
public class PlayPetData {
    private int mouseClickNum = 0; // 鼠标点击记数
    private int followMouseDistance = 0; // 跟随鼠标距离
    private int affectionLevel = 50; // 好感等级
    private int affectionPoints = 0; // 好感度点数
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
}
