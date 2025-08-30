package com.pinkcandy.screenwolf.bean;


/**
 * 宠物数据bean
 * 储存一只桌宠的信息
 */
public class PetData {
    private String id;
    private String name;
    private String info;
    private String mainClass;
    private String[] specialMessages;
    private String[] sadMessages;
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getInfo() {
        return info;
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
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setInfo(String info) {
        this.info = info;
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
