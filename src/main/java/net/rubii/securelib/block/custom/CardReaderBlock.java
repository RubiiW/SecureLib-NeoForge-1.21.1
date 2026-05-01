package net.rubii.securelib.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
import net.rubii.securelib.api.SecureLibUtils;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.network.CardReaderPayload;
import net.rubii.securelib.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACE;

public class CardReaderBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static String tooltip;

    public static final MapCodec<CardReaderBlock> CODEC = simpleCodec(CardReaderBlock::new);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CardReaderBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.securelib.card_readers").withStyle(ChatFormatting.GRAY));
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
        return new CardReaderBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof CardReaderBlockEntity){
                level.removeBlockEntity(pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter getter, BlockPos pos) {
        Level level = player.level();
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        CardReaderBlockEntity blockEntity = (CardReaderBlockEntity)level.getBlockEntity(pos);

        if (level.isClientSide()) return 0;

        if(stack.is(ModItems.READER_EDITOR)){
            if (
            blockEntity.getClearance() <= stack.get(ModDataComponents.CLEARANCE.get()) &&
            Objects.equals(blockEntity.getFrequency(), stack.get(ModDataComponents.FREQUENCY.get()))
            ){
                level.destroyBlock(pos, true);
                level.updateNeighbourForOutputSignal(pos, this);
            } else {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.data_mismatch"), true);
            }
            return 0;
        } else if (blockEntity.getClearance() == 0 && blockEntity.getFrequency() == 0) {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.destroy_requirement"), true);
            return 0;
        } else {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.destroy_process"), true);
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
        if (level.getBlockEntity(pos) instanceof CardReaderBlockEntity) {
            CardReaderBlockEntity blockEntity = (CardReaderBlockEntity) level.getBlockEntity(pos);

            if (player.getItemInHand(hand).is(ModItems.READER_EDITOR)){
                return readerEditor(blockEntity, stack, pos, player);
            }else if (player.getItemInHand(hand).is(ModTags.Items.KEYCARDS)){
                return keycard(blockEntity, stack, state, level, pos, player);
            }else {
                if (blockEntity.getClearance() == 0 && blockEntity.getFrequency() == 0){
                    player.displayClientMessage(Component.translatable("block.securelib.card_reader.missing_data"), true);
                } else {
                    if (player.getItemInHand(hand).is(ModTags.Items.SKELETON_KEYCARDS)) {
                        activate(level, state, player, pos);
                    }else{
                        player.displayClientMessage(Component.translatable("block.securelib.card_reader.need_keycard"), true);
                    }
                }
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

        } else {
            SecureLib.LOGGER.error("CardReaderBlock is not a CardReaderBlockEntity at pos: " + pos);
            return ItemInteractionResult.FAIL;
        }
    }

    private ItemInteractionResult readerEditor(BlockEntity blockEntity, ItemStack stack, BlockPos pos, Player player){

        if (SecureLibUtils.hasNoData(stack)) {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.editor_missing_data"), true);
            return ItemInteractionResult.SUCCESS;
        }

        if (blockEntity instanceof CardReaderBlockEntity be){
            if (SecureLibUtils.canInteract(be, stack)) {

                player.displayClientMessage(Component.translatable("block.securelib.card_reader.removal"), true);
                return ItemInteractionResult.SUCCESS;

            } else if (SecureLibUtils.hasNoData(blockEntity)) {

                Minecraft.getInstance().getConnection().send(
                        new CardReaderPayload(pos, stack.get(ModDataComponents.FREQUENCY.get()), stack.get(ModDataComponents.CLEARANCE.get()))
                );
                player.playNotifySound(SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
                return ItemInteractionResult.SUCCESS;

            } else {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.already_configured"), true);

                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    private ItemInteractionResult keycard(BlockEntity blockEntity, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player){

        if (SecureLibUtils.hasNoData(stack) && !SecureLibUtils.isSkeleton(stack)) {
            player.displayClientMessage(Component.translatable("item.securelib.keycard.missing_data"), true);
            return ItemInteractionResult.SUCCESS;
        }

        if (blockEntity instanceof CardReaderBlockEntity be) {
            if (SecureLibUtils.canInteract(be, stack) || SecureLibUtils.isSkeleton(stack)) {
                activate(level, state, player, pos);
            } else if (SecureLibUtils.hasNoData(blockEntity)) {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.missing_data"), true);
            } else {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.data_mismatch"), true);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return  ItemInteractionResult.SUCCESS;
    }

    public void activate(Level level, BlockState state, Player player, BlockPos pos) {
        if (!level.isClientSide()) {
            if (state.getValue(POWERED)){
                power(false, state, level, pos, player);
                return;
            }

            power(true, state, level, pos, player);
            SecureLib.delayTick(20, ()-> {
                power(false, state, level, pos, player);
            });
        }
    }

    public void power(boolean value, BlockState state, Level level, BlockPos pos, Player player) {
        level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(value)), 3);
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
        level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

        if (player == null) return;
        player.playNotifySound(value ? SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
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
