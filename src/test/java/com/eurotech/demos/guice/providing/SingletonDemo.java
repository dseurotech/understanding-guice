package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.NumberProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.util.function.Supplier;

/**
 * Different ways to declare a collaborator as singleton, or not
 *
 * @see <a href="https://github.com/google/guice/wiki/Scopes#applying-scopes">The official documentation</a> for further details.
 */
public class SingletonDemo {

    /*
     * Annotated Class examples: The class being instantiated is explicitly marked as singleton
     */

    @Singleton
    public static class SingletonAnnotatedClass implements NumberProvider {
        @Override
        public int giveMeTheNumber() {
            return 66;
        }
    }

    @Test
    @DisplayName("Even if not explicitly configured as singleton in the binding declaration, it still is a singleton")
    public void singletonAnnotatedClassIsSingletonEvenIfNotExplicitlyInScope() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(SingletonAnnotatedClass.class);
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Singleton-annotated class is still instantiated as a singleton, even if not explicitly bound")
    public void singletonAnnotatedClassIsSingletonEvenIfNotExplicitltBound() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(SingletonAnnotatedClass.class));
    }

    @Test
    @DisplayName("Annotated Class is still as singleton if created directly by Guice")
    public void singletonAnnotationOnClassIsAppliedToAutoBoundClasses() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            //No binding defined, requested classes need to be created on-the-fly
        });

        assert_Is_Singleton(() -> injector.getInstance(SingletonAnnotatedClass.class));
    }

    @Test
    @DisplayName("When Provided Manually, Singleton annotation is ignored")
    public void singletonAnnotatedClassIsNOTSingletonifProvidedManuallyWithoutAnnotation() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider annotatedClassButNotAnnotatedProvider() {
                return new SingletonAnnotatedClass();
            }
        });

        assert_Is_NOT_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Adding the singleton annotation to the manually Provisioning Method makes it a singleton again")
    public void singletonAnnotatedClassIsSingletonifProvidedManuallyWithAnnotation() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            NumberProvider annotated() {
                return new SingletonAnnotatedClass();
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    /*
     * External  Class examples: The class is not marked as a Singleton, possibly because it comes from an external library, or it should not ALWAYS be a singleton
     */
    public static class ExternalClassNotAnnotated implements NumberProvider {
        @Override
        public int giveMeTheNumber() {
            return 66;
        }
    }

    @Test
    @DisplayName("Not annotated class is not a singleton if normally bound")
    public void notAnnotatedClassIsNotSingletonByItself() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(ExternalClassNotAnnotated.class);
            }
        });

        assert_Is_NOT_Singleton(() -> injector.getInstance(NumberProvider.class));
    }


    @Test
    @DisplayName("Not annotated class is a singleton if declared as part of the Singleton Scope")
    public void notAnnotatedClassIsSingletonIfInScopeAnnotation() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(ExternalClassNotAnnotated.class).in(javax.inject.Singleton.class);
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Not annotated class is a singleton if declared as part of the Singleton Scope (alternative syntax)")
    public void notAnnotatedClassIsSingletonIfInSingletonScope() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(ExternalClassNotAnnotated.class).in(Scopes.SINGLETON);
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Not annotated class is a singleton if declared as eager singleton")
    public void notAnnotatedClassIsSingletonIfDeclaredAsEagerSingleton() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NumberProvider.class).to(ExternalClassNotAnnotated.class).asEagerSingleton();
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Not annotated class is not a singleton if manually provided")
    public void notAnnotatedClassIsNotSingletonIfProvidedAsIs() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider notAnnotated() {
                return new ExternalClassNotAnnotated();
            }
        });

        assert_Is_NOT_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    @Test
    @DisplayName("Not annotated class is a singleton if manually provided with the appropriate annotation")
    public void notAnnotatedClassIsSingletonIfProvidedWithSingletonAnnotation() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            @Singleton
            NumberProvider notAnnotated() {
                return new ExternalClassNotAnnotated();
            }
        });

        assert_Is_Singleton(() -> injector.getInstance(NumberProvider.class));
    }

    /**
     * Utility methods, used to de-clutter the tests and focus on the binding declaration
     */
    private static void assert_Is_Singleton(Supplier<NumberProvider> instanceProvider) {
        final NumberProvider firstInstance = instanceProvider.get();
        final NumberProvider secondInstance = instanceProvider.get();
        System.out.println(String.format("First  instance: %s", firstInstance));
        System.out.println(String.format("Second instance: %s", secondInstance));
        Assertions.assertNotNull(firstInstance);
        Assertions.assertNotNull(secondInstance);
        Assertions.assertEquals(firstInstance, secondInstance);
    }

    private static void assert_Is_NOT_Singleton(Supplier<NumberProvider> instanceProvider) {
        final NumberProvider firstInstance = instanceProvider.get();
        final NumberProvider secondInstance = instanceProvider.get();
        System.out.println(String.format("First  instance: %s", firstInstance));
        System.out.println(String.format("Second instance: %s", secondInstance));
        Assertions.assertNotNull(firstInstance);
        Assertions.assertNotNull(secondInstance);
        Assertions.assertNotEquals(firstInstance, secondInstance);
    }
}
