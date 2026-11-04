package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import net.rubii.securelib.api.block.entity.AbstractKeypadBlockEntity;
import net.rubii.securelib.screen.custom.KeypadMenu;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class KeypadBlockEntity extends AbstractKeypadBlockEntity implements MenuProvider {
    public KeypadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.KEYPAD_BE.get(), pos, blockState);
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        return Component.translatable("block.securelib.keypad");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new KeypadMenu(i, inventory, this);
    }
}
