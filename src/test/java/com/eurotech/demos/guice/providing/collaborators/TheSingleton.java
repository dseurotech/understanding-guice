package com.eurotech.demos.guice.providing.collaborators;

public class TheSingleton {

    private static TheSingleton instance = new TheSingleton();

    private TheSingleton() {
    }

    public static TheSingleton getInstance() {
        return instance;
    }
}


