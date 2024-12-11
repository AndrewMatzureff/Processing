package com.matzua.engine.app;

import com.matzua.engine.util.Validation;
import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.matzua.engine.util.Fun.*;
import static com.matzua.engine.util.Fun.SerializableLambda.MethodReference;

@Builder(setterPrefix = "with")
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfigManager<Cfg> {
//    static final String msgDefaultValueUndefined = "Option values in the DEFAULT config cannot be left NULL: '%s'.";
//    static final Function<Object[], IllegalStateException> errDefaultValueUndefined =
//        args -> new IllegalStateException(msgDefaultValueUndefined.formatted(args));

    static final String msgInvalidImplClass = "TODO: update message!!! '%s'.";
    static final Function<Object[], IllegalArgumentException> errInvalidImplClass =
        args -> new IllegalArgumentException(msgInvalidImplClass.formatted(args));

    private final Class<Cfg> configClass;
    private final Cfg defaultConfig;
    private final Cfg current;
    private final Cfg working;
    private final Map<String, Consumer<?>> sinkOptionSettersByConfigKey;
    private final Map<String, SerializableFunction<Cfg, ?>> configOptionGettersByConfigKey;
    private final Map<String, SerializableBiConsumer<Cfg, ?>> configOptionSettersByConfigKey;
    private boolean changed;

    public <T> void register(
        SerializableFunction<Cfg, T> configOptionGetter,
        SerializableBiConsumer<Cfg, T> configOptionSetter,
        Consumer<T> sinkOptionSetter
    ) {
        final MethodReference serializedOptionGetter = configOptionGetter.toMethodReference();
        final MethodReference serializedOptionSetter = configOptionSetter.toMethodReference();
        final String getterConfigKey = toValidConfigKey(serializedOptionGetter);
        final String setterConfigKey = toValidConfigKey(serializedOptionSetter);

        sinkOptionSettersByConfigKey.put(getterConfigKey, sinkOptionSetter);
        sinkOptionSettersByConfigKey.put(setterConfigKey, sinkOptionSetter);
        configOptionGettersByConfigKey.put(getterConfigKey, configOptionGetter);
        configOptionGettersByConfigKey.put(setterConfigKey, configOptionGetter);
        configOptionSettersByConfigKey.put(getterConfigKey, configOptionSetter);
        configOptionSettersByConfigKey.put(setterConfigKey, configOptionSetter);
    }

    public <T> T get(SerializableFunction<Cfg, T> optionGetter) {
        return getFrom(current, false, optionGetter)
            .or(() -> getFrom(defaultConfig, true, optionGetter))
            .orElse(null);
    }

    public <T> T getDefault(SerializableFunction<Cfg, T> optionGetter) {
        return getFrom(defaultConfig, false, optionGetter)
            .orElse(null);
    }

    public <T> void set(@NonNull T optionValue, @NonNull SerializableBiConsumer<Cfg, T> optionSetter) {
        Validation.requireNonNull(optionValue, optionSetter);
        optionSetter.accept(working, optionValue);
        changed = true;
    }

    public void unset(SerializableBiConsumer<Cfg, Object> optionSetter) {
        optionSetter.accept(working,
            get(configOptionGettersByConfigKey.get(toValidConfigKey(optionSetter.toMethodReference()))));
        changed = true;
    }

    public void reset(SerializableBiConsumer<Cfg, Object> optionSetter) {
        optionSetter.accept(working,
            getDefault(configOptionGettersByConfigKey.get(toValidConfigKey(optionSetter.toMethodReference()))));
        changed = true;
    }

    public <T> void sync(SerializableFunction<Cfg, T> optionGetter, Consumer<T> optionSetter) {
        optionSetter.accept(get(optionGetter));
    }

    @SafeVarargs
    public final <T> void sync(
        T optionsSourceInstance,
        String optionsSourceInstanceMethodName,
//        List<Class<?>> optionTypes,
//        List<Function<Cfg, ?>> optionGetters
        SerializableFunction<Cfg, ?>... optionGetters
    ) {
        final Object[] options = Arrays.stream(optionGetters)
            .map(f -> f.apply(working))
            .toArray();

        final Method method;

        try {
            method = optionsSourceInstance.getClass()
                .getMethod(optionsSourceInstanceMethodName,
                    Arrays.stream(options)
                        .map(Object::getClass)
                        .toArray(Class[]::new));

            method.invoke(optionsSourceInstance, options);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("TODO: Update this Exception!!!");
        }
    }

    private <T> Optional<T> getFrom(Cfg config, boolean bypassGetterValidation, SerializableFunction<Cfg, T> optionGetter) {
        if (!bypassGetterValidation) {
            Optional.of(optionGetter)
                .map(SerializableLambda::toMethodReference)
                .map(this::toValidConfigKey)
                .filter(configOptionGettersByConfigKey::containsKey)
                .orElseThrow(() -> new RuntimeException("TODO: Update this Exception!!!"));
        }

        return Optional.of(config).map(optionGetter);
    }

    private String toValidConfigKey(final MethodReference methodReference) {
        return Optional.of(methodReference)
            .map(MethodReference::implClass)
            .filter(configClass.getName().replace('.', '/')::equals)
            .map(str -> methodReference)
            .map(MethodReference::toReferenceName)
            .orElseThrow(s(errInvalidImplClass, msgInvalidImplClass));
    }
}
