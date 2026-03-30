package com.matzua.sample.jpg.simple.dagger.module;

//import com.matzua.engine.component.renderer.geom.WirePath;
//import com.matzua.engine.component.scene.Camera;
//import com.matzua.engine.component.scene.Position;
//import com.matzua.engine.core.EventManager;
//import com.matzua.engine.entity.EntityManager;
//import com.matzua.engine.component.input.InputController;
//import com.matzua.engine.renderer.Renderer;
//import com.matzua.engine.component.renderer.geom.Box;
//import com.matzua.engine.renderer.rays.RayCaster;
//import com.matzua.engine.renderer.rays.scene.CartesianGrid;
//import com.matzua.engine.util.Fun;
//import com.matzua.engine.util.SequenceMap;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.user.simple.SimpleEventManager;
import com.matzua.jpg.user.state.Type;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Module
public interface CoreModule {
    @Binds
    @Singleton
    IEventManager bindIEventManager(SimpleEventManager implementation);
    @Provides
    @Singleton
    static Map<Type<?>, List<Consumer<?>>>
    bindSubscribers() {return new HashMap<>();}
    @Provides
    @Singleton
    // TODO: make provider return interface IAppStore<ICanvas> and then inject ResourceFactory obtained from
    //  PGraphicsCanvasStore::getResourceFactory separately
    static AbstractResourceStore<ICanvas> canvasStore() {return new PGraphicsCanvasStore("masterKey", new HashMap<>());}
    @Provides
    @Singleton
    static IGameState gameState() {return () -> {};}
    @Provides
    @Singleton
    static IRenderer renderer() {return () -> {};}
//    @Provides
//    static AbstractApp
//    @Provides
//    static ConfigManager<Config> provideConfigManager() {
//        final ConfigManager<Config> configManager = ConfigManager.builder(Config.class)
//            .withCurrent(Config.builder().build())
//            .withWorking(Config.builder().build())
//            .withDefaultConfig(Config.builder().withWindowInfoTitle("App").build())
//            .withExecutableGettersByTarget(new HashMap<>())
//            .withExecutableGettersBySetter(new HashMap<>())
//            .withConfigOptionSetters(new HashSet<>())
//            .withConfigOptionGetters(new HashSet<>())
////            .withChanged(false)
//                .build();
//
//        configManager.register(Fun.SerializableTriConsumer.of(PGraphics::resize),
//            accessors(Config::getCanvasSizeWidth, Config::setCanvasSizeWidth),
//            accessors(Config::getCanvasSizeHeight, Config::setCanvasSizeHeight)
//        );
//
//        return configManager;
//    }
//
//    @Provides
//    @Singleton
//    static EventManager provideEventManager() {
//        return EventManager.builder()
//            .withAdapters(new HashMap<>())
//            .withSubscribers(new HashMap<>())
//            .build();
//    }
//
//    @Provides
//    @Singleton
//    @Named(value = "player")
//    static UUID providePlayer() {
//        return UUID.randomUUID();
//    }
//
//    @Provides
//    @Singleton
//    static EntityManager provideEntityManager(
//        EventManager eventManager,
//        ConfigManager<Config> configManager,
//        @Named(value = "player") UUID player
//    ) {
//        final EntityManager entityManager = EntityManager.builder()
//            .withComponents(new HashMap<>())
//            .withEventManager(eventManager)
//            .build();
//
//        final int w = configManager.get(Config::getCanvasSizeWidth);
//        final int h = configManager.get(Config::getCanvasSizeHeight);
//
////        IntStream.range(0, 99)
////            .forEach(i -> {
////                final float x = (float) Math.random() * w - w / 2f;
////                final float y = (float) Math.random() * h - h / 2f;
////                final float z = (float) Math.random() * (w - h) - (w - h) / 2f;
////                final float s = (int) (Math.random() * 25) + 1f;
////                final UUID id = UUID.randomUUID();
////                entityManager.attach(id, "main", new Box(eventManager, entityManager, s));
////                entityManager.attach(id, "main", new Position(x/2, y/2, z/2));
////            });
//
////        final UUID player = UUID.randomUUID();
//        final UUID wirePath  = UUID.randomUUID();
//        final UUID scene  = UUID.randomUUID();
//        final int gridWidth = 9;
//        final int gridHeight = 9;
//        final int[][] cells = Arrays.stream(new int[gridWidth][gridHeight])
//            .map(Arrays::stream)
//            .map(column -> column.map(cell -> cell + (int) (Math.random() * 16) * (int) (Math.random() + 0.5)))
//            .map(IntStream::toArray)
//            .toArray(int[][]::new);
//
//        entityManager
////            .attach(player, "main", new Box(eventManager, entityManager, 50))
//            .attach(player, "main", new Position(0, 0, 0))
//            .attach(player, "main", new InputController(entityManager, eventManager))
//            .attach(player, "main", new Camera(eventManager, entityManager, 58.72f))
//            .attach(scene, "main", new Position(0, 0, 0))
//            .attach(scene, "main", new CartesianGrid(
//                64,
//                gridWidth,
//                gridHeight,
//                cells,
//                eventManager
//            ));
////            .attach(wirePath, "main", WirePath.builder()
////                .withEntityManager(entityManager)
////                .withEventManager(eventManager)
////                .withPoints(WirePath.sphere(100, 0, 0, 0, 1))
////                .build());
//        return entityManager;
//    }
//
//    @Provides
//    @Singleton
//    static RayCaster provideRenderer(
//        EventManager eventManager,
//        EntityManager entityManager,
//        @Named(value = "player") UUID player
//    ) {
//        return RayCaster.builder()
//            .withPlayer(player)
//            .withEventManager(eventManager)
//            .withEntityManager(entityManager)
//            //.withOperations(new SequenceMap.Impl<>(HashMap::new, LinkedList::new))
//            .build();
//    }
}
