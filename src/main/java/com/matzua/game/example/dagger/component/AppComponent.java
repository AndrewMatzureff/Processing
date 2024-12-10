package com.matzua.game.example.dagger.component;

import com.matzua.engine.app.App;
import com.matzua.game.example.dagger.module.ConfigModule;
import com.matzua.game.example.dagger.module.CoreModule;
import dagger.Component;

@Component(modules = {CoreModule.class, ConfigModule.class})
public interface AppComponent {
    App getApp();
}
