package com.eurotech.demos.guice.providing.injection.collaborators;

import com.eurotech.demos.guice.NumberProvider;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Demo class to showcase the use of @Inject annotations on constructors
 */
public class ConstructorInjectableClass {

    public final String name;
    public final NumberProvider numberProvider;

    public ConstructorInjectableClass() {
        this.name = "none";
        this.numberProvider = null;
    }

    @Inject
    public ConstructorInjectableClass(@Named("theName") String name, NumberProvider numberProvider) {
        this.name = name;
        this.numberProvider = numberProvider;
    }

    @Override
    public String toString() {
        return "ConstructorInjectableClass{" +
                "name='" + name + '\'' +
                ", numberProvider=" + numberProvider +
                '}';
    }
}
