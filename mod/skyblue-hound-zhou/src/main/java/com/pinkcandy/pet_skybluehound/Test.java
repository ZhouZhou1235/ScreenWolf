package com.pinkcandy.pet_skybluehound;

import com.pinkcandy.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;


// 测试
public class Test {
    public static void main(String[] args){
        Launcher launcher = new Launcher();
        PetBase pet = new Pet_SkyblueHound(launcher);
        launcher.testLoadPet(pet);
    }
}
