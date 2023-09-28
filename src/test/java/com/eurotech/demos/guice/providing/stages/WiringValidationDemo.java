package com.eurotech.demos.guice.providing.stages;

import com.eurotech.demos.guice.providing.stages.classes.IntermediateClass;
import com.eurotech.demos.guice.providing.stages.classes.LeafClass;
import com.eurotech.demos.guice.providing.stages.classes.TimeTrackingClass;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class WiringValidationDemo {

    @Test
    public void developmentStageWillNotInstantiateUntilRequested() throws InterruptedException {
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ProvidingModule(true));
        final TimeTrackingClass instance = injector.getInstance(TimeTrackingClass.class);
        Assertions.assertNotNull(instance);
        Assertions.assertThrows(ProvisionException.class,
                () -> injector.getInstance(IntermediateClass.class),
                "com.google.inject.ProvisionException: Unable to provision, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at IntermediateClassThrowingImpl.<init>(IntermediateClassThrowingImpl.java:5)\n" +
                        "  while locating IntermediateClassThrowingImpl\n" +
                        "  while locating IntermediateClass");

        Assertions.assertNotNull(instance);
        Assertions.assertThrows(ProvisionException.class,
                () -> injector.getInstance(LeafClass.class),
                "com.google.inject.ProvisionException: Unable to provision, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at IntermediateClassThrowingImpl.<init>(IntermediateClassThrowingImpl.java:8)\n" +
                        "  while locating IntermediateClassThrowingImpl\n" +
                        "  at LeafClassImpl.<init>(LeafClassImpl.java:10)\n" +
                        "      \\_ for 1st parameter\n" +
                        "  while locating LeafClassImpl\n" +
                        "  while locating LeafClass");
    }

    @Test
    public void productionStageInstantiateWhenDeclared() throws InterruptedException {
        Assertions.assertThrows(CreationException.class,
                () -> Guice.createInjector(Stage.PRODUCTION, new ProvidingModule(true)),
                "Unable to create injector, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at IntermediateClassThrowingImpl.<init>(IntermediateClassThrowingImpl.java:8)\n" +
                        "  while locating IntermediateClassThrowingImpl\n" +
                        "  at WiringValidationDemo$2.configure(WiringValidationDemo.java:63)\n" +
                        "  while locating IntermediateClass");
    }

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.systemDefault());

    @Test
    public void productionStageInstantiateWhenDeclared2() throws InterruptedException {
        final Instant startupTime = new Date().toInstant();
        System.out.println(String.format("Startup: %s", formatter.format(startupTime)));

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, new ProvidingModule(false));
        Thread.sleep(500);
        final Instant instantiationTime = new Date().toInstant();
        System.out.println(String.format("Instantiated at: %s", formatter.format(instantiationTime)));
        final LeafClass leafClass = injector.getInstance(LeafClass.class);
        System.out.println(String.format("Instantiation time: %s", formatter.format(leafClass.getIntermediateClass().getTimeTrackingClass().getInstantiatedOn())));

    }

}

