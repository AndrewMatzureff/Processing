package com.matzua.jpg.core.app.resource.common;

public interface ITrustedClient extends ITrustedParticipant{
    void voidReceipt();
    boolean hasReceipt();
    void initTransaction();
    void completeTransaction();
    boolean hasTransactionInProgress();
    RuntimeException unauthorized();
    default void auth(ITrustedParticipant server) {
        if (hasTransactionInProgress()) throw new RuntimeException(
            "Tried to execute %s::%s while a transaction is already in progress!"
                .formatted(ITrustedClient.class.getSimpleName(), "auth")
        );
        initTransaction();
        server.auth(this);
        if (hasReceipt()) voidReceipt();
        else throw unauthorized();
        completeTransaction();
    }
}
