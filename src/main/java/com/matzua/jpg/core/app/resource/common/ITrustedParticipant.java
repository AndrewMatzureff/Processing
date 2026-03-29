package com.matzua.jpg.core.app.resource.common;

public interface ITrustedParticipant<T> {
    void auth(T other);
}
