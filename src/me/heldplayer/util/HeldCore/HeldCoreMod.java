
package me.heldplayer.util.HeldCore;

import me.heldplayer.util.HeldCore.config.Config;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class HeldCoreMod {

    protected Config config;

    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);

    public abstract ModInfo getModInfo();

    public abstract HeldCoreProxy getProxy();

    public void basePreInit(FMLPreInitializationEvent event) {
        this.preInit(event);

        this.config.load();
        this.config.saveOnChange();

        this.getProxy().preInit(event);
    }

    public void baseInit(FMLInitializationEvent event) {
        this.init(event);

        ModInfo info = this.getModInfo();

        HeldCore.initializeReporter(info.modId, info.modVersion);
        Updater.initializeUpdater(info.modId, info.modVersion);

        this.getProxy().init(event);
    }

    public void basePostInit(FMLPostInitializationEvent event) {
        this.postInit(event);

        this.getProxy().postInit(event);
    }

}
