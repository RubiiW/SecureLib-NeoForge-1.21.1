package net.rubii.securelib.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.rubii.securelib.block.ModBlocks;
import net.rubii.securelib.block.entity.CardPrinterBlockEntity;
import net.rubii.securelib.block.entity.KeypadBlockEntity;
import net.rubii.securelib.screen.ModMenuTypes;
import net.rubii.securelib.util.ModTags;

import java.awt.*;

public class KeypadMenu extends AbstractContainerMenu {

    public final KeypadBlockEntity blockEntity;
    private final Level level;

    public KeypadMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public KeypadMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.KEYPAD_MENU.get(), id);
        this.blockEntity = ((KeypadBlockEntity)entity);
        this.level = entity.getLevel();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return new ItemStack(Items.AIR);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.KEYPAD.get());
    }
}
