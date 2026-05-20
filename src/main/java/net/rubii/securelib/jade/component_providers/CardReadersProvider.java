package net.rubii.securelib.jade.component_providers;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.custom.CardReaderBlock;
import net.rubii.securelib.block.custom.KeypadReaderBlock;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.block.entity.KeypadReaderBlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public enum CardReadersProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();

        if (data.contains("Informative")) {
            if (!data.getBoolean("Informative")) return;
        }

        if (data.contains("Clearance")){
            String string = Component.translatable("tooltip.securelib.data_receiver.clearance").getString();
            tooltip.add(Component.literal(string + " " + data.getInt("Clearance")));
        }
        if (data.contains("LClearance") || data.contains("MClearance") || data.contains("RClearance")){
            String lPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.l").getString();
            String mPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.m").getString();
            String rPrefix = Component.translatable("tooltip.securelib.tridata_receiver.clearances.r").getString();
            int lValue = data.getInt("LClearance");
            int mValue = data.getInt("MClearance");
            int rValue = data.getInt("RClearance");

            Component component = Component.literal(
                    lPrefix + lValue + ", "  + mPrefix + mValue + ", "  + rPrefix + rValue
            ).withStyle(ChatFormatting.GRAY);

            tooltip.add(Component.translatable("tooltip.securelib.tridata_receiver.clearances"));
            tooltip.add(component);
        }
        if (accessor.showDetails()){
            if (data.contains("Frequency")){
                tooltip.add(Component.translatable("tooltip.securelib.data_receiver.frequency"));
                tooltip.add(Component.literal(" " + data.getInt("Frequency")));
            }
        }

    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlock() instanceof CardReaderBlock block) {
            data.putBoolean("Informative", block.informative);
        } else if (accessor.getBlock() instanceof KeypadReaderBlock block) {
            data.putBoolean("Informative", block.informative);
        }

        if (accessor.getBlockEntity() instanceof CardReaderBlockEntity be) {
            if (be.getClearance() != 0){
                data.putInt("Clearance", be.getClearance());
            }
            if (be.getLClearance() != -1){
                data.putInt("LClearance", be.getLClearance());
            }
            if (be.getMClearance() != -1){
                data.putInt("MClearance", be.getMClearance());
            }
            if (be.getRClearance() != -1){
                data.putInt("RClearance", be.getRClearance());
            }
            if (be.getFrequency() != 0){
                data.putInt("Frequency", be.getFrequency());
            }
        } else  if (accessor.getBlockEntity() instanceof KeypadReaderBlockEntity be) {
            if (be.getClearance() != 0){
                data.putInt("Clearance", be.getClearance());
            }
            if (be.getLClearance() != -1){
                data.putInt("LClearance", be.getLClearance());
            }
            if (be.getMClearance() != -1){
                data.putInt("MClearance", be.getMClearance());
            }
            if (be.getRClearance() != -1){
                data.putInt("RClearance", be.getRClearance());
            }
            if (be.getFrequency() != 0){
                data.putInt("Frequency", be.getFrequency());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "card_readers");
    }
}