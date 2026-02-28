package net.rubii.securelib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.rubii.securelib.block.entity.ModBlockEntities;
import net.rubii.securelib.components.ModDataComponents;
import net.rubii.securelib.item.ModItems;
import net.rubii.securelib.network.ModNetworking;
import net.rubii.securelib.screen.ModMenuTypes;
import net.rubii.securelib.screen.custom.CardPrinterScreen;
import net.rubii.securelib.screen.custom.CardWriterScreen;
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
import net.rubii.securelib.item.ModCreativeModeTabs;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SecureLib.MODID)
public class SecureLib {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "securelib";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SecureLib(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModNetworking::register);

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(ModMenuTypes.CARD_PRINTER_MENU.get(), CardPrinterScreen::new);
            event.register(ModMenuTypes.CARD_WRITER_MENU.get(), CardWriterScreen::new);
        }

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}