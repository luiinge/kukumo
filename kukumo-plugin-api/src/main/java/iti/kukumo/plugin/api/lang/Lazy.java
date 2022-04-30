package iti.kukumo.plugin.api.lang;

import java.util.function.Supplier;

public class Lazy<T> {

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    private final Supplier<T> supplier;
    private T instance;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (instance == null) {
            instance = supplier.get();
        }
        return instance;
    }

}
