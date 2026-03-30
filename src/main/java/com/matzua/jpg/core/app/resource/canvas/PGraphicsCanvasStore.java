package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.*;
import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;

//@RequiredArgsConstructor (onConstructor = @__({@Inject}))
public class PGraphicsCanvasStore extends AbstractResourceStore<ICanvas> {
    @Inject
    public PGraphicsCanvasStore(String masterKey, Map<String, ICanvas> canvasesById) {super(masterKey, canvasesById);}
    // ↓ IAppStore ↓ \.................................................................................................:
    // ↓ Misc. ↓ \.....................................................................................................:
    @Override
    public void root(String id, AbstractApp app) {
        final String internalRootId = app.toString();
        if (!resourcesById.containsKey(internalRootId)) {
            final PGraphicsRecipe rootAppGraphicsRecipe = () -> ((w, h) -> app.getGraphics());
            request(internalRootId, getTrustedFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            request(id, getTrustedFactory(rootAppGraphicsRecipe, PGraphicsIngredients.from(0)));
            return;
        }
        throw new RuntimeException("TODO: [Update] Tried to create a root canvas when one already exists."
            + "\n%s".formatted(resourcesById));
    }
    public TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, PGraphicsRecipe, PGraphicsIngredients> getTrustedFactory(
        PGraphicsRecipe recipe, PGraphicsIngredients ingredients
    ) {
        return getTrustedFactory(recipe, ingredients, TrustedFactory::new);//new TrustedResourceFactoryImpls(recipe, ingredients);
    }
    // ↓ Inner Classes ↓ \.............................................................................................:
    private record PGraphicsCanvas(PGraphics pGraphics) implements ICanvas {}
    private class TrustedFactory extends AbstractTrustedResourceFactory<PGraphicsRecipe, PGraphicsIngredients> {
//        final PGraphicsRecipe recipe;
//        final PGraphicsIngredients ingredients;
        TrustedFactory(PGraphicsRecipe recipe, PGraphicsIngredients ingredients) {
            super(recipe, ingredients);
//            this.recipe = recipe;
//            this.ingredients = ingredients;
        }
        @Override
        public ICanvas create() {
            final PGraphics pGraphics = recipe.get().apply(ingredients.width(), ingredients.height());
            return new PGraphicsCanvas(pGraphics);
        }
    }
}
