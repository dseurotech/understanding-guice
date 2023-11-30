package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberFactory;

public class TheAnswerFactory implements NumberFactory {
    @Override
    public int giveMeTheNumber() {
        return 42;
    }
}
