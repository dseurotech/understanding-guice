package com.eurotech.demos.guice.providing.stages;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.providing.stages.collaborators.LeafClass;
import com.eurotech.demos.guice.providing.stages.collaborators.LeafClassImpl;
import com.eurotech.demos.guice.providing.stages.collaborators.TimeTrackingClass;
import com.eurotech.demos.guice.providing.stages.collaborators.TimeTrackingClassImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Names;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class InstantiationTimeDemo {

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());

    public static class ProvidingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(TimeTrackingClass.class).to(TimeTrackingClassImpl.class).in(Singleton.class);
            bind(LeafClass.class).annotatedWith(Names.named("guiceControlled")).to(LeafClassImpl.class).in(Singleton.class);
            bind(LeafClass.class).annotatedWith(Names.named("concreteInstance")).toInstance(new LeafClassImpl(new TimeTrackingClassImpl()));
        }
    }

    @Test
    public void developStageDemo() throws InterruptedException {
        final Stage stage = Stage.DEVELOPMENT;
        final Instant injectorCreationTime = new Date().toInstant();
        final Injector injector = Guice.createInjector(stage, new ProvidingModule());
        Thread.sleep(800);
        final Instant instanceRequestTime = new Date().toInstant();
        final LeafClass guiceInstance = injector.getInstance(GuiceKeysUtils.named(LeafClass.class, "guiceControlled"));
        final LeafClass concreteInstance = injector.getInstance(GuiceKeysUtils.named(LeafClass.class, "concreteInstance"));
        //Guice-controlled singleton is created when requested in DEVELOP mode
        checkTimes(injectorCreationTime, instanceRequestTime, guiceInstance.getTimeTrackingClass().getInstantiatedOn(), stage.name() + " guice-controlled", Expected.CloserToInstanceRequest);
        //Concrete class is instantiated at injector creation
        checkTimes(injectorCreationTime, instanceRequestTime, concreteInstance.getTimeTrackingClass().getInstantiatedOn(), stage.name() + " concrete instance", Expected.CloserToInjectorCreation);
    }

    @Test
    public void productionStageDemo() throws InterruptedException {
        final Stage stage = Stage.PRODUCTION;
        final Instant injectorCreationTime = new Date().toInstant();
        final Injector injector = Guice.createInjector(stage, new ProvidingModule());
        Thread.sleep(800);
        final Instant instanceRequestTime = new Date().toInstant();
        final LeafClass guiceInstance = injector.getInstance(GuiceKeysUtils.named(LeafClass.class, "guiceControlled"));
        final LeafClass concreteInstance = injector.getInstance(GuiceKeysUtils.named(LeafClass.class, "concreteInstance"));
        //Guice-controlled is instantiated at injector creation in PRODUCTION stage
        checkTimes(injectorCreationTime, instanceRequestTime, guiceInstance.getTimeTrackingClass().getInstantiatedOn(), stage.name() + " guice-controlled", Expected.CloserToInjectorCreation);
        //Concrete class is instantiated at injector creation
        checkTimes(injectorCreationTime, instanceRequestTime, concreteInstance.getTimeTrackingClass().getInstantiatedOn(), stage.name() + " concrete instance", Expected.CloserToInjectorCreation);

    }

    private long calculateDistance(Instant t1, Instant t2) {
        return Math.abs(Duration.between(t1, t2).toMillis());
    }

    private enum Expected {
        CloserToInjectorCreation,
        CloserToInstanceRequest
    }

    private void checkTimes(Instant injectorCreationTime, Instant objectRequestTime, Instant instanceInternalTime, String context, Expected expectation) {
        System.out.println(String.format("%s: injector creation time: %s", context, formatter.format(injectorCreationTime)));
        System.out.println(String.format("%s: instance request  time: %s", context, formatter.format(objectRequestTime)));
        System.out.println(String.format("%s: instance creation time: %s", context, formatter.format(instanceInternalTime)));
        final long distanceFromInjectorCreation = calculateDistance(injectorCreationTime, instanceInternalTime);
        final long distanceFromInstanceRequest = calculateDistance(objectRequestTime, instanceInternalTime);
        System.out.println(String.format("%s: Distance from injector creation: %d ms", context, distanceFromInjectorCreation));
        System.out.println(String.format("%s: Distance from instance request : %d ms", context, distanceFromInstanceRequest));
        switch (expectation) {
            case CloserToInjectorCreation:
                Assertions.assertTrue(distanceFromInjectorCreation < 400);
                Assertions.assertTrue(distanceFromInstanceRequest > 400);
                Assertions.assertTrue(distanceFromInjectorCreation < distanceFromInstanceRequest);
                System.out.println(String.format("%s: Closer to Injector Creation\n", context));
                break;
            case CloserToInstanceRequest:
                Assertions.assertTrue(distanceFromInjectorCreation > 400);
                Assertions.assertTrue(distanceFromInstanceRequest < 400);
                Assertions.assertTrue(distanceFromInjectorCreation > distanceFromInstanceRequest);
                System.out.println(String.format("%s: Closer to Instance request\n", context));
                break;
        }
    }
}

