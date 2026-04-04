package net.rubii.securelib.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.rubii.securelib.SecureLib;

public record KeypadReaderPayloadInput(BlockPos blockPos, String input, String uuid) implements CustomPacketPayload {

    public static final Type<KeypadReaderPayloadInput> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "set_keypad_reader_input"));

    public static final StreamCodec<FriendlyByteBuf, KeypadReaderPayloadInput> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, KeypadReaderPayloadInput::blockPos,
                    ByteBufCodecs.STRING_UTF8, KeypadReaderPayloadInput::input,
                    ByteBufCodecs.STRING_UTF8, KeypadReaderPayloadInput::uuid,
                    KeypadReaderPayloadInput::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
