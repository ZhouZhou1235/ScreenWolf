package com.pinkcandy.screenwolf.bean;

// 宠物数据
public class PetData {
    private String name;
    private String info;
    private String jarName;
    private String mainClass;
    private String[] specialMessages;
    private String[] sadMessages;
    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
    }
    public String getJarName() {
        return jarName;
    }
    public String getMainClass() {
        return mainClass;
    }
    public String[] getSpecialMessages() {
        return specialMessages;
    }
    public String[] getSadMessages() {
        return sadMessages;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public void setJarName(String jarName) {
        this.jarName = jarName;
    }
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    public void setSpecialMessages(String[] specialMessages) {
        this.specialMessages = specialMessages;
    }
    public void setSadMessages(String[] sadMessages) {
        this.sadMessages = sadMessages;
    }
}
