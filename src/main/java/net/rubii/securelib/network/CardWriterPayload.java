package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record CardWriterPayload(BlockPos blockPos, String frequency, Integer clearance) implements CustomPacketPayload {

    public static final Type<CardWriterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_card_data"));

    public static final StreamCodec<FriendlyByteBuf, CardWriterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardWriterPayload::blockPos,
                    ByteBufCodecs.STRING_UTF8, CardWriterPayload::frequency,
                    ByteBufCodecs.VAR_INT, CardWriterPayload::clearance,
                    CardWriterPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
