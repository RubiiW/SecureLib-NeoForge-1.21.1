package net.rubii.securelib.jade;

import net.rubii.securelib.api.block.AbstractCardReaderBlock;
import net.rubii.securelib.api.block.AbstractKeypadReaderBlock;
import net.rubii.securelib.api.block.entity.AbstractCardReaderBlockEntity;
import net.rubii.securelib.api.block.entity.AbstractKeypadReaderBlockEntity;
import net.rubii.securelib.jade.component_providers.CardReadersProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompatibility implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CardReadersProvider.INSTANCE, AbstractCardReaderBlockEntity.class);
        registration.registerBlockDataProvider(CardReadersProvider.INSTANCE, AbstractKeypadReaderBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CardReadersProvider.INSTANCE, AbstractCardReaderBlock.class);
        registration.registerBlockComponent(CardReadersProvider.INSTANCE, AbstractKeypadReaderBlock.class);
    }
}
