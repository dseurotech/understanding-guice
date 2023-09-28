package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.providing.collaborators.CannedAnswerProvider;
import com.eurotech.demos.guice.providing.collaborators.NumberProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * expands the use of Singleton providing
 *
 * @see <a href="https://github.com/google/guice/wiki/Scopes">The official documentation</a> for further details.
 */
public class MoreSingletonExamples {

    @Test
    void withSingleton() throws InterruptedException {
        System.out.println("With singleton:");

        //Initialize WITH singleton
        final AtomicInteger numberProvider = new AtomicInteger(1);
        final Injector plainInjector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            NumberProvider random() {
                return new CannedAnswerProvider(numberProvider.getAndIncrement());
            }
        });
        final NumberProvider numberProvider1 = plainInjector.getInstance(NumberProvider.class);
        final NumberProvider numberProvider2 = plainInjector.getInstance(NumberProvider.class);
        final int number1 = numberProvider1.giveMeTheNumber();
        final int number2 = numberProvider2.giveMeTheNumber();

        System.out.println(String.format("Number 1: %d", number1));
        System.out.println(String.format("Number 2: %d", number2));
        //Same result
        Assertions.assertEquals(number1, number2);
        Assertions.assertEquals(1, number2);
        //As they are the same instance
        System.out.println(String.format("Instance 1: %s", numberProvider1));
        System.out.println(String.format("Instance 2: %s", numberProvider2));
        Assertions.assertEquals(numberProvider1, numberProvider2);
    }

    @Test
    void withoutSingleton() throws InterruptedException {
        System.out.println("Without singleton:");
        //Initialize WITHOUT singleton
        final AtomicInteger numberProvider = new AtomicInteger(1);
        final Injector plainInjector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider random() {
                return new CannedAnswerProvider(numberProvider.getAndIncrement());
            }
        });
        final NumberProvider numberProvider1 = plainInjector.getInstance(NumberProvider.class);
        final NumberProvider numberProvider2 = plainInjector.getInstance(NumberProvider.class);
        final int number1 = numberProvider1.giveMeTheNumber();
        final int number2 = numberProvider2.giveMeTheNumber();
        System.out.println(String.format("Number 1: %d", number1));
        System.out.println(String.format("Number 2: %d", number2));
        //Different results
        Assertions.assertNotEquals(number1, number2);
        Assertions.assertEquals(1, number1);
        Assertions.assertEquals(2, number2);
        //As thery are different instances
        System.out.println(String.format("Instance 1: %s", numberProvider1));
        System.out.println(String.format("Instance 2: %s", numberProvider2));
        Assertions.assertNotEquals(numberProvider1, numberProvider2);
    }

}
