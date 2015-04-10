package net.specialattack.forge.core;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.specialattack.forge.core.config.Config;

public abstract class SpACoreMod {

    public Config<?> config;

    public void preInit(FMLPreInitializationEvent event) {
        this.config.sort();
        this.config.load();
        this.config.saveOnChange();

        ModInfo info = this.getModInfo();
        if (info != null && info.modVersion != null) {
            event.getModMetadata().version = info.modVersion;
        }

        this.getProxy().preInit(event);
    }

    public abstract ModInfo getModInfo();

    public abstract SpACoreProxy getProxy();

    public void init(FMLInitializationEvent event) {
        this.getProxy().init(event);

        FMLCommonHandler.instance().bus().register(this);
    }

    public void postInit(FMLPostInitializationEvent event) {
        this.getProxy().postInit(event);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(this.getModInfo().modId)) {
            if (this.configChanged(event)) {
                this.config.save();
            }
        }
    }

    public boolean configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        return false;
    }

}
