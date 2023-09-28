package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.providing.collaborators.CompositeCollaborator;
import com.eurotech.demos.guice.providing.collaborators.NumberProvider;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Provided objects are automatically passed to other providing methods (a.k.a.: Linked Binding)
 *
 * @see <a href="https://github.com/google/guice/wiki/LinkedBindings">The official documentation</a> for further details.
 */
public class ImplicitInjectionDemo {

    @Test
    public void linkedBindings() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new TheAnswerProvider();
            }

            @Provides
            CompositeCollaborator compositeCollaborator(NumberProvider numberProvider) {
                return new CompositeCollaborator(numberProvider);
            }
        });
        final CompositeCollaborator compositeCollaborator = injector.getInstance(CompositeCollaborator.class);
        System.out.println(compositeCollaborator);
        // a number provider has been provided
        Assertions.assertNotNull(compositeCollaborator.numberProvider);

        final NumberProvider numberProvider = injector.getInstance(NumberProvider.class);
        System.out.println(numberProvider);
        //Because numberProvider is not a Singleton
        Assertions.assertNotEquals(numberProvider, compositeCollaborator.numberProvider);
    }

    @Test
    public void worksAcrossModules() {
        //Same as before, but now the two collaborators are defined in two separate modules (possibly in different projects)
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberProvider numberProvider() {
                return new TheAnswerProvider();
            }
        }, new AbstractModule() {
            @Provides
            CompositeCollaborator compositeCollaborator(NumberProvider numberProvider) {
                return new CompositeCollaborator(numberProvider);
            }
        });
        final CompositeCollaborator compositeCollaborator = injector.getInstance(CompositeCollaborator.class);
        System.out.println(compositeCollaborator);
        // a number provider has been provided
        Assertions.assertNotNull(compositeCollaborator.numberProvider);

        final NumberProvider numberProvider = injector.getInstance(NumberProvider.class);
        System.out.println(numberProvider);
        //Because numberProvider is not a Singleton
        Assertions.assertNotEquals(numberProvider, compositeCollaborator.numberProvider);
    }

}
