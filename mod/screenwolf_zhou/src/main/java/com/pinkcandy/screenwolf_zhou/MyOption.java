package com.pinkcandy.screenwolf_zhou;

import java.awt.Color;
import java.io.IOException;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.windows.PetOption;


// 蓝狗的面板
public class MyOption extends PetOption {
    public MyOption(Pet pet){
        super(pet);
        this.setBackground(new Color(100,120,150,200));
    }
    @Override
    public void loadButtonsToPanel(){
        super.loadButtonsToPanel();
        Pet myPet = (Pet)this.pet;
        try{
            this.addButton(
                GUtil.createImageIconFromBytes(JarFileUtil.readByteInJarFile(
                    myPet.getJarPath(),
                    "assets/images/button_dash.png"
                )),
                "冲刺",
                e->{myPet.snowDash();}
            );
        }
        catch(IOException e){System.err.println(e);}
    }
}
