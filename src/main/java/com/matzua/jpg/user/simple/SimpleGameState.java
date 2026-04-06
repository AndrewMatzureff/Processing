package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.IRenderer;
import com.matzua.jpg.core.app.resource.AbstractResourceStore;
import com.matzua.jpg.core.app.IAppStore;
import com.matzua.jpg.core.app.resource.ResourceFactory;
import com.matzua.jpg.user.state.Entity;
import com.matzua.jpg.user.state.Type;
import lombok.NonNull;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collectors;

public class SimpleGameState extends AbstractResourceStore<Entity> implements IGameState {
    private final IAppStore<ICanvas> canvasStore;
    @Inject public
    SimpleGameState(@NonNull String masterKey, Map<String, Entity> resourcesById, Set<String> roots, IAppStore<ICanvas> canvasStore) {
        super(masterKey, resourcesById, roots);
        this.canvasStore = canvasStore;
    }
    // ↓ IGameState ↓ \................................................................................................:
    @Override
    public void update() {
        resourcesById.values().forEach(Entity::update);
    }
    @Override
    public void render() {
        resourcesById
            .values()
            .stream()
            .map(e -> e.components)
            .flatMap(List::stream)
            .filter(Type.extending(IRenderer.class)::matches)
            .map(IRenderer.class::cast)
            .collect(Collectors.groupingBy(IRenderer::channel, Collectors.toList()))
            .forEach((channel, renderers) -> {
                try (var canvas = canvasStore.get(channel)) {
                    renderers.forEach(renderer -> renderer.onRender(canvas));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    // TODO: create actual entity recipe and ingredient classes
    public ResourceFactory<Entity> getTrustedFactory(UnaryOperator<Entity> recipe, Entity ingredients) {
        return getTrustedFactory(recipe, ingredients, TrustedFactory::new);
    }
    // ↓ Inner Classes ↓ \.............................................................................................:
    private class TrustedFactory extends AbstractTrustedResourceFactory<UnaryOperator<Entity>, Entity> {
        TrustedFactory(UnaryOperator<Entity> recipe, Entity ingredients) {
            super(recipe, ingredients);
        }
        @Override
        public Entity create() {
            completeTransaction();
            return recipe.apply(ingredients);
        }
    }
}
