package com.eurotech.demos.guice.providing.stages.classes;

import javax.inject.Inject;

public class IntermediateClassThrowingImpl implements IntermediateClass {

    @Inject
    public IntermediateClassThrowingImpl() {
        throw new RuntimeException("Cannot Instantiate this!");
    }

    @Override
    public TimeTrackingClass getTimeTrackingClass() {
        throw new RuntimeException("Nothing to return, class cannot be instantiated!");
    }
}
