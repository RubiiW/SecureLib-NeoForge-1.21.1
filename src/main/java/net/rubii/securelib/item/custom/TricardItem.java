package net.rubii.securelib.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.rubii.securelib.components.ModDataComponents;

import java.awt.*;
import java.util.List;
import java.util.Locale;

public class TricardItem extends Item {
    public TricardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (stack.get(DataComponents.DYED_COLOR) == null && stack.get(ModDataComponents.FREQUENCY) == null && stack.get(ModDataComponents.CLEARANCE) == null){
                components.add(Component.translatable("tooltip.securelib.data_receiver.no_data").withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.securelib.data_receiver.edit_double").withStyle(ChatFormatting.GRAY));
            }
            if (stack.get(ModDataComponents.CLEARANCE_R) != null || stack.get(ModDataComponents.CLEARANCE_M) != null || stack.get(ModDataComponents.CLEARANCE_L) != null){
                String lPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.l").getString();
                String mPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.m").getString();
                String rPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.r").getString();
                String lValue = stack.get(ModDataComponents.CLEARANCE_L) == null ? "0" : stack.get(ModDataComponents.CLEARANCE_L).toString();
                String mValue = stack.get(ModDataComponents.CLEARANCE_M) == null ? "0" : stack.get(ModDataComponents.CLEARANCE_M).toString();
                String rValue = stack.get(ModDataComponents.CLEARANCE_R) == null ? "0" : stack.get(ModDataComponents.CLEARANCE_R).toString();
                Component component = Component.literal(lPrefix + lValue + ", "  + mPrefix + mValue + ", "  + rPrefix + rValue).withStyle(ChatFormatting.GRAY);

                components.add(Component.translatable("tooltip.securelib.tridata_receiver.clearances").withStyle(ChatFormatting.GRAY));
                components.add(component);
            }
            if (stack.get(ModDataComponents.FREQUENCY) != null){
                String frequency = " " + stack.get(ModDataComponents.FREQUENCY);
                String translated = Component.translatable("tooltip.securelib.data_receiver.frequency").getString();
                Component component = Component.literal(translated + frequency).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
            if (stack.get(DataComponents.DYED_COLOR) != null){
                String hex = String.format(Locale.ROOT, " #%06X", stack.get(DataComponents.DYED_COLOR).rgb());
                String translated = Component.translatable("tooltip.securelib.data_receiver.color").getString();
                Component component = Component.literal(translated + hex).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
        } else {
            components.add(Component.translatable("tooltip.securelib.tridata_receiver").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, components, flag);
    }
}
