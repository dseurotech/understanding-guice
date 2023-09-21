package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.CannedAnswerProvider;
import com.eurotech.demos.guice.NumberProvider;
import com.eurotech.demos.guice.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Named;
import java.util.Set;

public class NamedDemo {

    @Test
    void namedInstances() {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(NumberProvider.class).annotatedWith(Names.named("boring")).toInstance(new CannedAnswerProvider(3));
                    }
                },
                new AbstractModule() {
                    @Named("toEverything")
                    @Provides
                    NumberProvider theAnswerProvider() {
                        return new TheAnswerProvider();
                    }
                });

        System.out.println("Instances by name:");
        final NumberProvider boringAnswer = injector.getInstance(GuiceKeysUtils.named(NumberProvider.class, "boring"));
        System.out.println(String.format("boring %s: %d", boringAnswer, boringAnswer.giveMeTheNumber()));
        final NumberProvider theAnswer = injector.getInstance(GuiceKeysUtils.named(NumberProvider.class, "toEverything"));
        System.out.println(String.format("toEverything: %s: %d", theAnswer, theAnswer.giveMeTheNumber()));
        Assertions.assertNotEquals(boringAnswer, theAnswer);
        //They do not contribute to sets
        Assertions.assertThrows(ConfigurationException.class, () ->
                        injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class))
                , "com.google.inject.ConfigurationException: Guice configuration errors:\n" +
                        "\n" +
                        "1) [Guice/MissingImplementation]: No implementation for Set<NumberProvider> was bound.\n" +
                        "\n" +
                        "Did you mean?\n" +
                        "    NumberProvider annotated with @Named(\"boring\") bound at NamedDemo$1.configure(NamedDemo.java:24)\n" +
                        "\n" +
                        "    NumberProvider annotated with @Named(\"toEverything\") bound at NamedDemo$2.theAnswerProvider(NamedDemo.java:31)\n" +
                        "\n" +
                        "Learn more:\n" +
                        "  https://github.com/google/guice/wiki/MISSING_IMPLEMENTATION\n" +
                        "\n" +
                        "1 error\n" +
                        "\n" +
                        "======================\n" +
                        "Full classname legend:\n" +
                        "======================\n" +
                        "Named:          \"com.google.inject.name.Named\"\n" +
                        "NamedDemo$1:    \"com.eurotech.demos.guice.providing.NamedDemo$1\"\n" +
                        "NamedDemo$2:    \"com.eurotech.demos.guice.providing.NamedDemo$2\"\n" +
                        "NumberProvider: \"com.eurotech.demos.guice.NumberProvider\"\n" +
                        "========================\n" +
                        "End of classname legend:\n" +
                        "========================\n")
        ;
    }

    @Test
    void namedInstancesIntoSetDefineTheNameOfTheSet() {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<NumberProvider> setBinder = Multibinder.newSetBinder(binder(), NumberProvider.class, Names.named("firstSet"));
                        setBinder.addBinding().toInstance(new CannedAnswerProvider(33));

                        Multibinder<NumberProvider> setBinder2 = Multibinder.newSetBinder(binder(), NumberProvider.class, Names.named("secondSet"));
                        setBinder2.addBinding().toInstance(new CannedAnswerProvider(44));

                        Multibinder<NumberProvider> unnamedSet = Multibinder.newSetBinder(binder(), NumberProvider.class);
                        unnamedSet.addBinding().toInstance(new CannedAnswerProvider(55));
                    }
                },
                new AbstractModule() {
                    @Named("firstSet")
                    @ProvidesIntoSet
                    NumberProvider firstSetAnswerProvider() {
                        return new TheAnswerProvider();
                    }

                    @Named("secondSet")
                    @ProvidesIntoSet
                    NumberProvider secondSetAnswerProvider() {
                        return new TheAnswerProvider();
                    }

                    @Named("secondSet")
                    @ProvidesIntoSet
                    @Singleton
                    NumberProvider anotherAnswerForNamedSecondSet() {
                        return new CannedAnswerProvider(66);
                    }

                    @ProvidesIntoSet
                    NumberProvider outOfNamedAnswerProvider() {
                        return new TheAnswerProvider();
                    }
                });
        System.out.println("First set:");
        final Set<NumberProvider> firstSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class, "firstSet"));
        firstSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(2, firstSetAnswers.size());
        System.out.println("Second set:");
        final Set<NumberProvider> secondSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class, "secondSet"));
        secondSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(3, secondSetAnswers.size());
        System.out.println("Unnamed set:");
        final Set<NumberProvider> unnamedSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class));
        unnamedSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(2, unnamedSetAnswers.size());

        System.out.println("Second set, second instantiation:");
        final Set<NumberProvider> secondSetAnswersSecondInstance = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberProvider.class, "secondSet"));
        secondSetAnswersSecondInstance.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));

        //First item is a singleton, so they are the same
        Assertions.assertEquals(secondSetAnswers.stream().findFirst().get(), secondSetAnswersSecondInstance.stream().findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does
        //Second item is a singleton, so they are the same
        Assertions.assertEquals(secondSetAnswers.stream().skip(1).findFirst().get(), secondSetAnswersSecondInstance.stream().skip(1).findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does
        //Third item is NOT a singleton, so they are the different instances
        Assertions.assertNotEquals(secondSetAnswers.stream().skip(2).findFirst().get(), secondSetAnswersSecondInstance.stream().skip(2).findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does

    }
}
