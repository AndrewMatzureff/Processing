package com.matzua.game.example.dagger.module;

import com.matzua.engine.app.ConfigManager;
import com.matzua.engine.app.config.Config;
import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.component.input.InputController;
import com.matzua.engine.renderer.Renderer;
import com.matzua.engine.component.renderer.geom.Box;
import com.matzua.engine.util.Fun;
import com.matzua.engine.util.SequenceMap;
import com.matzua.engine.util.Validation;
import dagger.Module;
import dagger.Provides;
import processing.core.PGraphics;

import javax.inject.Singleton;
import java.util.*;
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
        final EntityManager entityManager = EntityManager.builder()
            .withComponents(new HashMap<>())
            .withEventManager(eventManager)
            .build();

        IntStream.range(0, 10)
            .forEach(i -> {
                final float x = (float) Math.random() * configManager.get(Config::getCanvasSizeWidth);
                final float y = (float) Math.random() * configManager.get(Config::getCanvasSizeHeight);
                final float s = (int) (Math.random() * 25) + 1f;
                final UUID id = UUID.randomUUID();
                entityManager.attach(id, "main", new Box(eventManager, entityManager));
                entityManager.attach(id, "main", new Position(x, y, s));
            });

        entityManager.attach(
            entityManager.getComponents()
                .keySet()
                .stream()
                .findAny()
                .orElseThrow(Validation::newPlaceholderError)
                .entity(),
            "main",
            new InputController(entityManager, eventManager));
        return entityManager;
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
