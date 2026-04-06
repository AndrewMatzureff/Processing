package com.matzua.jpg.core.app;

import com.matzua.jpg.core.app.resource.ResourceFactory;

public interface IAppStore<Resource> {
    <Recipe, Ingredients> void create(String id, ResourceFactory<Resource> source);
    Resource get(String id);
    void remove(String id);
}
