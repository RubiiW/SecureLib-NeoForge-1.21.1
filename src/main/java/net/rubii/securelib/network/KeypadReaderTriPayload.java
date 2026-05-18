package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record KeypadReaderTriPayload(BlockPos blockPos, Integer frequency, Integer lClearance, Integer mClearance, Integer rClearance) implements CustomPacketPayload {

    public static final Type<KeypadReaderTriPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_keypad_reader_tridata"));

    public static final StreamCodec<FriendlyByteBuf, KeypadReaderTriPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, KeypadReaderTriPayload::blockPos,
                    ByteBufCodecs.VAR_INT, KeypadReaderTriPayload::frequency,
                    ByteBufCodecs.VAR_INT, KeypadReaderTriPayload::lClearance,
                    ByteBufCodecs.VAR_INT, KeypadReaderTriPayload::mClearance,
                    ByteBufCodecs.VAR_INT, KeypadReaderTriPayload::rClearance,
                    KeypadReaderTriPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
