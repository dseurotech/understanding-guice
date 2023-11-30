package com.eurotech.demos.guice.providing;

import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * showcases different ways of wiring a class in a module, all equivalent.
 *
 * @see <a href="https://github.com/google/guice/wiki/LinkedBindings">The official documentation</a> for further details.
 */
public class ProvidingEquivalenceDemos {
    @ImplementedBy(CannedAnswerFactory.class) //Demo purposes only, do not use this
    public interface NumberFactory {
        int giveMeTheNumber();
    }

    @Singleton //Guarantees singleton instantiation
    public static class CannedAnswerFactory implements NumberFactory {
        private final int theNumber;

        @Inject //Allows for auto-injection
        public CannedAnswerFactory(int theNumber) {
            this.theNumber = theNumber;
        }

        @Override
        public int giveMeTheNumber() {
            return theNumber;
        }
    }

    //This would already be enough
    public static class JustTheNumber extends AbstractModule {
        @Override
        protected void configure() {
            bind(Integer.class).toInstance(42);
            //NumberFactory not declared, will still be built if required explicitly or by other components
        }
    }

    // Even better with any of the following modules:
    public static class ExplicitCompactBinding extends AbstractModule {
        @Override
        protected void configure() {
            bind(NumberFactory.class).to(CannedAnswerFactory.class).in(Singleton.class);
        }
    }

    public static class ProvidesMethod extends AbstractModule {
        @Provides
        @Singleton
        public NumberFactory numberFactory(Integer myNumber) {
            return new CannedAnswerFactory(myNumber);
        }
    }

    public static class ProviderClassBinding extends AbstractModule {
        @Override
        protected void configure() {
            bind(NumberFactory.class).toProvider(NumberFactoryProvider.class).in(Singleton.class);
        }
    }

    public static class ProviderClassMethod extends AbstractModule {
        @Provides
        @Singleton
        public Provider<NumberFactory> numberFactoryProvider(Integer myNumber) {
            return new ProvidingEquivalenceDemos.NumberFactoryProvider(myNumber);
        }
    }

    private static class NumberFactoryProvider implements Provider<NumberFactory> {
        private final Integer myNumber;

        @Inject
        public NumberFactoryProvider(Integer myNumber) {
            this.myNumber = myNumber;
        }

        @Override
        public NumberFactory get() {
            return new CannedAnswerFactory(myNumber);
        }
    }
}
