package net.specialattack.forge.core.client.gui;

import org.lwjgl.opengl.GL11;

public final class GuiStateManager {

    private static byte textures;
    private static int stack = 0;

    private GuiStateManager() {
    }

    public static void reset() {
        GuiStateManager.textures = 0;
    }

    public static void enableTextures() {
        if (GuiStateManager.textures != 1) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GuiStateManager.textures = 1;
        }
    }

    public static void disableTextures() {
        if (GuiStateManager.textures != 2) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GuiStateManager.textures = 2;
        }
    }

}
