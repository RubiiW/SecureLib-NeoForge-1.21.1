package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record CardReaderTriPayload(BlockPos blockPos, Integer frequency, Integer lClearance, Integer mClearance, Integer rClearance) implements CustomPacketPayload {

    public static final Type<CardReaderTriPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_reader_tridata"));

    public static final StreamCodec<FriendlyByteBuf, CardReaderTriPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardReaderTriPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CardReaderTriPayload::frequency,
                    ByteBufCodecs.VAR_INT, CardReaderTriPayload::lClearance,
                    ByteBufCodecs.VAR_INT, CardReaderTriPayload::mClearance,
                    ByteBufCodecs.VAR_INT, CardReaderTriPayload::rClearance,
                    CardReaderTriPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
