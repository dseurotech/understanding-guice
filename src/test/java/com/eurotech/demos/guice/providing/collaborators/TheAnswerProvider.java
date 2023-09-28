package com.eurotech.demos.guice.providing.collaborators;

public class TheAnswerProvider implements NumberProvider {
    @Override
    public int giveMeTheNumber() {
        return 42;
    }
}
