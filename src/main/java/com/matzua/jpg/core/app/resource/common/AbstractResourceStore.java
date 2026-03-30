package com.matzua.jpg.core.app.resource.common;

import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.sys.AbstractApp;
import lombok.RequiredArgsConstructor;
import processing.core.PGraphics;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public abstract class AbstractResourceStore<Resource> implements IAppStore<Resource>, ITrustedClient {
    private final String masterKey;
    protected final Map<String, Resource> resourcesById;
    private String receipt = null;
    private boolean transactionInProgress = false;
    // ↓ ITrustedParticipant ↓ \.......................................................................................:
    public RuntimeException unauthorized() {
        return new RuntimeException(
            ("Unauthorized resource access! " +
                "Are you sure you're using your resource store-provided trusted factory implementation " +
                "(%s::getTrustedFactory)?")
                .formatted(getClass().getSimpleName())
        );
    }
    public boolean hasTransactionInProgress() {return transactionInProgress;}
    public void initTransaction() {transactionInProgress = true;}
    public void completeTransaction() {transactionInProgress = false;}
    // ↓ IAppStore ↓ \.................................................................................................:
    @Override
    public <Recipe, Ingredients> void request(String id, TrustedResourceFactory<IAppStore<Resource>, Resource, Recipe, Ingredients> source) {
        // TODO: null id, existing id and "root" cases
        auth(source);
        final Resource resource = source.create();
        resourcesById.put(id, resource);
    }

    @Override
    public void create(String id, ResourceFactory<Resource> source) {
//        // TODO: null id, existing id and "root" cases
//        final ICanvas value = source.create(this);
//        canvasesById.put(id, value);
    }

    @Override
    public Resource get(String id) {
        return resourcesById.get(id);
    }

    @Override
    public Resource remove(String id) {
        return resourcesById.remove(id);
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    public abstract void root(String id, AbstractApp app);
    public <Recipe, Ingredients> TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, Recipe, Ingredients> getTrustedFactory(
        Recipe recipe, Ingredients ingredients, BiFunction<Recipe, Ingredients, TrustedResourceFactory<IAppStore<ICanvas>, ICanvas, Recipe, Ingredients>> factoryProvider
    ) {
        return factoryProvider.apply(recipe, ingredients);
    }
    // TODO: make these protected.
    private void setReceipt() {receipt = masterKey;}
    public void voidReceipt() {receipt = null;}
    public boolean hasReceipt() {return receipt.equals(masterKey);}
    // ↓ Inner Classes ↓ \.............................................................................................:
    private record PGraphicsCanvas(PGraphics pGraphics) implements ICanvas {}
    protected abstract class AbstractTrustedResourceFactory<Recipe, Ingredients> implements TrustedResourceFactory<IAppStore<Resource>, Resource, Recipe, Ingredients> {
        protected final Recipe recipe;
        protected final Ingredients ingredients;
        protected AbstractTrustedResourceFactory(Recipe recipe, Ingredients ingredients) {
            this.recipe = recipe;
            this.ingredients = ingredients;
        }
        @Override
        public boolean hasTransactionInProgress() {
            return AbstractResourceStore.this.hasTransactionInProgress();
        }
        @Override
        public void auth(ITrustedParticipant store) {
            TrustedResourceFactory.super.auth(store);
            Optional
                .of(AbstractResourceStore.this)
                .filter(store::equals)
                .ifPresent(AbstractResourceStore::setReceipt);
        }
        @Override
        public abstract Resource create();
    }
}
