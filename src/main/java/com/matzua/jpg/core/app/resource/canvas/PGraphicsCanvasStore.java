package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.core.app.resource.common.ResourceFactory;
import com.matzua.jpg.core.sys.AbstractApp;
import lombok.AllArgsConstructor;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class PGraphicsCanvasStore implements IAppStore<ICanvas> {
    private final String masterKey;
    private final Map<String, ICanvas> canvasesById;
    // ↓ IAppStore ↓ \.................................................................................................:
    @Override
    public void create(String id, ResourceFactory<ICanvas> source) {
        // TODO: null id, existing id and "root" cases
        final ICanvas value = source.create(masterKey);
        canvasesById.put(id, value);
    }

    @Override
    public ICanvas get(String id) {
        return canvasesById.get(id);
    }

    @Override
    public ICanvas remove(String id) {
        return canvasesById.remove(id);
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    public void root(String id, AbstractApp app) {
        final String internalRootId = app.toString();
        if (!canvasesById.containsKey(internalRootId)) {
            final PGraphicsRecipe rootAppGraphicsRecipe = () -> ((w, h) -> app.getGraphics());
            create(internalRootId, getCanvasFactory(rootAppGraphicsRecipe, 0, 0));
            create(id, getCanvasFactory(rootAppGraphicsRecipe, 0, 0));
            return;
        }
        throw new RuntimeException("TODO: [Update] Tried to create a root canvas when one already exists."
        + "\n%s".formatted(canvasesById));
    }
    public ResourceFactory<ICanvas> getCanvasFactory(PGraphicsRecipe recipe, int width, int height) {
        return new PGraphicsCanvasFactory(recipe, width, height, masterKey);
    }
    // ↓ Inner Classes ↓ \.............................................................................................:
    private record PGraphicsCanvas(PGraphics pGraphics) implements ICanvas {}
    private record PGraphicsCanvasFactory(
        PGraphicsRecipe recipe, int width, int height, String key
    ) implements ResourceFactory<ICanvas> {
        @Override
        public ICanvas create(String key) {
            if (key().equals(key)) {
                final PGraphics pGraphics = recipe.get().apply(width, height);
                return new PGraphicsCanvas(pGraphics);
            } throw new RuntimeException(
                "Invalid master store key provided: \"%s\"; ".formatted(key)
                + "are you sure the provided ResourceFactory was provided by your "
                + "CanvasStore?"
            );
        }
    }
}
