package net.specialattack.forge.core.client.gui.animation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;
import net.specialattack.forge.core.client.gui.elements.IRotatableGuiElement;
import net.specialattack.forge.core.client.gui.elements.IScalableGuiElement;

@SideOnly(Side.CLIENT)
public class AddScaleTrack implements IAnimationTrack {

    private final Vec3 scale;
    private Vec3 originalScale;

    public AddScaleTrack(Vec3 scale) {
        this.scale = scale;
    }

    @Override
    public void updateAnimation(IGuiElement element, float ticks) {
        if (!(element instanceof IRotatableGuiElement)) {
            return;
        }
        IScalableGuiElement scalable = (IScalableGuiElement) element;
        if (this.originalScale == null) {
            this.originalScale = scalable.getScale();
        }
        scalable.setScale(originalScale.addVector(scale.xCoord * ticks, scale.yCoord * ticks, scale.zCoord * ticks));
    }

}
