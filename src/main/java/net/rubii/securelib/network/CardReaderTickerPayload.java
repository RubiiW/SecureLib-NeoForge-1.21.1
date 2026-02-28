package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CardReaderTickerPayload(BlockPos blockPos, Integer timer) implements CustomPacketPayload {

    public static final Type<CardReaderTickerPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("securelib", "set_reader_timer"));

    public static final StreamCodec<FriendlyByteBuf, CardReaderTickerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardReaderTickerPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CardReaderTickerPayload::timer,
                    CardReaderTickerPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
