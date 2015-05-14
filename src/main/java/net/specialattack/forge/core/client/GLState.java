package net.specialattack.forge.core.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import net.specialattack.forge.core.asm.SpACorePlugin;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

// http://stackoverflow.com/questions/5062301/gltexgen-in-opengl-es-2-0
@SideOnly(Side.CLIENT)
public class GLState {

    private static GLState.BooleanState LIGHTING_STATE = new GLState.BooleanState(GL11.GL_LIGHTING);
    private static GLState.BooleanState[] LIGHT_STATES = new GLState.BooleanState[8];
    private static GLState.BooleanState NORMALIZE_STATE = new GLState.BooleanState(GL11.GL_NORMALIZE);
    private static GLState.BooleanState RESCALE_NORMAL_STATE = new GLState.BooleanState(GL12.GL_RESCALE_NORMAL);
    private static GLState.ColorState COLOR_STATE = new GLState.ColorState();
    private static GLState.ColorLogicState COLOR_LOGIC_STATE = new GLState.ColorLogicState();
    private static GLState.AlphaState ALPHA_STATE = new GLState.AlphaState();
    private static GLState.BlendState BLEND_STATE = new GLState.BlendState();
    private static GLState.CullState CULL_STATE = new GLState.CullState();
    private static GLState.ClearState CLEAR_STATE = new GLState.ClearState();
    private static GLState.DepthState DEPTH_STATE = new GLState.DepthState();
    private static GLState.FogState FOG_STATE = new GLState.FogState();
    private static GLState.ViewportState VIEWPORT_STATE = new GLState.ViewportState();
    private static GLState.ScissorState SCISSOR_STATE = new GLState.ScissorState();
    private static GLState.TextureState[] TEXTURE_STATES = new GLState.TextureState[8];
    private static int activeTexture = 0;
    private static int shadeModel = GL11.GL_SMOOTH;
    private static float lineWidth = 1.0F;
    private static boolean drawing;

    static {
        for (int i = 0; i < 8; i++) {
            GLState.LIGHT_STATES[i] = new GLState.BooleanState(GL11.GL_LIGHT0 + i);
            GLState.TEXTURE_STATES[i] = new GLState.TextureState();
        }
    }

    private GLState() {
    }

    private static void throwError(String error) {
        throw new RuntimeException("Illegal attempt to change the GL state detected" + (error == null ? "" : ": " + error));
    }

    public static void checkError() {
        if (!GLState.drawing) {
            int error = GL11.glGetError();
            if (error != 0) {
                GLState.throwError(GLU.gluErrorString(error));
            }
        }
    }

    // BEGIN DELEGATE METHODS
    public static void glBegin(int mode) {
        if (!SpACorePlugin.stateManager) {
            GL11.glBegin(mode);
            return;
        }
        if (!GLState.drawing) {
            GL11.glBegin(mode);
            GLState.drawing = true;
        } else {
            GLState.throwError("Already drawing!");
        }
    }

    public static void glEnd() {
        if (!SpACorePlugin.stateManager) {
            GL11.glEnd();
            return;
        }
        if (GLState.drawing) {
            GL11.glEnd();
            GLState.drawing = false;
        } else {
            GLState.throwError("Not drawing!");
        }
    }

