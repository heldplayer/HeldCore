package net.specialattack.forge.core;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class SpACoreMod {

    public abstract ModInfo getModInfo();

    @Deprecated
    public SpACoreProxy getProxy() {
        return null;
    }

    public SpACoreProxy[] getProxies() {
        @SuppressWarnings("deprecation") SpACoreProxy proxy = this.getProxy();
        if (proxy == null) {
            return new SpACoreProxy[0];
        }
        return new SpACoreProxy[] { proxy };
    }

    public void preInit(FMLPreInitializationEvent event) {
        ModInfo info = this.getModInfo();
        if (info != null && info.modVersion != null) {
            event.getModMetadata().version = info.modVersion;
        }

        for (SpACoreProxy proxy : this.getProxies()) {
            proxy.preInit(event);
        }
    }

    public void init(FMLInitializationEvent event) {
        for (SpACoreProxy proxy : this.getProxies()) {
            proxy.init(event);
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        for (SpACoreProxy proxy : this.getProxies()) {
            proxy.postInit(event);
        }
    }
}
