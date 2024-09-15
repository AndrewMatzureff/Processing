package com.matzua.util;

import org.junit.jupiter.api.Test;

import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InvertibleManifoldTest {
    private static final char A = 'a';
    private static final char D = 'd';

    private static final String P1_LEFT = "player.left";
    private static final String P1_RIGHT = "player.right";

    // InvertibleManifold::values

    @Test
    public void GIVEN__an_empty_instance__WHEN__values_is_invoked__THEN__it_will_return_the_empty_set() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(new HashMap<>(), new HashMap<>());
        Set<String> expectation = Set.of();

        // WHEN
        Set<String> result = instance.values(A);

        // THEN
        assertEquals(expectation, result);
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__values_is_invoked_with_a_nonexistent_key__THEN__it_will_return_the_empty_set() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(
            new HashMap<>(Map.of(A, Set.of(P1_LEFT))),
            new HashMap<>(Map.of(P1_LEFT, Set.of(A))));

        Set<String> expectation = Set.of();

        // WHEN
        Set<String> result = instance.values(D);

        // THEN
        assertEquals(expectation, result);
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__values_is_invoked_with_the_existing_key__THEN__it_will_return_the_existing_value() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(
            new HashMap<>(Map.of(A, Set.of(P1_LEFT))),
            new HashMap<>(Map.of(P1_LEFT, Set.of(A))));

        Set<String> expectation = Set.of(P1_LEFT);

        // WHEN
        Set<String> result = instance.values(A);

        // THEN
        assertEquals(expectation, result);
    }

    // InvertibleManifold::keys

    @Test
    public void GIVEN__an_empty_instance__WHEN__keys_is_invoked__THEN__it_will_return_the_empty_set() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(new HashMap<>(), new HashMap<>());
        Set<Constable> expectation = Set.of();

        // WHEN
        Set<Constable> result = instance.keys(P1_LEFT);

        // THEN
        assertEquals(expectation, result);
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__keys_is_invoked_with_a_nonexistent_value__THEN__it_will_return_the_empty_set() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(
            new HashMap<>(Map.of(A, Set.of(P1_LEFT))),
            new HashMap<>(Map.of(P1_LEFT, Set.of(A))));

        Set<Constable> expectation = Set.of();

        // WHEN
        Set<Constable> result = instance.keys(P1_RIGHT);

        // THEN
        assertEquals(expectation, result);
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__keys_is_invoked_with_the_existing_value__THEN__it_will_return_the_existing_key() {
        // GIVEN
        InvertibleManifold<Constable, String> instance = new InvertibleManifold<>(
            new HashMap<>(Map.of(A, Set.of(P1_LEFT))),
            new HashMap<>(Map.of(P1_LEFT, Set.of(A))));

        Set<Constable> expectation = Set.of(A);

        // WHEN
        Set<Constable> result = instance.keys(P1_LEFT);

        // THEN
        assertEquals(expectation, result);
    }

    // InvertibleManifold::map

    @Test
    public void GIVEN__an_empty_instance__WHEN__map_is_invoked__THEN__it_will_contain_a_single_mapping() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(new HashMap<>(), new HashMap<>());
        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A)));

        // WHEN
        result.map(A, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(1, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__map_is_invoked_with_a_nonexistent_mapping__THEN__it_will_contain_a_pair_of_mappings() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, Set.of(P1_LEFT))),
                new HashMap<>(Map.of(P1_LEFT, Set.of(A))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(
                        A, Set.of(P1_LEFT),
                        D, Set.of(P1_RIGHT)),
                Map.of(
                        P1_LEFT, Set.of(A),
                        P1_RIGHT, Set.of(D)));

        // WHEN
        result.map(D, P1_RIGHT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(2, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__map_is_invoked_with_the_existing_mapping__THEN__it_will_remain_unchanged() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A)));

        // WHEN
        result.map(A, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(1, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__map_is_invoked_with_a_mapping_that_shares_an_existing_key__THEN__it_will_contain_a_pair_of_mappings_which_share_the_overloaded_key() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT, P1_RIGHT)),
                Map.of(P1_LEFT, Set.of(A), P1_RIGHT, Set.of(A)));

        // WHEN
        result.map(A, P1_RIGHT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(2, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__map_is_invoked_with_a_mapping_that_shares_an_existing_value__THEN__it_will_contain_a_pair_of_mappings_which_share_the_overloaded_value() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT), D, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A, D)));

        // WHEN
        result.map(D, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(2, result.size());
    }

    // InvertibleManifold::unmap

    @Test
    public void GIVEN__an_empty_instance__WHEN__unmap_is_invoked__THEN__it_will_remain_unchanged() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(new HashMap<>(), new HashMap<>());
        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(Map.of(), Map.of());

        // WHEN
        result.unmap(A, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(0, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__unmap_is_invoked_with_a_nonexistent_mapping__THEN__it_will_remain_unchanged() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A)));

        // WHEN
        result.unmap(D, P1_RIGHT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(1, result.size());
    }

    @Test
    public void GIVEN__a_singleton_instance__WHEN__unmap_is_invoked_with_the_existing_mapping__THEN__it_will_become_empty() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(Map.of(), Map.of());

        // WHEN
        result.unmap(A, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(0, result.size());
    }

    @Test
    public void GIVEN__a_doubleton_instance__WHEN__unmap_is_invoked_with_a_mapping_that_shares_an_existing_key__THEN__it_will_contain_a_single_mapping() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT, P1_RIGHT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A)), P1_RIGHT, new HashSet<>(Set.of(A)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A)));

        // WHEN
        result.unmap(A, P1_RIGHT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(1, result.size());
    }

    @Test
    public void GIVEN__a_doubleton_instance__WHEN__unmap_is_invoked_with_a_mapping_that_shares_an_existing_value__THEN__it_will_contain_a_single_mapping() {
        // GIVEN
        InvertibleManifold<Constable, String> result = new InvertibleManifold<>(
                new HashMap<>(Map.of(A, new HashSet<>(Set.of(P1_LEFT)), D, new HashSet<>(Set.of(P1_LEFT)))),
                new HashMap<>(Map.of(P1_LEFT, new HashSet<>(Set.of(A, D)))));

        InvertibleManifold<Constable, String> expectation = new InvertibleManifold<>(
                Map.of(A, Set.of(P1_LEFT)),
                Map.of(P1_LEFT, Set.of(A)));

        // WHEN
        result.unmap(D, P1_LEFT);

        // THEN
        assertEquals(expectation, result);
        assertEquals(1, result.size());
    }
}
