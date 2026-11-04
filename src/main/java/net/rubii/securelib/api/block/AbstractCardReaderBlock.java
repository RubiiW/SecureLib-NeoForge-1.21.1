package net.rubii.securelib.api.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.api.SecureLibUtils;
import net.rubii.securelib.api.block.entity.AbstractCardReaderBlockEntity;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.util.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbstractCardReaderBlock extends BaseEntityBlock {
    public SoundEvent enableSound;
    public SoundEvent disableSound;
    public SoundEvent failSound;
    public boolean informative;

    public SetupEvents setupEvents;
    public InteractionEvents interactionEvents;
    public RemovalEvents removalEvents;

    public AbstractCardReaderBlock(BlockBehaviour.Properties properties, SoundEvent enableSound, SoundEvent disableSound, SoundEvent failSound, boolean informative) {
        super(properties);
        this.enableSound = enableSound;
        this.disableSound = disableSound;
        this.failSound = failSound;
        this.informative = informative;

        this.setupEvents = new SetupEvents();
        this.interactionEvents = new InteractionEvents();
        this.removalEvents = new RemovalEvents();
    }

    public final MapCodec<AbstractCardReaderBlock> CODEC = simpleCodec(properties -> new AbstractCardReaderBlock(properties, enableSound, disableSound, failSound, informative));

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.securelib.card_readers").withStyle(ChatFormatting.GRAY));
    }

    @Override
    @NotNull
    protected RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        SecureLib.LOGGER.error("newBlockEntity() has not been overridden in class extending AbstractCardReaderBlock!");
        return null;
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof AbstractCardReaderBlockEntity){
                level.removeBlockEntity(pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    //DISPATCH
    @Override
    protected float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter getter, @NotNull BlockPos pos) {
        Level level = player.level();

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        AbstractCardReaderBlockEntity blockEntity = (AbstractCardReaderBlockEntity)level.getBlockEntity(pos);

        if (level.isClientSide() || blockEntity == null) return 0;

        if (stack.is(ModItems.READER_EDITOR)){
            return switch (SecureLibUtils.canInteract(blockEntity, stack)) {
                case SUCCESS -> removalEvents.remove(level, player, blockEntity, pos);
                case ITEM_NO_DATA -> removalEvents.onItemNoData(level, player, blockEntity, pos);
                case BLOCK_NO_DATA -> removalEvents.onBlockNoData(level, player, blockEntity, pos);
                case BOTH_NO_DATA -> removalEvents.onBothNoData(level, player, blockEntity, pos);
                case CLEARANCE_FAIL -> removalEvents.onClearanceFail(level, player, blockEntity, pos);
                case FREQUENCY_FAIL -> removalEvents.onFrequencyFail(level, player, blockEntity, pos);
                case COMPLETE_FAIL -> removalEvents.onCompleteFail(level, player, blockEntity, pos);
            };
        } else if (stack.is(ModItems.TRIREADER_EDITOR)) {
            return switch (SecureLibUtils.canTriInteract(blockEntity, stack)) {
                case SUCCESS -> removalEvents.remove(level, player, blockEntity, pos);
                case ITEM_NO_DATA -> removalEvents.onItemNoData(level, player, blockEntity, pos);
                case BLOCK_NO_DATA -> removalEvents.onBlockNoData(level, player, blockEntity, pos);
                case BOTH_NO_DATA -> removalEvents.onBothNoData(level, player, blockEntity, pos);
                case CLEARANCE_FAIL -> removalEvents.onClearancesFail(level, player, blockEntity, pos);
                case FREQUENCY_FAIL -> removalEvents.onFrequencyFail(level, player, blockEntity, pos);
                case COMPLETE_FAIL -> removalEvents.onCompleteFail(level, player, blockEntity, pos);
            };
        } else if (SecureLibUtils.hasNoData(blockEntity)) {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.destroy_requirement"), true);
            return 0;
        } else {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.destroy_process"), true);
            return 0;
        }
    }

    @Override
    @NotNull
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                              @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AbstractCardReaderBlockEntity blockEntity) {
            if (SecureLibUtils.isSkeleton(stack)) {
                return interactionEvents.activate(level, player, blockEntity, pos);
            }else if (player.getItemInHand(hand).is(ModItems.READER_EDITOR)){
                return readerEditor(blockEntity, stack, pos, player, level);
            }else if (player.getItemInHand(hand).is(ModItems.TRIREADER_EDITOR)){
                return trireaderEditor(blockEntity, stack, pos, player, level);
            }else if (player.getItemInHand(hand).is(ModTags.Items.KEYCARDS)){
                return keycard(blockEntity, stack, state, level, pos, player);
            }else if (player.getItemInHand(hand).is(ModTags.Items.TRICARDS)){
                return tricard(blockEntity, stack, state, level, pos, player);
            }else if (SecureLibUtils.hasNoData(blockEntity)) {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.missing_data"), true);
            }else {
                player.displayClientMessage(Component.translatable("block.securelib.card_reader.need_keycard"), true);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        } else {
            SecureLib.LOGGER.error("CardReaderBlock is not a AbstractCardReaderBlockEntity at pos: " + pos);
            return ItemInteractionResult.FAIL;
        }
    }

    //INTERACTION
    public ItemInteractionResult readerEditor(BlockEntity blockEntity, ItemStack stack, BlockPos pos, Player player, Level level) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (blockEntity instanceof AbstractCardReaderBlockEntity be){
            if (!SecureLibUtils.hasNoData(be)) return setupEvents.alreadyConfigured(level, player, be, pos);
            if (SecureLibUtils.hasNoData(stack)) return setupEvents.onItemNoData(level, player, be, pos);

            return switch (SecureLibUtils.canInteract(be, stack)){
                case SUCCESS -> setupEvents.alreadyConfigured(level, player, be, pos);
                case BLOCK_NO_DATA -> setupEvents.setupNormal(level, player, be, pos);
                case ITEM_NO_DATA -> setupEvents.onItemNoData(level, player, be, pos);
                case BOTH_NO_DATA -> setupEvents.onBothNoData(level, player, be, pos);
                default -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            };
        }

        return ItemInteractionResult.SUCCESS;
    }

    public ItemInteractionResult trireaderEditor(BlockEntity blockEntity, ItemStack stack, BlockPos pos, Player player, Level level) {
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (blockEntity instanceof AbstractCardReaderBlockEntity be){
            if (!SecureLibUtils.hasNoData(be)) return setupEvents.alreadyConfigured(level, player, be, pos);
            if (SecureLibUtils.hasNoData(stack)) return setupEvents.onItemNoData(level, player, be, pos);

            return switch (SecureLibUtils.canInteract(be, stack)){
                case SUCCESS -> setupEvents.alreadyConfigured(level, player, be, pos);
                case BLOCK_NO_DATA -> setupEvents.setupTri(level, player, be, pos);
                case ITEM_NO_DATA -> setupEvents.onItemNoData(level, player, be, pos);
                case BOTH_NO_DATA -> setupEvents.onBothNoData(level, player, be, pos);
                default -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            };
        }

        return ItemInteractionResult.SUCCESS;
    }

    public ItemInteractionResult keycard(BlockEntity blockEntity, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player){
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (blockEntity instanceof AbstractCardReaderBlockEntity be) {
            if (SecureLibUtils.isSkeleton(stack)) {
                interactionEvents.activate(level, player, be, pos);
                return ItemInteractionResult.SUCCESS;
            }

            if(SecureLibUtils.isTriData(blockEntity)) return interactionEvents.onIncompatibleCard(level, player, be, pos);

            return switch (SecureLibUtils.canInteract(be, stack)) {
                case SUCCESS -> interactionEvents.activate(level, player, be, pos);
                case CLEARANCE_FAIL -> interactionEvents.onClearanceFail(level, player, be, pos);
                case FREQUENCY_FAIL -> interactionEvents.onFrequencyFail(level, player, be, pos);
                case COMPLETE_FAIL -> interactionEvents.onCompleteFail(level, player, be, pos);
                case BLOCK_NO_DATA -> interactionEvents.onBlockNoData(level, player, be, pos);
                case ITEM_NO_DATA -> interactionEvents.onItemNoData(level, player, be, pos);
                case BOTH_NO_DATA -> interactionEvents.onBothNoData(level, player, be, pos);
            };
        }

        return  ItemInteractionResult.SUCCESS;
    }

    public ItemInteractionResult tricard(BlockEntity blockEntity, ItemStack stack, BlockState state, Level level, BlockPos pos, Player player){
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;

        if (blockEntity instanceof AbstractCardReaderBlockEntity be) {
            if (SecureLibUtils.isSkeleton(stack)) {
                interactionEvents.activate(level, player, be, pos);
                return ItemInteractionResult.SUCCESS;
            }

            if(!SecureLibUtils.isTriData(blockEntity)) return interactionEvents.onIncompatibleCard(level, player, be, pos);

            return switch (SecureLibUtils.canTriInteract(be, stack)) {
                case SUCCESS -> interactionEvents.activate(level, player, be, pos);
                case CLEARANCE_FAIL -> interactionEvents.onClearancesFail(level, player, be, pos);
                case FREQUENCY_FAIL -> interactionEvents.onFrequencyFail(level, player, be, pos);
                case COMPLETE_FAIL -> interactionEvents.onCompleteFail(level, player, be, pos);
                case BLOCK_NO_DATA -> interactionEvents.onBlockNoData(level, player, be, pos);
                case ITEM_NO_DATA -> interactionEvents.onItemNoData(level, player, be, pos);
                case BOTH_NO_DATA -> interactionEvents.onBothNoData(level, player, be, pos);
            };
        }

        return ItemInteractionResult.SUCCESS;
    }

    //EVENTS
    public class SetupEvents {
        public ItemInteractionResult alreadyConfigured(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos) {
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.already_configured"), true);
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onItemNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(
                    Component.translatable("item.securelib.reader_editor.missing_data"),
                    true
            );
            return  ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onBothNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            return onItemNoData(level, player, be, pos);
        }

        public ItemInteractionResult setupNormal(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            ItemStack stack = player.getMainHandItem();

            if (!SecureLibUtils.hasNoClearance(stack) && !SecureLibUtils.hasNoFrequency(stack)) {
                player.displayClientMessage(
                        Component.translatable("item.securelib.reader_editor.missing_data"),
                        true
                );
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            be.setClearance(stack.get(ModDataComponents.CLEARANCE));
            be.setFrequency(stack.get(ModDataComponents.FREQUENCY));
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult setupTri(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            ItemStack stack = player.getMainHandItem();

            if (SecureLibUtils.hasNoData(stack)) {
                player.displayClientMessage(
                        Component.translatable("item.securelib.reader_editor.missing_data"),
                        true
                );
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            be.setClearances(
                    stack.get(ModDataComponents.CLEARANCE_L),
                    stack.get(ModDataComponents.CLEARANCE_M),
                    stack.get(ModDataComponents.CLEARANCE_R)
            );
            be.setFrequency(stack.get(ModDataComponents.FREQUENCY));
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }
    }

    public class InteractionEvents {
        public ItemInteractionResult onClearanceFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            player.displayClientMessage(
                    informative ? Component.literal(
                            Component.translatable("item.securelib.data_reciever.mismatch_clearance_informative").getString()
                                    + be.getClearance())
                            : Component.translatable("item.securelib.data_reciever.mismatch_clearance"),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onClearancesFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            Component informativeComponent = Component.literal(
                    Component.translatable("item.securelib.data_reciever.mismatch_clearances_informative").getString() +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.l").getString() + be.getLClearance() + "," +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.m").getString() + be.getMClearance() + "," +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.r").getString() + be.getRClearance() + ","
            );

            player.displayClientMessage(
                    informative ? informativeComponent : Component.translatable("item.securelib.data_reciever.mismatch_clearances"),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onFrequencyFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            player.displayClientMessage(
                    informative ? Component.literal(
                            Component.translatable("item.securelib.data_reciever.mismatch_frequency_informative").getString()
                                    + be.getFrequency())
                            : Component.translatable("item.securelib.data_reciever.mismatch_frequency"),
                    true
            );

            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onCompleteFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            return onFrequencyFail(level, player, be, pos);
        }

        public ItemInteractionResult onBlockNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.missing_data"), true);
            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onItemNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(Component.translatable("item.securelib.card.missing_data"), true);
            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult onBothNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            return onBlockNoData(level, player, be, pos);
        }

        public ItemInteractionResult onIncompatibleCard(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(Component.translatable("item.securelib.card.type_mismatch"), true);
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        public ItemInteractionResult activate(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos) {
            return ItemInteractionResult.SUCCESS;
        }
    }

    public class RemovalEvents {
        public float onClearanceFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            player.displayClientMessage(
                    informative ? Component.literal(
                            Component.translatable("item.securelib.data_reciever.mismatch_clearance_informative").getString()
                                    + be.getClearance())
                            : Component.translatable("item.securelib.data_reciever.mismatch_clearance"),
                    true
            );

            return 0.0F;
        }

        public float onClearancesFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            Component informativeComponent = Component.literal(
                    Component.translatable("item.securelib.data_reciever.mismatch_clearances_informative").getString() +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.l").getString() + be.getLClearance() + "," +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.m").getString() + be.getMClearance() + "," +
                            Component.translatable("tooltip.securelib.tridata_receiver.clearances.r").getString() + be.getRClearance() + ","
            );

            player.displayClientMessage(
                    informative ? informativeComponent : Component.translatable("item.securelib.data_reciever.mismatch_clearances"),
                    true
            );

            return 0.0F;
        }

        public float onFrequencyFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.playSound(null, pos, failSound, SoundSource.BLOCKS, 1.0F, 1.0F);

            player.displayClientMessage(
                    informative ? Component.literal(
                            Component.translatable("item.securelib.data_reciever.mismatch_frequency_informative").getString()
                                    + be.getFrequency())
                            : Component.translatable("item.securelib.data_reciever.mismatch_frequency"),
                    true
            );

            return 0.0F;
        }

        public float onCompleteFail(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            return onFrequencyFail(level, player, be, pos);
        }

        public float onBlockNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(Component.translatable("block.securelib.card_reader.destroy_requirement"), true);
            return 0.0F;
        }

        public float onItemNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            player.displayClientMessage(Component.translatable("item.securelib.reader_editor.missing_data"), true);
            return 0.0F;
        }

        public float onBothNoData(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            return onBlockNoData(level, player, be, pos);
        }

        public float remove(Level level, Player player, AbstractCardReaderBlockEntity be, BlockPos pos){
            level.destroyBlock(pos, true);
            level.updateNeighbourForOutputSignal(pos, be.getBlockState().getBlock());
            return 0.0F;
        }
    }
}
