package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.providing.collaborators.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * showcases guice module examples, and different strategies to provide collaborators
 *
 * @see <a href="https://github.com/google/guice/wiki/LinkedBindings">The official documentation</a> for further details.
 */
public class ProvidingStrategiesDemos {

    @Test
    public void configure() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(TheAnswerProvider.class);
            }
        });
        final NumberProvider provided1 = injector.getInstance(NumberProvider.class);
        final NumberProvider provided2 = injector.getInstance(NumberProvider.class);
        Assertions.assertNotEquals(provided1, provided2);
    }

    @Test
    public void configureWithSingleton() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(TheAnswerProvider.class).in(Singleton.class);
            }
        });
        final NumberProvider provided1 = injector.getInstance(NumberProvider.class);
        final NumberProvider provided2 = injector.getInstance(NumberProvider.class);
        Assertions.assertEquals(provided1, provided2);
    }

    @Test
    public void provides() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new TheAnswerProvider();
            }
        });
        final NumberProvider provided1 = injector.getInstance(NumberProvider.class);
        final NumberProvider provided2 = injector.getInstance(NumberProvider.class);
        Assertions.assertNotEquals(provided1, provided2);
    }

    @Test
    public void providesWithSingleton() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            NumberProvider numberProvider() {
                return new TheAnswerProvider();
            }
        });
        final NumberProvider provided1 = injector.getInstance(NumberProvider.class);
        final NumberProvider provided2 = injector.getInstance(NumberProvider.class);
        Assertions.assertEquals(provided1, provided2);
    }
}
