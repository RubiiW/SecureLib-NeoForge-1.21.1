package net.rubii.securelib.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.block.entity.KeypadReaderBlockEntity;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.util.ModTags;

import java.util.Objects;

public class SecureLibUtils {

    /*
    HAS NO FREQUENCY
     */

    public static boolean hasNoFrequency(int frequency) {
        return frequency == 0;
    }

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

    public static boolean hasNoClearance(int clearance) {
        return clearance == 0;
    }

    public static boolean hasNoClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE, 0) == 0;
    }

    public static boolean hasNoClearance(BlockEntity blockEntity) {
        if (blockEntity instanceof CardReaderBlockEntity be) {
            return be.getClearance() == 0;
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            return be.getClearance() == 0;
        }
        return false;
    }

    /*
    HAS NO DATA
     */

    public static boolean hasNoData(int frequency, int clearance){
        return hasNoFrequency(frequency) && hasNoClearance(clearance);
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
    CAN INTERACT
     */

    public static boolean canInteract(int blockFrequency, int blockClearance, int itemFrequency, int itemClearance) {
        SecureLib.LOGGER.debug(itemFrequency + " " + itemClearance);

        return matchFrequency(blockFrequency, itemFrequency) && hasRequiredClearance(blockClearance, itemClearance);
    }

    public static boolean canInteract(BlockEntity blockEntity, int itemFrequency, int itemClearance) {
        int blockFrequency = 0;
        int blockClearance = 0;

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        }

        if (hasNoData(blockEntity)) return false;

        return canInteract(blockFrequency, blockClearance, itemClearance, itemFrequency);
    }

    public static boolean canInteract(int blockFrequency, int blockClearance, ItemStack stack) {
        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemClearance = stack.getOrDefault(ModDataComponents.CLEARANCE, 0);

        if (hasNoData(stack)) return false;

        return canInteract(blockFrequency, blockClearance, itemFrequency, itemClearance);
    }

    public static boolean canInteract(BlockEntity blockEntity, ItemStack stack) {
        int blockFrequency = 0;
        int blockClearance = 0;

        int itemFrequency = stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
        int itemClearance = stack.getOrDefault(ModDataComponents.CLEARANCE, 0);

        SecureLib.LOGGER.debug(itemFrequency + " " + itemClearance);

        if (blockEntity instanceof CardReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        } else if (blockEntity instanceof KeypadReaderBlockEntity be) {
            blockFrequency = be.getFrequency();
            blockClearance = be.getClearance();
        }

        if (hasNoData(blockEntity) || hasNoData(stack)) return false;

        return canInteract(blockFrequency, blockClearance, itemFrequency, itemClearance);
    }

    /*
    GET/SET FREQUENCY
     */

    public ItemStack setFrequency(ItemStack stack, int frequency) {
        stack.set(ModDataComponents.FREQUENCY, frequency);
        return stack;
    }

    public int getFrequency(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.FREQUENCY, 0);
    }

    /*
    GET/SET CLEARANCE
     */

    public ItemStack setClearance(ItemStack stack, int clearance) {
        stack.set(ModDataComponents.CLEARANCE, clearance);
        return stack;
    }

    public int getClearance(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CLEARANCE, 0);
    }

    /*
    OTHER
     */

    public static boolean isSkeleton(ItemStack stack) {
        return stack.is(ModTags.Items.SKELETON_KEYCARDS);
    }
}
