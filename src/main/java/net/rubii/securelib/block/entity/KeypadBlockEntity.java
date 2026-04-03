package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.rubii.securelib.block.custom.CardReaderBlock;
import net.rubii.securelib.block.custom.KeypadBlock;
import net.rubii.securelib.screen.custom.CardPrinterMenu;
import net.rubii.securelib.screen.custom.KeypadMenu;

import javax.annotation.Nullable;
import java.util.UUID;

public class KeypadBlockEntity extends BlockEntity implements MenuProvider {
    public KeypadBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.KEYPAD_BE.get(), pos, blockState);
    }

    private String code = ""; // DO NOT REMOVE THE = "" OR THE THING EXPLODE
    private String input = "";
    private boolean removal = false;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("code", code);
        tag.putString("input", input);
        tag.putBoolean("removal", removal);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        code = tag.getString("code");
        input = tag.getString("input");
        removal = tag.getBoolean("removal");
    }

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        this.code = code;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public String getInput() {
        return input == null ? "" : input;
    }

    public void setInput(String input) {
        this.input = input;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean getRemoval() {
        return removal;
    }

    public void setRemoval(boolean removal) {
        this.removal = removal;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void inputUpdated(UUID uuid) {
        if (input.equals(code)) {
            if (getBlockState().getBlock() instanceof KeypadBlock block){
                if (removal){
                    level.destroyBlock(getBlockPos(), true);
                    level.updateNeighbourForOutputSignal(getBlockPos(), block);
                }else{
                    block.activate(level, getBlockState(), level.getPlayerByUUID(uuid), getBlockPos());
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.securelib.keypad");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new KeypadMenu(i, inventory, this);
    }
}
