package com.matzua.engine.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static com.matzua.engine.util.Types.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class SequenceMapTest {
    private SequenceMap.Impl<String, String> sequenceMap;
    private @Mock Map<String, List<String>> mockMap;
    private @Mock UnaryOperator<List<String>> mockCopy;
    private AutoCloseable mocks;

    @BeforeEach
    public void open() {
        mocks = openMocks(this);
        sequenceMap = new SequenceMap.Impl<>(mockMap, mockCopy);
    }

    @AfterEach
    public void close() throws Exception {
        mocks.close();
    }

    private void stage_put(final String k, final String v) {
        doReturn(List.of(v))
            .when(mockMap)
            .merge(
                eq(k),
                eq(List.of(v)),
                any(cast(BiFunction.class)));
        when(mockCopy.apply(List.of(v))).thenReturn(List.of(v));
    }

    private void verify_put(final String k, final String v) {
        verify(mockMap).merge(
            eq(k),
            eq(List.of(v)),
            any(cast(BiFunction.class)));
        verify(mockCopy)
            .apply(List.of(v));
    }

    @Test
    public void putAt_delegatesMergeCallToUnderlyingMap() {
        // given
        final String k = "expectedKey";
        final String v = "expectedValue";
        final int i = 0;
        stage_put(k, v);

        // when
        sequenceMap.putAt(k, v, i);

        // then
        verify_put(k, v);
        verifyNoMoreInteractions(mockMap, mockCopy);
    }

    @Test
    public void putFirst_delegatesMergeCallToUnderlyingMap() {
        // given
        final String k = "expectedKey";
        final String v = "expectedValue";
        stage_put(k, v);

        // when
        sequenceMap.putFirst(k, v);

        // then
        verify_put(k, v);
        verifyNoMoreInteractions(mockMap, mockCopy);
    }

    @Test
    public void putLast_delegatesMergeCallToUnderlyingMap() {
        // given
        final String k = "expectedKey";
        final String v = "expectedValue";
        stage_put(k, v);

        // when
        sequenceMap.putLast(k, v);

        // then
        verify_put(k, v);
        verify(mockMap).get(k);
        verifyNoMoreInteractions(mockMap, mockCopy);
    }

    @Test
    public void stream_delegatesGetOrDefaultCallToUnderlyingMap() {
        // given
        final String k = "expectedKey";
        when(mockMap.getOrDefault(k, List.of())).thenReturn(List.of());

        // when
        final Stream<String> result = sequenceMap.stream(k);

        // then
        assertEquals(0, result.count());
        verify(mockMap).getOrDefault(k, List.of());
        verifyNoMoreInteractions(mockMap, mockCopy);
    }
}
