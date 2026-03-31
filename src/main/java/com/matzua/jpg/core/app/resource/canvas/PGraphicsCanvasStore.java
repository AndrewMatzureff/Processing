package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.*;
import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;

public class PGraphicsCanvasStore extends AbstractResourceStore<ICanvas> {
    @Inject
    public PGraphicsCanvasStore(String masterKey, Map<String, ICanvas> canvasesById) {super(masterKey, canvasesById);}
    // ↓ IAppStore ↓ \.................................................................................................:
    // ↓ AbstractResourceStore ↓ \.....................................................................................:
    @Override
    public void root(String id, AbstractApp app) {
        final String internalRootId = app.toString();
        if (!resourcesById.containsKey(internalRootId)) {
            final PGraphicsRecipe rootAppGraphicsRecipe = () -> ((w, h) -> app.getGraphics());
            create(internalRootId, getTrustedFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            create(id, getTrustedFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            return;
        }
        throw new RuntimeException("Tried to create a root canvas when one already exists!"
            + "\n%s".formatted(resourcesById));
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    public ResourceFactory<ICanvas> getTrustedFactory(PGraphicsRecipe recipe, PGraphicsIngredients ingredients) {
        return getTrustedFactory(recipe, ingredients, TrustedFactory::new);
    }
    // ↓ Inner Classes ↓ \.............................................................................................:
    private class TrustedFactory extends AbstractTrustedResourceFactory<PGraphicsRecipe, PGraphicsIngredients> {
        TrustedFactory(PGraphicsRecipe recipe, PGraphicsIngredients ingredients) {super(recipe, ingredients);}
        @Override
        public ICanvas create() {
            completeTransaction();
            final PGraphics pGraphics = recipe.get().apply(ingredients.width(), ingredients.height());
            return () -> pGraphics;
        }
    }
}
