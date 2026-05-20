package net.rubii.securelib.jade;

import net.rubii.securelib.SecureLib;
import net.rubii.securelib.block.custom.CardReaderBlock;
import net.rubii.securelib.block.custom.KeypadReaderBlock;
import net.rubii.securelib.block.entity.CardReaderBlockEntity;
import net.rubii.securelib.block.entity.KeypadReaderBlockEntity;
import net.rubii.securelib.jade.component_providers.CardReadersProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompatibility implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CardReadersProvider.INSTANCE, CardReaderBlockEntity.class);
        registration.registerBlockDataProvider(CardReadersProvider.INSTANCE, KeypadReaderBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CardReadersProvider.INSTANCE, CardReaderBlock.class);
        registration.registerBlockComponent(CardReadersProvider.INSTANCE, KeypadReaderBlock.class);
    }
}
