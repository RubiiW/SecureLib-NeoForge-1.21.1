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
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.api.SecureLibUtils;
import net.rubii.securelib.block.custom.CardWriterAltBlock;
import net.rubii.securelib.block.custom.CardWriterBlock;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.screen.custom.CardWriterAltMenu;
import net.rubii.securelib.screen.custom.CardWriterMenu;
import net.rubii.securelib.util.ModTags;

import javax.annotation.Nullable;

public class CardWriterAltBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private String frequency = ""; // DO NOT REMOVE THE = "" OR THE THING EXPLODE
    private Integer lClearance = 0;
    private Integer mClearance = 0;
    private Integer rClearance = 0;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 20;

    public CardWriterAltBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARD_WRITER_ALT_BE.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> CardWriterAltBlockEntity.this.progress;
                    case 1 -> CardWriterAltBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i){
                    case 0: CardWriterAltBlockEntity.this.progress = value;
                    case 1: CardWriterAltBlockEntity.this.maxProgress = value;
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
        tag.putInt("lClearance", lClearance);
        tag.putInt("mClearance", mClearance);
        tag.putInt("rClearance", rClearance);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");

        frequency = tag.getString("frequency");
        lClearance = tag.getInt("lClearance");
        mClearance = tag.getInt("mClearance");
        rClearance = tag.getInt("rClearance");
    }

    public Integer getLClearance() {
        return lClearance == null ? 0 : lClearance;
    }

    public Integer getMClearance() {
        return mClearance == null ? 0 : mClearance;
    }

    public Integer getRClearance() {
        return rClearance == null ? 0 : rClearance;
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
        return Component.translatable("block.securelib.tricard_writer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CardWriterAltMenu(i, inventory, this, data);
    }

    // CRAFTING

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (hasRecipe()){
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);
            blockState.setValue(CardWriterAltBlock.CRAFTING, true);

            if (hasCraftingFinished()){
                craftItem();
                resetProgress();
            }
        } else {
            blockState.setValue(CardWriterAltBlock.CRAFTING, false);
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

        if (lClearance != null && mClearance != null && rClearance != null) {
            output.set(ModDataComponents.CLEARANCE_L, lClearance);
            output.set(ModDataComponents.CLEARANCE_M, mClearance);
            output.set(ModDataComponents.CLEARANCE_R, rClearance);
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
        ItemStack output = new ItemStack(inventory.getStackInSlot(INPUT_SLOT).getItem(), 1);
        return (inventory.getStackInSlot(INPUT_SLOT).is(ModTags.Items.TRIDATA_RECEIVERS)) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output) &&
                SecureLibUtils.hasNoData(inventory.getStackInSlot(INPUT_SLOT)) &&
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