    public static void glEnable(int code) {
        if (!SpACorePlugin.stateManager) {
            GL11.glEnable(code);
            return;
        }
        if (code >= GL11.GL_LIGHT0 && code <= GL11.GL_LIGHT7) {
            GLState.LIGHT_STATES[code - GL11.GL_LIGHT0].setEnabled();
        } else {
            switch (code) {
                case GL11.GL_LIGHTING:
                    GLState.LIGHTING_STATE.setEnabled();
                    break;
                case GL11.GL_NORMALIZE:
                    GLState.NORMALIZE_STATE.setEnabled();
                    break;
                case GL12.GL_RESCALE_NORMAL:
                    GLState.RESCALE_NORMAL_STATE.setEnabled();
                    break;
                case GL11.GL_ALPHA_TEST:
                    GLState.ALPHA_STATE.ALPHA_TEST.setEnabled();
                    break;
                case GL11.GL_BLEND:
                    GLState.BLEND_STATE.BLEND.setEnabled();
                    break;
                case GL11.GL_DEPTH_TEST:
                    GLState.DEPTH_STATE.DEPTH_TEST.setEnabled();
                    break;
                case GL11.GL_TEXTURE_2D:
                    GLState.TEXTURE_STATES[GLState.activeTexture].TEXTURE_2D.setEnabled();
                    break;
                case GL11.GL_FOG:
                    GLState.FOG_STATE.FOG.setEnabled();
                    break;
                case GL11.GL_CULL_FACE:
                    GLState.CULL_STATE.CULL_FACE.setEnabled();
                    break;
                case GL11.GL_SCISSOR_TEST:
                    GLState.SCISSOR_STATE.SCISSOR_TEST.setEnabled();
                    break;
                case GL11.GL_COLOR_LOGIC_OP:
                    GLState.COLOR_LOGIC_STATE.COLOR_LOGIC_OP.setEnabled();
                    break;
                default:
                    GL11.glEnable(code);
                    break;
            }
        }
    }

    public static void glDisable(int code) {
        if (!SpACorePlugin.stateManager) {
            GL11.glDisable(code);
            return;
        }
        if (code >= GL11.GL_LIGHT0 && code <= GL11.GL_LIGHT7) {
            GLState.LIGHT_STATES[code - GL11.GL_LIGHT0].setDisabled();
        } else {
            switch (code) {
                case GL11.GL_LIGHTING:
                    GLState.LIGHTING_STATE.setDisabled();
                    break;
                case GL11.GL_NORMALIZE:
                    GLState.NORMALIZE_STATE.setDisabled();
                    break;
                case GL12.GL_RESCALE_NORMAL:
                    GLState.RESCALE_NORMAL_STATE.setDisabled();
                    break;
                case GL11.GL_ALPHA_TEST:
                    GLState.ALPHA_STATE.ALPHA_TEST.setDisabled();
                    break;
                case GL11.GL_BLEND:
                    GLState.BLEND_STATE.BLEND.setDisabled();
                    break;
                case GL11.GL_DEPTH_TEST:
                    GLState.DEPTH_STATE.DEPTH_TEST.setDisabled();
                    break;
                case GL11.GL_TEXTURE_2D:
                    GLState.TEXTURE_STATES[GLState.activeTexture].TEXTURE_2D.setDisabled();
                    break;
                case GL11.GL_FOG:
                    GLState.FOG_STATE.FOG.setDisabled();
                    break;
                case GL11.GL_CULL_FACE:
                    GLState.CULL_STATE.CULL_FACE.setDisabled();
                    break;
                case GL11.GL_SCISSOR_TEST:
                    GLState.SCISSOR_STATE.SCISSOR_TEST.setDisabled();
                    break;
                case GL11.GL_COLOR_LOGIC_OP:
                    GLState.COLOR_LOGIC_STATE.COLOR_LOGIC_OP.setDisabled();
                    break;
                default:
                    GL11.glDisable(code);
                    break;
            }
        }
    }

    public static void glAlphaFunc(int func, float ref) {
        if (!SpACorePlugin.stateManager) {
            GL11.glAlphaFunc(func, ref);
            return;
        }
        GLState.ALPHA_STATE.alphaFunc(func, ref);
    }

    public static void glShadeModel(int mode) {
        if (!SpACorePlugin.stateManager) {
            GL11.glShadeModel(mode);
            return;
        }
        if (mode != GL11.GL_SMOOTH && mode != GL11.GL_FLAT) {
            GLState.throwError(String.format("Invalid shade model: %H", mode));
        }
        if (GLState.shadeModel != mode) {
            GL11.glShadeModel(mode);
            GLState.checkError();
            GLState.shadeModel = mode;
        }
    }

    public static void glLineWidth(float width) {
        if (!SpACorePlugin.stateManager) {
            GL11.glLineWidth(width);
            return;
        }
        if (width <= 0.0F) {
            GLState.throwError(String.format("Invalid line width: %f", width));
        }
        if (GLState.lineWidth != width) {
            GL11.glLineWidth(width);
            GLState.checkError();
            GLState.lineWidth = width;
        }
    }

