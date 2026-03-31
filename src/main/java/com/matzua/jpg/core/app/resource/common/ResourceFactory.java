package com.matzua.jpg.core.app.resource.common;

public interface ResourceFactory<Resource> extends ITrustedParticipant {
    Resource create();
}
