package com.eurotech.demos.guice.providing.stages.collaborators;

import javax.inject.Inject;

public class AnotherLeafClassImpl implements AnotherLeafClass {

    private final TimeTrackingClass timeTrackingClass;

    @Inject
    public AnotherLeafClassImpl(TimeTrackingClass timeTrackingClass) {
        this.timeTrackingClass = timeTrackingClass;
    }

    @Override
    public TimeTrackingClass getTimeTrackingClass() {
        return timeTrackingClass;
    }
}
