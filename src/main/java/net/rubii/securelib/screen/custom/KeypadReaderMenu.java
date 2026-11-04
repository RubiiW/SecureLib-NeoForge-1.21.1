package net.rubii.securelib.screen.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.rubii.securelib.api.block.entity.AbstractKeypadReaderBlockEntity;
import net.rubii.securelib.screen.ModMenuTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KeypadReaderMenu extends AbstractContainerMenu {

    public final AbstractKeypadReaderBlockEntity blockEntity;
    private final Level level;

    public KeypadReaderMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, Objects.requireNonNull(inventory.player.level().getBlockEntity(extraData.readBlockPos())));
    }

    public KeypadReaderMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.KEYPAD_READER_MENU.get(), id);
        this.blockEntity = ((AbstractKeypadReaderBlockEntity)entity);
        this.level = entity.getLevel();
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player player, int i) {
        return new ItemStack(Items.AIR);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, level.getBlockState(blockEntity.getBlockPos()).getBlock());
    }
}
