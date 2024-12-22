package com.matzua.engine.app;

import com.matzua.engine.util.Validation;
import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.matzua.engine.util.Fun.*;
import static com.matzua.engine.util.Fun.SerializableLambda.MethodReference;
import static com.matzua.engine.util.Validation.newPlaceholderError;

@Builder(setterPrefix = "with")
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfigManager<Cfg, Tgt> {
    public record Accessors<Cfg>(SerializableFunction<Cfg, ?> getter, SerializableBiConsumer<Cfg, ?> setter) {
        private static <Cfg> Accessors<Cfg> of(SerializableFunction<Cfg, ?> getter, SerializableBiConsumer<Cfg, ?> setter) {
            return new Accessors<>(getter, setter);
        }
    }
    public static <Cfg, T> Accessors<Cfg> accessors(SerializableFunction<Cfg, T> getter, SerializableBiConsumer<Cfg, T> setter) {
        return Accessors.of(getter, setter);
    }

    static final String msgInvalidImplClass = "TODO: update message!!! '%s'.";
    static final Function<Object[], IllegalArgumentException> errInvalidImplClass =
        args -> new IllegalArgumentException(msgInvalidImplClass.formatted(args));

    private final Class<Cfg> configClass;
    private final Cfg defaultConfig;
    private final Cfg current;
    private final Cfg working;
    private final Map<MethodReference, SerializableFunction<Cfg, ?>[]> executableGettersByTarget;
    private final Map<MethodReference, SerializableFunction<Cfg, ?>> executableGettersBySetter;
    private final Set<MethodReference> configOptionGetters;
    private final Set<MethodReference> configOptionSetters;
    private boolean changed;

    public <T> void register(
        SerializableFunction<Cfg, T> configOptionGetter,
        SerializableBiConsumer<Cfg, T> configOptionSetter,
        SerializableBiConsumer<Tgt, T> sinkOptionSetter
    ) {
        register(sinkOptionSetter, accessors(configOptionGetter, configOptionSetter));
    }

    @SafeVarargs
    public final void register(
        SerializableLambda sinkOptionsSetter,
        Accessors<Cfg>... configOptionAccessors
    ) {
        final ArrayList<SerializableFunction<Cfg, ?>> getters = new ArrayList<>(configOptionAccessors.length);
        Arrays.stream(configOptionAccessors)
            .sequential()
            .forEach((accessors) -> {
                final MethodReference getterConfigKey = accessors.getter.toMethodReference(configClass);
                final MethodReference setterConfigKey = accessors.setter.toMethodReference(configClass);
                getters.add(accessors.getter);
                executableGettersBySetter.put(setterConfigKey, accessors.getter);
                configOptionGetters.add(getterConfigKey);
                configOptionSetters.add(setterConfigKey);
            });
        executableGettersByTarget.put(
            sinkOptionsSetter.toMethodReference(null),
            getters.<SerializableFunction<Cfg, ?>>toArray(SerializableFunction[]::new)
        );
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

    private <T> Optional<T> getFrom(
        Cfg config, boolean bypassRegistrationCheck, SerializableFunction<Cfg, T> optionGetter
    ) {
        if (!bypassRegistrationCheck) {
            Optional.of(optionGetter)
                .map(getter -> getter.toMethodReference(configClass))
                .filter(configOptionGetters::contains)
                .orElseThrow(Validation::newPlaceholderError);
        }

        return Optional.of(config).map(optionGetter);
    }

    public <T> void set(@NonNull T optionValue, @NonNull SerializableBiConsumer<Cfg, T> optionSetter) {
        Validation.requireNonNull(optionValue, optionSetter);
        optionSetter.accept(working, optionValue);
        changed = true;
    }

    public void unset(SerializableBiConsumer<Cfg, Object> optionSetter) {
        optionSetter.accept(working,
            get(executableGettersBySetter.get(optionSetter.toMethodReference(configClass))));
        changed = true;
    }

    public void reset(SerializableBiConsumer<Cfg, Object> optionSetter) {
        optionSetter.accept(working,
            getDefault(executableGettersBySetter.get(optionSetter.toMethodReference(configClass))));
        changed = true;
    }

    public void sync(Tgt optionsSourceInstance) {
        executableGettersByTarget.keySet().forEach(setter -> sync(optionsSourceInstance, setter));
    }

    private void sync(
        Tgt optionsSourceInstance,
        MethodReference optionsSetter
    ) {
        final Object[] options = Optional.of(optionsSetter)
            .map(executableGettersByTarget::get)
            .map(Arrays::stream)
            .orElseThrow(Validation::newPlaceholderError)
            .map(f -> f.apply(working))
            .toArray();

        final Method method;

        try {
            method = optionsSourceInstance.getClass()
                .getMethod(optionsSetter.implMethodName(),
                    Arrays.stream(options)
                        .map(Object::getClass)
                        .toArray(Class[]::new));

            method.invoke(optionsSourceInstance, options);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw newPlaceholderError();
        }
    }

    public static <Cfg, Tgt> ConfigManagerBuilder<Cfg, Tgt> builder(Class<Cfg> configClass, Class<Tgt> targetClass) {
        System.out.printf("Building %s<%s, %s>...%n",
            ConfigManager.class.getSimpleName(), configClass.getSimpleName(), targetClass.getSimpleName());

        return ConfigManager.<Cfg, Tgt>builder().withConfigClass(configClass);
    }

    private static <Cfg, Tgt> ConfigManagerBuilder<Cfg, Tgt> builder() {
        return new ConfigManagerBuilder<Cfg, Tgt>();
    }

    public static class ConfigManagerBuilder<Cfg, Tgt> {
        private ConfigManagerBuilder<Cfg, Tgt> withConfigClass(Class<Cfg> configClass) {
            this.configClass = configClass;
            return this;
        }
    }
}
