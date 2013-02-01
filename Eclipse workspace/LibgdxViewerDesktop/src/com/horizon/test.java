package com.horizon;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class test {
        public static void main (String[] args) {
                new LwjglApplication(new OpeningInDoorsGame(), "Game", 1024, 600, false);
        }
}
