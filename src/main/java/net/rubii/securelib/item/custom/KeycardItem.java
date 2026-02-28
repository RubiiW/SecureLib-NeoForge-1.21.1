package net.rubii.securelib.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.rubii.securelib.components.ModDataComponents;

import java.util.List;
import java.util.Locale;

public class KeycardItem extends Item {
    public KeycardItem(Properties properties) {
        super(properties);
    }

    /*
    GET/SET THE CLEARANCE
    [ITEMSTACK].get(ModDataComponents.CLEARANCE);
    [ITEMSTACK].set(ModDataComponents.CLEARANCE, [VALUE]);

    GET/SET THE COLOR
    [ITEMSTACK].get(Datacomponents.DYED_COLOR);
    [ITEMSTACK].set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt("[VALUE]", 16), false));
     */


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (stack.get(DataComponents.DYED_COLOR) == null && stack.get(ModDataComponents.FREQUENCY) == null && stack.get(ModDataComponents.CLEARANCE) == null){
                components.add(Component.translatable("tooltip.securelib.keycard.no_data").withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.securelib.keycard.edit").withStyle(ChatFormatting.GRAY));
            }

            if (stack.get(ModDataComponents.CLEARANCE) != null){
                String clearance = " " + stack.get(ModDataComponents.CLEARANCE).toString();
                String translated = Component.translatable("tooltip.securelib.keycard.clearance").getString();
                Component component = Component.literal(translated + clearance).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
            if (stack.get(ModDataComponents.FREQUENCY) != null){
                String frequency = " " + stack.get(ModDataComponents.FREQUENCY).toString();
                String translated = Component.translatable("tooltip.securelib.keycard.frequency").getString();
                Component component = Component.literal(translated + frequency).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
            if (stack.get(DataComponents.DYED_COLOR) != null){
                String hex = String.format(Locale.ROOT, " #%06X", stack.get(DataComponents.DYED_COLOR).rgb());
                String translated = Component.translatable("tooltip.securelib.keycard.color").getString();
                Component component = Component.literal(translated + hex).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
        } else {
            components.add(Component.translatable("tooltip.securelib.keycard").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, components, flag);
    }
}
