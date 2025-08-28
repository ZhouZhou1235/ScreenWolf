package com.pinkcandy.screenwolf.bean;


/**
 * 游戏信息bean
 * 储存屏幕有狼游戏的描述，作者，版本号等基本信息
 */
public class GameInfoData {
    private String title;
    private String version;
    private String owner;
    private String info;
    public String getTitle() {
        return title;
    }
    public String getVersion() {
        return version;
    }
    public String getOwner() {
        return owner;
    }
    public String getInfo() {
        return info;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public void setInfo(String info) {
        this.info = info;
    }
}
