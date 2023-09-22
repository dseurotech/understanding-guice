package com.eurotech.demos.guice.providing.injection;

import com.eurotech.demos.guice.CannedAnswerProvider;
import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Different field injection examples
 *
 * @see <a href="https://github.com/google/guice/wiki/Injections#field-injection">The official documentation</a> for further details.
 */
public class FieldInjectionDemo {

    @Test
    public void propertyInjection() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                //Direct binding of a concrete type, associated to itself
                bind(FieldInjectableClass.class);
            }
        });

        final FieldInjectableClass instance = injector.getInstance(FieldInjectableClass.class);
        System.out.println(instance);
        Assertions.assertNotNull(instance);
        Assertions.assertEquals("pippo", instance.name);
        Assertions.assertNotNull(instance.numberProvider);
    }

    @Test
    public void canStillBindManually() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
            }

            @Provides
            FieldInjectableClass constructorInjectableClass() {
                //Inject directives written inside the class will be ignored here
                final FieldInjectableClass instance = new FieldInjectableClass();
                instance.name = "pluto";
                instance.numberProvider = new CannedAnswerProvider(44);
                return instance;
            }
        });

        final FieldInjectableClass instancedThroughConstructor = injector.getInstance(FieldInjectableClass.class);
        System.out.println(instancedThroughConstructor);
        Assertions.assertNotNull(instancedThroughConstructor);
        Assertions.assertEquals("pluto", instancedThroughConstructor.name);
        Assertions.assertNotNull(instancedThroughConstructor.numberProvider);
        Assertions.assertEquals(CannedAnswerProvider.class, instancedThroughConstructor.numberProvider.getClass());
    }

    @Test
    public void autoBuildsClassThroughPropertyInjectionEvenIfItIsNotBound() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                //Class is not directly bound!!!!
//                bind(FieldInjectableClass.class);
            }
        });

        //And yet it is build, thanks to the fact that it could be injected
        final FieldInjectableClass instance = injector.getInstance(FieldInjectableClass.class);
        System.out.println(instance);
        Assertions.assertNotNull(instance);
        Assertions.assertEquals("pippo", instance.name);
        Assertions.assertNotNull(instance.numberProvider);
    }

}
