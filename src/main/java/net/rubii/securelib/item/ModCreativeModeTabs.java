package net.rubii.securelib.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SecureLib.MODID);

    private static ItemStack icon(){
        ItemStack ICON = new ItemStack(ModItems.KEYCARD.get());
        ICON.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt("B7BCE8", 16), false));
        return ICON;
    }

    public static final Supplier<CreativeModeTab> SECURELIB_TAB = CREATIVE_MODE_TAB.register("securelib_tab",
            () -> CreativeModeTab.builder()
                    .icon(ModCreativeModeTabs::icon)
                    .title(Component.literal("SecureLib"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.KEYCARD);
                        output.accept(ModItems.OPERATOR_KEYCARD);
                        output.accept(ModItems.READER_EDITOR);

                        output.accept(ModItems.BLANK_CARD);
                        output.accept(ModItems.MAGNETIC_BAND);
                        output.accept(ModItems.DATA_CHIP);

                        output.accept(ModBlocks.CARD_PRINTER);
                        output.accept(ModBlocks.CARD_WRITER);
                        output.accept(ModBlocks.CARD_READER);
                    })
    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
