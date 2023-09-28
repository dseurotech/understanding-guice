package com.eurotech.demos.guice.providing.stages.classes;

import java.time.Instant;

public interface TimeTrackingClass {
    Instant getInstantiatedOn();
}
