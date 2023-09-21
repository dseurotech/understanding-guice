package com.eurotech.demos.guice;

public class TheAnswerProvider implements NumberProvider{
    @Override
    public int giveMeTheNumber() {
        return 42;
    }
}
