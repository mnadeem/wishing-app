package com.github.mnadeem.wishing.function;

import java.util.function.Consumer;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final Exception e) {
        	throw new RuntimeException(e);
        }
    }

    void acceptThrows(T elem) throws Exception;
}