package net.rubii.securelib.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.screen.custom.CardPrinterMenu;
import net.rubii.securelib.screen.custom.CardWriterMenu;
import net.rubii.securelib.screen.custom.KeypadMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, SecureLib.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<KeypadMenu>> KEYPAD_MENU =
            registerMenuType("keypad_menu", KeypadMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CardPrinterMenu>> CARD_PRINTER_MENU =
            registerMenuType("card_printer_menu", CardPrinterMenu::new);

    public static final DeferredHolder<MenuType<?>, MenuType<CardWriterMenu>> CARD_WRITER_MENU =
            registerMenuType("card_writer_menu", CardWriterMenu::new);

    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
