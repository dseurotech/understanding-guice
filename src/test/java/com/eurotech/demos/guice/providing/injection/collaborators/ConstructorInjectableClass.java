package com.eurotech.demos.guice.providing.injection.collaborators;

import com.eurotech.demos.guice.NumberFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Demo class to showcase the use of @Inject annotations on constructors
 */
public class ConstructorInjectableClass {

    public final String name;
    public final NumberFactory numberFactory;

    public ConstructorInjectableClass() {
        this.name = "none";
        this.numberFactory = null;
    }

    @Inject
    public ConstructorInjectableClass(@Named("theName") String name, NumberFactory numberFactory) {
        this.name = name;
        this.numberFactory = numberFactory;
    }

    @Override
    public String toString() {
        return "ConstructorInjectableClass{" +
                "name='" + name + '\'' +
                ", numberFactory=" + numberFactory +
                '}';
    }
}
