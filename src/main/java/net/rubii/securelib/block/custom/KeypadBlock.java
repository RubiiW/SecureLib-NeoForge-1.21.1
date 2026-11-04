package net.rubii.securelib.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.api.block.AbstractKeypadBlock;
import net.rubii.securelib.api.block.entity.AbstractKeypadBlockEntity;
import net.rubii.securelib.block.entity.KeypadBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;
import static net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACE;
import static net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACING;

public class KeypadBlock extends AbstractKeypadBlock {
    public KeypadBlock(Properties properties, SoundEvent enableSound, SoundEvent disableSound) {
        super(properties, enableSound, disableSound);
        this.interactionEvents = new InteractionEvents();
    }

    //BLOCK ENTITY
    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new KeypadBlockEntity(blockPos, blockState);
    }

    @Override
    @NotNull
    protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context){
        Direction direction = state.getValue(FACING);
        return switch (state.getValue(FACE)) {
            case FLOOR -> switch (direction) {
                case EAST, WEST -> Block.box(3.0, 0.0, 5.0, 13.0, 2.0, 11.0);
                case NORTH, SOUTH, UP, DOWN -> Block.box(5.0, 0.0, 3.0, 11.0, 2.0, 13.0);
            };
            case WALL -> switch (direction) {
                case EAST -> Block.box(0.0, 3.0, 5.0, 2.0, 13.0, 11.0);
                case WEST -> Block.box(14.0, 3.0, 5.0, 16.0, 13.0, 11.0);
                case SOUTH -> Block.box(5.0, 3.0, 0.0, 11.0, 13.0, 2.0);
                case NORTH, UP, DOWN -> Block.box(5.0, 3.0, 14.0, 11.0, 13.0, 16.0);
            };
            case CEILING -> switch (direction) {
                case EAST, WEST -> Block.box(3.0, 14.0, 5.0, 13.0, 16.0, 11.0);
                case NORTH, SOUTH, UP, DOWN -> Block.box(5.0, 14.0, 3.0, 11.0, 16.0, 13.0);
            };
        };
    }

    //BLOCKSTATE
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FaceAttachedHorizontalDirectionalBlock.FACING);
        builder.add(FaceAttachedHorizontalDirectionalBlock.FACE);
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for(Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR).setValue(FaceAttachedHorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
            } else {
                blockstate = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(FACE, AttachFace.WALL).setValue(FaceAttachedHorizontalDirectionalBlock.FACING, direction.getOpposite());
            }

            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                return blockstate;
            }
        }

        return null;
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return canAttach(level, pos, getConnectedDirection(state).getOpposite());
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true); // Break and drop
        }
    }

    private Direction getConnectedDirection(BlockState state) {
        switch (state.getValue(FACE)) {
            case CEILING -> {
                return Direction.DOWN;
            }
            case FLOOR -> {
                return Direction.UP;
            }
            default -> {
                return state.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
            }
        }
    }

    public static boolean canAttach(LevelReader reader, BlockPos pos, Direction direction) {
        BlockPos blockpos = pos.relative(direction);
        return reader.getBlockState(blockpos).isFaceSturdy(reader, blockpos, direction.getOpposite());
    }

    //SIGNAL
    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
        return blockState.getValue(POWERED) && getConnectedDirection(blockState) == side ? 15 : 0;
    }

    //LOGIC
    public void power(boolean value, BlockState state, Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof KeypadBlockEntity){
            level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(value)), 3);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

            if (player == null) return;
            player.playNotifySound(value ? SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public class InteractionEvents extends AbstractKeypadBlock.InteractionEvents {
        @Override
        public ItemInteractionResult activate(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos) {
            if (!level.isClientSide()) {
                BlockState state = be.getBlockState();

                if (state.getValue(POWERED)){
                    power(false, state, level, pos, player);
                    return ItemInteractionResult.SUCCESS;
                }

                level.playSound(null, pos, enableSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                power(true, state, level, pos, player);
                SecureLib.delayTick(20, ()-> {
                    power(false, state, level, pos, player);
                    level.playSound(null, pos, disableSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                });
            }

            return ItemInteractionResult.SUCCESS;
        }
    }
}
