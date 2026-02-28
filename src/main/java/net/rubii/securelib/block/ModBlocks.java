package net.rubii.securelib.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.custom.CardPrinterBlock;
import net.rubii.securelib.block.custom.CardReaderBlock;
import net.rubii.securelib.block.custom.CardWriterBlock;
import net.rubii.securelib.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SecureLib.MODID);

    public static final DeferredBlock<Block> CARD_READER = registerBlock("card_reader", () -> new CardReaderBlock(BlockBehaviour.Properties.of()
            .strength(30f).sound(SoundType.METAL).destroyTime(99999999999999f).requiresCorrectToolForDrops().noOcclusion()
    ));
    public static final DeferredBlock<Block> CARD_PRINTER = registerBlock("card_printer", () -> new CardPrinterBlock(BlockBehaviour.Properties.of()
            .strength(30f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion()
    ));
    public static final DeferredBlock<Block> CARD_WRITER = registerBlock("card_writer", () -> new CardWriterBlock(BlockBehaviour.Properties.of()
            .strength(30f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion()
    ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
