package net.specialattack.forge.core.seasonal;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import java.util.ArrayList;
import java.util.List;
import net.specialattack.forge.core.SpACoreProxy;

public class CommonSeasonal extends SpACoreProxy {

    protected static List<ISeasonal> seasonals = new ArrayList<ISeasonal>();

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        for (ISeasonal seasonal : CommonSeasonal.seasonals) {
            if (seasonal.itsTheSeason()) {
                seasonal.init();
            }
        }
    }
}
