package net.rubii.securelib.client;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.item.ModItems;

@EventBusSubscriber(modid = SecureLib.MODID, value = Dist.CLIENT)
public class ModItemColorRenderer {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    if (tintIndex != 1) return 0xFFFFFFFF;
                    if (stack.get(DataComponents.DYED_COLOR) == null) return 0xFFFFFFFF;

                    DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                    return 0xFF000000 | color.rgb();
                },
                ModItems.KEYCARD.get()
        );
        event.register((stack, tintIndex) -> {
                    if (tintIndex != 1) return 0xFFFFFFFF;
                    if (stack.get(DataComponents.DYED_COLOR) == null) return 0xFFFFFFFF;

                    DyedItemColor color = stack.get(DataComponents.DYED_COLOR);
                    return 0xFF000000 | color.rgb();
                },
                ModItems.OPERATOR_KEYCARD.get()
        );
    }
}
