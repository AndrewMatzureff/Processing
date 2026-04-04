package com.matzua.jpg.core.app.resource.common;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IAppStore<Resource> {
    <Recipe, Ingredients> void create(String id, ResourceFactory<Resource> source);
    Resource get(String id);
    Resource remove(String id);
//    Stream<Resource> stream();
    Resource checkout(String id);
    void checkin(String id);
}
