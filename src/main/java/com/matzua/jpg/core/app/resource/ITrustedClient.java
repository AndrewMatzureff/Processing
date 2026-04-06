package com.matzua.jpg.core.app.resource;

public interface ITrustedClient extends ITrustedParticipant{
    String FIX_CLIENT_MISSING_RECEIPT = "Ensure that your %s#auth(%s) implementation has properly set the "
        .formatted(ITrustedParticipant.class.getSimpleName(), ITrustedClient.class.getSimpleName()) +
        "receipt artifact in your trusted client.";
    void voidReceipt();
    boolean hasReceipt();
    void initTransaction();
    void completeTransaction();
    RuntimeException unauthorized(String message);
    @Override
    default void auth(ITrustedParticipant server) {
        server.auth(this);
        if (hasReceipt()) voidReceipt();
        else throw unauthorized(FIX_CLIENT_MISSING_RECEIPT);
    }
}
