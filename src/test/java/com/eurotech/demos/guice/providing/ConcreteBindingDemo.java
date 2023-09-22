package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.CannedAnswerProvider;
import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Demonstrate how to bind concrete classes, without relying on an interface
 *
 * @see <a href="https://github.com/google/guice/wiki/InstanceBindings">The official documentation</a> for further details.
 */
public class ConcreteBindingDemo {

    @Test
    public void justBindTheConcreteClass() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                //Direct binding of a concrete type, associated to itself
                bind(TheAnswerProvider.class);
            }
        });

        final TheAnswerProvider concreteInstance = injector.getInstance(TheAnswerProvider.class);
        System.out.println(concreteInstance);
        Assertions.assertNotNull(concreteInstance);
    }

    @Test
    public void canBindToASpecificInstance() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                //Direct binding of a specific instance of the concrete type, associated to itself
                bind(CannedAnswerProvider.class).toInstance(new CannedAnswerProvider(42));
            }
        });

        final CannedAnswerProvider concreteInstance = injector.getInstance(CannedAnswerProvider.class);
        System.out.println(String.format("%s: %d", concreteInstance, concreteInstance.giveMeTheNumber()));
        Assertions.assertNotNull(concreteInstance);
    }

    @Test
    public void concreteClassWillNotBeAssociatedToItsInterfaces() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TheAnswerProvider.class);
            }
        });

        //Can't retrieve it based on the interface
        Assertions.assertThrows(ConfigurationException.class, () -> injector.getInstance(NumberProvider.class), "Guice configuration errors:\n" +
                "\n" +
                "1) [Guice/MissingImplementation]: No implementation for NumberProvider was bound.\n" +
                "\n" +
                "Learn more:\n" +
                "  https://github.com/google/guice/wiki/MISSING_IMPLEMENTATION\n" +
                "\n" +
                "1 error\n" +
                "\n" +
                "======================\n" +
                "Full classname legend:\n" +
                "======================\n" +
                "NumberProvider: \"com.eurotech.demos.guice.NumberProvider\"\n" +
                "========================\n" +
                "End of classname legend:\n" +
                "========================\n" +
                "\n" +
                "\n" +
                "\tat com.google.inject.internal.InjectorImpl.getProvider(InjectorImpl.java:1127)\n" +
                "\tat com.google.inject.internal.InjectorImpl.getProvider(InjectorImpl.java:1087)\n" +
                "\tat com.google.inject.internal.InjectorImpl.getInstance(InjectorImpl.java:1139)\n" +
                "\tat com.eurotech.demos.guice.providing.ConcreteBindingDemo.concreteBinding(ConcreteBindingDemo.java:25)");
    }

    @Test
    public void cannotBindAConcreteToItself() {
        Assertions.assertThrows(CreationException.class, () -> Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TheAnswerProvider.class).to(TheAnswerProvider.class);
            }
        }), "Unable to create injector, see the following errors:\n" +
                "\n" +
                "1) [Guice/RecursiveBinding]: Binding points to itself. Key: TheAnswerProvider\n" +
                "  at ConcreteBindingDemo$1.configure(ConcreteBindingDemo.java:24)\n" +
                "\n" +
                "1 error\n" +
                "\n" +
                "======================\n" +
                "Full classname legend:\n" +
                "======================\n" +
                "ConcreteBindingDemo$1: \"com.eurotech.demos.guice.providing.ConcreteBindingDemo$1\"\n" +
                "TheAnswerProvider:     \"com.eurotech.demos.guice.TheAnswerProvider\"\n" +
                "========================\n" +
                "End of classname legend:\n" +
                "========================\n" +
                "\n" +
                "\n" +
                "\tat com.google.inject.internal.Errors.throwCreationExceptionIfErrorsExist(Errors.java:576)\n" +
                "\tat com.google.inject.internal.InternalInjectorCreator.initializeStatically(InternalInjectorCreator.java:163)\n" +
                "\tat com.google.inject.internal.InternalInjectorCreator.build(InternalInjectorCreator.java:110)\n" +
                "\tat com.google.inject.Guice.createInjector(Guice.java:87)\n" +
                "\tat com.google.inject.Guice.createInjector(Guice.java:69)\n" +
                "\tat com.google.inject.Guice.createInjector(Guice.java:59)\n" +
                "\tat com.eurotech.demos.guice.providing.ConcreteBindingDemo.concreteBinding(ConcreteBindingDemo.java:21)");
    }
}
