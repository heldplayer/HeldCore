package net.specialattack.forge.core.client.gui.animation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.elements.IGuiElement;

@SideOnly(Side.CLIENT)
public class SineAnimation implements IAnimation {

    private final IAnimationTrack track;
    private int ticks;
    private float amplitude, offset, speed;

    public SineAnimation(IAnimationTrack track, float amplitude, float offset, float speed) {
        this.track = track;
        this.amplitude = amplitude;
        this.offset = offset;
        this.speed = speed;
    }

    @Override
    public void progressTicks(int ticks) {
        this.ticks += ticks;
    }

    @Override
    public void prepareDraw(IGuiElement element, float partialTicks) {
        float ticks = (float) this.ticks + partialTicks;
        float sin = (float) Math.sin(ticks * speed) * this.amplitude;
        this.track.updateAnimation(element, sin + offset);
    }

}
