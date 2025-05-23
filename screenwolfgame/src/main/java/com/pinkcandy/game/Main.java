package com.pinkcandy.game;

import com.pinkcandy.pet.Zhou;
import com.pinkcandy.screenwolf.GArea;
import com.pinkcandy.screenwolf.TransparentScreen;
import com.pinkcandy.screenwolf.base.PetBase;

public class Main {
    public static void main(String[] args){
        TransparentScreen screen = new TransparentScreen(GArea.SCREEN_dimension);
        PetBase wolf1 = new Zhou(GArea.DEFAULT_bodySize);
        screen.petAddMouseActionEcho(wolf1);
        screen.add(wolf1);
    }
}
