
package net.specialattack.forge.core;

import net.specialattack.forge.core.config.Config;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class SpACoreMod {

    protected Config config;

    public abstract ModInfo getModInfo();

    public abstract SpACoreProxy getProxy();

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
    }

    public void postInit(FMLPostInitializationEvent event) {
        this.getProxy().postInit(event);
    }

}
