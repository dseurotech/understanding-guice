package com.eurotech.demos.guice.providing.stages.classes;

import javax.inject.Inject;

public class LeafClassImpl implements LeafClass {

    private final IntermediateClass intermediateClass;

    @Inject
    public LeafClassImpl(IntermediateClass intermediateClass) {
        this.intermediateClass = intermediateClass;
    }

    @Override
    public IntermediateClass getIntermediateClass() {
        return intermediateClass;
    }
}
