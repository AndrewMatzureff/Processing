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
    // ↓ Test Class ↓ \................................................................................................:
    // ↓ Associations ↓ \..............................................................................................:
    // ↓ Dependencies ↓ \..............................................................................................:
    // ↓ Misc. ↓ \.....................................................................................................:
    private static final Class<?> testRootClass = Collection.class;
    private static final Class<?> testSubClass = List.class;
    private static final Class<?> testLeafClass = ArrayList.class;
    private static final Collection<?> testRootInstance = new HashSet<>();
    private static final List<?> testSubInstance = new LinkedList<>();
    private static final ArrayList<?> testLeafInstance = new ArrayList<>();
    @BeforeEach
    public void setup() {
        super.setup();
    }
    // ↓ matches ↓ \...................................................................................................:
    private static Stream<Arguments> expectedClassMatches() {
        return Stream.of(
            // ↓ testLeafClass → IS ↓ \................................................................................:
            Arguments.of(testLeafClass, Type.Relation.IS, testLeafClass, true),
            Arguments.of(testLeafClass, Type.Relation.IS, testSubClass, false),
            Arguments.of(testLeafClass, Type.Relation.IS, testRootClass, false),
            // ↓ testLeafClass → EXTENDS ↓ \...........................................................................:
            Arguments.of(testLeafClass, Type.Relation.EXTENDS, testLeafClass, true),
            Arguments.of(testLeafClass, Type.Relation.EXTENDS, testSubClass, true),
            Arguments.of(testLeafClass, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testLeafClass ENCLOSES ↓ \............................................................................:
            Arguments.of(testLeafClass, Type.Relation.ENCLOSES, testLeafClass, true),
            Arguments.of(testLeafClass, Type.Relation.ENCLOSES, testSubClass, false),
            Arguments.of(testLeafClass, Type.Relation.ENCLOSES, testRootClass, false),

            // ↓ testSubClass → IS ↓ \.................................................................................:
            Arguments.of(testSubClass, Type.Relation.IS, testLeafClass, false),
            Arguments.of(testSubClass, Type.Relation.IS, testSubClass, true),
            Arguments.of(testSubClass, Type.Relation.IS, testRootClass, false),
            // ↓ testSubClass → EXTENDS ↓ \............................................................................:
            Arguments.of(testSubClass, Type.Relation.EXTENDS, testLeafClass, false),
            Arguments.of(testSubClass, Type.Relation.EXTENDS, testSubClass, true),
            Arguments.of(testSubClass, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testSubClass ENCLOSES ↓ \.............................................................................:
            Arguments.of(testSubClass, Type.Relation.ENCLOSES, testLeafClass, true),
            Arguments.of(testSubClass, Type.Relation.ENCLOSES, testSubClass, true),
            Arguments.of(testSubClass, Type.Relation.ENCLOSES, testRootClass, false),

            // ↓ testRootClass → IS ↓ \................................................................................:
            Arguments.of(testRootClass, Type.Relation.IS, testLeafClass, false),
            Arguments.of(testRootClass, Type.Relation.IS, testSubClass, false),
            Arguments.of(testRootClass, Type.Relation.IS, testRootClass, true),
            // ↓ testRootClass → EXTENDS ↓ \...........................................................................:
            Arguments.of(testRootClass, Type.Relation.EXTENDS, testLeafClass, false),
            Arguments.of(testRootClass, Type.Relation.EXTENDS, testSubClass, false),
            Arguments.of(testRootClass, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testRootClass ENCLOSES ↓ \............................................................................:
            Arguments.of(testRootClass, Type.Relation.ENCLOSES, testLeafClass, true),
            Arguments.of(testRootClass, Type.Relation.ENCLOSES, testSubClass, true),
            Arguments.of(testRootClass, Type.Relation.ENCLOSES, testRootClass, true)
        );
    }

    private static Stream<Arguments> expectedInstanceMatches() {
        return Stream.of(
            // ↓ testLeafInstance → IS ↓ \.............................................................................:
            Arguments.of(testLeafInstance, Type.Relation.IS, testLeafClass, true),
            Arguments.of(testLeafInstance, Type.Relation.IS, testSubClass, false),
            Arguments.of(testLeafInstance, Type.Relation.IS, testRootClass, false),
            // ↓ testLeafInstance → EXTENDS ↓ \........................................................................:
            Arguments.of(testLeafInstance, Type.Relation.EXTENDS, testLeafClass, true),
            Arguments.of(testLeafInstance, Type.Relation.EXTENDS, testSubClass, true),
            Arguments.of(testLeafInstance, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testLeafInstance ENCLOSES ↓ \.........................................................................:
            Arguments.of(testLeafInstance, Type.Relation.ENCLOSES, testLeafClass, true),
            Arguments.of(testLeafInstance, Type.Relation.ENCLOSES, testSubClass, false),
            Arguments.of(testLeafInstance, Type.Relation.ENCLOSES, testRootClass, false),

            // ↓ testSubInstance → IS ↓ \..............................................................................:
            Arguments.of(testSubInstance, Type.Relation.IS, testLeafClass, false),
            Arguments.of(testSubInstance, Type.Relation.IS, testSubClass, false), // LinkedList != List
            Arguments.of(testSubInstance, Type.Relation.IS, testRootClass, false),
            // ↓ testSubInstance → EXTENDS ↓ \.........................................................................:
            Arguments.of(testSubInstance, Type.Relation.EXTENDS, testLeafClass, false),
            Arguments.of(testSubInstance, Type.Relation.EXTENDS, testSubClass, true),
            Arguments.of(testSubInstance, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testSubInstance ENCLOSES ↓ \..........................................................................:
            Arguments.of(testSubInstance, Type.Relation.ENCLOSES, testLeafClass, false), // LL !<- ArrayList
            Arguments.of(testSubInstance, Type.Relation.ENCLOSES, testSubClass, false), // LL !<- List
            Arguments.of(testSubInstance, Type.Relation.ENCLOSES, testRootClass, false),

            // ↓ testRootInstance → IS ↓ \.............................................................................:
            Arguments.of(testRootInstance, Type.Relation.IS, testLeafClass, false),
            Arguments.of(testRootInstance, Type.Relation.IS, testSubClass, false),
            Arguments.of(testRootInstance, Type.Relation.IS, testRootClass, false), // HashMap != Collection
            // ↓ testRootInstance → EXTENDS ↓ \........................................................................:
            Arguments.of(testRootInstance, Type.Relation.EXTENDS, testLeafClass, false),
            Arguments.of(testRootInstance, Type.Relation.EXTENDS, testSubClass, false),
            Arguments.of(testRootInstance, Type.Relation.EXTENDS, testRootClass, true),
            // ↓ testRootInstance ENCLOSES ↓ \.........................................................................:
            Arguments.of(testRootInstance, Type.Relation.ENCLOSES, testLeafClass, false), // HM !<- ArrayList
            Arguments.of(testRootInstance, Type.Relation.ENCLOSES, testSubClass, false), // HashMap !<- List
            Arguments.of(testRootInstance, Type.Relation.ENCLOSES, testRootClass, false) // HM !<- Collection
        );
    }

    @ParameterizedTest
    @MethodSource ("expectedClassMatches")
    void when_matches__given_class__then_return_expected_result(
        Class<?> test, Type.Relation relation, Class<?> type, boolean expected
    ) {
        assertEquals(expected, Type.initializerByRelation(relation).apply(type).matches(test));
    }

    @ParameterizedTest
    @MethodSource ("expectedInstanceMatches")
    void when_matches__given_instance__then_return_expected_result(
        Collection<?> test, Type.Relation relation, Class<?> type, boolean expected
    ) {
        assertEquals(expected, Type.initializerByRelation(relation).apply(type).matches(test));
    }
}
