package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberProvider;

public class TheAnswerProvider implements NumberProvider {
    @Override
    public int giveMeTheNumber() {
        return 42;
    }
}
