package net.rubii.securelib.item;


import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.item.custom.EditorItem;
import net.rubii.securelib.item.custom.KeycardItem;
import net.rubii.securelib.item.custom.OperatorKeycardItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SecureLib.MODID);

    public static final DeferredItem<Item> OPERATOR_KEYCARD = ITEMS.register("operator_keycard", () -> new OperatorKeycardItem(new Item.Properties()
            .stacksTo(1).rarity(Rarity.EPIC)));

    public static final DeferredItem<Item> KEYCARD = ITEMS.register("keycard", () -> new KeycardItem(new Item.Properties()
            .stacksTo(1)));

    public static final DeferredItem<Item> BLANK_CARD = ITEMS.register("blank_card", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MAGNETIC_BAND = ITEMS.register("magnetic_band", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> READER_EDITOR = ITEMS.register("reader_editor", () -> new EditorItem(new Item.Properties()
            .stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
