package net.specialattack.forge.core.seasonal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSeasonal extends CommonSeasonal {

    static {
        CommonSeasonal.seasonals.add(new HalloweenSeasonal());
    }
}
