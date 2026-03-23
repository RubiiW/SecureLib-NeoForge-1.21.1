package net.rubii.securelib.api;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CardReaderRegistry {
    private static final List<Supplier<Block>> BLOCKS = new ArrayList<>();

    public static void register(Supplier<Block> block) {
        BLOCKS.add(block);
    }

    public static Block[] getBlocks() {
        return BLOCKS.stream()
                .map(Supplier::get)
                .toArray(Block[]::new);
    }
}
