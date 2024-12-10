package com.matzua.engine.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Map;
import java.util.function.Consumer;

import static com.matzua.engine.util.Fun.SerializableBiConsumer;
import static com.matzua.engine.util.Fun.SerializableFunction;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class ConfigManagerTest {
    @Data
    @AllArgsConstructor
    private static class Cfg { private String option; }
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

    @Test
    public void register_optionAccessorsAreMappedToExpectedKeys() {
        // given
        final SerializableFunction<Cfg, String> cfgOptionGetter = Cfg::getOption;
        final SerializableBiConsumer<Cfg, String> cfgOptionSetter = Cfg::setOption;
        final Consumer<String> sinkOptionSetter = s -> {};

        // when
        configManager.register(cfgOptionGetter, cfgOptionSetter, sinkOptionSetter);

        // then
        verify(mockSinkOptionSettersByConfigKey, times(1))
            .put("getOption", sinkOptionSetter);
        verify(mockSinkOptionSettersByConfigKey, times(1))
            .put("setOption", sinkOptionSetter);
        verify(mockConfigOptionSettersByConfigKey, times(1))
            .put("getOption", cfgOptionSetter);
        verify(mockConfigOptionSettersByConfigKey, times(1))
            .put("setOption", cfgOptionSetter);
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .put("getOption", cfgOptionGetter);
        verify(mockConfigOptionGettersByConfigKey, times(1))
            .put("setOption", cfgOptionGetter);
    }
}
