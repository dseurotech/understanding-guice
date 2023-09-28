package com.eurotech.demos.guice.providing.injection.collaborators;

import com.eurotech.demos.guice.providing.collaborators.NumberProvider;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Demo class to showcase the use of @Inject annotations on fields
 */
public class FieldInjectableClass {

    @Named("theName")
    @Inject
    public String name;
    @Inject
    public NumberProvider numberProvider;

    @Override
    public String toString() {
        return "FieldInjectableClass{" +
                "name='" + name + '\'' +
                ", numberProvider=" + numberProvider +
                '}';
    }
}
