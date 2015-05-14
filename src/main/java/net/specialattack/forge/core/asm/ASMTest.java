package net.specialattack.forge.core.asm;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.specialattack.forge.core.client.GLState;
import net.specialattack.forge.core.client.RenderReplacements;

public class ASMTest {

    private RenderGameOverlayEvent eventParent;

    public static void glBlendFunc(int srcRGB, int destRGB, int srcAlpha, int destAlpha) {
        GLState.glBlendFunc(srcRGB, destRGB, srcAlpha, destAlpha);
    }

    protected void renderHUDText(int width, int height) {
        RenderReplacements.renderHUDText(this.eventParent, width, height);
    }

}
