package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.resource.AbstractResourceStore;
import com.matzua.jpg.core.app.IAppStore;
import com.matzua.jpg.core.app.resource.ResourceFactory;
import com.matzua.jpg.user.Component;
import com.matzua.jpg.user.state.Entity;
import lombok.NonNull;

import javax.inject.Inject;
import java.util.*;
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
        // TODO: instead of ICanvas-based framesByChannel, make a readonly superinterface with no PGraphics write access
        final Map<String, ICanvas> framesByChannel = new HashMap<>();

        // Group all entities' components according to their "channel" string.
        final Map<String, List<Component>> componentsByChannel = resourcesById
            .values()
            .stream()
            .map(e -> e.components)
            .flatMap(List::stream)
            .collect(Collectors.groupingBy(Component::channel, Collectors.toList()));

        // Component::onRender
        componentsByChannel.forEach((channel, renderers) -> {
                try (var canvas = canvasStore.get(channel)) {
                    framesByChannel.put(channel, canvas);
                    renderers.forEach(renderer -> renderer.onRender(canvas));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

        // Component::postRender
        componentsByChannel.forEach((channel, renderers) -> {
                try (var canvas = canvasStore.get(channel)) {
                    renderers.forEach(renderer -> renderer.postRender(canvas, framesByChannel));
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
