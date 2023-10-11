package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerProvider;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.ProvidesIntoMap;
import com.google.inject.multibindings.StringMapKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

/**
 * On how to provide multiple implementations of an interface, grouped into sets
 *
 * @see <a href="https://github.com/google/guice/wiki/Multibindings#multibindings">The official documentation</a> for further details
 */
public class MapProvidingStrategies {

    @Test
    void providesIntoSet() {
        //Create Injector, with two different declaration methods across three modules
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Multibinder for inline instantiation (@Inject eligible)
                        MapBinder<String, NumberProvider> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberProvider.class);
                        mapBinder.addBinding("ONE").to(TheAnswerProvider.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Each module can redefine the multibinder, despite the word "new" in "newSetBinder" they will all merge in the end
                        MapBinder<String, NumberProvider> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberProvider.class);
                        //toInstance automatically implied Singleton injection
                        mapBinder.addBinding("TWO").toInstance(new CannedAnswerProvider(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoMap
                    @StringMapKey("THREE")
                        //Or just use this annotation in a providing method
                    NumberProvider numberProvider() {
                        return new CannedAnswerProvider(33);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ClassInjectingMap.class);
                    }
                });
        //fetch/create instances
        final Map<String, NumberProvider> instances = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberProvider.class));
        Assertions.assertEquals(3, instances.size());
        instances.entrySet().stream().forEach(System.out::println);
        //fetch/create instances again (different copies will be produces for 2 of the 3)
        final Map<String, NumberProvider> instances2 = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberProvider.class));
        instances2.entrySet().stream().forEach(System.out::println);
        //Proof that it is not the same set
        Assertions.assertNotEquals(instances, instances2);
        //Bonus track: injection into a class
        Assertions.assertEquals(3, injector.getInstance(ClassInjectingMap.class).getProviderMap().size());
    }

    public static class ClassInjectingMap {
        @Inject
        private Map<String, NumberProvider> providerMap;

        public Map<String, NumberProvider> getProviderMap() {
            return providerMap;
        }
    }

    @Test
    void providesIntoMapWithSingletons() {
        //Create Injector, with two different declaration methods
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        MapBinder<String, NumberProvider> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberProvider.class);
                        //Singleton is specified manually
                        mapBinder.addBinding("ONE").to(TheAnswerProvider.class).in(Singleton.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        MapBinder<String, NumberProvider> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberProvider.class);
                        //Singleton implicit in toInstance method
                        mapBinder.addBinding("TWO").toInstance(new CannedAnswerProvider(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoMap
                    @StringMapKey("THREE")
                    //In this case we need to use the Singleton annotation
                    @Singleton
                    NumberProvider numberProvider() {
                        return new CannedAnswerProvider(33);
                    }
                });
        //fetch/create instances
        final Map<String, NumberProvider> instances = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberProvider.class));
        instances.entrySet().stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch the same set of instances again
        final Map<String, NumberProvider> instances2 = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberProvider.class));
        instances2.entrySet().stream().forEach(System.out::println);
        //Proof that it is the same set
        Assertions.assertEquals(instances, instances2);
    }


}
