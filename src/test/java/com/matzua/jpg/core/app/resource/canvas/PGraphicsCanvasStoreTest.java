package com.matzua.jpg.core.app.resource.canvas;

import com.matzua.jpg.TestBase;
import com.matzua.jpg.core.app.ICanvas;
import com.matzua.jpg.core.app.resource.common.ResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PGraphicsCanvasStoreTest extends TestBase {
    // ↓ Test Class ↓ \____________________________________________________________________
    private PGraphicsCanvasStore testPGraphicsCanvasStore;
    // ↓ Associations ↓ \__________________________________________________________________
    private final String testMasterKey = "testMasterKey";
    private Map<String, ICanvas> testCanvasesById;
    // ↓ Dependencies ↓ \__________________________________________________________________
    @Mock
    private ResourceFactory<ICanvas> mockResourceFactory;
    @Mock
    private ICanvas mockCanvas;
    // ↓ Misc. ↓ \_________________________________________________________________________
    @BeforeEach
    public void setup() {
        super.setup();
        testCanvasesById = new HashMap<>();
        testPGraphicsCanvasStore
            = new PGraphicsCanvasStore(testMasterKey, testCanvasesById);
    }
    // ↓ create ↓ \________________________________________________________________________
    @Test
    void when_create__given_valid_key_and_factory__then_new_canvas_entry_created() {
        // given
        final String testKey = "testKey";
        when(mockResourceFactory.create(testMasterKey)).thenReturn(mockCanvas);
        // when
        testPGraphicsCanvasStore.create(testKey, mockResourceFactory);
        // then
        assertEquals(mockCanvas, testCanvasesById.get(testKey));
        verify(mockResourceFactory).create(testMasterKey);
    }
    // ↓ get ↓ \___________________________________________________________________________
    @Test
    void when_get__given_valid_key_and_existing_entry__then_return_canvas() {
        // given
        final String testKey = "testKey";
        when(mockResourceFactory.create(testMasterKey)).thenReturn(mockCanvas);
        testPGraphicsCanvasStore.create(testKey, mockResourceFactory);
        // when
        final ICanvas result = testPGraphicsCanvasStore.get(testKey);
        // then
        assertEquals(mockCanvas, testCanvasesById.get(testKey));
    }
    // ↓ remove ↓ \________________________________________________________________________
    @Test
    void when_remove__given_valid_key_and_existing_entry__then_remove_canvas() {
        // given
        final String testKey = "testKey";
        when(mockResourceFactory.create(testMasterKey)).thenReturn(mockCanvas);
        testPGraphicsCanvasStore.create(testKey, mockResourceFactory);
        // when
        final ICanvas result = testPGraphicsCanvasStore.remove(testKey);
        // then
        assertFalse(testCanvasesById.containsKey(testKey));
        assertEquals(mockCanvas, result);
    }
}
