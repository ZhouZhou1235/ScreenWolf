package com.pinkcandy.screenwolf.bean;

// 桌宠包数据
public class ModData {
    private String id;
    private String modName;
    private String modInfo;
    private String[] petidList;
    public String getId() {
        return id;
    }
    public String getmodInfo() {
        return modInfo;
    }
    public String getmodName() {
        return modName;
    }
    public String[] getPetidList() {
        return petidList;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setmodInfo(String modInfo) {
        this.modInfo = modInfo;
    }
    public void setmodName(String modName) {
        this.modName = modName;
    }
    public void setPetidList(String[] petidList) {
        this.petidList = petidList;
    }
}
