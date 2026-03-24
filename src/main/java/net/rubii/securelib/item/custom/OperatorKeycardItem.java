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

public class OperatorKeycardItem extends Item {
    public OperatorKeycardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (stack.get(DataComponents.DYED_COLOR) == null){
                components.add(Component.translatable("tooltip.securelib.data_receiver.no_data").withStyle(ChatFormatting.GRAY));
                components.add(Component.translatable("tooltip.securelib.data_receiver.edit_color").withStyle(ChatFormatting.GRAY));
            } else {
                String hex = String.format(Locale.ROOT, " #%06X", stack.get(DataComponents.DYED_COLOR).rgb());
                String translated = Component.translatable("tooltip.securelib.data_receiver.color").getString();
                Component component = Component.literal(translated + hex).withStyle(ChatFormatting.GRAY);

                components.add(component);
            }
        } else {
            components.add(Component.translatable("tooltip.securelib.data_receiver").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, components, flag);
    }
}
