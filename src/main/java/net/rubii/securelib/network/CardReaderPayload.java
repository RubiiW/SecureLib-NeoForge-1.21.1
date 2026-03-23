package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record CardReaderPayload(BlockPos blockPos, Integer frequency, Integer clearance) implements CustomPacketPayload {

    public static final Type<CardReaderPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_reader_data"));

    public static final StreamCodec<FriendlyByteBuf, CardReaderPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardReaderPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CardReaderPayload::frequency,
                    ByteBufCodecs.VAR_INT, CardReaderPayload::clearance,
                    CardReaderPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
