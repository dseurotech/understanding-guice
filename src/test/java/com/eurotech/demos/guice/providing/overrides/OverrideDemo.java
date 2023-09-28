package com.eurotech.demos.guice.providing.overrides;

import com.eurotech.demos.guice.CannedAnswerProvider;
import com.eurotech.demos.guice.NumberProvider;
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
                bind(NumberProvider.class).toInstance(new CannedAnswerProvider(33));
            }
        }).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).toInstance(new CannedAnswerProvider(44));
            }
        }));

        final NumberProvider res = injector.getInstance(NumberProvider.class);
        Assertions.assertNotNull(res);
        System.out.println(String.format("%s: %d", res, res.giveMeTheNumber()));
        Assertions.assertEquals(44, res.giveMeTheNumber());
    }

    @Test
    public void canOverrideKey2() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new CannedAnswerProvider(33);
            }
        }).with(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new CannedAnswerProvider(44);
            }
        }));

        final NumberProvider res = injector.getInstance(NumberProvider.class);
        Assertions.assertNotNull(res);
        System.out.println(String.format("%s: %d", res, res.giveMeTheNumber()));
        Assertions.assertEquals(44, res.giveMeTheNumber());
    }

    @Test
    public void cannotUseBaseObjectInOverride() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new CannedAnswerProvider(33);
            }
        }).with(new AbstractModule() {
            @Provides
            NumberProvider numberProvider(NumberProvider overridden) {
                return new CannedAnswerProvider(overridden.giveMeTheNumber());
            }
        }));
        Assertions.assertThrows(ProvisionException.class, () ->
                        injector.getInstance(NumberProvider.class),
                "1) [Guice/ErrorInCustomProvider]: IllegalStateException: This is a proxy used to support circular references. The object we're proxying is not constructed yet. Please wait until after injection has completed to use this object.\n" +
                        "  at OverrideDemo$5.numberProvider(OverrideDemo.java:65)\n" +
                        "      \\_ installed by: Modules$OverrideModule -> OverrideDemo$5\n" +
                        "  while locating NumberProvider");
    }
}
