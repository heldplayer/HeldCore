package net.specialattack.forge.core.asm;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import java.io.IOException;

public class SpACoreAccessTransformer extends AccessTransformer {

    public SpACoreAccessTransformer() throws IOException {
        super("spacore_at.cfg");
    }

}
