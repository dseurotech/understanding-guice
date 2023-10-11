package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberProvider;

public class CompositeCollaborator {

    public final NumberProvider numberProvider;

    private CompositeCollaborator() {
        this.numberProvider = null;
    }

    public CompositeCollaborator(NumberProvider numberProvider) {
        this.numberProvider = numberProvider;
    }

    @Override
    public String toString() {
        return "CompositeCollaborator{" +
                "numberProvider=" + numberProvider +
                '}';
    }
}
