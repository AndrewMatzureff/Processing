package com.matzua.jpg.user.simple;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.IEventManager;
import com.matzua.jpg.core.app.IGameState;
import com.matzua.jpg.core.app.draw.IRenderer;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.common.AbstractResourceStore;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.core.app.resource.common.ITrustedParticipant;
import com.matzua.jpg.core.app.resource.common.ResourceFactory;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.user.Component;
import com.matzua.jpg.user.state.Entity;
import com.matzua.jpg.user.state.Type;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class SimpleGameState extends AbstractResourceStore<Entity> implements IGameState {
    private final IAppStore<ICanvas> canvasStore;
    @Inject public
    SimpleGameState(@NonNull String masterKey, Map<String, Entity> resourcesById, IAppStore<ICanvas> canvasStore) {
        super(masterKey, resourcesById);
        this.canvasStore = canvasStore;
    }

    @Override
    public Entity checkout(String id) {
        return null;
    }

    @Override
    public void checkin(String id) {

    }

    // ↓ AbstractResourceStore ↓ \.....................................................................................:
    @Override
    public void root(String id, AbstractApp app) {
        throw new UnsupportedOperationException("TODO: implement");
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
//                canvasStore.get(channel).pGraphics().beginDraw();
                final var canvas = canvasStore.checkout(channel);
                renderers.forEach(renderer -> renderer.onRender(canvas));
                canvasStore.checkin(channel);
//                canvasStore.get(channel).pGraphics().endDraw();
            });
//
//        forEach(entity -> {
//            entity.components
//                .stream()
//                .filter(Type.extending(IRenderer.class)::matches)
//                .map(IRenderer.class::cast)
//                .collect(Collectors.groupingBy(IRenderer::channel, Collectors.toList()))
//                .forEach((channel, renderers) -> {
//                    canvasStore.get(channel).pGraphics().beginDraw();
////                    canvasStore.checkout(channel);
//                    renderers.forEach(Component::onRender);
////                    canvasStore.checkin(channel);
//                    canvasStore.get(channel).pGraphics().endDraw();
//                });
//        });

//        canvasStore.forEach(iCanvas -> iCanvas.pGraphics().beginDraw());
//        forEach(Entity::render);
//        canvasStore.forEach(iCanvas -> iCanvas.pGraphics().endDraw());
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
