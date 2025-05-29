package com.pinkcandy.screenwolfgame;

import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolfpet.TudouUmbreon;
import com.pinkcandy.screenwolfpet.Zhou;

public class Main {
    public static void main(String[] args){
        TransparentScreen screen = new TransparentScreen(GArea.SCREEN_dimension);
        screen.add(new Zhou());
        screen.add(new TudouUmbreon());
    }
}
