package net.rubii.securelib.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rubii.securelib.block.entity.*;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                CardReaderPayload.TYPE,
                CardReaderPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof CardReaderBlockEntity reader) {
                            reader.setFrequency(payload.frequency());
                            reader.setClearance(payload.clearance());
                            reader.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                KeypadPayloadCode.TYPE,
                KeypadPayloadCode.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof KeypadBlockEntity keypad) {
                            keypad.setCode(payload.code());
                            keypad.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                KeypadPayloadInput.TYPE,
                KeypadPayloadInput.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof KeypadBlockEntity keypad) {
                            keypad.setInput(payload.input());
                            keypad.inputUpdated(UUID.fromString(payload.uuid()));
                            keypad.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                KeypadReaderPayload.TYPE,
                KeypadReaderPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof KeypadReaderBlockEntity reader) {
                            reader.setFrequency(payload.frequency());
                            reader.setClearance(payload.clearance());
                            reader.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                KeypadReaderPayloadCode.TYPE,
                KeypadReaderPayloadCode.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof KeypadReaderBlockEntity keypad) {
                            keypad.setCode(payload.code());
                            keypad.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                KeypadReaderPayloadInput.TYPE,
                KeypadReaderPayloadInput.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof KeypadReaderBlockEntity keypad) {
                            keypad.setInput(payload.input());
                            keypad.inputUpdated(UUID.fromString(payload.uuid()));
                            keypad.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                CardPrinterPayload.TYPE,
                CardPrinterPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof CardPrinterBlockEntity printer) {
                            printer.setName(payload.name());
                            printer.setColor(payload.color());
                            printer.setChanged();
                        }
                    });
                }
        );

        registrar.playToServer(
                CardWriterPayload.TYPE,
                CardWriterPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof CardWriterBlockEntity writer) {
                            writer.setFrequency(payload.frequency());
                            writer.setClearance(payload.clearance());
                            writer.setChanged();
                        }
                    });
                }
        );
    }
}
