package net.rubii.securelib;

import net.minecraft.util.Tuple;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.rubii.securelib.api.SecureLibBlocksRegistry;
import net.rubii.securelib.block.entity.ModBlockEntities;
import net.rubii.securelib.block.entity.renderer.CardWriterBlocKEntityRenderer;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.network.ModNetworking;
import net.rubii.securelib.screen.ModMenuTypes;
import net.rubii.securelib.screen.custom.CardPrinterScreen;
import net.rubii.securelib.screen.custom.CardWriterScreen;
import net.rubii.securelib.screen.custom.KeypadReaderScreen;
import net.rubii.securelib.screen.custom.KeypadScreen;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import net.rubii.securelib.block.ModBlocks;
import net.rubii.securelib.util.ModCreativeModeTabs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SecureLib.MODID)
public class SecureLib {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "securelib";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SecureLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(ModNetworking::register);

        SecureLibBlocksRegistry.registerCardReader(ModBlocks.CARD_READER);
        SecureLibBlocksRegistry.registerKeypad(ModBlocks.KEYPAD);
        SecureLibBlocksRegistry.registerKeypadReader(ModBlocks.KEYPAD_READER);

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private static final Collection<Tuple<Runnable, Integer>> DELAYED_ACTIONS_QUEUE = new ConcurrentLinkedQueue<>();

    //Made by MCreator, I did not really found any other way to do it without using my previous Networking system, which feels kinda over-engineered
    public static void delayTick(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            DELAYED_ACTIONS_QUEUE.add(new Tuple<>(action, tick));
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(ModMenuTypes.KEYPAD_MENU.get(), KeypadScreen::new);
            event.register(ModMenuTypes.KEYPAD_READER_MENU.get(), KeypadReaderScreen::new);
            event.register(ModMenuTypes.CARD_PRINTER_MENU.get(), CardPrinterScreen::new);
            event.register(ModMenuTypes.CARD_WRITER_MENU.get(), CardWriterScreen::new);
        }

        @SubscribeEvent
        public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(ModBlockEntities.CARD_WRITER_BE.get(), CardWriterBlocKEntityRenderer::new);
        }

    }

    @SubscribeEvent
    public void tick(ServerTickEvent.Post event) {
        //Delayed actions system, still made by MCreator, amazing work from their side
        List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
        DELAYED_ACTIONS_QUEUE.forEach(action -> {
            action.setB(action.getB() - 1);
            if (action.getB() == 0)
                actions.add(action);
        });
        actions.forEach(e -> e.getA().run());
        DELAYED_ACTIONS_QUEUE.removeAll(actions);
    }

}