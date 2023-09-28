package com.eurotech.demos.guice.providing.stages.collaborators;

import javax.inject.Inject;

public class LeafClassImpl implements LeafClass {

    private final TimeTrackingClass timeTrackingClass;

    @Inject
    public LeafClassImpl(TimeTrackingClass timeTrackingClass) {
        this.timeTrackingClass = timeTrackingClass;
    }

    @Override
    public TimeTrackingClass getTimeTrackingClass() {
        return timeTrackingClass;
    }
}
