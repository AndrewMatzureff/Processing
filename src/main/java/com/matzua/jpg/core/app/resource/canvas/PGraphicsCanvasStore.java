package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.AbstractResourceStore;
import com.matzua.jpg.core.app.resource.ResourceFactory;
import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class PGraphicsCanvasStore extends AbstractResourceStore<ICanvas> {
    public static final Function<AbstractApp, ICanvas> ROOT_CANVAS_RECIPE = app -> new RootCanvas(app.getGraphics());
    record RootCanvas(PGraphics pGraphics) implements ICanvas {
        @Override public void open() {}
        @Override public void close() {}
    }
    @Inject public PGraphicsCanvasStore(
        String masterKey, Map<String, ICanvas> canvasesById, Set<String> roots
    ) {
        super(masterKey, canvasesById, roots);
    }
    // ↓ IAppStore ↓ \.................................................................................................:
    // ↓ AbstractResourceStore ↓ \.....................................................................................:
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
