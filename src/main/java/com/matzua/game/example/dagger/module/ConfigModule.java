package com.matzua.game.example.dagger.module;

import com.matzua.engine.app.config.Config;
import dagger.Module;
import dagger.Provides;

@Module
public interface ConfigModule {
    @Provides
    static Config provideConfig() {
        return Config.builder().build();
    }
}
