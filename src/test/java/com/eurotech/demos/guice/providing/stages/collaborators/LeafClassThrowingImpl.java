package com.eurotech.demos.guice.providing.stages.collaborators;

import javax.inject.Inject;

public class LeafClassThrowingImpl implements LeafClass {

    @Inject
    public LeafClassThrowingImpl() {
        throw new RuntimeException("Cannot Instantiate this!");
    }

    @Override
    public TimeTrackingClass getTimeTrackingClass() {
        throw new RuntimeException("Nothing to return, class cannot be instantiated!");
    }
}
