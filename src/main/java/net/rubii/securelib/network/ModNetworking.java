package net.rubii.securelib.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rubii.securelib.api.block.AbstractKeypadBlock;
import net.rubii.securelib.api.block.AbstractKeypadReaderBlock;
import net.rubii.securelib.api.block.entity.AbstractCardReaderBlockEntity;
import net.rubii.securelib.api.block.entity.AbstractKeypadBlockEntity;
import net.rubii.securelib.api.block.entity.AbstractKeypadReaderBlockEntity;
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
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractCardReaderBlockEntity reader) {
                        reader.setFrequency(payload.frequency());
                        reader.setClearance(payload.clearance());
                        reader.setChanged();
                    }
                })
        );

        registrar.playToServer(
                CardReaderTriPayload.TYPE,
                CardReaderTriPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractCardReaderBlockEntity reader) {
                        reader.setFrequency(payload.frequency());
                        reader.setClearances(payload.lClearance(), payload.mClearance(), payload.rClearance());
                        reader.setChanged();
                    }
                })
        );

        registrar.playToServer(
                KeypadPayloadCode.TYPE,
                KeypadPayloadCode.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadBlockEntity be) {
                        if (player.level().getBlockState(payload.blockPos()).getBlock() instanceof AbstractKeypadBlock keypad) {
                            keypad.setupEvents.setupCode(player.level(), player, be, payload.blockPos(), payload.code());
                        }
                    }
                })
        );

        registrar.playToServer(
                KeypadPayloadInput.TYPE,
                KeypadPayloadInput.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadBlockEntity be) {
                        if (player.level().getBlockState(payload.blockPos()).getBlock() instanceof AbstractKeypadBlock keypad) {
                            keypad.interactionEvents.input(player.level(), player, be, payload.blockPos(), payload.input());
                        }
                    }
                })
        );

        registrar.playToServer(
                KeypadReaderPayload.TYPE,
                KeypadReaderPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadReaderBlockEntity reader) {
                        reader.setFrequency(payload.frequency());
                        reader.setClearance(payload.clearance());
                        reader.setChanged();
                    }
                })
        );

        registrar.playToServer(
                KeypadReaderTriPayload.TYPE,
                KeypadReaderTriPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadReaderBlockEntity reader) {
                        reader.setFrequency(payload.frequency());
                        reader.setClearances(payload.lClearance(), payload.mClearance(), payload.rClearance());
                        reader.setChanged();
                    }
                })
        );

        registrar.playToServer(
                KeypadReaderPayloadCode.TYPE,
                KeypadReaderPayloadCode.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadReaderBlockEntity be) {
                        if (player.level().getBlockState(payload.blockPos()).getBlock() instanceof AbstractKeypadReaderBlock keypadReader) {
                            keypadReader.setupEvents.setupCode(player.level(), player, be, payload.blockPos(), payload.code());
                        }
                    }
                })
        );

        registrar.playToServer(
                KeypadReaderPayloadInput.TYPE,
                KeypadReaderPayloadInput.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof AbstractKeypadReaderBlockEntity be) {
                        if (player.level().getBlockState(payload.blockPos()).getBlock() instanceof AbstractKeypadReaderBlock keypadReader) {
                            keypadReader.interactionEvents.input(player.level(), player, be, payload.blockPos(), payload.input());
                        }
                    }
                })
        );

        registrar.playToServer(
                CardPrinterPayload.TYPE,
                CardPrinterPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof CardPrinterBlockEntity printer) {
                        printer.setName(payload.name());
                        printer.setColor(payload.color());
                        printer.setChanged();
                    }
                })
        );

        registrar.playToServer(
                CardWriterPayload.TYPE,
                CardWriterPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof CardWriterBlockEntity writer) {
                        writer.setFrequency(payload.frequency());
                        writer.setClearance(payload.clearance());
                        writer.setChanged();
                    }
                })
        );

        registrar.playToServer(
                CardWriterAltPayload.TYPE,
                CardWriterAltPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = (ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.blockPos()) instanceof CardWriterAltBlockEntity writer) {
                        writer.setFrequency(payload.frequency());
                        writer.setClearances(payload.lClearance(), payload.mClearance(), payload.rClearance());
                        writer.setChanged();
                    }
                })
        );
    }
}
