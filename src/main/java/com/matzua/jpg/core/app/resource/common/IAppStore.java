package com.matzua.jpg.core.app.resource.common;

public interface IAppStore<Resource> {
    <Recipe, Ingredients> void create(String id, ResourceFactory<Resource> source);
    Resource get(String id);
    Resource remove(String id);
}
