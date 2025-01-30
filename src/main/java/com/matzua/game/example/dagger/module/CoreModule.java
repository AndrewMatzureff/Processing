package com.matzua.game.example.dagger.module;

import com.matzua.engine.app.ConfigManager;
import com.matzua.engine.app.config.Config;
import com.matzua.engine.component.renderer.geom.WirePath;
import com.matzua.engine.component.scene.Camera;
import com.matzua.engine.component.scene.Position;
import com.matzua.engine.core.EventManager;
import com.matzua.engine.entity.EntityManager;
import com.matzua.engine.component.input.InputController;
import com.matzua.engine.renderer.Renderer;
import com.matzua.engine.component.renderer.geom.Box;
import com.matzua.engine.util.Fun;
import com.matzua.engine.util.SequenceMap;
import dagger.Module;
import dagger.Provides;
import processing.core.PGraphics;

import javax.inject.Singleton;
import java.util.*;
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

        final int w = configManager.get(Config::getCanvasSizeWidth);
        final int h = configManager.get(Config::getCanvasSizeHeight);

        IntStream.range(0, 99)
            .forEach(i -> {
                final float x = (float) Math.random() * w - w / 2f;
                final float y = (float) Math.random() * h - h / 2f;
                final float z = (float) Math.random() * (w - h) - (w - h) / 2f;
                final float s = (int) (Math.random() * 25) + 1f;
                final UUID id = UUID.randomUUID();
                entityManager.attach(id, "main", new Box(eventManager, entityManager, s));
                entityManager.attach(id, "main", new Position(x, y, z));
            });

        final UUID player = UUID.randomUUID();
        final UUID wirePath  = UUID.randomUUID();
        entityManager
            .attach(player, "main", new Box(eventManager, entityManager, 50))
            .attach(player, "main", new Position(0, 0, 0))
            .attach(player, "main", new InputController(entityManager, eventManager))
            .attach(player, "main", new Camera(eventManager, entityManager, 160))
            .attach(wirePath, "main", new Position(0, 0, 0))
            .attach(wirePath, "main", WirePath.builder()
                .withEntityManager(entityManager)
                .withEventManager(eventManager)
                .withPoints(WirePath.sphere(100, 0, 0, 0, 1))
                .build());
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
