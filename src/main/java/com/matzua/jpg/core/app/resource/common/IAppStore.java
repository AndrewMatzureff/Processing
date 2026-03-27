package com.matzua.jpg.core.app.resource.common;

public interface IAppStore<Resource> {
//    String create(Value value);
    void create(String id, ResourceFactory<Resource> source);
    Resource get(String id);
    Resource remove(String id);
//    default Value update(String key, Value value) {
//        final Value old = remove(key);
//        create(key, value);
//        return old;
//    }

}
