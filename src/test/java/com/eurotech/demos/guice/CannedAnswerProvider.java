package com.eurotech.demos.guice;

public class CannedAnswerProvider implements NumberProvider {
    private final int theNumber;

    public CannedAnswerProvider(int theNumber) {
        this.theNumber = theNumber;
    }

    @Override
    public int giveMeTheNumber() {
        return theNumber;
    }
}
