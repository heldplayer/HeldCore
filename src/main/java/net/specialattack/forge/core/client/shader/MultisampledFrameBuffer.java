package net.specialattack.forge.core.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@SideOnly(Side.CLIENT)
public class MultisampledFrameBuffer extends Framebuffer {

    private int samples;

    public MultisampledFrameBuffer(int width, int height, boolean hasDepth, int samples) {
        super(width, height, hasDepth);
        this.samples = samples;
    }

    @Override
    public void createFramebuffer(int width, int height) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        this.framebufferTextureWidth = width;
        this.framebufferTextureHeight = height;

        if (!OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferClear();
        } else {
            this.framebufferObject = OpenGlHelper.func_153165_e();
            this.framebufferTexture = TextureUtil.glGenTextures();

            if (this.useDepth) {
                this.depthBuffer = OpenGlHelper.func_153185_f();
            }

            this.setFramebufferFilter(GL11.GL_LINEAR);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.framebufferTexture);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, this.framebufferObject);
            OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, this.framebufferTexture, 0);

            if (this.useDepth) {
                OpenGlHelper.func_153176_h(OpenGlHelper.field_153199_f, this.depthBuffer);
                if (MinecraftForgeClient.getStencilBits() == 0) {
                    MultisampledFrameBuffer.createMultisampledBuffer(OpenGlHelper.field_153199_f, this.samples, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
                    OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, OpenGlHelper.field_153199_f, this.depthBuffer);
                } else {
                    MultisampledFrameBuffer.createMultisampledBuffer(OpenGlHelper.field_153199_f, this.samples, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
                    OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
                    OpenGlHelper.func_153190_b(OpenGlHelper.field_153198_e, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.field_153199_f, this.depthBuffer);
                }
            }

            this.framebufferClear();
            this.unbindFramebufferTexture();
        }
    }

    public static void createMultisampledBuffer(int target, int samples, int internalFormat, int width, int height) {
        if (OpenGlHelper.framebufferSupported) {
            switch (OpenGlHelper.field_153212_w) {
                case 0:
                    GL30.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height);
                    break;
                case 1:
                    ARBFramebufferObject.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height);
                    break;
                case 2:
                    EXTFramebufferObject.glRenderbufferStorageEXT(target, samples, internalFormat, width);
                    break;
            }
        }
    }

}
