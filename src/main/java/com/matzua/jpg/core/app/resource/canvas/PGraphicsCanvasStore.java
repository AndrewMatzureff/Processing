package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.IAppStore;
import com.matzua.jpg.core.app.resource.common.ResourceFactory;
import lombok.AllArgsConstructor;
import processing.core.PGraphics;

import javax.inject.Inject;
import java.util.Map;

@AllArgsConstructor(onConstructor = @__({@Inject}))
public class PGraphicsCanvasStore implements IAppStore<ICanvas> {
    private final String masterKey;
    private final Map<String, ICanvas> canvasesById;
    // ↓ IAppStore ↓ \_____________________________________________________________________
    @Override
    public void create(String key, ResourceFactory<ICanvas> source) {
        final ICanvas value = source.create(masterKey);
        canvasesById.put(key, value);
    }

    @Override
    public ICanvas get(String key) {
        return canvasesById.get(key);
    }

    @Override
    public ICanvas remove(String key) {
        return canvasesById.remove(key);
    }
    // ↓ Misc. ↓ \_________________________________________________________________________
    public ResourceFactory<ICanvas> getCanvasFactory(
        PGraphicsRecipe recipe, int width, int height
    ) {
        return new PGraphicsCanvasFactory(recipe, width, height, masterKey);
    }
    // ↓ Inner Classes ↓ \_________________________________________________________________
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
