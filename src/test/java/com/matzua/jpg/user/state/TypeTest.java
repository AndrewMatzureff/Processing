package com.matzua.jpg.user.state;

import com.matzua.jpg.TestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TypeTest extends TestBase {
    // ↓ Test Class ↓ \.................................................................................................
    // ↓ Associations ↓ \...............................................................................................
    // ↓ Dependencies ↓ \...............................................................................................
    // ↓ Misc. ↓ \......................................................................................................
    private static final Class<?> testLeafType = ArrayList.class;
    private static final Class<?> testSubType = List.class;
    private static final Class<?> testRootType = Collection.class;
    @BeforeEach
    public void setup() {
        super.setup();
    }
    // ↓ matches ↓ \....................................................................................................
    private static Stream<Arguments> expectedMatches() {
        return Stream.of(
            // ↓ testLeafType → IS ↓ \..................................................................................
            Arguments.of(testLeafType, Type.Relation.IS, testLeafType, true),
            Arguments.of(testLeafType, Type.Relation.IS, testSubType, false),
            Arguments.of(testLeafType, Type.Relation.IS, testRootType, false),
            // ↓ testLeafType → EXTENDS ↓ \.............................................................................
            Arguments.of(testLeafType, Type.Relation.EXTENDS, testLeafType, true),
            Arguments.of(testLeafType, Type.Relation.EXTENDS, testSubType, true),
            Arguments.of(testLeafType, Type.Relation.EXTENDS, testRootType, true),
            // ↓ testLeafType ENCLOSES ↓ \..............................................................................
            Arguments.of(testLeafType, Type.Relation.ENCLOSES, testLeafType, true),
            Arguments.of(testLeafType, Type.Relation.ENCLOSES, testSubType, false),
            Arguments.of(testLeafType, Type.Relation.ENCLOSES, testRootType, false),

            // ↓ testSubType → IS ↓ \...................................................................................
            Arguments.of(testSubType, Type.Relation.IS, testLeafType, false),
            Arguments.of(testSubType, Type.Relation.IS, testSubType, true),
            Arguments.of(testSubType, Type.Relation.IS, testRootType, false),
            // ↓ testSubType → EXTENDS ↓ \..............................................................................
            Arguments.of(testSubType, Type.Relation.EXTENDS, testLeafType, false),
            Arguments.of(testSubType, Type.Relation.EXTENDS, testSubType, true),
            Arguments.of(testSubType, Type.Relation.EXTENDS, testRootType, true),
            // ↓ testSubType ENCLOSES ↓ \...............................................................................
            Arguments.of(testSubType, Type.Relation.ENCLOSES, testLeafType, true),
            Arguments.of(testSubType, Type.Relation.ENCLOSES, testSubType, true),
            Arguments.of(testSubType, Type.Relation.ENCLOSES, testRootType, false),

            // ↓ testRootType → IS ↓ \..................................................................................
            Arguments.of(testRootType, Type.Relation.IS, testLeafType, false),
            Arguments.of(testRootType, Type.Relation.IS, testSubType, false),
            Arguments.of(testRootType, Type.Relation.IS, testRootType, true),
            // ↓ testRootType → EXTENDS ↓ \.............................................................................
            Arguments.of(testRootType, Type.Relation.EXTENDS, testLeafType, false),
            Arguments.of(testRootType, Type.Relation.EXTENDS, testSubType, false),
            Arguments.of(testRootType, Type.Relation.EXTENDS, testRootType, true),
            // ↓ testRootType ENCLOSES ↓ \..............................................................................
            Arguments.of(testRootType, Type.Relation.ENCLOSES, testLeafType, true),
            Arguments.of(testRootType, Type.Relation.ENCLOSES, testSubType, true),
            Arguments.of(testRootType, Type.Relation.ENCLOSES, testRootType, true)
        );
    }

    @ParameterizedTest
    @MethodSource ("expectedMatches")
    void when_matches__given_valid_args__then_return_expected_result(
        Class<?> test, Type.Relation relation, Class<?> type, boolean expected
    ) {
        assertEquals(expected, Type.initializerByRelation(relation).apply(type).matches(test));
    }
}
