package com.matzua.jpg.core.app.resource.common;

import com.matzua.jpg.TestBase;
import com.matzua.jpg.core.sys.AbstractApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.matzua.jpg.core.app.resource.common.AbstractResourceStore.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AbstractResourceStoreTest extends TestBase {
    // ↓ Test Class ↓ \................................................................................................:
    private static class TestAbstractResourceStore extends AbstractResourceStore<String> {
        public TestAbstractResourceStore(String masterKey, Map<String, String> resourcesById) {
            super(masterKey, resourcesById);
        }
        @Override
        public void root(String id, AbstractApp app) {}
        public ResourceFactory<String> getTrustedFactory(StringRecipe recipe, StringIngredients ingredients) {
            return getTrustedFactory(recipe, ingredients, TrustedFactory::new);
        }
        private interface StringRecipe extends Supplier<Function<char[], String>> {}
        private interface StringIngredients extends Supplier<char[]> {}
        private class TrustedFactory extends AbstractTrustedResourceFactory<StringRecipe, StringIngredients> {
            TrustedFactory(StringRecipe recipe, StringIngredients ingredients) {super(recipe, ingredients);}
            @Override
            public String create() {
                completeTransaction();
                return recipe.get().apply(ingredients.get());
            }
        }
    }
    private TestAbstractResourceStore spyAbstractResourceStore;
    // ↓ Associations ↓ \..............................................................................................:
    private Map<String, String> testStringsById;
    // ↓ Dependencies ↓ \..............................................................................................:
    private String testId;
    private String testResource;
    private ResourceFactory<String> spyResourceFactory;
    // ↓ Misc. ↓ \.....................................................................................................:
    @BeforeEach
    public void setup() {
        super.setup();
        testStringsById = new HashMap<>();
        spyAbstractResourceStore = spy(new TestAbstractResourceStore("testMasterKey", testStringsById));
        testId = "testId";
        testResource = "testResource";
        spyResourceFactory = spy(
            spyAbstractResourceStore.getTrustedFactory(() -> (String::new), testResource::toCharArray)
        );
    }
    private void restoreSpies() {
        final String newId = "%s::%s::%s".formatted(this, "restoreSpies", System.nanoTime());
        spyAbstractResourceStore.create(newId, spyResourceFactory);
        spyAbstractResourceStore.remove(newId);
        clearInvocations(spyAbstractResourceStore, spyResourceFactory);
    }
    // ↓ create ↓ \....................................................................................................:
    @Test
    void when_create__given_valid_key_and_factory__then_new_resource_entry_created() {
        // given & when
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        // then
        assertEquals(testResource, testStringsById.get(testId));
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
        doReturn(testResource).when(spyResourceFactory).create(); // Simulate create() without completeTransaction().
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
        final String result = spyAbstractResourceStore.get(testId);
        // then
        assertEquals(testResource, result);
    }
    // ↓ remove ↓ \....................................................................................................:
    @Test
    void when_remove__given_valid_key_and_existing_entry__then_remove_resource() {
        // given
        spyAbstractResourceStore.create(testId, spyResourceFactory);
        // when
        final String result = spyAbstractResourceStore.remove(testId);
        // then
        assertFalse(testStringsById.containsKey(testId));
        assertEquals(testResource, result);
    }
    // ↓ getTrustedFactory ↓ \.........................................................................................:
    @Test
    void when_getTrustedFactory__given_transaction_is_in_progress__then_fail() {
        // given & when
        final RuntimeException result = assertThrows(RuntimeException.class,
            () -> spyAbstractResourceStore.getTrustedFactory(() -> (String::new), testResource::toCharArray));
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
    // ↓ Misc. ↓ \.....................................................................................................:
    @Test
    void when_new__given_null_master_key__then_fail() {
        // given, when & then
        assertThrows(NullPointerException.class, () -> new TestAbstractResourceStore(null, testStringsById));
    }
}
