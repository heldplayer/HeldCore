package net.specialattack.forge.core.seasonal;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSeasonal extends CommonSeasonal {

    static {
        CommonSeasonal.seasonals.add(new HalloweenSeasonal());
    }
}
