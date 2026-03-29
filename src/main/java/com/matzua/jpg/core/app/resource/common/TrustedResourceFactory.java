package com.matzua.jpg.core.app.resource.common;

public interface TrustedResourceFactory<Owner, Resource, Recipe, Ingredients> extends ResourceFactory<Resource>, ITrustedParticipant {
    Resource create();
    // TODO: properly integrate TrustedResourceFactory into ResourceFactory hierarchy.
    default Resource create(String key) {throw new RuntimeException("Refactor this!!!");}
    void auth(ITrustedParticipant owner);
}
