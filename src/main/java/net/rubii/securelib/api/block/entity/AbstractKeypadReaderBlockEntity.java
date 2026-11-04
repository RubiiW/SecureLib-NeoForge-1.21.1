package net.rubii.securelib.api.block.entity;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.api.block.AbstractKeypadReaderBlock;
import net.rubii.securelib.block.custom.KeypadReaderBlock;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class AbstractKeypadReaderBlockEntity extends BlockEntity implements MenuProvider {
    public AbstractKeypadReaderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    private String code = "";
    private String input = "";
    private boolean removal = false;

    private Integer frequency = 0;
    private Integer clearance = 0;
    private Integer lClearance = -1;
    private Integer mClearance = -1;
    private Integer rClearance = -1;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        tag.putString("code", code);
        tag.putString("input", input);
        tag.putBoolean("removal", removal);

        tag.putInt("frequency", frequency);
        tag.putInt("clearance", clearance);
        tag.putInt("lClearance", lClearance);
        tag.putInt("mClearance", mClearance);
        tag.putInt("rClearance", rClearance);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        code = tag.getString("code");
        input = tag.getString("input");
        removal = tag.getBoolean("removal");

        frequency = tag.getInt("frequency");
        clearance = tag.getInt("clearance");
        lClearance = tag.getInt("lClearance");
        mClearance = tag.getInt("mClearance");
        rClearance = tag.getInt("rClearance");
    }

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        if (level == null) return;
        if (code == null){
            SecureLib.LOGGER.error("Tried to set null Code, cancelling");
            return;
        }

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
        if (level == null) return;
        if (input == null){
            SecureLib.LOGGER.error("Tried to set null Input, cancelling");
            return;
        }

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
        if (level == null) return;

        this.removal = removal;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Integer getClearance() {
        return clearance == null ? 0 : clearance;
    }

    public Integer getLClearance() {
        return lClearance == null ? -1 : lClearance;
    }

    public Integer getMClearance() {
        return mClearance == null ? -1 : mClearance;
    }

    public Integer getRClearance() {
        return rClearance == null ? -1 : rClearance;
    }

    public void setClearance(Integer clear) {
        if (level == null) return;
        if (clear == null){
            SecureLib.LOGGER.error("Tried to set null Clearance, cancelling");
            return;
        }

        this.clearance = clear;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setClearances(Integer l, Integer m, Integer r) {
        if (level == null) return;
        if (l == null){
            SecureLib.LOGGER.error("Tried to set null L Clearance, cancelling");
            return;
        }
        if (m == null){
            SecureLib.LOGGER.error("Tried to set null M Clearance, cancelling");
            return;
        }
        if (r == null){
            SecureLib.LOGGER.error("Tried to set null R Clearance, cancelling");
            return;
        }

        this.lClearance = l;
        this.mClearance = m;
        this.rClearance = r;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Integer getFrequency() {
        return frequency == null ? 0 : frequency;
    }

    public void setFrequency(Integer freq) {
        if (level == null) return;
        if (freq == null){
            SecureLib.LOGGER.error("Tried to set null Frequency, cancelling");
            return;
        }

        this.frequency = freq;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void inputUpdated(Player player) {
        if (level == null) return;

        if (getBlockState().getBlock() instanceof AbstractKeypadReaderBlock block){
            if (removal){
                if (input.equals(code)) {
                    block.removalEvents.remove(level, player, this, getBlockPos());
                }else{
                    block.removalEvents.cancel(level, player, this, getBlockPos());
                }
            }else{
                if (input.equals(code)) {
                    block.interactionEvents.activate(level, player, this, getBlockPos());
                } else {
                    block.interactionEvents.onCodeFail(level, player, this, getBlockPos());
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
    @NotNull
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries){
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    @NotNull
    public Component getDisplayName() {
        SecureLib.LOGGER.error("getDisplayName() has not been overridden in class extending AbstractKeypadReaderBlockEntity!");
        return Component.literal("AbstractKeypadReaderBlockEntity");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        SecureLib.LOGGER.error("createMenu() has not been overridden in class extending AbstractKeypadReaderBlockEntity!");
        return null;
    }
}
