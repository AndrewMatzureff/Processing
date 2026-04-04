package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.*;
import com.matzua.jpg.core.sys.AbstractApp;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

public class PGraphicsCanvasStore extends AbstractResourceStore<ICanvas> {
    private final Set<String> checkout;
    private final Set<String> roots;
    @Inject public PGraphicsCanvasStore(
        String masterKey, Map<String, ICanvas> canvasesById, Set<String> checkout, Set<String> roots
    ) {
        super(masterKey, canvasesById);
        this.checkout = checkout;
        this.roots = roots;
    }
    public ICanvas checkout(String id) {
        if (!roots.contains(id) && checkout.add(id)) {
            resourcesById.get(id).pGraphics().beginDraw();
        }
        return resourcesById.get(id);
    }
    public void checkin(String id) {
        if (!roots.contains(id) && checkout.remove(id)) {
            resourcesById.get(id).pGraphics().endDraw();
        }
    }
    // ↓ IAppStore ↓ \.................................................................................................:
//    @Override
//    public ICanvas get(String id) {
//        final ICanvas canvas = super.get(id);
////        if (!roots.contains(id) && checkout.add(id)) {
////            canvas.pGraphics().beginDraw();
////        }
//        return canvas;
//    }
    // ↓ AbstractResourceStore ↓ \.....................................................................................:
    @Override
    public void root(String id, AbstractApp app) {
        final String internalRootId = app.toString();
        roots.add(id);
        roots.add(internalRootId);
        checkout.addAll(roots);
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
