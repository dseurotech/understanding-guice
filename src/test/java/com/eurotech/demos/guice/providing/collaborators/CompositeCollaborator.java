package com.eurotech.demos.guice.providing.collaborators;

import com.eurotech.demos.guice.NumberFactory;

public class CompositeCollaborator {

    public final NumberFactory numberFactory;

    private CompositeCollaborator() {
        this.numberFactory = null;
    }

    public CompositeCollaborator(NumberFactory numberFactory) {
        this.numberFactory = numberFactory;
    }

    @Override
    public String toString() {
        return "CompositeCollaborator{" +
                "numberFactory=" + numberFactory +
                '}';
    }
}
