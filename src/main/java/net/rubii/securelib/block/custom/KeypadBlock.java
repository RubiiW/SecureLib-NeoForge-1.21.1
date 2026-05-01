package net.rubii.securelib.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.entity.KeypadBlockEntity;
import net.rubii.securelib.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACE;

public class KeypadBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public static final MapCodec<KeypadBlock> CODEC = simpleCodec(KeypadBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public KeypadBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.securelib.keypads").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        Direction direction = state.getValue(FaceAttachedHorizontalDirectionalBlock.FACING);
        switch (state.getValue(FACE)) {
            case FLOOR:
                return switch (direction) {
                    case EAST, WEST -> Block.box(3.0, 0.0, 5.0, 13.0, 2.0, 11.0);
                    case NORTH, SOUTH, UP, DOWN -> Block.box(5.0, 0.0, 3.0, 11.0, 2.0, 13.0);
                };
            case WALL:
                return switch (direction) {
                    case EAST -> Block.box(0.0, 3.0, 5.0, 2.0, 13.0, 11.0);
                    case WEST -> Block.box(14.0, 3.0, 5.0, 16.0, 13.0, 11.0);
                    case SOUTH -> Block.box(5.0, 3.0, 0.0, 11.0, 13.0, 2.0);
                    case NORTH, UP, DOWN -> Block.box(5.0, 3.0, 14.0, 11.0, 13.0, 16.0);
                };
            case CEILING:
                return switch (direction) {
                    case EAST, WEST -> Block.box(3.0, 14.0, 5.0, 13.0, 16.0, 11.0);
                    case NORTH, SOUTH, UP, DOWN -> Block.box(5.0, 14.0, 3.0, 11.0, 16.0, 13.0);
                };
            default:
                return SHAPE;
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FaceAttachedHorizontalDirectionalBlock.FACING);
        builder.add(FaceAttachedHorizontalDirectionalBlock.FACE);
        builder.add(POWERED);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canAttach(level, pos, getConnectedDirection(state).getOpposite());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true); // Break and drop
        }
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

    // BLOCK ENTITY STUFF

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new KeypadBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof KeypadBlockEntity) {
                level.removeBlockEntity(pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        Level level = player.level();
        KeypadBlockEntity blockEntity = (KeypadBlockEntity)level.getBlockEntity(pos);

        if (level.isClientSide()) return 0;

        if (Objects.equals(blockEntity.getCode(), "")) {
            player.displayClientMessage(Component.translatable("block.securelib.keypad.destroy_requirement"), true);
            return 0;
        } else if (player.isCrouching()) {
            SecureLib.LOGGER.info("open");
            open(level, pos, player);
            blockEntity.setRemoval(true);
            return 0;
        } else {
            player.displayClientMessage(Component.translatable("block.securelib.keypad.destroy_process"), true);
            return 0;
        }
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) && getConnectedDirection(blockState) == side ? 15 : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(ModTags.Items.KEYPAD_BYPASS)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return open(level, pos, player) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return open(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public boolean open(Level level, BlockPos pos, Player player) {
        if(level.getBlockEntity(pos) instanceof KeypadBlockEntity blockEntity){
            if (!level.isClientSide){
                player.openMenu(new SimpleMenuProvider(blockEntity, Component.translatable("block.securelib.keypad")), pos);
                return true;
            }
        }

        return false;
    }

    public void activate(Level level, BlockState state, Player player, BlockPos pos) {
        if (!level.isClientSide()) {
            if (state.getValue(POWERED)){
                power(false, state, level, pos, player);
                return;
            }

            player.playNotifySound(SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            power(true, state, level, pos, player);
            SecureLib.delayTick(20, ()-> {
                power(false, state, level, pos, player);
                player.playNotifySound(SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            });
        }
    }

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
}
