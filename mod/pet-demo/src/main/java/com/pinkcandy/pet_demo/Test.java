package com.pinkcandy.pet_demo;

import com.pinkcandy.screenwolf.Launcher;
import com.pinkcandy.screenwolf.base.PetBase;


// 测试
// 提示：由于特定资源读取机制，需要先导出jar包放到petsPath。
public class Test {
    public static void main(String[] args){
        Launcher launcher = new Launcher();
        PetBase pet = new Pet_Demo(launcher);
        launcher.testLoadPet(pet);
    }
}