    public static void glBlendFunc(int src, int dest) {
        if (!SpACorePlugin.stateManager) {
            GL11.glBlendFunc(src, dest);
            return;
        }
        GLState.BLEND_STATE.blendFunc(src, dest);
    }

    public static void glBlendFunc(int srcRGB, int destRGB, int srcAlpha, int destAlpha) {
        if (!SpACorePlugin.stateManager) {
            OpenGlHelper.glBlendFunc(srcRGB, destRGB, srcAlpha, destAlpha);
            return;
        }
        GLState.BLEND_STATE.blendFunc(srcRGB, destRGB, srcAlpha, destAlpha);
    }

    public static void glCullFace(int mode) {
        if (!SpACorePlugin.stateManager) {
            GL11.glCullFace(mode);
            return;
        }
        GLState.CULL_STATE.cullFace(mode);
    }

    public static void glLogicOp(int opcode) {
        if (!SpACorePlugin.stateManager) {
            GL11.glLogicOp(opcode);
            return;
        }
        GLState.COLOR_LOGIC_STATE.logicOp(opcode);
    }

    public static void glClearDepth(double depth) {
        if (!SpACorePlugin.stateManager) {
            GL11.glClearDepth(depth);
            return;
        }
        GLState.CLEAR_STATE.clearDepth(depth);
    }

    public static void glClearColor(float red, float green, float blue, float alpha) {
        if (!SpACorePlugin.stateManager) {
            GL11.glClearColor(red, green, blue, alpha);
            return;
        }
        GLState.CLEAR_STATE.clearColor(red, green, blue, alpha);
    }

    public static void glDepthFunc(int func) {
        if (!SpACorePlugin.stateManager) {
            GL11.glDepthFunc(func);
            return;
        }
        GLState.DEPTH_STATE.depthFunc(func);
    }

    public static void glDepthMask(boolean flag) {
        if (!SpACorePlugin.stateManager) {
            GL11.glDepthMask(flag);
            return;
        }
        GLState.DEPTH_STATE.depthMask(flag);
    }

    public static void glActiveTexture(int texture) {
        if (!SpACorePlugin.stateManager) {
            GL13.glActiveTexture(texture);
            return;
        }
        GLState.setActiveTexture(texture - GL13.GL_TEXTURE0);
    }

    public static void glViewport(int x, int y, int width, int height) {
        if (!SpACorePlugin.stateManager) {
            GL11.glViewport(x, y, width, height);
            return;
        }
        GLState.VIEWPORT_STATE.glViewport(x, y, width, height);
    }

    public static void glScissor(int x, int y, int width, int height) {
        if (!SpACorePlugin.stateManager) {
            GL11.glScissor(x, y, width, height);
            return;
        }
        GLState.SCISSOR_STATE.glScissor(x, y, width, height);
    }

    public static void glFogf(int pname, float param) {
        if (!SpACorePlugin.stateManager) {
            GL11.glFogf(pname, param);
            return;
        }
        switch (pname) {
            case GL11.GL_FOG_DENSITY:
                GLState.FOG_STATE.fogDensity(param);
                break;
            case GL11.GL_FOG_START:
                GLState.FOG_STATE.fogStart(param);
                break;
            case GL11.GL_FOG_END:
                GLState.FOG_STATE.fogEnd(param);
                break;
            default:
                GL11.glFogf(pname, param);
                break;
        }
    }

    public static void glFogi(int pname, int param) {
        if (!SpACorePlugin.stateManager) {
            GL11.glFogi(pname, param);
            return;
        }
        switch (pname) {
            case GL11.GL_FOG_MODE:
                GLState.FOG_STATE.fogMode(param);
                break;
            case NVFogDistance.GL_FOG_DISTANCE_MODE_NV:
                GLState.FOG_STATE.fogDistanceModeNv(param);
                break;
            default:
                GL11.glFogi(pname, param);
                break;
        }
    }

