package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.rubii.securelib.block.custom.CardReaderBlock;

import javax.annotation.Nullable;

public class CardReaderBlockEntity extends BlockEntity {
    public CardReaderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CARD_READER_BE.get(), pos, blockState);
    }

    private Integer frequency = 0; // DO NOT REMOVE THE = 0 OR THE THING EXPLODE
    private Integer clearance = 0;
    private Integer timer = -1;

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("frequency", frequency);
        tag.putInt("clearance", clearance);
        tag.putInt("timer", timer);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        frequency = tag.getInt("frequency");
        clearance = tag.getInt("clearance");
        timer = tag.getInt("timer");
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

    public Integer getTimer() { return timer == null ? -1 : timer; }

    public void setTimer(Integer timer) {
        this.timer = timer;
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        if (timer == -1) return;
        timer--;

        if (timer == 0) {
            if (state.getValue(CardReaderBlock.POWERED)) {
                Block block = state.getBlock();

                if (block instanceof CardReaderBlock cardReaderBlock) {
                    cardReaderBlock.power(false, state, level, pos, null);
                }
            }
            timer = -1;
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
