package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record KeypadReaderPayloadCode(BlockPos blockPos, String code) implements CustomPacketPayload {

    public static final Type<KeypadReaderPayloadCode> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_keypad_reader_code"));

    public static final StreamCodec<FriendlyByteBuf, KeypadReaderPayloadCode> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, KeypadReaderPayloadCode::blockPos,
                    ByteBufCodecs.STRING_UTF8, KeypadReaderPayloadCode::code,
                    KeypadReaderPayloadCode::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
