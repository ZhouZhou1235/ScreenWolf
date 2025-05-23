package com.pinkcandy.pet;

import java.util.HashMap;
import java.util.Map;

import com.pinkcandy.screenwolf.GArea;

public class AnimationTable {
    public Map<String,Map<String,String>> animationTable = new HashMap<>();
    public AnimationTable(){
        String petName = "zhou";
        // todo 资源导入设计
        String imageFramesPath = GArea.GAME_workPath+"\\screenwolfpet\\src\\main\\java\\com\\pinkcandy\\ImageFrames\\";
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("default",imageFramesPath+petName+"\\default\\");
        hashMap.put("focus",imageFramesPath+petName+"\\focus\\");
        animationTable.put("zhou",hashMap);
    }
}
