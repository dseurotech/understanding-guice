package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberFactory;

public class CannedAnswerFactory implements NumberFactory {
    private final int theNumber;

    public CannedAnswerFactory(int theNumber) {
        this.theNumber = theNumber;
    }

    @Override
    public int giveMeTheNumber() {
        return theNumber;
    }
}
