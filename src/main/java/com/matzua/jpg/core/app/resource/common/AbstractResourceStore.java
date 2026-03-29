package com.matzua.jpg.core.app.resource.common;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsIngredients;
import com.matzua.jpg.core.app.resource.canvas.PGraphicsRecipe;
import com.matzua.jpg.core.sys.AbstractApp;
import com.matzua.jpg.user.state.Type;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor (onConstructor = @__({@Inject}))
public class AbstractResourceStore implements
    IAppStore<ICanvas>,
    ITrustedParticipant<TrustedResourceFactory<IAppStore<ICanvas>, ICanvas>>
{
    private final String masterKey;
    private final Map<String, ICanvas> resourcesById;
    private String receipt = null;
    // ↓ ITrustedParticipant ↓ \.......................................................................................:
    public void auth(TrustedResourceFactory<IAppStore<ICanvas>, ICanvas> source) {
        source.auth(this);
        if (receipt.equals(masterKey)) receipt = null;
        else throw new RuntimeException(
            "Unauthorized resource access!"
                + " Are you sure you're using your resource store's trusted factory implementation (%s::getTrustedFactory)?"
                .formatted(getClass().getSimpleName())
        );
    }
    // ↓ IAppStore ↓ \.................................................................................................:
    @Override
    public void create(String id, TrustedResourceFactory<IAppStore<ICanvas>, ICanvas> source) {
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
            create(internalRootId, getCanvasFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            create(id, getCanvasFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            return;
        }
        throw new RuntimeException("TODO: [Update] Tried to create a root canvas when one already exists."
        + "\n%s".formatted(resourcesById));
    }
    public TrustedResourceFactory<IAppStore<ICanvas>, ICanvas> getCanvasFactory(
        PGraphicsRecipe recipe, PGraphicsIngredients ingredients
    ) {
        return new TrustedPGraphicsCanvasFactory(recipe, ingredients);
    }
    // ↓ Inner Classes ↓ \.............................................................................................:
    private record PGraphicsCanvas(PGraphics pGraphics) implements ICanvas {}
    private record TrustedPGraphicsCanvasFactory(
        PGraphicsRecipe recipe, PGraphicsIngredients ingredients
    ) implements TrustedResourceFactory<IAppStore<ICanvas>, ICanvas> {
        @Override
        public void auth(IAppStore<ICanvas> owner) {
            Optional
                .of(owner.getClass())
                .filter(Type.extending(AbstractResourceStore.class)::matches)
                .map($ -> owner)
                .map(AbstractResourceStore.class::cast)
                .ifPresent(store -> store.receipt = store.masterKey); // Untrusted factories cannot perform this step.
        }
        @Override
        public ICanvas create() {
            final PGraphics pGraphics = recipe.get().apply(ingredients.width(), ingredients().height());
            return new PGraphicsCanvas(pGraphics);
        }
    }
}
