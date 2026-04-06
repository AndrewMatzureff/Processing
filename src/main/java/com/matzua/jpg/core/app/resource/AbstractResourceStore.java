package com.matzua.jpg.core.app.resource;

import com.matzua.jpg.core.app.IAppStore;
import com.matzua.jpg.core.app.IControlledAccessResource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class AbstractResourceStore<Resource extends IControlledAccessResource> implements IAppStore<Resource>, ITrustedClient {
    static final String FORMAT_ERR_FIX = "%s %s";
    static final String ERR_UNAUTHORIZED_RESOURCE_ACCESS = "Unauthorized resource access!";
    static final String FIX_TRUSTED_FACTORY = "Are you sure you're using your resource store-provided " +
        "trusted factory implementation (%s::getTrustedFactory)?"
            .formatted(AbstractResourceStore.class.getSimpleName());
    static final String FIX_INCOMPLETE_TRANSACTION = "%s did not complete the transaction!"
        .formatted(ResourceFactory.class.getSimpleName());
    static final String FIX_INIT_IN_PROGRESS_TRANSACTION
        = "Tried to initiate a transaction while one was already in progress!";
    static final String FIX_COMPLETE_UNINIT_TRANSACTION
        = "Tried to complete a transaction before even initiating one!";
    @NonNull private final String masterKey;
    protected final Map<String, Resource> resourcesById;
    private String receipt = null;
    private boolean transactionInProgress = false;
    private final Set<String> roots;

    public <Recipe extends Function<Ingredients, Resource>, Ingredients> void root(
        String id,
        Recipe recipe,
        Ingredients ingredients
    ) {
        if (roots.add(id)) {
            create(id, getTrustedFactory(
                recipe,
                ingredients,
                (r, i) -> new AbstractTrustedResourceFactory<Recipe, Ingredients>(r, i) {
                    @Override
                    public Resource create() {
                        completeTransaction();
                        return recipe.apply(ingredients);
                    }
                }
            ));
        } else throw new RuntimeException("Tried to reassign an existing root resource (\"%s\")!".formatted(id)
            + "\n%s".formatted(roots));
    }
    // ↓ ITrustedParticipant ↓ \.......................................................................................:
    // ↓ ITrustedClient ↓ \............................................................................................:
    @Override
    public RuntimeException unauthorized(String message) {
        return new RuntimeException(FORMAT_ERR_FIX.formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, message));
    }
    // TODO: add state validations to receipt accessors
    @Override public void voidReceipt() {receipt = null;}
    @Override public boolean hasReceipt() {return masterKey.equals(receipt);}
    @Override
    public void initTransaction() {
        if (transactionInProgress) throw unauthorized(FIX_INIT_IN_PROGRESS_TRANSACTION);
        else transactionInProgress = true;
    }
    @Override
    public void completeTransaction() {
        if (transactionInProgress) transactionInProgress = false;
        else throw unauthorized(FIX_COMPLETE_UNINIT_TRANSACTION);
    }
    private boolean hasTransactionInProgress() {return transactionInProgress;}
    // ↓ IAppStore ↓ \.................................................................................................:
    @Override
    public <Recipe, Ingredients> void create(String id, ResourceFactory<Resource> source) {
        // TODO: null id, existing id and "root" cases
        auth(source);
        final Resource resource = source.create();
        if (hasTransactionInProgress()) throw unauthorized(FIX_INCOMPLETE_TRANSACTION);
        resourcesById.put(id, resource);
    }
    @Override public Resource get(String id) {
        if (!roots.contains(id)) resourcesById.get(id).open();
        return resourcesById.get(id);
    }
    @Override public void remove(String id) {
        try (var resource = resourcesById.remove(id)) {resource.open();} catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    public <Recipe, Ingredients> ResourceFactory<Resource> getTrustedFactory(
        Recipe recipe,
        Ingredients ingredients,
        BiFunction<Recipe, Ingredients, ResourceFactory<Resource>> factoryProvider
    ) {
        initTransaction();
        return factoryProvider.apply(recipe, ingredients);
    }
    private void setReceipt() {receipt = masterKey;}
    // ↓ Inner Classes ↓ \.............................................................................................:
    protected abstract class AbstractTrustedResourceFactory<Recipe, Ingredients> implements ResourceFactory<Resource> {
        protected final Recipe recipe;
        protected final Ingredients ingredients;
        protected AbstractTrustedResourceFactory(Recipe recipe, Ingredients ingredients) {
            this.recipe = recipe;
            this.ingredients = ingredients;
        }
        @Override public abstract Resource create();
        @Override
        public void auth(ITrustedParticipant store) {
            Optional
                .of(AbstractResourceStore.this)
                .filter(store::equals)
                .filter(AbstractResourceStore::hasTransactionInProgress)
                .orElseThrow(this::unauthorized)
                .setReceipt();
        }
        private RuntimeException unauthorized() {return AbstractResourceStore.this.unauthorized(FIX_TRUSTED_FACTORY);}
    }
}
