package com.matzua.client.dagger.component;

import com.matzua.engine.App;
import com.matzua.client.dagger.module.LayerModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {LayerModule.class})
public interface AppComponent {
    App getApp();
}
