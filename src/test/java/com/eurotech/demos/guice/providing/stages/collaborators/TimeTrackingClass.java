package com.eurotech.demos.guice.providing.stages.collaborators;

import java.time.Instant;

public interface TimeTrackingClass {
    Instant getInstantiatedOn();
}
