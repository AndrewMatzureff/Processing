package com.matzua.jpg.core.app.resource;

import com.matzua.jpg.TestBase;
import com.matzua.jpg.core.app.IControlledAccessResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static com.matzua.jpg.core.app.resource.AbstractResourceStore.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractResourceStoreTest extends TestBase {
    // ↓ Test Class ↓ \................................................................................................:
    private interface TestResource extends IControlledAccessResource {}
    private static class TestAbstractResourceStore extends AbstractResourceStore<TestResource> {
        public TestAbstractResourceStore(String masterKey, Map<String, TestResource> resourcesById, Set<String> roots) {
            super(masterKey, resourcesById, roots);
        }
        public ResourceFactory<TestResource> getTrustedFactory(TestResourceRecipe recipe, TestResourceIngredients ingredients) {
            return getTrustedFactory(recipe, ingredients, TrustedFactory::new);
        }
        private interface TestResourceRecipe extends UnaryOperator<TestResource> {}
        private interface TestResourceIngredients extends Supplier<TestResource> {}
        private class TrustedFactory extends AbstractTrustedResourceFactory<TestResourceRecipe, TestResourceIngredients> {
            TrustedFactory(TestResourceRecipe recipe, TestResourceIngredients ingredients) {super(recipe, ingredients);}
            @Override
            public TestResource create() {
                completeTransaction();
                return recipe.apply(ingredients.get());
            }
        }
    }
    private TestAbstractResourceStore spyAbstractResourceStore;
    // ↓ Associations ↓ \..............................................................................................:
    private Map<String, TestResource> testResourcesById;
    private Set<String> testRoots;
    // ↓ Dependencies ↓ \..............................................................................................:
    private String testId;
    private TestResource mockResource;
    private ResourceFactory<TestResource> spyResourceFactory;
    // ↓ Misc. ↓ \.....................................................................................................:
    @BeforeEach
    public void setup() {
        super.setup();
        testResourcesById = new HashMap<>();
        testRoots = new HashSet<>();
        spyAbstractResourceStore = spy(new TestAbstractResourceStore("testMasterKey", testResourcesById, testRoots));
        testId = "testId";
        // TODO: https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
        mockResource = mock();
        spyResourceFactory = spy(
            spyAbstractResourceStore.getTrustedFactory(car -> car, () -> mockResource)
        );
    }
    private void restoreSpies() {
        final String newId = "%s::%s::%s".formatted(this, "restoreSpies", System.nanoTime());
        spyAbstractResourceStore.create(newId, spyResourceFactory);
        spyAbstractResourceStore.remove(newId);
        clearInvocations(spyAbstractResourceStore, spyResourceFactory, mockResource);
    }
    // ↓ create ↓ \....................................................................................................:
    @Test
    void when_create__given_valid_key_and_factory__then_new_resource_entry_created() {
        // given & when
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        // then
        assertEquals(mockResource, testResourcesById.get(testId));
        verify(spyResourceFactory).create();
        verify(spyResourceFactory).auth(spyAbstractResourceStore);
    }
    @Test
    void when_create__given_uninitiated_transaction__then_fail() {
        // given
        restoreSpies(); // Complete transaction initiated in setup(), remove created entry and clear spy invocations.
        // when
        final RuntimeException result
            = assertThrows(RuntimeException.class, () -> spyAbstractResourceStore.create(testId, spyResourceFactory));
        // then
        assertEquals(FORMAT_ERR_FIX
            .formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, FIX_TRUSTED_FACTORY), result.getMessage());
    }
    @Test
    void when_create__given_incomplete_transaction_from_untrusted_factory__then_fail() {
        // given
        restoreSpies(); // Complete transaction initiated in setup(), remove created entry and clear spy invocations.
        spyResourceFactory = spy(spyAbstractResourceStore.getTrustedFactory(mock(), mock())); // Initiate transaction.
        doNothing().when(spyAbstractResourceStore).auth(spyResourceFactory); // Bypass auth and go straight to create().
        doReturn(mockResource).when(spyResourceFactory).create(); // Simulate create() without completeTransaction().
        // when
        final RuntimeException result
            = assertThrows(RuntimeException.class, () -> spyAbstractResourceStore.create(testId, spyResourceFactory));
        // then
        assertEquals(FORMAT_ERR_FIX
            .formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, FIX_INCOMPLETE_TRANSACTION), result.getMessage());
    }
    @Test
    void when_create__given_missing_receipt_from_untrusted_factory__then_fail() {
        // given
        restoreSpies(); // Complete transaction initiated in setup(), remove created entry and clear spy invocations.
        spyResourceFactory = spy(spyAbstractResourceStore.getTrustedFactory(mock(), mock())); // Initiate transaction.
        doNothing().when(spyResourceFactory).auth(spyAbstractResourceStore); // Skip auth by factory, so no receipt set.
        // when
        final RuntimeException result
            = assertThrows(RuntimeException.class, () -> spyAbstractResourceStore.create(testId, spyResourceFactory));
        // then
        assertEquals(FORMAT_ERR_FIX
            .formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, FIX_CLIENT_MISSING_RECEIPT), result.getMessage());
    }
    // ↓ get ↓ \.......................................................................................................:
    @Test
    void when_get__given_valid_key_and_existing_entry__then_return_resource() {
        // given
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        // when
        final TestResource result = spyAbstractResourceStore.get(testId);
        // then
        assertEquals(mockResource, result);
    }
    @Test
    void when_get__given_existing_root_id__then_return_unopened_resource() {
        // given
        restoreSpies();
        spyAbstractResourceStore.root(testId, i -> i, mockResource);
        // when
        final TestResource result = spyAbstractResourceStore.get(testId);
        // then
        assertEquals(mockResource, result);
        verifyNoInteractions(mockResource);
    }
    // ↓ remove ↓ \....................................................................................................:
    @Test
    void when_remove__given_open_resource_failure__then_throw_RuntimeException() throws Exception {
        // given
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        doThrow(Exception.class).when(mockResource).close();
        // when & then
        assertThrows(RuntimeException.class, () -> spyAbstractResourceStore.remove(testId));
        verify(mockResource).open();
    }
    @Test
    void when_remove__given_valid_key_and_existing_entry__then_remove_resource() {
        // given
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        // when
        spyAbstractResourceStore.remove(testId);
        // then
        assertFalse(testResourcesById.containsKey(testId));
    }
    // ↓ getTrustedFactory ↓ \.........................................................................................:
    @Test
    void when_getTrustedFactory__given_transaction_is_in_progress__then_fail() {
        // given & when
        final RuntimeException result = assertThrows(RuntimeException.class,
            () -> spyAbstractResourceStore.getTrustedFactory(r -> r, () -> mockResource));
        // then
        assertEquals(FORMAT_ERR_FIX
            .formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, FIX_INIT_IN_PROGRESS_TRANSACTION), result.getMessage());
    }
    // ↓ completeTransaction ↓ \.......................................................................................:
    @Test
    void when_completeTransaction__given_uninitiated_transaction__then_fail() {
        // given
        restoreSpies(); // Complete transaction initiated in setup(), remove created entry and clear spy invocations.
        // when
        final RuntimeException result = assertThrows(RuntimeException.class, () -> spyResourceFactory.create());
        // then
        assertEquals(FORMAT_ERR_FIX
            .formatted(ERR_UNAUTHORIZED_RESOURCE_ACCESS, FIX_COMPLETE_UNINIT_TRANSACTION), result.getMessage());
        verify(spyAbstractResourceStore).completeTransaction();
    }
    // ↓ root ↓ \.....................................................................................................:
    @Test
    void when_root__given_existing_root_id__then_fail() {
        // given
        restoreSpies();
        spyAbstractResourceStore.root(testId, i -> i, mockResource);
        // when & then
        assertThrows(RuntimeException.class, () -> spyAbstractResourceStore.root(testId, i -> i, mockResource));
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    @Test
    void when_new__given_null_master_key__then_fail() {
        // given, when & then
        assertThrows(NullPointerException.class, () -> new TestAbstractResourceStore(null, testResourcesById, testRoots));
    }
}
