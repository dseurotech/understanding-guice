package com.eurotech.demos.guice.providing.stages.classes;

import javax.inject.Inject;

public class IntermediateClassImpl implements IntermediateClass {

    private final TimeTrackingClass timeTrackingClass;

    @Inject
    public IntermediateClassImpl(TimeTrackingClass timeTrackingClass) {
        this.timeTrackingClass = timeTrackingClass;
    }

    @Override
    public TimeTrackingClass getTimeTrackingClass() {
        return timeTrackingClass;
    }
}
