package com.eurotech.demos.guice.providing.stages;

import com.eurotech.demos.guice.providing.stages.classes.IntermediateClass;
import com.eurotech.demos.guice.providing.stages.classes.IntermediateClassImpl;
import com.eurotech.demos.guice.providing.stages.classes.IntermediateClassThrowingImpl;
import com.eurotech.demos.guice.providing.stages.classes.LeafClass;
import com.eurotech.demos.guice.providing.stages.classes.LeafClassImpl;
import com.eurotech.demos.guice.providing.stages.classes.TimeTrackingClass;
import com.eurotech.demos.guice.providing.stages.classes.TimeTrackingClassImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

class ProvidingModule extends AbstractModule {
    private final boolean intermediateFail;

    public ProvidingModule(boolean intermediateFail) {

        this.intermediateFail = intermediateFail;
    }

    @Override
    protected void configure() {
        bind(TimeTrackingClass.class).to(TimeTrackingClassImpl.class).in(Singleton.class);
        bind(String.class).annotatedWith(Names.named("someName")).toInstance("pluto");
        if (intermediateFail) {
            bind(IntermediateClass.class).to(IntermediateClassThrowingImpl.class).in(Singleton.class);
        } else {
            bind(IntermediateClass.class).to(IntermediateClassImpl.class).in(Singleton.class);
        }
        bind(LeafClass.class).to(LeafClassImpl.class).in(Singleton.class);
    }
}
