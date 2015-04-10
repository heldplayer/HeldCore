package net.specialattack.forge.core.asm;

import com.google.common.eventbus.EventBus;
import java.util.Arrays;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
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
