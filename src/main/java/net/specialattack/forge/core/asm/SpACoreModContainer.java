package net.specialattack.forge.core.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import java.util.Arrays;
import net.specialattack.forge.core.Objects;

public class SpACoreModContainer extends DummyModContainer {

    public SpACoreModContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "spacoreforge";
        meta.name = "SpACoreForge";
        meta.version = Objects.MOD_INFO.modVersion;
        meta.authorList = Arrays.asList("heldplayer", "mbl111");
        meta.description = "Coremod for SpACore";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

}
