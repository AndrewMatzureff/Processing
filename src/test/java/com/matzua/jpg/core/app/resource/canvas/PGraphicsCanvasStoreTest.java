package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.TestBase;
import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.sys.AbstractApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import processing.core.PGraphics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PGraphicsCanvasStoreTest extends TestBase {
    // ↓ Test Class ↓ \................................................................................................:
    private PGraphicsCanvasStore testPGraphicsCanvasStore;
    // ↓ Associations ↓ \..............................................................................................:
    private Map<String, ICanvas> testCanvasesById;
    // ↓ Dependencies ↓ \..............................................................................................:
    private String testId;
    @Mock
    private AbstractApp mockApp;
    // ↓ Misc. ↓ \.....................................................................................................:
    @BeforeEach
    public void setup() {
        super.setup();
        testCanvasesById = new HashMap<>();
        testPGraphicsCanvasStore = new PGraphicsCanvasStore("testMasterKey", testCanvasesById);
        testId = "testId";
    }
    // ↓ create ↓ \....................................................................................................:
    @Test
    void when_create__given_valid_new_entry_with_real_inputs__then_create_new_resource_entry() {
        // given
        final PGraphics mockPGraphics = mock();
        final int testWidth = 320, testHeight = 200;
        final var resourceFactory = testPGraphicsCanvasStore
            .getTrustedFactory(PGraphicsRecipe.from(mockApp), PGraphicsIngredients.from(testWidth, testHeight));
        when(mockApp.createGraphics(testWidth, testHeight)).thenReturn(mockPGraphics);
        // when
        testPGraphicsCanvasStore.create(testId, resourceFactory);
        // then
        assertNotNull(testPGraphicsCanvasStore.get(testId));
        assertEquals(mockPGraphics, testPGraphicsCanvasStore.get(testId).pGraphics());
    }
    // ↓ getTrustedFactory ↓ \.........................................................................................:
    @Test
    void when_getTrustedFactory__given_valid_recipe_and_ingredients__then_return_new_operational_trusted_factory() {
        // given
        final PGraphicsRecipe mockRecipe = mock();
        final PGraphicsIngredients mockIngredients = mock();
        final PGraphics mockPGraphics = mock();
        final BiFunction<Integer, Integer, PGraphics> mockPGraphicsSource = mock();
        final int testWidth = 320, testHeight = 200;
        final var spyPGraphicsCanvasStore = spy(testPGraphicsCanvasStore);
        when(mockRecipe.get()).thenReturn(mockPGraphicsSource);
        when(mockIngredients.width()).thenReturn(testWidth);
        when(mockIngredients.height()).thenReturn(testHeight);
        when(mockPGraphicsSource.apply(testWidth, testHeight)).thenReturn(mockPGraphics);
        // when
        final var resourceFactory = spyPGraphicsCanvasStore.getTrustedFactory(mockRecipe, mockIngredients);
        // then
        assertNotNull(resourceFactory);
        verify(spyPGraphicsCanvasStore).getTrustedFactory(eq(mockRecipe), eq(mockIngredients), any());
        spyPGraphicsCanvasStore.create(testId, resourceFactory);
        assertNotNull(spyPGraphicsCanvasStore.get(testId));
        assertEquals(mockPGraphics, spyPGraphicsCanvasStore.get(testId).pGraphics());
    }
    // ↓ root ↓ \......................................................................................................:
    @Test
    void when_root__given_valid_new_entry__then_create_new_root_resource_entry() {
        // given
        final String testInternalRootId = mockApp.toString();
        final PGraphics mockPGraphics = mock();
        when(mockApp.getGraphics()).thenReturn(mockPGraphics);
        // when
        testPGraphicsCanvasStore.root(testId, mockApp);
        // then
        assertNotNull(testPGraphicsCanvasStore.get(testId));
        assertNotNull(testPGraphicsCanvasStore.get(testInternalRootId));
        assertEquals(mockPGraphics, testPGraphicsCanvasStore.get(testId).pGraphics());
        assertEquals(mockPGraphics, testPGraphicsCanvasStore.get(testInternalRootId).pGraphics());
    }
    // ↓ root ↓ \......................................................................................................:
    @Test
    void when_root__given_existing_entry__then_fail() {
        // given
        testPGraphicsCanvasStore.root(testId, mockApp);
        // when & then
        assertThrows(RuntimeException.class, () -> testPGraphicsCanvasStore.root("Different: " + testId, mockApp));
    }
    // ↓ Misc. ↓ \.....................................................................................................:
    @Test
    void when_new__given_null_master_key__then_fail() {
        // given, when & then
        assertThrows(NullPointerException.class, () -> new PGraphicsCanvasStore(null, testCanvasesById));
    }
}
