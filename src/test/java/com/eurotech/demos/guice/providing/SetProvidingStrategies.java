package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerProvider;
import com.eurotech.demos.guice.providing.collaborators.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerProvider;
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
                        Multibinder<NumberProvider> setBinder = Multibinder.newSetBinder(binder(), NumberProvider.class);
                        setBinder.addBinding().to(TheAnswerProvider.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        //Each module can redefine the multibinder, despite the word "new" in "newSetBinder" they will all merge in the end
                        Multibinder<NumberProvider> setBinder = Multibinder.newSetBinder(binder(), NumberProvider.class);
                        //toInstance automatically implied Singleton injection
                        setBinder.addBinding().toInstance(new CannedAnswerProvider(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoSet
                        //Or just use this annotation in a providing method
                    NumberProvider numberProvider() {
                        return new CannedAnswerProvider(33);
                    }
                });
        //fetch/create instances
        final Set<NumberProvider> instances = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class));
        instances.stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch/create instances again (different copies will be produces for 2 of the 3)
        final Set<NumberProvider> instances2 = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class));
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
                        Multibinder<NumberProvider> setBinder = Multibinder.newSetBinder(binder(), NumberProvider.class);
                        //Singleton is specified manually
                        setBinder.addBinding().to(TheAnswerProvider.class).in(Singleton.class);
                    }
                },
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<NumberProvider> setBinder = Multibinder.newSetBinder(binder(), NumberProvider.class);
                        //Singleton implicit in toInstance method
                        setBinder.addBinding().toInstance(new CannedAnswerProvider(33));
                    }
                },
                new AbstractModule() {
                    @ProvidesIntoSet
                    //In this case we need to use the Singleton annotation
                    @Singleton
                    NumberProvider numberProvider() {
                        return new CannedAnswerProvider(33);
                    }
                });
        //fetch/create instances
        final Set<NumberProvider> instances = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class));
        instances.stream().forEach(System.out::println);
        Assertions.assertEquals(3, instances.size());
        //fetch the same set of instances again
        final Set<NumberProvider> instances2 = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class));
        instances2.stream().forEach(System.out::println);
        //Proof that it is the same set
        Assertions.assertEquals(instances, instances2);
    }


}
