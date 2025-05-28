package com.pinkcandy.screenwolf;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.pinkcandy.screenwolf.bean.PetData;

// 全局工作逻辑类
public class GWorkArea {
    // 宠物图片帧资源地址
    static public String PET_imageFramePath = GArea.GAME_workPath+"\\screenwolfpet\\src\\main\\java\\com\\pinkcandy\\ImageFrames\\";
    // 宠物数据地址
    static public String PET_petdataPath = GArea.GAME_workPath+"\\screenwolfpet\\src\\main\\java\\com\\pinkcandy\\PetData\\";
    // 载入宠物动画帧地图
    static public HashMap<String,String> loadPetAnimationMap(String filename){
        String jsonpetdata = GArea.readFile(GWorkArea.PET_petdataPath+filename);
        PetData petData = JSON.parseObject(jsonpetdata).toJavaObject(PetData.class);
        String petid = petData.getId();
        String[] animationNames = petData.getAnimationNames();
        HashMap<String,String> imageFrameHashmap = new HashMap<>();
        for(String animationName:animationNames){
            imageFrameHashmap.put(
                animationName,
                PET_imageFramePath+petid+"\\"+animationName+"\\"
            );
        }
        return imageFrameHashmap;
    }
}
