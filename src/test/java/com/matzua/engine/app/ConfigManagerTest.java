package com.matzua.engine.app;

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
import org.mockito.Mock;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.matzua.engine.util.Fun.SerializableBiConsumer;
import static com.matzua.engine.util.Fun.SerializableFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ConfigManagerTest {
    @Data
    @AllArgsConstructor
    private static final class Cfg { private String option; }
    private AutoCloseable mocks;
    private ConfigManager<Cfg> configManager;

    @Mock
    private Cfg mockDefaultConfig;
    @Mock
    private Cfg mockCurrent;
    @Mock
    private Cfg mockWorking;
    @Mock
    private Map<String, Consumer<?>> mockSinkOptionSettersByConfigKey;
    @Mock
    private Map<String, SerializableFunction<Cfg, ?>> mockConfigOptionGettersByConfigKey;
    @Mock
    private Map<String, SerializableBiConsumer<Cfg, ?>> mockConfigOptionSettersByConfigKey;

    @BeforeEach
    public void open() {
        mocks = openMocks(this);
        configManager = ConfigManager.<Cfg>builder()
            .withConfigClass(Cfg.class)
            .withDefaultConfig(mockDefaultConfig)
            .withCurrent(mockCurrent)
            .withWorking(mockWorking)
            .withConfigOptionGettersByConfigKey(mockConfigOptionGettersByConfigKey)
            .withConfigOptionSettersByConfigKey(mockConfigOptionSettersByConfigKey)
            .withSinkOptionSettersByConfigKey(mockSinkOptionSettersByConfigKey)
            .build();
    }

    @AfterEach
    public void close() throws Exception {
        mocks.close();
    }

    // =============================================================================================== // register \\\\

    @Test
    public void register_resultsInGivenOptionAccessorsMappedToExpectedKeys() {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final Consumer<String> sinkOptionSetter = s -> {};

        // when
        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // then
        verifyRegistration(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter, "Cfg::getOption", "Cfg::setOption");
        verifyNoMoreInteractions(
            mockSinkOptionSettersByConfigKey,
            mockConfigOptionSettersByConfigKey,
            mockConfigOptionGettersByConfigKey,
            mockCurrent,
            mockDefaultConfig
        );
    }

    private void verifyRegistration(
        SerializableFunction<Cfg, String> cfgOptionGetter,
        SerializableBiConsumer<Cfg, String> cfgOptionSetter,
        Consumer<String> sinkOptionSetter,
        String getterKey,
        String setterKey
    ) {
        verify(mockSinkOptionSettersByConfigKey, times(1))
            .put(getterKey, sinkOptionSetter);
        verify(mockSinkOptionSettersByConfigKey, times(1))
            .put(setterKey, sinkOptionSetter);
        verify(mockConfigOptionSettersByConfigKey, times(1))
            .put(getterKey, cfgOptionSetter);
        verify(mockConfigOptionSettersByConfigKey, times(1))
            .put(setterKey, cfgOptionSetter);
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .put(getterKey, cfgOptionGetter);
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .put(setterKey, cfgOptionGetter);
    }

    // ============================================================================================= // getDefault \\\\

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "expectation")
    public void getDefault_withRegisteredOption_returnsExpectedDefaultValue(final String expectation) {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final Consumer<String> sinkOptionSetter = s -> {};

        when(mockDefaultConfig.getOption())
            .thenReturn(expectation);
        when(mockConfigOptionGettersByConfigKey.containsKey("Cfg::getOption"))
            .thenReturn(true);

        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // when
        final String result = configManager.getDefault(cfgOptionGetter);

        // then
        assertEquals(expectation, result);

        verifyRegistration(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter, "Cfg::getOption", "Cfg::setOption");
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .containsKey("Cfg::getOption");
        verify(mockDefaultConfig, times(1))
            .getOption();
        verifyNoMoreInteractions(mockConfigOptionGettersByConfigKey, mockCurrent, mockDefaultConfig);
    }

    @Test
    public void getDefault_withUnregisteredOption_throwsRuntimeException() {
        // given
        when(mockConfigOptionGettersByConfigKey.containsKey("Cfg::getOption"))
            .thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> configManager.getDefault(Cfg::getOption));

        verify(mockConfigOptionGettersByConfigKey, times(1))
            .containsKey("Cfg::getOption");
        verifyNoMoreInteractions(mockConfigOptionGettersByConfigKey, mockCurrent, mockDefaultConfig);
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
        final Consumer<String> sinkOptionSetter = s -> {};

        when(mockCurrent.getOption())
            .thenReturn(currentValue);
        when(mockDefaultConfig.getOption())
            .thenReturn(defaultValue);
        when(mockConfigOptionGettersByConfigKey.containsKey("Cfg::getOption"))
            .thenReturn(true);

        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // when
        final String result = configManager.get(cfgOptionGetter);

        // then
        assertEquals(expectation, result);

        verifyRegistration(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter, "Cfg::getOption", "Cfg::setOption");
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .containsKey("Cfg::getOption");
        verify(mockCurrent, times(expectedCurrentTimes))
            .getOption();
        verify(mockDefaultConfig, times(expectedDefaultTimes))
            .getOption();
        verifyNoMoreInteractions(mockConfigOptionGettersByConfigKey, mockCurrent, mockDefaultConfig);
    }

    @Test
    public void get_withUnregisteredOption_throwsRuntimeException() {
        // given
        when(mockConfigOptionGettersByConfigKey.containsKey("Cfg::getOption"))
            .thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> configManager.get(Cfg::getOption));

        verify(mockConfigOptionGettersByConfigKey, times(1))
            .containsKey("Cfg::getOption");
        verifyNoMoreInteractions(mockConfigOptionGettersByConfigKey, mockCurrent, mockDefaultConfig);
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
