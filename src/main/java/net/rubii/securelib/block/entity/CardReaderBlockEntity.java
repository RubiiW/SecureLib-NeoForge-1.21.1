package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CardReaderBlockEntity extends BlockEntity {
    public CardReaderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CARD_READER_BE.get(), pos, blockState);
    }

    private Integer frequency = 0; // DO NOT REMOVE THE = 0 OR THE THING EXPLODE
    private Integer clearance = 0;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("frequency", frequency);
        tag.putInt("clearance", clearance);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        frequency = tag.getInt("frequency");
        clearance = tag.getInt("clearance");
    }

    public Integer getClearance() {
        return clearance == null ? 0 : clearance;
    }

    public void setClearance(Integer clear) {
        this.clearance = clear;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public Integer getFrequency() {
        return frequency == null ? 0 : frequency;
    }

    public void setFrequency(Integer freq) {
        this.frequency = freq;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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
}
