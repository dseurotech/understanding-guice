package com.eurotech.demos.guice;

public class CompositeCollaborator {

    public final NumberProvider numberProvider;

    private CompositeCollaborator() {
        this.numberProvider = null;
    }

    public CompositeCollaborator(NumberProvider numberProvider) {
        this.numberProvider = numberProvider;
    }

    public int doubleTheNumber() {
        return numberProvider.giveMeTheNumber() * 2;
    }

    @Override
    public String toString() {
        return "CompositeCollaborator{" +
                "numberProvider=" + numberProvider +
                '}';
    }
}
