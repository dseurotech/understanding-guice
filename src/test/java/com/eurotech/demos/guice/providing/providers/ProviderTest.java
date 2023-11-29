package com.eurotech.demos.guice.providing.providers;

import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;

public class ProviderTest {
    public static class MyProvider implements Provider<NumberProvider> {
        @Override
        public NumberProvider get() {
            final CannedAnswerProvider cannedAnswerProvider = new CannedAnswerProvider(42);
            System.out.println("Hello World!");
            return cannedAnswerProvider;
        }

    }

    @Test
    public void testStuff() {
        System.out.println("Here!");
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).toProvider(MyProvider.class);
            }
        });
        System.out.println("Now Here!");
        final NumberProvider res = injector.getInstance(NumberProvider.class);
        System.out.println("And Then Here!");
    }

    @Test
    public void testMoreStuff() {
        System.out.println("Here!");
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).toProvider(MyProvider.class).asEagerSingleton();
            }
        });
        System.out.println("Now Here!");
        final NumberProvider res = injector.getInstance(NumberProvider.class);
        System.out.println("And Then Here!");
    }
}