    public static void glFog(int pname, FloatBuffer params) {
        if (!SpACorePlugin.stateManager) {
            GL11.glFog(pname, params);
            return;
        }
        switch (pname) {
            case GL11.GL_FOG_DENSITY:
                GLState.FOG_STATE.fogDensity(params.get(0));
                break;
            case GL11.GL_FOG_START:
                GLState.FOG_STATE.fogStart(params.get(0));
                break;
            case GL11.GL_FOG_END:
                GLState.FOG_STATE.fogEnd(params.get(0));
                break;
            case GL11.GL_FOG_COLOR:
                GLState.FOG_STATE.fogColor(params.get(0), params.get(1), params.get(2), params.get(3));
                break;
            default:
                GL11.glFog(pname, params);
                break;
        }
    }

    public static void glFog(int pname, IntBuffer params) {
        if (!SpACorePlugin.stateManager) {
            GL11.glFog(pname, params);
            return;
        }
        switch (pname) {
            case GL11.GL_FOG_MODE:
                GLState.FOG_STATE.fogMode(params.get(0));
                break;
            case NVFogDistance.GL_FOG_DISTANCE_MODE_NV:
                GLState.FOG_STATE.fogDistanceModeNv(params.get(0));
                break;
            default:
                GL11.glFog(pname, params);
                break;
        }
    }

