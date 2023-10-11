package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberProvider;

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
