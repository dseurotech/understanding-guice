package com.eurotech.demos.guice.providing.stages;

import com.eurotech.demos.guice.providing.stages.classes.TimeTrackingClass;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class InstantiationTimeDemo {

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());

    @Test
    public void developmentStageInstantiateWhenRequested() throws InterruptedException {
        final Instant startupTime = new Date().toInstant();
        System.out.println(String.format("Startup %s: %s", Stage.DEVELOPMENT, formatter.format(startupTime)));
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ProvidingModule(false));
        Thread.sleep(500);
        final Instant instantiationTime = new Date().toInstant();
        final TimeTrackingClass instance = injector.getInstance(TimeTrackingClass.class);
        System.out.println(String.format("Instantiated at: %s", formatter.format(instantiationTime)));
        System.out.println(String.format("Instantiated with stage %s: %s", Stage.DEVELOPMENT, formatter.format(instance.getInstantiatedOn())));
        final long distanceFromStartup = calculateDistance(startupTime, instance.getInstantiatedOn());
        final long distanceFromRequest = calculateDistance(instantiationTime, instance.getInstantiatedOn());
        System.out.println(String.format("Distance from injector creation: %d ms", distanceFromStartup));
        System.out.println(String.format("Distance from instance retrieval: %d ms", distanceFromRequest));
        Assertions.assertFalse(distanceFromStartup < 50);
        Assertions.assertTrue(distanceFromRequest < 50);
    }

    @Test
    public void productionStageInstantiateWhenDeclared() throws InterruptedException {
        final Instant startupTime = new Date().toInstant();
        System.out.println(String.format("Startup %s: %s", Stage.PRODUCTION, formatter.format(startupTime)));
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new ProvidingModule(false));
        Thread.sleep(500);
        final Instant instantiationTime = new Date().toInstant();
        final TimeTrackingClass instance = injector.getInstance(TimeTrackingClass.class);
        System.out.println(String.format("Instantiated at: %s", formatter.format(instantiationTime)));
        System.out.println(String.format("Instantiated with stage %s: %s", Stage.PRODUCTION, formatter.format(instance.getInstantiatedOn())));
        final long distanceFromStartup = calculateDistance(startupTime, instance.getInstantiatedOn());
        final long distanceFromRequest = calculateDistance(instantiationTime, instance.getInstantiatedOn());
        System.out.println(String.format("Distance from injector creation: %d ms", distanceFromStartup));
        System.out.println(String.format("Distance from instance retrieval: %d ms", distanceFromRequest));
        Assertions.assertTrue(distanceFromStartup < 50);
        Assertions.assertFalse(distanceFromRequest < 50);
    }

    private long calculateDistance(Instant t1, Instant t2) {
        return Math.abs(Duration.between(t1, t2).toMillis());
    }

}

