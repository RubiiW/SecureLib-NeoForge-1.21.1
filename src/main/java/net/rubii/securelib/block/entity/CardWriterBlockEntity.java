package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.rubii.securelib.block.custom.CardWriterBlock;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.screen.custom.CardWriterMenu;

import javax.annotation.Nullable;

public class CardWriterBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private String frequency = ""; // DO NOT REMOVE THE = "" OR THE THING EXPLODE
    private Integer clearance = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 20;

    public CardWriterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARD_WRITER_BE.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> CardWriterBlockEntity.this.progress;
                    case 1 -> CardWriterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i){
                    case 0: CardWriterBlockEntity.this.progress = value;
                    case 1: CardWriterBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void drops(){
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++){
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("progress", progress);
        tag.putInt("maxProgress", maxProgress);

        tag.putString("frequency", frequency);
        tag.putInt("clearance", clearance);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");

        frequency = tag.getString("frequency");
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

    public String getFrequency() {
        return frequency == null ? "" : frequency;
    }

    public void setFrequency(String freq) {
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.securelib.card_writer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CardWriterMenu(i, inventory, this, data);
    }

    // CRAFTING

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (hasRecipe()){
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);
            blockState.setValue(CardWriterBlock.CRAFTING, true);

            if (hasCraftingFinished()){
                craftItem();
                resetProgress();
            }
        } else {
            blockState.setValue(CardWriterBlock.CRAFTING, false);
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 20;
    }

    private void craftItem() {
        ItemStack output = inventory.getStackInSlot(INPUT_SLOT).copy();

        Integer hash = frequency.hashCode(); //FREQUENCY COOKER (From raw to final)

        switch (frequency){
            case "":
            case null:
                break;
            default: output.set(ModDataComponents.FREQUENCY, hash); break;
        }

        switch(clearance){
            case 0:
            case null:
                break;
            default: output.set(ModDataComponents.CLEARANCE, clearance); break;
        }

        output.setCount(inventory.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount());

        inventory.extractItem(INPUT_SLOT, 1, false);

        inventory.setStackInSlot(OUTPUT_SLOT, output.copy()) ;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress + 1;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe(){
        ItemStack output = new ItemStack(ModItems.KEYCARD.get(), 1);
        return (inventory.getStackInSlot(INPUT_SLOT).is(ModItems.KEYCARD) || inventory.getStackInSlot(INPUT_SLOT).is(ModItems.READER_EDITOR)) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output) &&
                inventory.getStackInSlot(INPUT_SLOT).get(ModDataComponents.CLEARANCE) == null &&
                inventory.getStackInSlot(INPUT_SLOT).get(ModDataComponents.FREQUENCY) == null &&
                frequency != null && !frequency.isEmpty();
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || inventory.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
        int currentCount = inventory.getStackInSlot(OUTPUT_SLOT).getCount();

        return maxCount >= currentCount + count;
    }
}
