package net.specialattack.forge.core.client;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.specialattack.forge.core.CommonDebug;
import net.specialattack.forge.core.client.shader.ShaderCallback;
import net.specialattack.forge.core.client.shader.ShaderManager;
import net.specialattack.forge.core.client.shader.ShaderProgram;
import net.specialattack.forge.core.client.shader.ShaderUniform;
import org.lwjgl.BufferUtils;

@SideOnly(Side.CLIENT)
public class ClientDebug extends CommonDebug {

    public static final FloatBuffer COLOR_MATRIX = BufferUtils.createFloatBuffer(9);
    public static boolean colorBlindEnabled = true; // TODO: set to false by default
    public static ShaderManager.ShaderBinding colorBlindShader;

    public static void setColorMode(ColorMode mode) {
        ClientDebug.setColorMode(mode.matrix);
    }

    public static void setColorMode(float[] matrix) {
        ClientDebug.COLOR_MATRIX.clear();
        ClientDebug.COLOR_MATRIX.put(matrix);
        ClientDebug.COLOR_MATRIX.rewind();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyHandler.registerKeyBind(new KeyHandler.KeyData(new KeyBinding("key.spacore:debug", 0, "key.categories.misc"), false) {
            @Override
            public void keyDown(boolean isRepeat) {
                super.keyDown(isRepeat);
                if (!isRepeat) {
                    ClientDebug.setColorMode(ClientDebug.ColorMode.getRandom());
                }
            }
        });
        ClientDebug.setColorMode(ClientDebug.ColorMode.NORMAL);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ClientDebug.colorBlindShader = ShaderManager.getShader(new ResourceLocation("spacore:shaders/color"));
        if (ClientDebug.colorBlindShader != null && ClientDebug.colorBlindShader.getShader() != null) {
            ShaderProgram shader = ClientDebug.colorBlindShader.getShader();
            shader.addCallback(new ShaderCallback() {

                private float time;
                private float prevPartial;

                @Override
                public void call(ShaderProgram program) {
                    ShaderUniform colorCorrection = program.getUniform("colorCorrection");
                    colorCorrection.setMatrix3(true, ClientDebug.COLOR_MATRIX);
                }
            });
        }
    }

    public enum ColorMode {
        // Credit: http://web.archive.org/web/20081014161121/http://www.colorjack.com/labs/colormatrix/
        NORMAL(new float[][] { new float[] { 100.0F, 0.0F, 0.0F }, new float[] { 0.0F, 100.0F, 0.0F }, new float[] { 0.0F, 0.0F, 100.0F } }),
        PROTANOPIA(new float[][] { new float[] { 56.667F, 43.333F, 0.0F }, new float[] { 55.833F, 44.167F, 0.0F }, new float[] { 0.0F, 24.167F, 75.833F } }),
        PROTANOMALY(new float[][] { new float[] { 81.667F, 18.333F, 0.0F }, new float[] { 33.333F, 66.667F, 0.0F }, new float[] { 0.0F, 12.5F, 87.5F } }),
        DEUTERANOPIA(new float[][] { new float[] { 62.5F, 37.5F, 0.0F }, new float[] { 70.0F, 30.0F, 0.0F }, new float[] { 0.0F, 30.0F, 70.0F } }),
        DEUTERANOMALY(new float[][] { new float[] { 80.0F, 20.0F, 0.0F }, new float[] { 25.833F, 74.167F, 0.0F }, new float[] { 0.0F, 14.167F, 85.833F } }),
        TRITONAPIA(new float[][] { new float[] { 95.0F, 5.0F, 0.0F }, new float[] { 0.0F, 43.333F, 56.667F }, new float[] { 0.0F, 47.5F, 52.5F } }),
        TRITANOMALY(new float[][] { new float[] { 96.667F, 3.333F, 0.0F }, new float[] { 0.0F, 73.333F, 26.667F }, new float[] { 0.0F, 18.333F, 81.667F } }),
        ACHROMATOPSIA(new float[][] { new float[] { 29.9F, 58.7F, 11.4F }, new float[] { 29.9F, 58.7F, 11.4F }, new float[] { 29.9F, 58.7F, 11.4F } }),
        ACHROMATOMALY(new float[][] { new float[] { 61.8F, 32.0F, 6.2F }, new float[] { 16.3F, 77.5F, 6.2F }, new float[] { 16.3F, 32.0F, 51.6F } });

        private final float[] matrix;

        ColorMode(float[][] transformations) {
            this.matrix = new float[9];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.matrix[i * 3 + j] = transformations[i][j] / 100.0F;
                }
            }
        }

        public static ColorMode getRandom() {
            Random rand = new Random();
            return ColorMode.values()[rand.nextInt(ColorMode.values().length)];
        }
    }
}
