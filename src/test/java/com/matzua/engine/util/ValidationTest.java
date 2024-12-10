package com.matzua.engine.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidationTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5})
    public void requireNonNull_withValidObjects_succeeds(int count) {
        Validation.requireNonNull(IntStream.range(0, count).boxed().toArray());
        Validation.requireNonNull("test", IntStream.range(0, count).boxed().toArray());
    }

    @Test
    public void requireNonNull_withNullObject_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Validation.requireNonNull(new Object(), null));
        assertThrows(NullPointerException.class, () -> Validation.requireNonNull("test", new Object(), null), "test");
    }
}
