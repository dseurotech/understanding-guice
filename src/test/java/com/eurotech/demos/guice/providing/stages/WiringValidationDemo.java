package com.eurotech.demos.guice.providing.stages;

import com.eurotech.demos.guice.providing.stages.collaborators.LeafClass;
import com.eurotech.demos.guice.providing.stages.collaborators.LeafClassThrowingImpl;
import com.eurotech.demos.guice.providing.stages.collaborators.TimeTrackingClass;
import com.eurotech.demos.guice.providing.stages.collaborators.TimeTrackingClassImpl;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WiringValidationDemo {
    public static class ProvidingModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(TimeTrackingClass.class).to(TimeTrackingClassImpl.class).in(Singleton.class);
            bind(LeafClass.class).to(LeafClassThrowingImpl.class).in(Singleton.class);
        }
    }

    @Test
    public void developmentStageWillNotInstantiateUntilRequested() throws InterruptedException {
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ProvidingModule());
        final TimeTrackingClass instance = injector.getInstance(TimeTrackingClass.class);
        Assertions.assertNotNull(instance);
        Assertions.assertThrows(ProvisionException.class,
                () -> injector.getInstance(LeafClass.class),
                "com.google.inject.ProvisionException: Unable to provision, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at LeafClassThrowingImpl.<init>(LeafClassThrowingImpl.java:5)\n" +
                        "  while locating LeafClassThrowingImpl\n" +
                        "  while locating LeafClass");

        Assertions.assertNotNull(instance);
        Assertions.assertThrows(ProvisionException.class,
                () -> injector.getInstance(LeafClass.class),
                "com.google.inject.ProvisionException: Unable to provision, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at LeafClassThrowingImpl.<init>(LeafClassThrowingImpl.java:8)\n" +
                        "  while locating LeafClassThrowingImpl\n" +
                        "  at LeafClassImpl.<init>(LeafClassImpl.java:10)\n" +
                        "      \\_ for 1st parameter\n" +
                        "  while locating LeafClassImpl\n" +
                        "  while locating LeafClass");
    }

    @Test
    public void productionStageInstantiateWhenDeclared() throws InterruptedException {
        Assertions.assertThrows(CreationException.class,
                () -> Guice.createInjector(Stage.PRODUCTION, new ProvidingModule()),
                "Unable to create injector, see the following errors:\n" +
                        "\n" +
                        "1) [Guice/ErrorInjectingConstructor]: RuntimeException: Cannot Instantiate this!\n" +
                        "  at LeafClassThrowingImpl.<init>(LeafClassThrowingImpl.java:8)\n" +
                        "  while locating LeafClassThrowingImpl\n" +
                        "  at WiringValidationDemo$2.configure(WiringValidationDemo.java:63)\n" +
                        "  while locating LeafClass");
    }
}

