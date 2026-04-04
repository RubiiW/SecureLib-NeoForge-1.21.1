package net.rubii.securelib.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.rubii.securelib.SecureLib;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> CARD_READERS = createTag("card_readers");
        public static final TagKey<Block> KEYPADS = createTag("keypads");
        public static final TagKey<Block> KEYPAD_READERS = createTag("keypad_readers");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SKELETON_KEYCARDS = createTag("skeleton_keycards");
        public static final TagKey<Item> KEYCARDS = createTag("keycards");
        public static final TagKey<Item> DATA_RECEIVER = createTag("data_receiver");
        public static final TagKey<Item> KEYPAD_BYPASS = createTag("keypad_bypass");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, name));
        }
    }
}
