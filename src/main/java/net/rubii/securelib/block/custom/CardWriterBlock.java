package net.rubii.securelib.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rubii.securelib.block.entity.CardWriterBlockEntity;
import net.rubii.securelib.block.entity.ModBlockEntities;

import javax.annotation.Nullable;

public class CardWriterBlock extends BaseEntityBlock {
    public static final MapCodec<CardWriterBlock> CODEC = simpleCodec(CardWriterBlock::new);
    public static final VoxelShape N_SHAPE = Block.box(0, 0, 6, 16, 10, 15);
    public static final VoxelShape S_SHAPE = Block.box(0, 0, 1, 16, 10, 10);
    public static final VoxelShape E_SHAPE = Block.box(1, 0, 0, 10, 10, 16);
    public static final VoxelShape W_SHAPE = Block.box(6, 0, 0, 15, 10, 16);
    public static final BooleanProperty CRAFTING = BooleanProperty.create("crafting");

    public CardWriterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec(){
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ePos) {
        Direction dir = state.getValue(HorizontalDirectionalBlock.FACING);
        switch (dir) {
            case NORTH: return N_SHAPE;
            case SOUTH: return S_SHAPE;
            case EAST: return E_SHAPE;
            case WEST: return W_SHAPE;
            default: return N_SHAPE;
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING);
        builder.add(CRAFTING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockstate){
        return new CardWriterBlockEntity(blockPos, blockstate);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof CardWriterBlockEntity cardWriterBlockEntity){
                cardWriterBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof CardWriterBlockEntity cardWriterBlockEntity){
            if (!level.isClientSide){
                if(player.isCrouching()){
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                } else {
                    player.openMenu(new SimpleMenuProvider(cardWriterBlockEntity, Component.translatable("block.securelib.card_writer")), pos);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        if (level.isClientSide){
            return null;
        }

        return createTickerHelper(type, ModBlockEntities.CARD_WRITER_BE.get(),
                (level1, blockPos, blockState, blockEntity) -> blockEntity.tick(level1, blockPos, blockState));
    }
}
