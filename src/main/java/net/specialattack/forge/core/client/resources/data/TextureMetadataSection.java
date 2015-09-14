package net.specialattack.forge.core.client.resources.data;

import net.minecraft.client.resources.data.IMetadataSection;

public class TextureMetadataSection implements IMetadataSection {

    public Repeat repeat;
    public int textureWidth, textureHeight;

    public static class Repeat {

        public final int borderLeft, borderTop, borderRight, borderBottom;

        public Repeat(int borderLeft, int borderTop, int borderRight, int borderBottom) {
            this.borderLeft = borderLeft;
            this.borderTop = borderTop;
            this.borderRight = borderRight;
            this.borderBottom = borderBottom;
        }
    }
}
