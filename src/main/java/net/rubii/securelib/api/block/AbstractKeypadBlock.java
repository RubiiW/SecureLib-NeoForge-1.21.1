package net.rubii.securelib.api.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.rubii.securelib.api.block.entity.AbstractKeypadBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AbstractKeypadBlock extends BaseEntityBlock {
    public SoundEvent enableSound;
    public SoundEvent disableSound;

    public SetupEvents setupEvents;
    public InteractionEvents interactionEvents;
    public RemovalEvents removalEvents;

    public AbstractKeypadBlock(BlockBehaviour.Properties properties, SoundEvent enableSound, SoundEvent disableSound) {
        super(properties);
        this.enableSound = enableSound;
        this.disableSound = disableSound;

        this.setupEvents = new SetupEvents();
        this.interactionEvents = new InteractionEvents();
        this.removalEvents = new RemovalEvents();
    }

    public final MapCodec<AbstractKeypadBlock> CODEC = simpleCodec(properties -> new AbstractKeypadBlock(properties, enableSound, disableSound));

    @Override
    @NotNull
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        components.add(Component.translatable("tooltip.securelib.keypads").withStyle(ChatFormatting.GRAY));
    }

    @Override
    @NotNull
    protected RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        SecureLib.LOGGER.error("newBlockEntity() has not been overridden in class extending AbstractKeypadReaderBlock!");
        return null;
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof AbstractKeypadBlockEntity) {
                level.removeBlockEntity(pos);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    //DISPATCH
    @Override
    protected float getDestroyProgress(@NotNull BlockState state, Player player, @NotNull BlockGetter getter, @NotNull BlockPos pos) {
        Level level = player.level();
        AbstractKeypadBlockEntity blockEntity = (AbstractKeypadBlockEntity)level.getBlockEntity(pos);

        if (level.isClientSide() || blockEntity == null) return 0;

        if (Objects.equals(blockEntity.getCode(), "")) {
            player.displayClientMessage(Component.translatable("block.securelib.keypad.destroy_requirement"), true);
            return 0;
        } else if (player.isCrouching()) {
            interactionEvents.open(level, player, blockEntity, pos);
            blockEntity.setRemoval(true);
            return 0;
        } else {
            player.displayClientMessage(Component.translatable("block.securelib.keypad.destroy_process"), true);
            return 0;
        }
    }

    @Override
    @NotNull
    protected InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                               @NotNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AbstractKeypadBlockEntity be) return interactionEvents.open(level, player, be, pos);
        return InteractionResult.PASS;
    }

    //EVENTS
    public class SetupEvents {
        public void setupCode(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos, String code) {
            be.setCode(code);
            be.setChanged();
            player.displayClientMessage(Component.translatable("block.securelib.keypad.code_set"), true);
        }
    }

    public class InteractionEvents {
        public void onCodeFail(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos){
        }

        public ItemInteractionResult activate(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos) {
            return ItemInteractionResult.SUCCESS;
        }

        public InteractionResult open(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos) {
            if(level.getBlockEntity(pos) instanceof AbstractKeypadBlockEntity blockEntity){
                if (!level.isClientSide){
                    player.openMenu(new SimpleMenuProvider(blockEntity, Component.translatable("block.securelib.keypad_reader")), pos);
                }
            }

            return InteractionResult.SUCCESS;
        }

        public void input(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos, String input) {
            be.setInput(input);
            be.inputUpdated(player);
            be.setChanged();
        }
    }

    public class RemovalEvents {
        public void cancel(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos){
            be.setRemoval(false);
            level.updateNeighbourForOutputSignal(pos, be.getBlockState().getBlock());
        }

        public void remove(Level level, Player player, AbstractKeypadBlockEntity be, BlockPos pos){
            level.destroyBlock(pos, true);
            level.updateNeighbourForOutputSignal(pos, be.getBlockState().getBlock());
        }
    }
}
