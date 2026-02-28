package net.rubii.securelib.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.screen.custom.CardPrinterMenu;
import net.rubii.securelib.util.ModTags;

import javax.annotation.Nullable;

public class CardPrinterBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(3){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private static final int INK_SLOT = 0;
    private static final int CARD_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;
    private String color = ""; // DO NOT REMOVE THE = "" OR THE THING EXPLODE
    private String name = "";

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;

    public CardPrinterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARD_PRINTER_BE.get(), pos, state);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> CardPrinterBlockEntity.this.progress;
                    case 1 -> CardPrinterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i){
                    case 0: CardPrinterBlockEntity.this.progress = value;
                    case 1: CardPrinterBlockEntity.this.maxProgress = value;
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
        tag.putString("color", color);
        tag.putString("name", name);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
        maxProgress = tag.getInt("maxProgress");
        color = tag.getString("color");
        name = tag.getString("name");
    }

    public String getColor() {
        return color == null ? "" : color;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setColor(String color) {
        this.color = color;
        setChanged();
        if (!level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void setName(String name) {
        this.name = name;
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
        return Component.translatable("block.securelib.card_printer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CardPrinterMenu(i, inventory, this, data);
    }

    // CRAFTING

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (hasRecipe()){
            increaseCraftingProgress();
            setChanged(level, blockPos, blockState);

            if (hasCraftingFinished()){
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 200;
    }

    private void craftItem() {
        ItemStack output = inventory.getStackInSlot(CARD_SLOT).copy();

        switch (color){
            case "": output.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt("FFFFFF", 16), false)); break;
            case null: output.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt("FFFFFF", 16), false)); break;
            default: output.set(DataComponents.DYED_COLOR, new DyedItemColor(Integer.parseInt(color, 16), false));
        }

        switch(name){
            case "": output.set(DataComponents.ITEM_NAME, Component.translatable("item.securelib.keycard")); break;
            case null: output.set(DataComponents.ITEM_NAME, Component.translatable("item.securelib.keycard")); break;
            default: output.set(DataComponents.ITEM_NAME, Component.literal(name));
        }

        output.setCount(inventory.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount());

        inventory.extractItem(INK_SLOT, 1, false);
        inventory.extractItem(CARD_SLOT, 1, false);

        inventory.setStackInSlot(OUTPUT_SLOT, output.copy()) ;
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress + 5;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe(){
        ItemStack output = inventory.getStackInSlot(CARD_SLOT).copy();
        return inventory.getStackInSlot(INK_SLOT).is(Items.INK_SAC) &&
                inventory.getStackInSlot(CARD_SLOT).is(ModTags.Items.KEYCARDS) &&
                canInsertAmountIntoOutputSlot(output.getCount()) &&
                canInsertItemIntoOutputSlot(output);
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
