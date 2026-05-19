package net.rubii.securelib.client;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;

@EventBusSubscriber(modid = SecureLib.MODID, value = Dist.CLIENT)
public class ModItemsRendering {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, layerIndex) -> {
                    if (layerIndex != 1) return 0xFFFFFFFF;
                    if (stack.get(DataComponents.DYED_COLOR) == null) return 0xFFFFFFFF;

                    DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                    return 0xFF000000 | color.rgb();
                },
                ModItems.KEYCARD.get()
        );
        event.register((stack, layerIndex) -> {
                    if (layerIndex != 1) return 0xFFFFFFFF;
                    if (stack.get(DataComponents.DYED_COLOR) == null) return 0xFFFFFFFF;

                    DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                    return 0xFF000000 | color.rgb();
                },
                ModItems.OPERATOR_KEYCARD.get()
        );
        event.register((stack, layerIndex) -> {
                    if (layerIndex == 1){
                        if (stack.get(DataComponents.DYED_COLOR) == null) return 0xFFFFFFFF;

                        DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                        return 0xFF000000 | color.rgb();
                    } else if (layerIndex == 2){
                        if (stack.get(ModDataComponents.CLEARANCE_R) == null) return 0xFF8D8D8D;
                        int level = stack.get(ModDataComponents.CLEARANCE_R);

                        return getFromLevel(level);
                    } else if (layerIndex == 3){
                        if (stack.get(ModDataComponents.CLEARANCE_M) == null) return 0xFF8D8D8D;
                        int level = stack.get(ModDataComponents.CLEARANCE_M);

                        return getFromLevel(level);
                    } else if (layerIndex == 4){
                        if (stack.get(ModDataComponents.CLEARANCE_L) == null) return 0xFF8D8D8D;
                        int level = stack.get(ModDataComponents.CLEARANCE_L);

                        return getFromLevel(level);
                    } else return 0xFFFFFFFF;
                },
                ModItems.TRICARD.get()
        );
    }

    private static int getFromLevel(int level){
        return switch (level) {
            case 0 -> 0xFF8D8D8D;
            case 1 -> 0xFFFFEC00;
            case 2 -> 0xFFFF7C00;
            case 3 -> 0xFFFF0000;
            default -> 0xFFFF00EC;
        };
    }
}
