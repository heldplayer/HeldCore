package net.specialattack.forge.core.packet;

import com.google.common.base.Throwables;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpAPacketHandler<P extends SpAPacket<P>> extends SimpleNetworkWrapper {

    public static boolean debug = Boolean.parseBoolean(System.getProperty("spacore.packet.debug", "false"));
    public static final Logger log = LogManager.getLogger("SpACore:Pckt");

    public SpAPacketHandler(String channelName, Class<? extends P>... packetClasses) {
        super(channelName);

        for (int i = 0; i < packetClasses.length; i++) {
            Class<? extends P> clazz = packetClasses[i];
            P handler = this.instantiate(clazz);
            this.registerMessage(handler, clazz, i + 1, handler.getReceivingSide());
        }
    }

    private P instantiate(Class<? extends P> handler) {
        try {
            return handler.newInstance();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
