package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record CardWriterAltPayload(BlockPos blockPos, String frequency, Integer lClearance, Integer mClearance, Integer rClearance) implements CustomPacketPayload {

    public static final Type<CardWriterAltPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_tricard_data"));

    public static final StreamCodec<FriendlyByteBuf, CardWriterAltPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CardWriterAltPayload::blockPos,
                    ByteBufCodecs.STRING_UTF8, CardWriterAltPayload::frequency,
                    ByteBufCodecs.VAR_INT, CardWriterAltPayload::lClearance,
                    ByteBufCodecs.VAR_INT, CardWriterAltPayload::mClearance,
                    ByteBufCodecs.VAR_INT, CardWriterAltPayload::rClearance,
                    CardWriterAltPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
