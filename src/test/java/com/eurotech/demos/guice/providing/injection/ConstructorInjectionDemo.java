package com.eurotech.demos.guice.providing.injection;

import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerProvider;
import com.eurotech.demos.guice.providing.collaborators.CompositeCollaborator;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerProvider;
import com.eurotech.demos.guice.providing.injection.collaborators.ConstructorInjectableClass;
import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Different constructor injection examples
 *
 * @see <a href="https://github.com/google/guice/wiki/Injections">The official documentation</a> and <a href="https://github.com/google/guice/wiki/ToConstructorBindings">this</a> for further details.
 */
public class ConstructorInjectionDemo {

    @Test
    public void constructorInjection() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                //Direct binding of a concrete type, associated to itself
                bind(ConstructorInjectableClass.class);
            }
        });

        final ConstructorInjectableClass instancedThroughConstructor = injector.getInstance(ConstructorInjectableClass.class);
        System.out.println(instancedThroughConstructor);
        Assertions.assertNotNull(instancedThroughConstructor);
        Assertions.assertEquals("pippo", instancedThroughConstructor.name);
        Assertions.assertNotNull(instancedThroughConstructor.numberProvider);
    }

    @Test
    public void autoBuildsClassThroughInjectableConstructorEvenIfItIsNotBound() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                //Class is not directly bound!!!!
//                bind(ConstructorInjectableClass.class);
            }
        });

        //And yet it is build, thanks to the fact that it could be injected
        final ConstructorInjectableClass instancedThroughConstructor = injector.getInstance(ConstructorInjectableClass.class);
        System.out.println(instancedThroughConstructor);
        Assertions.assertNotNull(instancedThroughConstructor);
        Assertions.assertEquals("pippo", instancedThroughConstructor.name);
        Assertions.assertNotNull(instancedThroughConstructor.numberProvider);
    }

    @Test
    public void canBindNotMarkedConstructorByExplicitlyReferencingIt() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                try {
                    bind(CompositeCollaborator.class).toConstructor(CompositeCollaborator.class.getConstructor(NumberProvider.class));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        final CompositeCollaborator instance = injector.getInstance(CompositeCollaborator.class);
        System.out.println(instance);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.numberProvider);
    }

    @Test
    public void cannotOtherwiseSimplyBindClassWithNoPublicParameterlessConstructor() {
        Assertions.assertThrows(CreationException.class, () -> Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(TheAnswerProvider.class);
                bind(CompositeCollaborator.class);
            }
        }), "No injectable constructor for type CompositeCollaborator.\n" +
                "\n" +
                "class CompositeCollaborator has a private no-arg constructor but the class is not private. Guice can only use a private no-arg constructor if it is defined in a private class.\n" +
                "\n" +
                "Requested by:\n" +
                "1  : CompositeCollaborator.class(CompositeCollaborator.java:11)\n" +
                "     at ConstructorInjectionDemo$3.configure(ConstructorInjectionDemo.java:67)\n" +
                "\n" +
                "Learn more:\n" +
                "  https://github.com/google/guice/wiki/MISSING_CONSTRUCTOR\n" +
                "\n" +
                "1 error\n" +
                "\n" +
                "======================\n" +
                "Full classname legend:\n" +
                "======================\n" +
                "CompositeCollaborator: \"com.eurotech.demos.guice.providing.collaborators.CompositeCollaborator\"\n" +
                "ConstructorInjectionDemo$3:       \"com.eurotech.demos.guice.providing.injection.ConstructorInjectionDemo$3\"\n" +
                "========================\n" +
                "End of classname legend:\n" +
                "========================");
    }

    @Test
    public void canStillBindManually() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class)
                        .annotatedWith(Names.named("theName"))
                        .toInstance("pippo");
                bind(String.class)
                        .annotatedWith(Names.named("anotherName"))
                        .toInstance("pluto");
                bind(NumberProvider.class).toInstance(new CannedAnswerProvider(44));
            }

            @Provides
            ConstructorInjectableClass constructorInjectableClass(@Named("anotherName") String name, NumberProvider numberProvider) {
                //Inject directives written inside the class will be ignored here
                return new ConstructorInjectableClass(name, numberProvider);
            }
        });

        final ConstructorInjectableClass instancedThroughConstructor = injector.getInstance(ConstructorInjectableClass.class);
        System.out.println(instancedThroughConstructor);
        Assertions.assertNotNull(instancedThroughConstructor);
        Assertions.assertEquals("pluto", instancedThroughConstructor.name);
        Assertions.assertNotNull(instancedThroughConstructor.numberProvider);
        Assertions.assertEquals(CannedAnswerProvider.class, instancedThroughConstructor.numberProvider.getClass());
    }

}
