package net.rubii.securelib.api;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.api.enums.CardInteractionResult;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.block.entity.KeypadReaderBlockEntity;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.network.CardReaderPayload;
import net.rubii.securelib.network.CardReaderTriPayload;
import net.rubii.securelib.network.KeypadReaderPayload;
import net.rubii.securelib.network.KeypadReaderTriPayload;
import net.rubii.securelib.util.ModTags;

import java.util.Objects;

public class SecureLibUtils {

    /*
    HAS NO FREQUENCY
     */

    public static boolean hasNoFrequency(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FREQUENCY, 0) == 0;
    }

    public static boolean hasNoFrequency(BlockEntity blockEntity) {
        if (blockEntity instanceof CardReaderBlockEntity be) {
            return be.getFrequency() == 0;
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            return be.getFrequency() == 0;
        }
        return false;
    }

    /*
    HAS NO CLEARANCE
     */

    public static boolean hasNoClearance(ItemStack stack) {
        if (stack.is(ModTags.Items.KEYCARDS)){
            return stack.getOrDefault(ModDataComponents.CLEARANCE, 0) == 0;
        } else if (stack.is(ModTags.Items.TRICARDS)){
            return stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1) == -1 &&
                    stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1) == -1 &&
                    stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1) == -1;
        }
        return true;
    }

    public static boolean hasNoClearance(BlockEntity blockEntity) {
        if (blockEntity instanceof CardReaderBlockEntity be) {
            return be.getClearance() == 0 && be.getLClearance() == -1 && be.getMClearance() == -1 && be.getRClearance() == -1;
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            return be.getClearance() == 0 && be.getLClearance() == -1 && be.getMClearance() == -1 && be.getRClearance() == -1;
        }
        return true;
    }

    /*
    HAS NO DATA
     */

    public static boolean hasNoData(int frequency, int clearance){
        return frequency == 0 && clearance == 0;
    }

    public static boolean hasNoData(int frequency, int lClearance, int mClearance, int rClearance){
        return frequency == 0 && lClearance == -1 && mClearance == -1 && rClearance == -1;
    }

    public static boolean hasNoData(ItemStack stack){
        return hasNoFrequency(stack) && hasNoClearance(stack);
    }

    public static boolean hasNoData(BlockEntity blockEntity){
        return hasNoFrequency(blockEntity) && hasNoClearance(blockEntity);
    }

    /*
    MATCH FREQUENCY
    */

    public static boolean matchFrequency(int blockFrequency, int itemFrequency) {
        return Objects.equals(blockFrequency, itemFrequency);
    }

    public static boolean matchFrequency(BlockEntity blockEntity, int itemFrequency) {
        int blockFrequency = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        }

        if (hasNoFrequency(blockEntity)) return false;

        return matchFrequency(blockFrequency, itemFrequency);
    }

    public static boolean matchFrequency(int blockFrequency, ItemStack stack) {
        if (hasNoFrequency(stack)) return false;

        return matchFrequency(blockFrequency, stack.getOrDefault(ModDataComponents.FREQUENCY, 0));
    }

    public static boolean matchFrequency(BlockEntity blockEntity, ItemStack stack) {
        int blockFrequency = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        }

        if (hasNoFrequency(blockEntity) || hasNoFrequency(stack)) return false;

        return matchFrequency(blockFrequency, stack.getOrDefault(ModDataComponents.FREQUENCY, 0));
    }

    /*
    HAS REQUIRED CLEARANCE
     */

    public static boolean hasRequiredClearance(int blockClearance, int itemClearance) {
        return blockClearance <= itemClearance;
    }

    public static boolean hasRequiredClearance(BlockEntity blockEntity, int itemClearance) {
        int blockClearance = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getClearance();
        }

        if (hasNoClearance(blockEntity)) return false;

        return hasRequiredClearance(blockClearance, itemClearance);
    }

    public static boolean hasRequiredClearance(int blockClearance, ItemStack stack) {
        if (hasNoClearance(stack)) return false;

        return hasRequiredClearance(blockClearance, stack.getOrDefault(ModDataComponents.CLEARANCE, 0));
    }

    public static boolean hasRequiredClearance(BlockEntity blockEntity, ItemStack stack) {
        int blockClearance = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getClearance();
        }

        if (hasNoClearance(blockEntity) || hasNoClearance(stack)) return false;

        return hasRequiredClearance(blockClearance, stack.getOrDefault(ModDataComponents.CLEARANCE, 0));
    }

    /*
    HAS REQUIRED CLEARANCES
     */

    public static boolean hasRequiredClearances(int blockLClearance, int blockMClearance, int blockRClearance, int itemLClearance, int itemMClearance, int itemRClearance) {
        return blockLClearance <= itemLClearance && blockMClearance <= itemMClearance && blockRClearance <= itemRClearance;
    }

    public static boolean hasRequiredClearances(BlockEntity blockEntity, int itemLClearance, int itemMClearance, int itemRClearance) {
        int blockLClearance = -1;
        int blockMClearance = -1;
        int blockRClearance = -1;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        }

        if (hasNoClearance(blockEntity)) return false;

        return hasRequiredClearances(blockLClearance, blockMClearance, blockRClearance, itemLClearance, itemMClearance, itemRClearance);
    }

    public static boolean hasRequiredClearances(int blockLClearance, int blockMClearance, int blockRClearance, ItemStack stack) {
        int itemLClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1);
        int itemMClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1);
        int itemRClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1);

        if (hasNoClearance(stack)) return false;

        return hasRequiredClearances(blockLClearance, blockMClearance, blockMClearance, itemLClearance, itemMClearance, itemRClearance);
    }

    public static boolean hasRequiredClearances(BlockEntity blockEntity, ItemStack stack) {
        int blockLClearance = -1;
        int blockMClearance = -1;
        int blockRClearance = -1;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        }

        int itemLClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1);
        int itemMClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1);
        int itemRClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1);

        if (hasNoClearance(blockEntity) || hasNoClearance(stack)) return false;

        return hasRequiredClearances(blockLClearance, blockMClearance, blockRClearance, itemLClearance, itemMClearance, itemRClearance);
    }

    /*
    CAN INTERACT
     */

    public static CardInteractionResult canInteract(int blockFrequency, int blockClearance, int itemFrequency, int itemClearance) {
        if (hasNoData(blockFrequency, blockClearance)) return CardInteractionResult.BLOCK_NO_DATA;
        if (hasNoData(itemFrequency, itemClearance)) return CardInteractionResult.ITEM_NO_DATA;
        if (hasNoData(blockFrequency, blockClearance) && hasNoData(itemFrequency, itemClearance)) return CardInteractionResult.BOTH_NO_DATA;

        boolean matchFrequency = matchFrequency(blockFrequency, itemFrequency);

        boolean hasClearance = hasRequiredClearance(blockClearance, itemClearance);

        if (matchFrequency && hasClearance) return CardInteractionResult.SUCCESS;
        else if (matchFrequency && !hasClearance) return CardInteractionResult.CLEARANCE_FAIL;
        else if (!matchFrequency && hasClearance) return CardInteractionResult.FREQUENCY_FAIL;
        else return CardInteractionResult.COMPLETE_FAIL;
    }

    public static CardInteractionResult canInteract(BlockEntity blockEntity, int itemFrequency, int itemClearance) {
        int blockFrequency = 0;
        int blockClearance = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        }

        if (hasNoData(blockEntity)) return CardInteractionResult.BLOCK_NO_DATA;

        return canInteract(blockFrequency, blockClearance, itemClearance, itemFrequency);
    }

    public static CardInteractionResult canInteract(int blockFrequency, int blockClearance, ItemStack stack) {
        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemClearance = stack.getOrDefault(ModDataComponents.CLEARANCE, 0);

        if (hasNoData(stack)) return CardInteractionResult.ITEM_NO_DATA;

        return canInteract(blockFrequency, blockClearance, itemFrequency, itemClearance);
    }

    public static CardInteractionResult canInteract(BlockEntity blockEntity, ItemStack stack) {
        int blockFrequency = 0;
        int blockClearance = 0;

        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemClearance = stack.getOrDefault(ModDataComponents.CLEARANCE, 0);

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        }

        if (hasNoData(blockEntity)) return CardInteractionResult.BLOCK_NO_DATA;
        if (hasNoData(stack)) return CardInteractionResult.ITEM_NO_DATA;
        if (hasNoData(blockEntity) && hasNoData(stack)) return CardInteractionResult.BOTH_NO_DATA;

        return canInteract(blockFrequency, blockClearance, itemFrequency, itemClearance);
    }

        /*
    CAN TRI-INTERACT
     */

    public static CardInteractionResult canTriInteract(int blockFrequency, int blockLClearance, int blockMClearance, int blockRClearance, int itemFrequency, int itemLClearance, int itemMClearance, int itemRClearance) {
        if (hasNoData(blockFrequency, blockLClearance, blockMClearance, blockRClearance)) return CardInteractionResult.BLOCK_NO_DATA;
        if (hasNoData(itemFrequency, itemLClearance, itemMClearance, itemRClearance)) return CardInteractionResult.ITEM_NO_DATA;
        if (hasNoData(blockFrequency, blockLClearance, blockMClearance, blockRClearance) &&
                hasNoData(itemFrequency, itemLClearance, itemMClearance, itemRClearance)) return CardInteractionResult.BOTH_NO_DATA;

        boolean matchFrequency = matchFrequency(blockFrequency, itemFrequency);

        boolean hasClearances =
                hasRequiredClearances(blockLClearance, blockMClearance, blockRClearance, itemLClearance, itemMClearance, itemRClearance);

        if (matchFrequency && hasClearances) return CardInteractionResult.SUCCESS;
        else if (matchFrequency && !hasClearances) return CardInteractionResult.CLEARANCE_FAIL;
        else if (!matchFrequency && hasClearances) return CardInteractionResult.FREQUENCY_FAIL;
        else return CardInteractionResult.COMPLETE_FAIL;
    }

    public static CardInteractionResult canTriInteract(BlockEntity blockEntity, int itemFrequency, int itemLClearance, int itemMClearance, int itemRClearance) {
        int blockFrequency = 0;
        int blockLClearance = -1;
        int blockMClearance = -1;
        int blockRClearance = -1;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        }

        if (hasNoData(blockEntity)) return CardInteractionResult.BLOCK_NO_DATA;

        return canTriInteract(blockFrequency, blockLClearance, blockMClearance, blockRClearance, itemLClearance, itemMClearance, itemRClearance, itemFrequency);
    }

    public static CardInteractionResult canTriInteract(int blockFrequency, int blockLClearance, int blockMClearance, int blockRClearance, ItemStack stack) {
        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemLClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1);
        int itemMClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1);
        int itemRClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1);

        if (hasNoData(stack)) return CardInteractionResult.ITEM_NO_DATA;

        return canTriInteract(blockFrequency, blockLClearance, blockMClearance, blockRClearance, itemFrequency, itemLClearance, itemMClearance, itemRClearance);
    }

    public static CardInteractionResult canTriInteract(BlockEntity blockEntity, ItemStack stack) {
        int blockFrequency = 0;
        int blockLClearance = -1;
        int blockMClearance = -1;
        int blockRClearance = -1;

        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemLClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1);
        int itemMClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1);
        int itemRClearance = stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1);

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockLClearance = be.getLClearance();
            blockMClearance = be.getMClearance();
            blockRClearance = be.getRClearance();
        }

        if (hasNoData(blockEntity)) return CardInteractionResult.BLOCK_NO_DATA;
        if (hasNoData(stack)) return CardInteractionResult.ITEM_NO_DATA;
        if (hasNoData(blockEntity) && hasNoData(stack)) return CardInteractionResult.BOTH_NO_DATA;

        return canTriInteract(blockFrequency, blockLClearance, blockMClearance, blockRClearance, itemFrequency, itemLClearance, itemMClearance, itemRClearance);
    }

    /*
    GET/SET FREQUENCY
     */

    public static ItemStack setFrequency(ItemStack stack, int frequency) {
        stack.set(ModDataComponents.FREQUENCY, frequency);
        return stack;
    }

    public static BlockEntity setFrequency(BlockEntity blockEntity, int frequency) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        assert level != null;
        if (level.isClientSide()){
            if (blockEntity instanceof CardReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new CardReaderPayload(pos, frequency, be.getClearance())
                );
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new KeypadReaderPayload(pos, frequency, be.getClearance())
                );
                return level.getBlockEntity(pos);
            }
        } else {
            if (blockEntity instanceof CardReaderBlockEntity be) {
                be.setFrequency(frequency);
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                be.setFrequency(frequency);
                return level.getBlockEntity(pos);
            }
        }
        return null;
    }

    public static int getFrequency(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
    }

    public static int getFrequency(BlockEntity blockEntity) {
        if (hasNoFrequency(blockEntity)) return 0;

        int blockFrequency = 0;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
        }

        return blockFrequency;
    }

    /*
    GET/SET CLEARANCE
     */

    public static ItemStack setClearance(ItemStack stack, int clearance) {
        stack.set(ModDataComponents.CLEARANCE, clearance);
        return stack;
    }

    public static BlockEntity setClearance(BlockEntity blockEntity, int clearance) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        assert level != null;
        if (level.isClientSide()){
            if (blockEntity instanceof CardReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new CardReaderPayload(pos, be.getFrequency(), clearance)
                );
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new KeypadReaderPayload(pos, be.getFrequency(), clearance)
                );
                return level.getBlockEntity(pos);
            }
        } else {
            if (blockEntity instanceof CardReaderBlockEntity be) {
                be.setClearance(clearance);
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                be.setClearance(clearance);
                return level.getBlockEntity(pos);
            }
        }
        return null;
    }

    public static int getClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE, 0);
    }

    public static int getClearance(BlockEntity blockEntity) {
        if (hasNoClearance(blockEntity)) return 0;

        int blockClearance = 0;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getClearance();
        }

        return blockClearance;
    }

    /*
    GET/SET CLEARANCES
     */

    public static ItemStack setClearances(ItemStack stack, int lClearance, int mClearance, int rClearance) {
        stack.set(ModDataComponents.CLEARANCE_L, lClearance);
        stack.set(ModDataComponents.CLEARANCE_M, mClearance);
        stack.set(ModDataComponents.CLEARANCE_R, rClearance);
        return stack;
    }

    public static BlockEntity setClearances(BlockEntity blockEntity, int lClearance, int mClearance, int rClearance) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        assert level != null;
        if (level.isClientSide()){
            if (blockEntity instanceof CardReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new CardReaderTriPayload(pos, be.getFrequency(), lClearance, mClearance, rClearance)
                );
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(
                        new KeypadReaderTriPayload(pos, be.getFrequency(), lClearance, mClearance, rClearance)
                );
                return level.getBlockEntity(pos);
            }
        } else {
            if (blockEntity instanceof CardReaderBlockEntity be) {
                be.setClearances(lClearance, mClearance, rClearance);
                return level.getBlockEntity(pos);
            } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
                be.setClearances(lClearance, mClearance, rClearance);
                return level.getBlockEntity(pos);
            }
        }
        return null;
    }

    public static int getLClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE_L, -1);
    }

    public static int getMClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE_M, -1);
    }

    public static int getRClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE_R, -1);
    }

    public static int getLClearance(BlockEntity blockEntity) {
        if (hasNoClearance(blockEntity)) return -1;

        int blockClearance = -1;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getLClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getLClearance();
        }

        return blockClearance;
    }

    public static int getMClearance(BlockEntity blockEntity) {
        if (hasNoClearance(blockEntity)) return -1;

        int blockClearance = -1;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getMClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getMClearance();
        }

        return blockClearance;
    }

    public static int getRClearance(BlockEntity blockEntity) {
        if (hasNoClearance(blockEntity)) return -1;

        int blockClearance = -1;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockClearance = be.getRClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockClearance = be.getRClearance();
        }

        return blockClearance;
    }

    /*
    KEYCARD
     */

    public static ItemStack keycard(ItemStack stack, Component name, String rgb, boolean showInTooltip) {
        if (stack.is(ModTags.Items.KEYCARDS)) {
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(rgb, 16), showInTooltip));
            stack.set(DataComponents.ITEM_NAME, name);
        }else{
            SecureLib.LOGGER.error("[SecureLibAPI] Error: ItemStack is not a Keycard");
        }

        return stack;
    }

    public static ItemStack keycard(ItemStack stack, Component name, Integer rgb, boolean showInTooltip) {
        if (stack.is(ModTags.Items.KEYCARDS)) {
            String str = rgb.toString().split("x")[1];
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(str, 16), showInTooltip));
            stack.set(DataComponents.ITEM_NAME, name);
        }else{
            SecureLib.LOGGER.error("[SecureLibAPI] Error: ItemStack is not a Keycard");
        }

        return stack;
    }

    public static ItemStack keycard(ItemStack stack, Component name, Integer r, Integer g, Integer b, boolean showInTooltip) {
        if (stack.is(ModTags.Items.KEYCARDS)) {
            String rgb = r.toString() + b.toString() + g.toString();
            String str = rgb.split("x")[1];
            stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(str, 16), showInTooltip));
            stack.set(DataComponents.ITEM_NAME, name);
        }else{
            SecureLib.LOGGER.error("[SecureLibAPI] Error: ItemStack is not a Keycard");
        }

        return stack;
    }

    public static ItemStack keycard(Component name, String rgb, boolean showInTooltip) {
        ItemStack stack = new ItemStack(ModItems.KEYCARD.get());

        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(rgb, 16), showInTooltip));
        stack.set(DataComponents.ITEM_NAME, name);

        return stack;
    }

    public static ItemStack keycard(Component name, Integer rgb, boolean showInTooltip) {
        ItemStack stack = new ItemStack(ModItems.KEYCARD.get());

        String str = rgb.toString().split("x")[1];
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(str, 16), showInTooltip));
        stack.set(DataComponents.ITEM_NAME, name);

        return stack;
    }

    public static ItemStack keycard(Component name, Integer r, Integer g, Integer b, boolean showInTooltip) {
        ItemStack stack = new ItemStack(ModItems.KEYCARD.get());

        String rgb = r.toString() + b.toString() + g.toString();
        String str = rgb.split("x")[1];
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(str, 16), showInTooltip));
        stack.set(DataComponents.ITEM_NAME, name);

        return stack;
    }

    /*
    OTHER
     */

    public static boolean isSkeleton(ItemStack stack) {
        return stack.is(ModTags.Items.SKELETON_KEYCARDS);
    }

    public static boolean isTriData(BlockEntity blockEntity) {
        if (hasNoData(blockEntity)) return false;
        if (blockEntity instanceof CardReaderBlockEntity be) {
            return be.getClearance() == 0;
        }
        if (blockEntity instanceof KeypadReaderBlockEntity be) {
            return be.getClearance() == 0;
        }

        return false;
    }
}