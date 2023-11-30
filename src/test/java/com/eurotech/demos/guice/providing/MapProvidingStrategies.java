package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerFactory;
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
                        MapBinder<String, NumberFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberFactory.class);
                        mapBinder.addBinding("ONE").to(TheAnswerFactory.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Each module can redefine the multibinder, despite the word "new" in "newSetBinder" they will all merge in the end
                        MapBinder<String, NumberFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberFactory.class);
                        //toInstance automatically implied Singleton injection
                        mapBinder.addBinding("TWO").toInstance(new CannedAnswerFactory(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoMap
                    @StringMapKey("THREE")
                        //Or just use this annotation in a providing method
                    NumberFactory numberFactory() {
                        return new CannedAnswerFactory(33);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ClassInjectingMap.class);
                    }
                });
        //fetch/create instances
        final Map<String, NumberFactory> instances = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberFactory.class));
        Assertions.assertEquals(3, instances.size());
        instances.entrySet().stream().forEach(System.out::println);
        //fetch/create instances again (different copies will be produces for 2 of the 3)
        final Map<String, NumberFactory> instances2 = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberFactory.class));
        instances2.entrySet().stream().forEach(System.out::println);
        //Proof that it is not the same set
        Assertions.assertNotEquals(instances, instances2);
        //Bonus track: injection into a class
        Assertions.assertEquals(3, injector.getInstance(ClassInjectingMap.class).getProviderMap().size());
    }

    public static class ClassInjectingMap {
        @Inject
        private Map<String, NumberFactory> providerMap;

        public Map<String, NumberFactory> getProviderMap() {
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
                        MapBinder<String, NumberFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberFactory.class);
                        //Singleton is specified manually
                        mapBinder.addBinding("ONE").to(TheAnswerFactory.class).in(Singleton.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        MapBinder<String, NumberFactory> mapBinder = MapBinder.newMapBinder(binder(), String.class, NumberFactory.class);
                        //Singleton implicit in toInstance method
                        mapBinder.addBinding("TWO").toInstance(new CannedAnswerFactory(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoMap
                    @StringMapKey("THREE")
                    //In this case we need to use the Singleton annotation
                    @Singleton
                    NumberFactory numberFactory() {
                        return new CannedAnswerFactory(33);
                    }
                });
        //fetch/create instances
        final Map<String, NumberFactory> instances = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberFactory.class));
        instances.entrySet().stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch the same set of instances again
        final Map<String, NumberFactory> instances2 = injector.getInstance(GuiceKeysUtils.keyForMapOf(String.class, NumberFactory.class));
        instances2.entrySet().stream().forEach(System.out::println);
        //Proof that it is the same set
        Assertions.assertEquals(instances, instances2);
    }


}
