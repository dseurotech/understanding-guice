package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.CannedAnswerProvider;
import com.eurotech.demos.guice.NumberProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;

public class MoreSingletonExamples {

    @Test
    void withSingleton() throws InterruptedException {
        System.out.println("With singleton:");

        //Initialize WITH singleton
        final Injector plainInjector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            NumberProvider random() {
                return new CannedAnswerProvider(new Random(new Date().toInstant().toEpochMilli()).nextInt());
            }
        });
        final NumberProvider numberProvider1 = plainInjector.getInstance(NumberProvider.class);
        Thread.sleep(1);
        final NumberProvider numberProvider2 = plainInjector.getInstance(NumberProvider.class);
        final int number1 = numberProvider1.giveMeTheNumber();
        final int number2 = numberProvider2.giveMeTheNumber();

        System.out.println(String.format("Number 1: %d", number1));
        System.out.println(String.format("Number 2: %d", number2));
        //Same result
        Assertions.assertEquals(number1, number2);
        //As they are the same instance
        System.out.println(String.format("Instance 1: %s", numberProvider1));
        System.out.println(String.format("Instance 2: %s", numberProvider2));
        Assertions.assertEquals(numberProvider1, numberProvider2);
    }

    @Test
    void withoutSingleton() throws InterruptedException {
        System.out.println("Without singleton:");
        //Initialize WITHOUT singleton
        final Injector plainInjector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider random() {
                return new CannedAnswerProvider(new Random(new Date().toInstant().toEpochMilli()).nextInt());
            }
        });
        final NumberProvider numberProvider1 = plainInjector.getInstance(NumberProvider.class);
        Thread.sleep(1);
        final NumberProvider numberProvider2 = plainInjector.getInstance(NumberProvider.class);
        final int number1 = numberProvider1.giveMeTheNumber();
        final int number2 = numberProvider2.giveMeTheNumber();
        System.out.println(String.format("Number 1: %d", number1));
        System.out.println(String.format("Number 2: %d", number2));
        //Different results
        Assertions.assertNotEquals(number1, number2);
        //As thery are different instances
        System.out.println(String.format("Instance 1: %s", numberProvider1));
        System.out.println(String.format("Instance 2: %s", numberProvider2));
        Assertions.assertNotEquals(numberProvider1, numberProvider2);
    }

}
