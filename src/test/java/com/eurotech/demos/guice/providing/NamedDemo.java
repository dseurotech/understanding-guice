package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.GuiceKeysUtils;
import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CannedAnswerFactory;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerFactory;
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

/**
 * when you have to provide multiple implementations of a common interface, and you need to choose which one to use
 *
 * @see <a href="https://github.com/google/guice/wiki/BindingAnnotations#named">The official documentation</a> for further details.
 */
public class NamedDemo {

    @Test
    void namedInstances() {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(NumberFactory.class).annotatedWith(Names.named("boring")).toInstance(new CannedAnswerFactory(3));
                    }
                },
                new AbstractModule() {
                    @Named("toEverything")
                    @Provides
                    NumberFactory theAnswerProvider() {
                        return new TheAnswerFactory();
                    }
                });

        System.out.println("Instances by name:");
        final NumberFactory boringAnswer = injector.getInstance(GuiceKeysUtils.named(NumberFactory.class, "boring"));
        System.out.println(String.format("boring %s: %d", boringAnswer, boringAnswer.giveMeTheNumber()));
        final NumberFactory theAnswer = injector.getInstance(GuiceKeysUtils.named(NumberFactory.class, "toEverything"));
        System.out.println(String.format("toEverything: %s: %d", theAnswer, theAnswer.giveMeTheNumber()));
        Assertions.assertNotEquals(boringAnswer, theAnswer);
        //They do not contribute to sets
        Assertions.assertThrows(ConfigurationException.class, () ->
                        injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class))
                , "com.google.inject.ConfigurationException: Guice configuration errors:\n" +
                        "\n" +
                        "1) [Guice/MissingImplementation]: No implementation for Set<NumberFactory> was bound.\n" +
                        "\n" +
                        "Did you mean?\n" +
                        "    NumberFactory annotated with @Named(\"boring\") bound at NamedDemo$1.configure(NamedDemo.java:24)\n" +
                        "\n" +
                        "    NumberFactory annotated with @Named(\"toEverything\") bound at NamedDemo$2.theAnswerSupplier(NamedDemo.java:31)\n" +
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
                        "NumberFactory: \"com.eurotech.demos.guice.NumberFactory\"\n" +
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
                        Multibinder<NumberFactory> setBinder = Multibinder.newSetBinder(binder(), NumberFactory.class, Names.named("firstSet"));
                        setBinder.addBinding().toInstance(new CannedAnswerFactory(33));

                        Multibinder<NumberFactory> setBinder2 = Multibinder.newSetBinder(binder(), NumberFactory.class, Names.named("secondSet"));
                        setBinder2.addBinding().toInstance(new CannedAnswerFactory(44));

                        Multibinder<NumberFactory> unnamedSet = Multibinder.newSetBinder(binder(), NumberFactory.class);
                        unnamedSet.addBinding().toInstance(new CannedAnswerFactory(55));
                    }
                },
                new AbstractModule() {
                    @Named("firstSet")
                    @ProvidesIntoSet
                    NumberFactory firstSetAnswerFactory() {
                        return new TheAnswerFactory();
                    }

                    @Named("secondSet")
                    @ProvidesIntoSet
                    NumberFactory secondSetAnswerFactory() {
                        return new TheAnswerFactory();
                    }

                    @Named("secondSet")
                    @ProvidesIntoSet
                    @Singleton
                    NumberFactory anotherAnswerForNamedSecondSet() {
                        return new CannedAnswerFactory(66);
                    }

                    @ProvidesIntoSet
                    NumberFactory outOfNamedAnswerFactory() {
                        return new TheAnswerFactory();
                    }
                });
        System.out.println("First set:");
        final Set<NumberFactory> firstSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class, "firstSet"));
        firstSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(2, firstSetAnswers.size());
        System.out.println("Second set:");
        final Set<NumberFactory> secondSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class, "secondSet"));
        secondSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(3, secondSetAnswers.size());
        System.out.println("Unnamed set:");
        final Set<NumberFactory> unnamedSetAnswers = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class));
        unnamedSetAnswers.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));
        Assertions.assertEquals(2, unnamedSetAnswers.size());

        System.out.println("Second set, second instantiation:");
        final Set<NumberFactory> secondSetAnswersSecondInstance = injector.getInstance(GuiceKeysUtils.keyForSetOf(NumberFactory.class, "secondSet"));
        secondSetAnswersSecondInstance.forEach(sa -> System.out.println(String.format("%s: %d", sa, sa.giveMeTheNumber())));

        //First item is a singleton, so they are the same
        Assertions.assertEquals(secondSetAnswers.stream().findFirst().get(), secondSetAnswersSecondInstance.stream().findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does
        //Second item is a singleton, so they are the same
        Assertions.assertEquals(secondSetAnswers.stream().skip(1).findFirst().get(), secondSetAnswersSecondInstance.stream().skip(1).findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does
        //Third item is NOT a singleton, so they are the different instances
        Assertions.assertNotEquals(secondSetAnswers.stream().skip(2).findFirst().get(), secondSetAnswersSecondInstance.stream().skip(2).findFirst().get());//Not the best test, as set does not guarantee order, however the specific implementation used here does

    }
}
