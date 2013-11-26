
package me.heldplayer.util.HeldCore;

import me.heldplayer.util.HeldCore.config.Config;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class HeldCoreMod {

    protected Config config;

    public abstract ModInfo getModInfo();

    public abstract HeldCoreProxy getProxy();

    public boolean shouldReport() {
        return true;
    }

    public void preInit(FMLPreInitializationEvent event) {
        this.config.load();
        this.config.saveOnChange();

        this.getProxy().preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        if (shouldReport()) {
            ModInfo info = this.getModInfo();

            HeldCore.initializeReporter(info.modId, info.modVersion);
            Updater.initializeUpdater(info.modId, info.modVersion);

        }

        this.getProxy().init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        this.getProxy().postInit(event);
    }

}
