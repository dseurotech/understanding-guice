package com.eurotech.demos.guice.providing;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Set;

import static com.google.inject.util.Types.setOf;

public class GuiceKeysUtils {
    public static <T> Key<Set<T>> keyForSetOf(Class<T> type) {
        return Key.get((TypeLiteral<Set<T>>) TypeLiteral.get(setOf(type)));
    }

    public static <T> Key<Set<T>> keyForSetOf(Class<T> type, String name) {
        return Key.get((TypeLiteral<Set<T>>) TypeLiteral.get(setOf(type)), Names.named(name));
    }

    public static <T> Key<T> named(Class<T> type, String name) {
        return Key.get(type, Names.named(name));
    }
}
