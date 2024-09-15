package com.matzua.client.dagger.module;

import com.matzua.engine.behavior.Component;
import com.matzua.engine.behavior.InputController;
import com.matzua.engine.behavior.Position;
import com.matzua.engine.core.EntityManager;
import com.matzua.engine.graphics.Render;
import dagger.Module;
import dagger.Provides;
import processing.core.PGraphics;

import javax.inject.Singleton;
import java.util.stream.IntStream;

import static com.matzua.client.Constants.ENTITY_PLAYER;

@Module
public interface EntityModule {
    @Provides
    @Singleton
    static EntityManager provideEntityManager() {
        final EntityManager entityManager = new EntityManager();

        entityManager.create(ENTITY_PLAYER,
            new Position(ENTITY_PLAYER, 0,0,0),
            new InputController(ENTITY_PLAYER, entityManager));

        boxes(10, entityManager);

        return entityManager;
    }

    static void boxes(int n, EntityManager entityManager) {
        IntStream.range(0, n).forEach(i -> {
            final int id = i + ENTITY_PLAYER + 1;
            entityManager.create(id,
                new Position(id,
                    (float) Math.random() * 100 - 50,
                    (float) Math.random() * 100 - 50,
                    (float) Math.random() * 100 - 50),
                (Render) new Render() {
                    @Override
                    public void onRender(PGraphics g) {
                        super.onRender(g);
                        Position p = entityManager.get(Position.class).from(id).orElseThrow();

                        g.push();
                        g.stroke(0);
                        g.fill(0);
    //                    g.translate(p.getX(), p.getY(), p.getZ());
                        g.box(45);
                        g.pop();
                    }
                });
        });
    }
}
