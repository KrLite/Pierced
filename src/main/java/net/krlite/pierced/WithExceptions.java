package net.krlite.pierced;

import net.krlite.pierced.annotation.Silent;

import java.util.HashMap;

public abstract class WithExceptions {
    private @Silent final HashMap<Long, Exception> exceptions = new HashMap<>();

    protected HashMap<Long, Exception> exceptions() {
        return new HashMap<>(exceptions);
    }

    protected void addException(Exception e) {
        exceptions.put(System.currentTimeMillis(), e);
    }
}
