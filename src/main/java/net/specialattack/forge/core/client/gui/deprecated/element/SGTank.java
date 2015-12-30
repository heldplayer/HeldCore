package net.specialattack.forge.core.client.gui.deprecated.element;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.client.gui.GuiHelper;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;

@SideOnly(Side.CLIENT)
public class SGTank extends SGComponent {

    private FluidTank tank;

    public SGTank(FluidTank tank) {
        this.tank = tank;
    }

    public SGTank() {
    }

    public FluidTank getTank() {
        return this.tank;
    }

    public void setTank(FluidTank tank) {
        this.tank = tank;
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTicks) {
        FluidTank tank = this.getTank();
        if (tank != null) {
            GlStateManager.translate(this.getLeft(SizeContext.INNER), this.getTop(SizeContext.INNER), this.getZLevel());
            float filled = (float) tank.getFluidAmount() / (float) tank.getCapacity();
            if (filled > 1.0F) {
                filled = 1.0F;
            }
            int height = this.getHeight(SizeContext.INNER);
            int width = this.getWidth(SizeContext.INNER);
            int lheight = (int) (height * filled);
            if (tank.getFluid() != null) {
                GuiHelper.drawFluid(tank.getFluid().getFluid(), 0, height - lheight, width, lheight, this.getZLevel());
            }
        }
    }
}
