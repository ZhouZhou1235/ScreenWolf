package com.pinkcandy.screenwolf.bean;

// 宠物数据
public class PetData {
    private String name;
    private String info;
    private String jarName;
    private String mainClass;
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
}
