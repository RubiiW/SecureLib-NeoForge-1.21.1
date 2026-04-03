package net.rubii.securelib.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.rubii.securelib.api.SecureLibBlocksRegistry;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SecureLib.MODID);

    public static final Supplier<BlockEntityType<CardReaderBlockEntity>> CARD_READER_BE =
            BLOCK_ENTITIES.register("card_reader_be", () -> BlockEntityType.Builder.of(
                    CardReaderBlockEntity::new, SecureLibBlocksRegistry.getCardReaders()).build(null));

    public static final Supplier<BlockEntityType<CardReaderBlockEntity>> KEYPAD_BE =
            BLOCK_ENTITIES.register("keypad_be", () -> BlockEntityType.Builder.of(
                    CardReaderBlockEntity::new, SecureLibBlocksRegistry.getKeypads()).build(null));

    public static final Supplier<BlockEntityType<CardReaderBlockEntity>> KEYPAD_READER_BE =
            BLOCK_ENTITIES.register("keypad_reader_be", () -> BlockEntityType.Builder.of(
                    CardReaderBlockEntity::new, SecureLibBlocksRegistry.getKeypadReaders()).build(null));

    public static final Supplier<BlockEntityType<CardPrinterBlockEntity>> CARD_PRINTER_BE =
            BLOCK_ENTITIES.register("card_printer_be", () -> BlockEntityType.Builder.of(
                    CardPrinterBlockEntity::new, ModBlocks.CARD_PRINTER.get()).build(null));

    public static final Supplier<BlockEntityType<CardWriterBlockEntity>> CARD_WRITER_BE =
            BLOCK_ENTITIES.register("card_writer_be", () -> BlockEntityType.Builder.of(
                    CardWriterBlockEntity::new, ModBlocks.CARD_WRITER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
