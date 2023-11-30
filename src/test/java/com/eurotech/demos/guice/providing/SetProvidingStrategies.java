package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * On how to provide multiple implementations of an interface, grouped into sets
 *
 * @see <a href="https://github.com/google/guice/wiki/Multibindings#multibindings">The official documentation</a> for further details
 */
public class SetProvidingStrategies {

    @Test
    void providesIntoSet() {
        //Create Injector, with two different declaration methods across three modules
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Multibinder for inline instantiation (@Inject eligible)
                        Multibinder<NumberFactory> setBinder = Multibinder.newSetBinder(binder(), NumberFactory.class);
                        setBinder.addBinding().to(TheAnswerFactory.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Each module can redefine the multibinder, despite the word "new" in "newSetBinder" they will all merge in the end
                        Multibinder<NumberFactory> setBinder = Multibinder.newSetBinder(binder(), NumberFactory.class);
                        //toInstance automatically implied Singleton injection
                        setBinder.addBinding().toInstance(new CannedAnswerFactory(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoSet
                        //Or just use this annotation in a providing method
                    NumberFactory numberFactory() {
                        return new CannedAnswerFactory(33);
                    }
                });
        //fetch/create instances
        final Set<NumberFactory> instances = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class));
        instances.stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch/create instances again (different copies will be produces for 2 of the 3)
        final Set<NumberFactory> instances2 = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class));
        //Proof that it is not the same set
        Assertions.assertNotEquals(instances, instances2);
    }

    @Test
    void providesIntoSetWithSingletons() {
        //Create Injector, with two different declaration methods
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<NumberFactory> setBinder = Multibinder.newSetBinder(binder(), NumberFactory.class);
                        //Singleton is specified manually
                        setBinder.addBinding().to(TheAnswerFactory.class).in(Singleton.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<NumberFactory> setBinder = Multibinder.newSetBinder(binder(), NumberFactory.class);
                        //Singleton implicit in toInstance method
                        setBinder.addBinding().toInstance(new CannedAnswerFactory(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoSet
                    //In this case we need to use the Singleton annotation
                    @Singleton
                    NumberFactory numberFactory() {
                        return new CannedAnswerFactory(33);
                    }
                });
        //fetch/create instances
        final Set<NumberFactory> instances = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class));
        instances.stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch the same set of instances again
        final Set<NumberFactory> instances2 = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class));
        instances2.stream().forEach(System.out::println);
        //Proof that it is the same set
        Assertions.assertEquals(instances, instances2);
    }


}
