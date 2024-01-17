package com.eurotech.demos.guice.providing.multibinding;

import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.StringMapKey;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.Test;

public class MapOverrides {

    @Test
    public void testStuff() {
        final Injector injector = Guice.createInjector(Modules.override(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
                bind(ClassInjectingMap.class).in(Singleton.class);
            }

            @StringMapKey("Two")
            @ProvidesIntoMap()
            NumberFactory two() {
                return new CannedAnswerFactory(2);
            }
        }).with(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
            }

            @StringMapKey("Two")
            @ProvidesIntoMap()
            NumberFactory two() {
                return new CannedAnswerFactory(22);
            }
        }));
        final ClassInjectingMap instance = injector.getInstance(ClassInjectingMap.class);
        System.out.println(instance.getProviderMap());
    }
}
