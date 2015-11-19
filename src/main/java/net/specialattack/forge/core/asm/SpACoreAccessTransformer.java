package net.specialattack.forge.core.asm;

import java.io.IOException;
import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

public class SpACoreAccessTransformer extends AccessTransformer {

    public SpACoreAccessTransformer() throws IOException {
        super("spacore_at.cfg");
    }
}
