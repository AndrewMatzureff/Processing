package com.matzua.game.example.dagger.module;

import com.matzua.engine.app.ConfigManager;
import com.matzua.engine.app.config.Config;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.renderer.Renderer;
import com.matzua.engine.util.Fun;
import com.matzua.engine.util.SequenceMap;
import dagger.Module;
import dagger.Provides;
import processing.core.PGraphics;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.matzua.engine.app.ConfigManager.accessors;

@Module
public interface CoreModule {
    @Provides
    static ConfigManager<Config> provideConfigManager() {
        final ConfigManager<Config> configManager = ConfigManager.builder(Config.class)
            .withCurrent(Config.builder().build())
            .withWorking(Config.builder().build())
            .withDefaultConfig(Config.builder().withWindowInfoTitle("App").build())
            .withExecutableGettersByTarget(new HashMap<>())
            .withExecutableGettersBySetter(new HashMap<>())
            .withConfigOptionSetters(new HashSet<>())
            .withConfigOptionGetters(new HashSet<>())
//            .withChanged(false)
                .build();

        configManager.register(Fun.SerializableTriConsumer.of(PGraphics::resize),
            accessors(Config::getCanvasSizeWidth, Config::setCanvasSizeWidth),
            accessors(Config::getCanvasSizeHeight, Config::setCanvasSizeHeight)
        );

        return configManager;
    }

    @Provides
    @Singleton
    static EventManager provideEventManager() {
        return EventManager.builder()
            .withAdapters(new HashMap<>())
            .withSubscribers(new HashMap<>())
            .build();
    }

    @Provides
    @Singleton
    static EntityManager provideEntityManager(EventManager eventManager, ConfigManager<Config> configManager) {
        return EntityManager.builder()
            .withEntities(IntStream.range(0, 10)
                .mapToObj(i -> new float[] {
                    (float) Math.random() * configManager.get(Config::getCanvasSizeWidth),
                    (float) Math.random() * configManager.get(Config::getCanvasSizeHeight)
                })
                .map(xy -> new EntityManager.Entity(xy[0], xy[1], eventManager))
                .collect(Collectors.toList()))
            .withEventManager(eventManager)
            .build();
    }

    @Provides
    @Singleton
    static Renderer provideRenderer(EventManager eventManager) {
        return Renderer.builder()
            .withEventManager(eventManager)
            .withOperations(new SequenceMap.Impl<>(HashMap::new, LinkedList::new))
            .build();
    }
}
