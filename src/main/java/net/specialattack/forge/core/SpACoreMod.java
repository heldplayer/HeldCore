package net.specialattack.forge.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class SpACoreMod {

    public abstract ModInfo getModInfo();

    public abstract SpACoreProxy getProxy();

    public void preInit(FMLPreInitializationEvent event) {
        ModInfo info = this.getModInfo();
        if (info != null && info.modVersion != null) {
            event.getModMetadata().version = info.modVersion;
        }

        this.getProxy().preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        this.getProxy().init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        this.getProxy().postInit(event);
    }
}
