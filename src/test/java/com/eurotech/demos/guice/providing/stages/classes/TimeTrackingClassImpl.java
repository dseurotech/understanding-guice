package com.eurotech.demos.guice.providing.stages.classes;

import java.time.Instant;
import java.util.Date;

public class TimeTrackingClassImpl implements TimeTrackingClass {
    private final Instant instantiatedOn;

    public TimeTrackingClassImpl() {
        instantiatedOn = new Date().toInstant();
    }

    @Override
    public Instant getInstantiatedOn() {
        return instantiatedOn;
    }
}
