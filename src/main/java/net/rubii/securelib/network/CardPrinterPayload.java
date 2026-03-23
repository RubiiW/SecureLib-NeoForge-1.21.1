package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CardPrinterPayload(BlockPos blockPos, String name, String color) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CardPrinterPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_printer_data"));

    public static final StreamCodec<FriendlyByteBuf, CardPrinterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardPrinterPayload::blockPos,
                    ByteBufCodecs.STRING_UTF8, CardPrinterPayload::name,
                    ByteBufCodecs.STRING_UTF8, CardPrinterPayload::color,
                    CardPrinterPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
