package com.eurotech.demos.guice.providing;

import com.eurotech.demos.guice.NumberFactory;
import com.eurotech.demos.guice.providing.collaborators.CompositeCollaborator;
import com.eurotech.demos.guice.providing.collaborators.TheAnswerFactory;
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
            NumberFactory numberFactory() {
                return new TheAnswerFactory();
            }

            @Provides
            CompositeCollaborator compositeCollaborator(NumberFactory numberFactory) {
                return new CompositeCollaborator(numberFactory);
            }
        });
        final CompositeCollaborator compositeCollaborator = injector.getInstance(CompositeCollaborator.class);
        System.out.println(compositeCollaborator);
        // a number factory has been provided
        Assertions.assertNotNull(compositeCollaborator.numberFactory);

        final NumberFactory numberFactory = injector.getInstance(NumberFactory.class);
        System.out.println(numberFactory);
        //Because numberFactory is not a Singleton
        Assertions.assertNotEquals(numberFactory, compositeCollaborator.numberFactory);
    }

    @Test
    public void worksAcrossModules() {
        //Same as before, but now the two collaborators are defined in two separate modules (possibly in different projects)
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Provides
            NumberFactory numberFactory() {
                return new TheAnswerFactory();
            }
        }, new AbstractModule() {
            @Provides
            CompositeCollaborator compositeCollaborator(NumberFactory numberFactory) {
                return new CompositeCollaborator(numberFactory);
            }
        });
        final CompositeCollaborator compositeCollaborator = injector.getInstance(CompositeCollaborator.class);
        System.out.println(compositeCollaborator);
        // a number factory has been provided
        Assertions.assertNotNull(compositeCollaborator.numberFactory);

        final NumberFactory numberFactory = injector.getInstance(NumberFactory.class);
        System.out.println(numberFactory);
        //Because numberFactory is not a Singleton
        Assertions.assertNotEquals(numberFactory, compositeCollaborator.numberFactory);
    }

}
