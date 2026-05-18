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
    private Integer lClearance = -1;
    private Integer mClearance = -1;
    private Integer rClearance = -1;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("frequency", frequency);
        tag.putInt("clearance", clearance);
        tag.putInt("lClearance", lClearance);
        tag.putInt("mClearance", mClearance);
        tag.putInt("rClearance", rClearance);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        frequency = tag.getInt("frequency");
        clearance = tag.getInt("clearance");
        lClearance = tag.getInt("lClearance");
        mClearance = tag.getInt("mClearance");
        rClearance = tag.getInt("rClearance");
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
        this.clearance = clear;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setClearances(Integer l, Integer m, Integer r) {
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
