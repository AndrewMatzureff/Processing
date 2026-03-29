package com.matzua.jpg.core.app.resource.common;

public interface TrustedResourceFactory<T, R> extends ResourceFactory<R>, ITrustedParticipant<T> {
    R create();
    // TODO: properly integrate TrustedResourceFactory into ResourceFactory hierarchy.
    default R create(String key) {throw new RuntimeException("Refactor this!!!");}
    void auth(T owner);
}
