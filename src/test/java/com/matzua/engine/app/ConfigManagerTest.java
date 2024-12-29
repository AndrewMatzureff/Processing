package com.matzua.engine.app;

import com.matzua.engine.util.Fun;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.matzua.engine.app.ConfigManager.accessors;
import static com.matzua.engine.util.Fun.*;
import static com.matzua.engine.util.Fun.SerializableLambda.MethodReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ConfigManagerTest {
    @Data
    @AllArgsConstructor
    private static final class Cfg { private String option; }
    @Data
    @AllArgsConstructor
    private static final class Tgt { private String option; }
    private AutoCloseable mocks;
    private ConfigManager<Cfg> configManager;

    @Mock
    private Cfg mockDefaultConfig;
    @Mock
    private Cfg mockCurrent;
    @Mock
    private Cfg mockWorking;
    @Mock
    private Map<MethodReference, SerializableFunction<Cfg, ?>[]> mockExecutableGettersByTarget;
    @Mock
    private Map<MethodReference, SerializableFunction<Cfg, ?>> mockExecutableGettersBySetter;
    @Mock
    private Set<MethodReference> mockConfigOptionGetters;
    @Mock
    private Set<MethodReference> mockConfigOptionSetters;

    @BeforeEach
    public void open() {
        mocks = openMocks(this);
        configManager = ConfigManager.builder(Cfg.class)
            .withDefaultConfig(mockDefaultConfig)
            .withCurrent(mockCurrent)
            .withWorking(mockWorking)
            .withExecutableGettersByTarget(mockExecutableGettersByTarget)
            .withExecutableGettersBySetter(mockExecutableGettersBySetter)
            .withConfigOptionGetters(mockConfigOptionGetters)
            .withConfigOptionSetters(mockConfigOptionSetters)
            .build();
    }

    @AfterEach
    public void close() throws Exception {
        verifyNoMoreInteractions(
            mockExecutableGettersByTarget,
            mockConfigOptionSetters,
            mockConfigOptionGetters,
            mockExecutableGettersBySetter,
            mockCurrent,
            mockWorking,
            mockDefaultConfig
        );
        mocks.close();
    }

    @SafeVarargs
    @SuppressWarnings(value = "unchecked")
    private void verifyRegistration(
        Fun.SerializableLambda targetOptionsSetter,
        ConfigManager.Accessors<Cfg>... configOptionAccessors
    ) {
        ArgumentCaptor<SerializableFunction<Cfg, String>[]> gettersCaptor
            = ArgumentCaptor.forClass(SerializableFunction[].class);

        final List<? extends SerializableFunction<Cfg, ?>> getters = Arrays.stream(configOptionAccessors)
            .map(accessors -> {
                final MethodReference getterConfigKey = accessors.getter().toMethodReference(Cfg.class);
                final MethodReference setterConfigKey = accessors.setter().toMethodReference(Cfg.class);
                verify(mockExecutableGettersBySetter).put(setterConfigKey, accessors.getter());
                verify(mockConfigOptionGetters).add(getterConfigKey);
                verify(mockConfigOptionSetters).add(setterConfigKey);
                return accessors.getter();
            })
            .toList();

        verify(mockExecutableGettersByTarget)
            .put(eq(targetOptionsSetter.toMethodReference(null)), gettersCaptor.capture());

        assertEquals(getters, Arrays.asList(gettersCaptor.getValue()));
    }

    // =============================================================================================== // register \\\\

    @Test
    public void register_resultsInGivenOptionAccessorsMappedToExpectedKeys() {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final SerializableBiConsumer<Tgt, String> sinkOptionSetter = (tgt, str) -> {};

        // when
        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // then
        verifyRegistration(
            sinkOptionSetter,
            accessors(cfgOptionGetter, cfgOptionSetter)
        );
    }

    // ============================================================================================= // getDefault \\\\

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "expectation")
    public void getDefault_withRegisteredOption_returnsExpectedDefaultValue(final String expectation) {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final SerializableBiConsumer<Tgt, String> sinkOptionSetter = (tgt, str) -> {};
        final MethodReference getterReference = cfgOptionGetter.toMethodReference(Cfg.class);

        when(mockDefaultConfig.getOption())
            .thenReturn(expectation);
        when(mockConfigOptionGetters.contains(getterReference))
            .thenReturn(true);

        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // when
        final String result = configManager.getDefault(cfgOptionGetter);

        // then
        assertEquals(expectation, result);

        verifyRegistration(sinkOptionSetter, accessors(cfgOptionGetter, cfgOptionSetter));
        verify(mockConfigOptionGetters)
            .contains(getterReference);
        verify(mockDefaultConfig)
            .getOption();
    }

    @Test
    public void getDefault_withUnregisteredOption_throwsRuntimeException() {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final MethodReference getterReference = cfgOptionGetter.toMethodReference(Cfg.class);

        when(mockConfigOptionGetters.contains(getterReference))
            .thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> configManager.getDefault(cfgOptionGetter));

        verify(mockConfigOptionGetters)
            .contains(getterReference);
    }

    // ==================================================================================================== // get \\\\

    @ParameterizedTest
    @MethodSource
    public void get_withRegisteredOption_returnsExpectedValue(
        final String currentValue,
        final String defaultValue,
        final String expectation,
        final int expectedCurrentTimes,
        final int expectedDefaultTimes
    ) {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final SerializableBiConsumer<Tgt, String> sinkOptionSetter = (tgt, str) -> {};
        final MethodReference getterReference = cfgOptionGetter.toMethodReference(Cfg.class);

        when(mockCurrent.getOption())
            .thenReturn(currentValue);
        when(mockDefaultConfig.getOption())
            .thenReturn(defaultValue);
        when(mockConfigOptionGetters.contains(getterReference))
            .thenReturn(true);

        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // when
        final String result = configManager.get(cfgOptionGetter);

        // then
        assertEquals(expectation, result);

        verifyRegistration(sinkOptionSetter, accessors(cfgOptionGetter, cfgOptionSetter));
        verify(mockConfigOptionGetters)
            .contains(getterReference);
        verify(mockCurrent, times(expectedCurrentTimes))
            .getOption();
        verify(mockDefaultConfig, times(expectedDefaultTimes))
            .getOption();
    }

    @Test
    public void get_withUnregisteredOption_throwsRuntimeException() {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final MethodReference getterReference = cfgOptionGetter.toMethodReference(Cfg.class);

        when(mockConfigOptionGetters.contains(getterReference))
            .thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> configManager.get(cfgOptionGetter));

        verify(mockConfigOptionGetters)
            .contains(getterReference);
    }

    //// argument providers \\ ===================================================================================== \\

    private static Stream<Arguments> get_withRegisteredOption_returnsExpectedValue() {
        return Stream.of(
            Arguments.of(null,              null,           null,           1, 1),
            Arguments.of(null,              "defaultValue", "defaultValue", 1, 1),
            Arguments.of("currentValue",    null,           "currentValue", 1, 0),
            Arguments.of("currentValue",    "defaultValue", "currentValue", 1, 0)
        );
    }

    // ==================================================================================================== // ... \\\\
}
