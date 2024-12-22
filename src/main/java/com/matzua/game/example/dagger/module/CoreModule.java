package com.matzua.game.example.dagger.module;

import com.matzua.engine.app.App;
import com.matzua.engine.app.ConfigManager;
import com.matzua.engine.app.config.Config;
import dagger.Module;
import dagger.Provides;

import java.util.HashMap;
import java.util.HashSet;

@Module
public interface CoreModule {
    @Provides
    static ConfigManager<Config, App> provideConfigManager() {
        return ConfigManager.builder(Config.class, App.class)
            .withCurrent(Config.builder().build())
            .withWorking(Config.builder().build())
            .withDefaultConfig(Config.builder().withWindowInfoTitle("App").build())
            .withExecutableGettersByTarget(new HashMap<>())
            .withExecutableGettersBySetter(new HashMap<>())
            .withConfigOptionSetters(new HashSet<>())
            .withConfigOptionGetters(new HashSet<>())
//            .withChanged(false)
                .build();
    }
}
