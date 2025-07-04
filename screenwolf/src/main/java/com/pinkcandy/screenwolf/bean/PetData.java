package com.pinkcandy.screenwolf.bean;

// 宠物数据
public class PetData {
    private String id;
    private String name;
    private String info;
    private String[] animationNames;
    private String[] messageBubbleList;
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
}
