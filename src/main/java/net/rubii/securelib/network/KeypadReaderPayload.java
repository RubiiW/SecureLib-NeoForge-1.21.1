package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record KeypadReaderPayload(BlockPos blockPos, Integer frequency, Integer clearance) implements CustomPacketPayload {

    public static final Type<KeypadReaderPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_keypad_reader_data"));

    public static final StreamCodec<FriendlyByteBuf, KeypadReaderPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, KeypadReaderPayload::blockPos,
                    ByteBufCodecs.VAR_INT, KeypadReaderPayload::frequency,
                    ByteBufCodecs.VAR_INT, KeypadReaderPayload::clearance,
                    KeypadReaderPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
