
package me.heldplayer.util.HeldCore.asm;

import java.util.Arrays;

import me.heldplayer.util.HeldCore.Objects;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

public class HeldCoreModContainer extends DummyModContainer {

    public HeldCoreModContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "HeldCore-Core";
        meta.name = "HeldCore-Core";
        meta.version = Objects.MOD_VERSION;
        meta.authorList = Arrays.asList("heldplayer");
        meta.description = "Coremod for HeldCore";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

}
