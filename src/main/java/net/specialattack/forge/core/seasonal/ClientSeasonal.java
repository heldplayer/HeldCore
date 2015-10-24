package net.specialattack.forge.core.seasonal;

public class ClientSeasonal extends CommonSeasonal {

    static {
        CommonSeasonal.seasonals.add(new HalloweenSeasonal());
    }
}
