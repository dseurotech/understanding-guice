package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.NumberProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * showcases guice module examples, and different strategies to provide collaborators
 *
 * @see <a href="https://github.com/google/guice/wiki/LinkedBindings">The official documentation</a> for further details.
 */
public class ProvidingStrategiesDemos {

    public static class SimpleNumberProvider implements NumberProvider {
        public SimpleNumberProvider() {
        }

        @Override
        public int giveMeTheNumber() {
            return 42;
        }
    }

    @Test
    @DisplayName("Binding the collaborator, keyed to its interface type")
    public void bind() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(SimpleNumberProvider.class);
            }
        });
        final NumberProvider providedByInterface = injector.getInstance(NumberProvider.class);
        System.out.println(providedByInterface);
        Assertions.assertNotNull(providedByInterface);
    }

    @Test
    @DisplayName("Providing the collaborator manually")
    public void provides() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new SimpleNumberProvider();
            }
        });
        final NumberProvider providedByInterface = injector.getInstance(NumberProvider.class);
        System.out.println(providedByInterface);
        Assertions.assertNotNull(providedByInterface);
    }

    @Test
    @DisplayName("If the class is easily instantiable (parameterless constructor), Guice builds it on the fly upon request")
    public void autoBuilds() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
        });
        final NumberProvider builtOnTheFly = injector.getInstance(SimpleNumberProvider.class);
        System.out.println(builtOnTheFly);
        Assertions.assertNotNull(builtOnTheFly);
    }

}
