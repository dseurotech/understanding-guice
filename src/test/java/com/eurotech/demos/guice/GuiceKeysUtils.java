package com.eurotech.demos.guice;

import com.eurotech.demos.guice.providing.collaborators.NumberProvider;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Map;
import java.util.Set;

import static com.google.inject.util.Types.mapOf;
import static com.google.inject.util.Types.setOf;

public class GuiceKeysUtils {
    public static <T> Key<Set<T>> keyForSetOf(Class<T> type) {
        return Key.get((TypeLiteral<Set<T>>) TypeLiteral.get(setOf(type)));
    }

    public static <T> Key<Map<String, T>> keyForMapOf(Class<String> keyType, Class<NumberProvider> valueType) {
        return Key.get((TypeLiteral<Map<String, T>>) TypeLiteral.get(mapOf(keyType, valueType)));
    }

    public static <T> Key<Set<T>> keyForSetOf(Class<T> type, String named) {
        return Key.get((TypeLiteral<Set<T>>) TypeLiteral.get(setOf(type)), Names.named(named));
    }

    public static <T> Key<T> named(Class<T> type, String named) {
        return Key.get(type, Names.named(named));
    }
}