    // Whoever uses this function should die in a fire
    public static void glColor3b(byte red, byte green, byte blue) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor3b(red, green, blue);
            return;
        }
        float fRed = (float) ((int) red + 128) / 128.0F - 1.0F;
        float fGreen = (float) ((int) green + 128) / 128.0F - 1.0F;
        float fBlue = (float) ((int) blue + 128) / 128.0F - 1.0F;
        GLState.glColor4f(fRed, fGreen, fBlue, 1.0F);
    }

    // Whoever uses this function should die in a fire as well
    public static void glColor3ub(byte red, byte green, byte blue) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor3ub(red, green, blue);
            return;
        }
        float fRed = (float) ((int) red + 128) / 255.0F;
        float fGreen = (float) ((int) green + 128) / 255.0F;
        float fBlue = (float) ((int) blue + 128) / 255.0F;
        GLState.glColor4f(fRed, fGreen, fBlue, 1.0F);
    }

    public static void glColor3d(double red, double green, double blue) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor3d(red, green, blue);
            return;
        }
        GLState.glColor4f((float) red, (float) green, (float) blue, 1.0F);
    }

    public static void glColor3f(float red, float green, float blue) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor3f(red, green, blue);
            return;
        }
        GLState.glColor4f(red, green, blue, 1.0F);
    }

    // Die in a fire
    public static void glColor4b(byte red, byte green, byte blue, byte alpha) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor4b(red, green, blue, alpha);
            return;
        }
        float fRed = (float) ((int) red + 128) / 128.0F - 1.0F;
        float fGreen = (float) ((int) green + 128) / 128.0F - 1.0F;
        float fBlue = (float) ((int) blue + 128) / 128.0F - 1.0F;
        float fAlpha = (float) ((int) alpha + 128) / 128.0F - 1.0F;
        GLState.glColor4f(fRed, fGreen, fBlue, fAlpha);
    }

    // You know the drill
    public static void glColor4ub(byte red, byte green, byte blue, byte alpha) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor4ub(red, green, blue, alpha);
            return;
        }
        float fRed = (float) ((int) red + 128) / 255.0F;
        float fGreen = (float) ((int) green + 128) / 255.0F;
        float fBlue = (float) ((int) blue + 128) / 255.0F;
        float fAlpha = (float) ((int) alpha + 128) / 255.0F;
        GLState.glColor4f(fRed, fGreen, fBlue, fAlpha);
    }

    public static void glColor4d(double red, double green, double blue, double alpha) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor4d(red, green, blue, alpha);
            return;
        }
        GLState.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
    }

    public static void glColor4f(float red, float green, float blue, float alpha) {
        if (!SpACorePlugin.stateManager) {
            GL11.glColor4f(red, green, blue, alpha);
            return;
        }
        GLState.COLOR_STATE.glColor(red, green, blue, alpha);
    }

    // END DELEGATE METHODS

    // 1.7.10 specific method, calls to this get generated automatically
    public static void resetColor() {
        if (!SpACorePlugin.stateManager) {
            return;
        }
        // Let the state reset the color
        GLState.COLOR_STATE.resetGlColor();
    }

    /**
     * Set the current active texture
     *
     * @param index
     *         The texture to activate, from 0 to 7
     */
    public static void setActiveTexture(int index) {
        if (GLState.activeTexture != index && index >= 0 && index < 8) {
            if (OpenGlHelper.field_153215_z) {
                ARBMultitexture.glActiveTextureARB(index + ARBMultitexture.GL_TEXTURE0_ARB);
            } else {
                GL13.glActiveTexture(index + GL13.GL_TEXTURE0);
            }
            GLState.checkError();
            GLState.activeTexture = index;
        }
    }

    private static class AlphaState {

        public GLState.BooleanState ALPHA_TEST = new GLState.BooleanState(GL11.GL_ALPHA_TEST);
        public int func = GL11.GL_ALWAYS;
        public float ref = -1.0F;

        public void alphaFunc(int func, float ref) {
            if (func >= GL11.GL_NEVER && func <= GL11.GL_ALWAYS) {
                if (this.func != func || this.ref != ref) {
                    GL11.glAlphaFunc(func, ref);
                    GLState.checkError();
                    this.func = func;
                    this.ref = ref;
                }
            } else {
                GLState.throwError(String.format("Invalid alpha function: %H", func));
            }
        }
    }

    private static class BlendState {

        public GLState.BooleanState BLEND = new GLState.BooleanState(GL11.GL_BLEND);
        public int srcRGB;
        public int destRGB;
        public int srcAlpha;
        public int destAlpha;

        public void blendFunc(int src, int dest) {
            if (isValid(src) && isValid(dest)) {
                GL11.glBlendFunc(src, dest);
                GLState.checkError();
                this.srcRGB = src;
                this.destRGB = dest;
                this.srcAlpha = src;
                this.destAlpha = dest;
            } else if (isValid(src)) {
                GLState.throwError(String.format("Invalid destination blend function: %H", dest));
            } else {
                GLState.throwError(String.format("Invalid source blend function: %H", src));
            }
        }

        public void blendFunc(int srcRGB, int destRGB, int srcAlpha, int destAlpha) {
            if (isValid(srcRGB) && isValid(destRGB) && isValid(srcAlpha) && isValid(destAlpha)) {
                if (OpenGlHelper.openGL14) { // glBlendSepperate supported?
                    if (this.srcRGB != srcRGB || this.destRGB != destRGB || this.srcAlpha != srcAlpha || this.destAlpha != destAlpha) {
                        if (OpenGlHelper.field_153211_u) { // Use extension or GL14?
                            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(srcRGB, destRGB, srcAlpha, destAlpha);
                        } else {
                            GL14.glBlendFuncSeparate(srcRGB, destRGB, srcAlpha, destAlpha);
                        }
                        GLState.checkError();
                        this.srcRGB = srcRGB;
                        this.destRGB = destRGB;
                        this.srcAlpha = srcAlpha;
                        this.destAlpha = destAlpha;
                    }
                } else {
                    this.blendFunc(srcRGB, destRGB);
                }
            } else if (!isValid(srcRGB)) {
                GLState.throwError(String.format("Invalid source RGB blend function: %H", srcRGB));
            } else if (!isValid(destRGB)) {
                GLState.throwError(String.format("Invalid destination RGB blend function: %H", destRGB));
            } else if (!isValid(srcAlpha)) {
                GLState.throwError(String.format("Invalid source alpha blend function: %H", srcAlpha));
            } else {
                GLState.throwError(String.format("Invalid destination alpha blend function: %H", destAlpha));
            }
        }

        private static boolean isValid(int func) {
            return func == GL11.GL_ZERO || func == GL11.GL_ONE || (func >= GL11.GL_SRC_COLOR && func <= GL11.GL_SRC_ALPHA_SATURATE) || (func >= GL11.GL_CONSTANT_COLOR && func <= GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        }
    }

    private static class BooleanState {

        private final int cap;
        private boolean state = false;

        public BooleanState(int cap) {
            this.cap = cap;
        }

        public void setEnabled() {
            if (!this.state) {
                GL11.glEnable(this.cap);
                GLState.checkError();
                this.state = true;
            }
        }

        public void setDisabled() {
            if (this.state) {
                GL11.glDisable(this.cap);
                GLState.checkError();
                this.state = false;
            }
        }
    }

    private static class ColorState {

        public float red = 1.0F;
        public float green = 1.0F;
        public float blue = 1.0F;
        public float alpha = 1.0F;

        public ColorState(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public ColorState() {
        }

        public void resetGlColor() {
            GL11.glColor4f(this.red, this.green, this.blue, this.alpha);
            GLState.checkError();
        }

        public boolean setColor(float red, float green, float blue, float alpha) {
            if (this.red != red || this.green != green || this.blue != blue || this.alpha != alpha) {
                this.red = red;
                this.green = green;
                this.blue = blue;
                this.alpha = alpha;
                return true;
            }
            return false;
        }

        public void glColor(float red, float green, float blue, float alpha) {
            if (this.setColor(red, green, blue, alpha)) {
                GL11.glColor4f(red, green, blue, alpha);
                GLState.checkError();
            }
        }
    }

    private static class ColorLogicState {

        public GLState.BooleanState COLOR_LOGIC_OP = new GLState.BooleanState(GL11.GL_COLOR_LOGIC_OP);
        public int logicOp = GL11.GL_BACK;

        public void logicOp(int opcode) {
            if (opcode >= GL11.GL_CLEAR && opcode == GL11.GL_SET) {
                if (this.logicOp != opcode) {
                    GL11.glLogicOp(opcode);
                    GLState.checkError();
                    this.logicOp = opcode;
                }
            } else {
                GLState.throwError(String.format("Invalid color logic opcode: %H", opcode));
            }
        }
    }

    private static class CullState {

        public GLState.BooleanState CULL_FACE = new GLState.BooleanState(GL11.GL_CULL_FACE);
        public int cullFace = GL11.GL_BACK;

        public void cullFace(int mode) {
            if (mode == GL11.GL_FRONT || mode == GL11.GL_BACK || mode == GL11.GL_FRONT_AND_BACK) {
                if (this.cullFace != mode) {
                    GL11.glCullFace(mode);
                    GLState.checkError();
                    this.cullFace = mode;
                }
            } else {
                GLState.throwError(String.format("Invalid cull face mode: %H", mode));
            }
        }
    }

    private static class ClearState {

        public GLState.ColorState color = new GLState.ColorState(0.0F, 0.0F, 0.0F, 0.0F);
        public double clearDepth = 1.0D;

        public void clearColor(float red, float green, float blue, float alpha) {
            if (this.color.setColor(red, green, blue, alpha)) {
                GL11.glClearColor(red, green, blue, alpha);
                GLState.checkError();
            }
        }

        public void clearDepth(double depth) {
            depth = MathHelper.clamp_double(depth, 0.0D, 1.0D);
            if (this.clearDepth != depth) {
                GL11.glClearDepth(depth);
                GLState.checkError();
                this.clearDepth = depth;
            }
        }
    }

    private static class DepthState {

        public GLState.BooleanState DEPTH_TEST = new GLState.BooleanState(GL11.GL_DEPTH_TEST);
        public boolean depthMask = true;
        public int depthFunc = GL11.GL_LESS;

        public void depthMask(boolean state) {
            if (this.depthMask != state) {
                GL11.glDepthMask(state);
                GLState.checkError();
                this.depthMask = state;
            }
        }

        public void depthFunc(int func) {
            if (func >= GL11.GL_NEVER && func <= GL11.GL_ALWAYS) {
                if (this.depthFunc != func) {
                    GL11.glDepthFunc(func);
                    GLState.checkError();
                    this.depthFunc = func;
                }
            } else {
                GLState.throwError(String.format("Invalid depth function: %H", func));
            }
        }
    }

    private static class FogState {

        public GLState.BooleanState FOG = new GLState.BooleanState(GL11.GL_FOG);
        public GLState.ColorState color = new GLState.ColorState(0.0F, 0.0F, 0.0F, 0.0F);
        private int mode = GL11.GL_EXP;
        private int distanceModeNV = NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV;
        private float density = 1.0F;
        private float start = 0.0F;
        private float end = 1.0F;

        private FloatBuffer buffer = BufferUtils.createFloatBuffer(4);

        public void fogMode(int mode) {
            if (mode == GL11.GL_LINEAR || mode == GL11.GL_EXP || mode == GL11.GL_EXP2) {
                if (this.mode != mode) {
                    GL11.glFogi(GL11.GL_FOG_MODE, mode);
                    GLState.checkError();
                    this.mode = mode;
                }
            } else {
                GLState.throwError(String.format("Invalid fog mode: %H", mode));
            }
        }

        public void fogColor(float red, float green, float blue, float alpha) {
            if (this.color.setColor(red, green, blue, alpha)) {
                GL11.glFog(GL11.GL_FOG_COLOR, this.fillBuffer(red, green, blue, alpha));
                GLState.checkError();
            }
        }

        private FloatBuffer fillBuffer(float red, float green, float blue, float alpha) {
            this.buffer.clear();
            this.buffer.put(red).put(green).put(blue).put(alpha);
            this.buffer.flip();
            return this.buffer;
        }

        public void fogDensity(float density) {
            if (this.density != density) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, density);
                GLState.checkError();
                this.density = density;
            }
        }

        public void fogStart(float start) {
            if (this.start != start) {
                GL11.glFogf(GL11.GL_FOG_START, start);
                GLState.checkError();
                this.start = start;
            }
        }

        public void fogEnd(float end) {
            if (this.end != end) {
                GL11.glFogf(GL11.GL_FOG_END, end);
                GLState.checkError();
                this.end = end;
            }
        }

        public void fogDistanceModeNv(int distanceModeNV) {
            if (distanceModeNV == NVFogDistance.GL_EYE_RADIAL_NV || distanceModeNV == NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV) {
                if (this.distanceModeNV != distanceModeNV) {
                    GL11.glFogi(NVFogDistance.GL_FOG_DISTANCE_MODE_NV, distanceModeNV);
                    GLState.checkError();
                    this.distanceModeNV = distanceModeNV;
                }
            } else {
                GLState.throwError(String.format("Invalid fog distance mode: %H", mode));
            }
        }
    }

    private static class ScissorState {

        public int x, y;
        public int width, height;
        public GLState.BooleanState SCISSOR_TEST = new GLState.BooleanState(GL11.GL_SCISSOR_TEST);

        public void glScissor(int x, int y, int width, int height) {
            if (width < 0 || height < 0) {
                GLState.throwError(String.format("Invalid width/height combination: %dx%d", width, height));
            } else {
                if (this.x != x || this.y != y || this.width != width || this.height != height) {
                    GL11.glScissor(x, y, width, height);
                    GLState.checkError();
                    this.x = x;
                    this.y = y;
                    this.width = width;
                    this.height = height;
                }
            }
        }

    }

    private static class TextureState {

        public GLState.BooleanState TEXTURE_2D = new GLState.BooleanState(GL11.GL_TEXTURE_2D);
        public int texture = -1;

        public void bind(int texture) {
            if (this.texture != texture) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
                GLState.checkError();
                this.texture = texture;
            }
        }

        public void removed(int texture) {
            if (this.texture == texture) {
                this.texture = -1;
            }
        }
    }

    private static class ViewportState {

        public int x, y;
        public int width, height;

        public void glViewport(int x, int y, int width, int height) {
            if (width < 0 || height < 0) {
                GLState.throwError(String.format("Invalid width/height combination: %dx%d", width, height));
            } else {
                if (this.x != x || this.y != y || this.width != width || this.height != height) {
                    GL11.glViewport(x, y, width, height);
                    GLState.checkError();
                    this.x = x;
                    this.y = y;
                    this.width = width;
                    this.height = height;
                }
            }
        }

    }

}
