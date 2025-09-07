package com.pinkcandy.screenwolf_zhou;

import java.awt.Color;
import java.io.IOException;
import com.pinkcandy.screenwolf.utils.GUtil;
import com.pinkcandy.screenwolf.utils.JarFileUtil;
import com.pinkcandy.screenwolf.windows.PetOption;


// 蓝狗的面板
public class MyOption extends PetOption {
    private Pet thePet;
    public MyOption(Pet pet){
        super(pet);
        this.thePet = pet;
        loadButtonsToPanel();
        adjustWindowSize();
        this.setBackground(new Color(100,120,150,200));
    }
    @Override
    protected void readyToPaint(){
        updateStatusText();
        setupDragBehavior();
        startStatusUpdate();
    }
    @Override
    public void loadButtonsToPanel(){
        super.loadButtonsToPanel();
        try{
            this.addButton(
                GUtil.createImageIconFromBytes(JarFileUtil.readByteInJarFile(
                    this.thePet.getJarPath(),
                    "assets/images/button_dash.png"
                )),
                "冲刺",
                e->{this.thePet.snowDash();}
            );
        }
        catch(IOException e){System.err.println(e);}
    }
}
