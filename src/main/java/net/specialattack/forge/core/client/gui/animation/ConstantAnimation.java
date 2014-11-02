package net.specialattack.forge.core.client.gui.animation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;

@SideOnly(Side.CLIENT)
public class ConstantAnimation implements IAnimation {

    private final IAnimationTrack track;
    private int ticks;

    public ConstantAnimation(IAnimationTrack track) {
        this.track = track;
    }

    @Override
    public void progressTicks(int ticks) {
        this.ticks += ticks;
    }

    @Override
    public void prepareDraw(IGuiElement element, float partialTicks) {
        this.track.updateAnimation(element, (float) this.ticks + partialTicks);
    }

}
