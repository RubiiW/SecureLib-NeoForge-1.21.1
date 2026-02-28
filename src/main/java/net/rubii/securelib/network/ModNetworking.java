package net.rubii.securelib.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.rubii.securelib.block.entity.CardPrinterBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.block.entity.CardWriterBlockEntity;

public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

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
                CardReaderTickerPayload.TYPE,
                CardReaderTickerPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ServerPlayer player = (ServerPlayer) context.player();
                        if (player.level().getBlockEntity(payload.blockPos()) instanceof CardReaderBlockEntity reader) {
                            reader.setTimer(payload.timer());
                            reader.setChanged();
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
