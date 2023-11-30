package com.eurotech.demos.guice.providing.providers;

import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;

public class ProviderTest {
    public static class MyProvider implements Provider<NumberFactory> {
        @Override
        public NumberFactory get() {
            final CannedAnswerFactory cannedAnswerFactory = new CannedAnswerFactory(42);
            System.out.println("Hello World!");
            return cannedAnswerFactory;
        }

    }

    @Test
    public void testStuff() {
        System.out.println("Here!");
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberFactory.class).toProvider(MyProvider.class);
            }
        });
        System.out.println("Now Here!");
        final NumberFactory res = injector.getInstance(NumberFactory.class);
        System.out.println("And Then Here!");
    }

    @Test
    public void testMoreStuff() {
        System.out.println("Here!");
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberFactory.class).toProvider(MyProvider.class).asEagerSingleton();
            }
        });
        System.out.println("Now Here!");
        final NumberFactory res = injector.getInstance(NumberFactory.class);
        System.out.println("And Then Here!");
    }
}
