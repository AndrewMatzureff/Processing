package com.matzua.client.dagger.module;

import com.matzua.engine.App;
import com.matzua.engine.behavior.Capture;
import com.matzua.client.WorldLayer;
import com.matzua.engine.core.EntityManager;
import com.matzua.engine.core.Layer;
import dagger.Module;
import dagger.Provides;
import processing.core.PGraphics;

import javax.inject.Singleton;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.matzua.client.Constants.ENTITY_PLAYER;

@Module(includes = {EntityModule.class})
public interface LayerModule {
    @Provides
    @Singleton
    static Deque<Layer> provideLayers(EntityManager entityManager) {
        return new LinkedList<>(List.of(
            new WorldLayer(entityManager),
            new Layer() {
                public void onAttach(App app) {
                    entityManager.create(ENTITY_PLAYER,
                        new Capture(ENTITY_PLAYER, entityManager, app.getOrCreateCaptureTarget(), this));
                }
                public void onRender(PGraphics g) {
                    Optional.of(Capture.class)
                        .map(entityManager::get)
                        .ifPresent(captures -> captures.forEach(capture -> capture.onRender(g), this));
//                    entityManager.get(Capture.class).from(ENTITY_PLAYER).r

                }
            }
        ));
    }
}
