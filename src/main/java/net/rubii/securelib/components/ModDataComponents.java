package net.rubii.securelib.components;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.SecureLib;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SecureLib.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CLEARANCE =
            register("clearance", builder ->
                    builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FREQUENCY =
            register("frequency", builder ->
                    builder.persistent(ExtraCodecs.intRange(-2147483648, 2147483647)).networkSynchronized(ByteBufCodecs.VAR_INT)
            );

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>>
        register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return DATA_COMPONENTS_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS_TYPES.register(eventBus);
    }
}
