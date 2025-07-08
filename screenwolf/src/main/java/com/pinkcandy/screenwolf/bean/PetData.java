package com.pinkcandy.screenwolf.bean;

// 宠物数据
public class PetData {
    private String id;
    private String name;
    private String info;
    private String[] animationNames;
    private String[] messageBubbleList;
    private String iconPath;
    private String mainClass;
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String[] getAnimationNames() {
        return animationNames;
    }
    public String[] getMessageBubbleList() {
        return messageBubbleList;
    }
    public String getIconPath() {
        return iconPath;
    }
    public String getMainClass() {
        return mainClass;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public void setAnimationNames(String[] animationNames) {
        this.animationNames = animationNames;
    }
    public void setMessageBubbleList(String[] messageBubbleList) {
        this.messageBubbleList = messageBubbleList;
    }
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
