package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.rubii.securelib.api.block.entity.AbstractCardReaderBlockEntity;

public class CardReaderBlockEntity extends AbstractCardReaderBlockEntity {
    public CardReaderBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CARD_READER_BE.get(), pos, blockState);
    }
}
