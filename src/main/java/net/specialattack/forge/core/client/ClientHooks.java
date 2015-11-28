package net.specialattack.forge.core.client;

import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.specialattack.forge.core.Objects;
import net.specialattack.forge.core.client.shader.ShaderManager;
import net.specialattack.forge.core.event.WorldChangedEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix3f;

@SideOnly(Side.CLIENT)
public final class ClientHooks {

    protected static final FloatBuffer COLOR_MATRIX = BufferUtils.createFloatBuffer(16);
    public static ShaderManager.ShaderBinding colorBlindShader;
    private static Set<ClientHooks.ScreenColorizer> colorizers = new TreeSet<ClientHooks.ScreenColorizer>(new Comparator<ClientHooks.ScreenColorizer>() {
        @Override
        public int compare(ClientHooks.ScreenColorizer o1, ClientHooks.ScreenColorizer o2) {
            int compared = Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
            if (compared != 0) {
                return compared;
            }
            return o1.getIdentifier().compareTo(o2.getIdentifier());
        }
    });

    private ClientHooks() {
    }

    public static void clientLoadWorld(WorldClient world) {
        Objects.MAIN_EVENT_BUS.post(new WorldChangedEvent(world));
        ClientProxy.syncClientInstance.worldChanged(world);
    }

    public static void framebufferRenderPre() {
        if (OpenGlHelper.shadersSupported && OpenGlHelper.framebufferSupported) {
            boolean dirty = false;
            for (ClientHooks.ScreenColorizer colorizer : ClientHooks.colorizers) {
                if (colorizer.isDirty()) {
                    dirty = true;
                    break;
                }
            }

            if (dirty) {
                Matrix3f matrix = new Matrix3f();
                matrix.setIdentity();
                for (ClientHooks.ScreenColorizer colorizer : ClientHooks.colorizers) {
                    matrix = colorizer.apply(matrix);
                }

                ClientHooks.COLOR_MATRIX.clear();
                matrix.store(ClientHooks.COLOR_MATRIX);
                ClientHooks.COLOR_MATRIX.rewind();
            }
            ClientHooks.colorBlindShader.getShader().bind();
        }
    }

    public static void framebufferRenderPost() {
        if (OpenGlHelper.shadersSupported && OpenGlHelper.framebufferSupported) {
            ClientHooks.colorBlindShader.getShader().unbind();
        }
    }

    public static void addColorizer(ClientHooks.ScreenColorizer colorizer) {
        ClientHooks.colorizers.add(colorizer);
    }

    public interface ScreenColorizer {

        /**
         * Apply this colorizer to the color matrix to get a new color matrix.
         *
         * @param original
         *         The original color matrix, may be null.
         *
         * @return The resulting matrix, may be null.
         */
        Matrix3f apply(Matrix3f original);

        /**
         * Gets the priority of the colorizer, INT_MAX is reserved for the colorblindness colorizer.
         *
         * @return An integer priority, higher values equal later execution.
         */
        int getPriority();

        /**
         * Returns a String representing a unique identifier for this colorizer.
         * Used to sort if 2 colorizers have the same priority so that the resulting order is consistent.
         *
         * @return The unique identifier of this colorizer, should be in the format "modid:name"
         */
        String getIdentifier();

        /**
         * Returns whether this colorizer has changed state since the last call of this method, used to rebuild the color matrix.
         * Note that rebuilding this matrix can be an expensive operation, so try to not make this happen too often.
         *
         * @return True if this colorizer has changed state since last time, false otherwise.
         */
        boolean isDirty();
    }
}
