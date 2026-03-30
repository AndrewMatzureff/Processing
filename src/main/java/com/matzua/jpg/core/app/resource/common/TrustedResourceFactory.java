package com.matzua.jpg.core.app.resource.common;

public interface TrustedResourceFactory<Owner, Resource, Recipe, Ingredients> extends ResourceFactory<Resource>, ITrustedParticipant {
    Resource create();
    // TODO: properly integrate TrustedResourceFactory into ResourceFactory hierarchy.
    default Resource create(String key) {throw new RuntimeException("Refactor this!!!");}
    boolean hasTransactionInProgress();
    default void auth(ITrustedParticipant other) {
        if (!hasTransactionInProgress()) throw new RuntimeException(
            ("Tried to initiate a transaction from the serving participant via %s::%s! By default, a transaction " +
                "must be initiated by a %s " +
                "(e.g.: AbstractResourceStore::auth must be invoked before TrustedResourceFactory::auth).")
                .formatted(TrustedResourceFactory.class.getSimpleName(), "auth", ITrustedClient.class.getSimpleName())
        );
    }
}
