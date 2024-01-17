package com.eurotech.demos.guice.providing.multibinding;

import com.eurotech.demos.guice.NumberFactory;

import javax.inject.Inject;
import java.util.Map;

public class ClassInjectingMap {
    @Inject
    private Map<String, NumberFactory> providerMap;

    public Map<String, NumberFactory> getProviderMap() {
        return providerMap;
    }
}
