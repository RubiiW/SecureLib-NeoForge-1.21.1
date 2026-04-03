package net.rubii.securelib.api;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SecureLibBlocksRegistry {
    private static final List<Supplier<Block>> CARD_READERS = new ArrayList<>();

    public static void registerCardReader(Supplier<Block> block) {
        CARD_READERS.add(block);
    }

    public static Block[] getCardReaders() {
        return CARD_READERS.stream()
                .map(Supplier::get)
                .toArray(Block[]::new);
    }

    private static final List<Supplier<Block>> KEYPADS = new ArrayList<>();

    public static void registerKeypad(Supplier<Block> block) {
        KEYPADS.add(block);
    }

    public static Block[] getKeypads() {
        return KEYPADS.stream()
                .map(Supplier::get)
                .toArray(Block[]::new);
    }

    private static final List<Supplier<Block>> KEYPAD_READERS = new ArrayList<>();

    public static void registerKeypadReader(Supplier<Block> block) {
        KEYPAD_READERS.add(block);
    }

    public static Block[] getKeypadReaders() {
        return KEYPAD_READERS.stream()
                .map(Supplier::get)
                .toArray(Block[]::new);
    }
}
