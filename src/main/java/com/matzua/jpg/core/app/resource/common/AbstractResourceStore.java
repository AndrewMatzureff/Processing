package com.matzua.jpg.core.app.resource.common;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsCanvasStore;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsIngredients;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsRecipe;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.user.state.Type;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractResourceStore implements
    IAppStore<ICanvas>,
    ITrustedParticipant//<TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, PGraphicsRecipe, PGraphicsIngredients>>
{
    private final String masterKey;
    private final Map<String, ICanvas> resourcesById;
    private String receipt = null;
    // ↓ ITrustedParticipant ↓ \.......................................................................................:
    public void auth(ITrustedParticipant factory) {
        factory.auth(this);
        if (receipt.equals(masterKey)) receipt = null;
        else throw new RuntimeException(
            "Unauthorized resource access!"
                + " Are you sure you're using your resource store's trusted factory implementation (%s::getTrustedFactory)?"
                .formatted(getClass().getSimpleName())
        );
    }
    // ↓ IAppStore ↓ \.................................................................................................:
    @Override
    public <Recipe, Ingredients> void request(String id, TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, Recipe, Ingredients> source) {
        // TODO: null id, existing id and "root" cases
        auth(source);
        final ICanvas value = source.create();
        resourcesById.put(id, value);
    }

    @Override
    public void create(String id, ResourceFactory<ICanvas> source) {
//        // TODO: null id, existing id and "root" cases
//        final ICanvas value = source.create(this);
//        canvasesById.put(id, value);
    }

    @Override
    public ICanvas get(String id) {
        return resourcesById.get(id);
    }

    @Override
    public ICanvas remove(String id) {
        return resourcesById.remove(id);
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    public void root(String id, AbstractApp app) {
        final String internalRootId = app.toString();
        if (!resourcesById.containsKey(internalRootId)) {
            final PGraphicsRecipe rootAppGraphicsRecipe = () -> ((w, h) -> app.getGraphics());
            request(internalRootId, getCanvasFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            request(id, getCanvasFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            return;
        }
        throw new RuntimeException("TODO: [Update] Tried to create a root canvas when one already exists."
        + "\n%s".formatted(resourcesById));
    }
    public TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, PGraphicsRecipe, PGraphicsIngredients> getCanvasFactory(
        PGraphicsRecipe recipe, PGraphicsIngredients ingredients
    ) {
        return new TrustedPGraphicsCanvasFactory(recipe, ingredients);
    }
    // TODO: make these protected.
    private void setReceipt() {receipt = masterKey;}
    protected void voidReceipt() {receipt = null;}
    protected boolean hasReceipt() {return receipt.equals(masterKey);}
    // ↓ Inner Classes ↓ \.............................................................................................:
    private record PGraphicsCanvas(PGraphics pGraphics) implements ICanvas {}
    private class TrustedPGraphicsCanvasFactory implements TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, PGraphicsRecipe, PGraphicsIngredients> {
        final PGraphicsRecipe recipe;
        final PGraphicsIngredients ingredients;
        TrustedPGraphicsCanvasFactory(PGraphicsRecipe recipe, PGraphicsIngredients ingredients) {
            this.recipe = recipe;
            this.ingredients = ingredients;
        }
        @Override
        public void auth(ITrustedParticipant store) {
//            final Consumer<AbstractResourceStore> c = Ab
            Optional
//                .of(owner.getClass())
//                .filter(Type.extending(AbstractResourceStore.class)::matches)
//                .map($ -> owner)
//                .map(AbstractResourceStore.class::cast)
//                .ifPresent(AbstractResourceStore::setReceipt); // Untrusted factories cannot perform this step.
                .of(AbstractResourceStore.this)
                .filter(store::equals)
                .ifPresent(AbstractResourceStore::setReceipt);
        }
        @Override
        public ICanvas create() {
            final PGraphics pGraphics = recipe.get().apply(ingredients.width(), ingredients.height());
            return new PGraphicsCanvas(pGraphics);
        }
    }
}
