
package me.heldplayer.util.HeldCore.asm;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class HeldCoreAccessTransformer extends AccessTransformer {

    public HeldCoreAccessTransformer() throws IOException {
        super("heldcore_at.cfg");
    }

}
