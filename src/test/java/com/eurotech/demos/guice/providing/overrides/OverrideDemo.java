package com.eurotech.demos.guice.providing.overrides;

import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OverrideDemo {

    @Test
    public void canOverrideKey() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberFactory.class).toInstance(new CannedAnswerFactory(33));
            }
        }).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberFactory.class).toInstance(new CannedAnswerFactory(44));
            }
        }));

        final NumberFactory res = injector.getInstance(NumberFactory.class);
        Assertions.assertNotNull(res);
        System.out.println(String.format("%s: %d", res, res.giveMeTheNumber()));
        Assertions.assertEquals(44, res.giveMeTheNumber());
    }

    @Test
    public void canOverrideKey2() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Provides
            NumberFactory numberFactory() {
                return new CannedAnswerFactory(33);
            }
        }).with(new AbstractModule() {
            @Provides
            NumberFactory numberFactory() {
                return new CannedAnswerFactory(44);
            }
        }));

        final NumberFactory res = injector.getInstance(NumberFactory.class);
        Assertions.assertNotNull(res);
        System.out.println(String.format("%s: %d", res, res.giveMeTheNumber()));
        Assertions.assertEquals(44, res.giveMeTheNumber());
    }

    @Test
    public void cannotUseBaseObjectInOverride() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Provides
            NumberFactory numberFactory() {
                return new CannedAnswerFactory(33);
            }
        }).with(new AbstractModule() {
            @Provides
            NumberFactory numberFactory(NumberFactory overridden) {
                return new CannedAnswerFactory(overridden.giveMeTheNumber());
            }
        }));
        Assertions.assertThrows(ProvisionException.class, () ->
                        injector.getInstance(NumberFactory.class),
                "1) [Guice/ErrorInCustomProvider]: IllegalStateException: This is a proxy used to support circular references. The object we're proxying is not constructed yet. Please wait until after injection has completed to use this object.\n" +
                        "  at OverrideDemo$5.numberFactory(OverrideDemo.java:65)\n" +
                        "      \\_ installed by: Modules$OverrideModule -> OverrideDemo$5\n" +
                        "  while locating NumberFactory");
    }
}
