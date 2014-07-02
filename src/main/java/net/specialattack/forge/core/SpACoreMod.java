
package net.specialattack.forge.core;

import net.specialattack.forge.core.config.Config;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class SpACoreMod {

    public Config config;

    public abstract ModInfo getModInfo();

    public abstract SpACoreProxy getProxy();

    public boolean configChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        return false;
    }

    public boolean shouldReport() {
        return true;
    }

    public void preInit(FMLPreInitializationEvent event) {
        this.config.load();
        this.config.saveOnChange();

        ModInfo info = this.getModInfo();
        event.getModMetadata().version = info.modVersion;

        this.getProxy().preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        if (this.shouldReport()) {
            ModInfo info = this.getModInfo();

            SpACore.initializeReporter(info.modId, info.modVersion);
        }

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

}
