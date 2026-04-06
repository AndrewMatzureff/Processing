package com.matzua.jpg.core.app.resource;

public interface ResourceFactory<Resource> extends ITrustedParticipant {
    Resource create();
}
