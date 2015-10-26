package net.specialattack.forge.core.asm;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.launchwrapper.IClassTransformer;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.*;

public class SpACoreGLTransformer implements IClassTransformer {

    private File saveFolder;

    private static boolean changed = false;
    private static final String STATE_MANAGER = "net/specialattack/forge/core/client/GLState";

    private static final List<Replacement> replacements = Lists.newArrayList();

    public SpACoreGLTransformer() {
        this.saveFolder = new File("." + File.separator + "asm" + File.separator + "spacore_statemanager");
        if (!this.saveFolder.exists()) {
            this.saveFolder.mkdir();
        }
    }

    static {
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glBegin", "glBegin", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glEnd", "glEnd", "()V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glEnable", "glEnable", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glDisable", "glDisable", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glPushMatrix", "glPushMatrix", "()V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glPopMatrix", "glPopMatrix", "()V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex2i", "glVertex2i", "(II)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex2f", "glVertex2f", "(FF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex2d", "glVertex2d", "(DD)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex3i", "glVertex3i", "(III)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex3f", "glVertex3f", "(FFF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex3d", "glVertex3d", "(DDD)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex4i", "glVertex4i", "(IIII)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex4f", "glVertex4f", "(FFFF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glVertex4d", "glVertex4d", "(DDDD)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glAlphaFunc", "glAlphaFunc", "(IF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glBlendFunc", "glBlendFunc", "(II)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor3b", "glColor3b", "(BBB)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor3f", "glColor3f", "(FFF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor3d", "glColor3d", "(DDD)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor3ub", "glColor3ub", "(BBB)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor4b", "glColor4b", "(BBBB)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor4f", "glColor4f", "(FFFF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor4d", "glColor4d", "(DDDD)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glColor4ub", "glColor4ub", "(BBBB)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL13", "glActiveTexture", "glActiveTexture", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glBindTexture", "glBindTexture", "(II)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/ARBMultitexture", "glActiveTextureARB", "glActiveTexture", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glFogf", "glFogf", "(IF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glFogi", "glFogi", "(II)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glFog", "glFog", "(ILjava/nio/FloatBuffer;)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glFog", "glFog", "(ILjava/nio/IntBuffer;)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glViewport", "glViewport", "(IIII)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glScissor", "glScissor", "(IIII)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glClearColor", "glClearColor", "(FFFF)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glClearDepth", "glClearDepth", "(D)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glDepthFunc", "glDepthFunc", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glDepthMask", "glDepthMask", "(Z)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glCullFace", "glCullFace", "(I)V"));
        SpACoreGLTransformer.replacements.add(new Replacement("org/lwjgl/opengl/GL11", "glLogicOp", "glLogicOp", "(I)V"));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (original == null) {
            return null;
        }
        // TODO: SplashProgress.pause/resume to handle possible bad state switching, maybe mark the entire GL state as being dirty?
        if (!transformedName.startsWith("net.specialattack.forge.core.client.GLState") && !transformedName.startsWith("cpw.mods.fml.client.SplashProgress")) {
            ClassReader reader = new ClassReader(original);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor visitor = writer;

            SpACoreGLTransformer.changed = false;

            if (SpACorePlugin.config.stateManager && transformedName.equals("net.minecraft.client.renderer.OpenGlHelper")) {
                visitor = new ClassVisitor(Opcodes.ASM4, visitor) {

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                        if (desc.equals("(IIII)V") && (name.equals("glBlendFunc") || name.equals("func_148821_a") || name.equals("c"))) {
                            return new DummyMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    MethodVisitor mv = this.mv;
                                    if (mv != null) {
                                        SpACoreGLTransformer.changed = true;
                                        /*
                                         * Replace method with proxy:
                                         *
                                         * public static void glBlendFunc(int srcRGB, int destRGB, int srcAlpha, int destAlpha) {
                                         *     GLState.glBlendFunc(srcRGB, destRGB, srcAlpha, destAlpha);
                                         * }
                                         */
                                        int line = 72;
                                        Label l0 = new Label();
                                        mv.visitLabel(l0);
                                        mv.visitLineNumber(9003 + line, l0);
                                        mv.visitVarInsn(Opcodes.ILOAD, 0);
                                        mv.visitVarInsn(Opcodes.ILOAD, 1);
                                        mv.visitVarInsn(Opcodes.ILOAD, 2);
                                        mv.visitVarInsn(Opcodes.ILOAD, 3);
                                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/GLState", "glBlendFunc", "(IIII)V", false);
                                        Label l1 = new Label();
                                        mv.visitLabel(l1);
                                        mv.visitLineNumber(9011 + line, l1);
                                        mv.visitInsn(Opcodes.RETURN);
                                        Label l2 = new Label();
                                        mv.visitLabel(l2);
                                        mv.visitLocalVariable("srcRGB", "I", null, l0, l2, 0);
                                        mv.visitLocalVariable("destRGB", "I", null, l0, l2, 1);
                                        mv.visitLocalVariable("srcAlpha", "I", null, l0, l2, 2);
                                        mv.visitLocalVariable("destAlpha", "I", null, l0, l2, 3);
                                        mv.visitMaxs(4, 4);
                                    }
                                }
                            };
                        }
                        return super.visitMethod(access, name, desc, signature, exceptions);
                    }
                };
            }

            visitor = new ClassVisitor(Opcodes.ASM4, visitor) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);

                    visitor = new MethodVisitor(Opcodes.ASM4, visitor) {

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (opcode == Opcodes.INVOKESTATIC) {
                                for (Replacement replacement : SpACoreGLTransformer.replacements) {
                                    if (desc.equals(replacement.descriptor)) {
                                        if (SpACorePlugin.config.stateManager) {
                                            if (owner.equals(replacement.owner) && name.equals(replacement.name)) {
                                                SpACoreGLTransformer.changed = true;
                                                owner = SpACoreGLTransformer.STATE_MANAGER;
                                                name = replacement.replacement;
                                            }
                                        } else { // If the state manager is disabled, reverse the process to increase speed.
                                            if (owner.equals(SpACoreGLTransformer.STATE_MANAGER) && name.equals(replacement.replacement)) {
                                                SpACoreGLTransformer.changed = true;
                                                owner = replacement.owner;
                                                name = replacement.name;
                                            }
                                        }
                                    }
                                }
                            }
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }

                        @Override // Compatability
                        @SuppressWarnings("deprecation")
                        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                            if (opcode == Opcodes.INVOKESTATIC) {
                                for (Replacement replacement : SpACoreGLTransformer.replacements) {
                                    if (desc.equals(replacement.descriptor)) {
                                        if (SpACorePlugin.config.stateManager) {
                                            if (owner.equals(replacement.owner) && name.equals(replacement.name)) {
                                                SpACoreGLTransformer.changed = true;
                                                owner = SpACoreGLTransformer.STATE_MANAGER;
                                                name = replacement.replacement;
                                            }
                                        } else { // If the state manager is disabled, reverse the process to increase speed.
                                            if (owner.equals(SpACoreGLTransformer.STATE_MANAGER) && name.equals(replacement.replacement)) {
                                                SpACoreGLTransformer.changed = true;
                                                owner = replacement.owner;
                                                name = replacement.name;
                                            }
                                        }
                                    }
                                }
                            }
                            super.visitMethodInsn(opcode, owner, name, desc);
                        }
                    };

                    if (SpACorePlugin.config.stateManager) { // Disabled if the state manager is disabled
                        // 1.7.10 Specific transformer because call lists make the colour state dirty
                        visitor = new SequenceMethodVisitor(Opcodes.ASM4, visitor) {

                            @Override
                            public void visitLdcInsn(Object cst) {
                                super.visitLdcInsn(cst);
                                if (cst instanceof Integer) {
                                    if ((Integer) cst == GL11.GL_COLOR_ARRAY) {
                                        this.flag = true;
                                    }
                                }
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if (this.flag && opcode == Opcodes.INVOKESTATIC && owner.equals("org/lwjgl/opengl/GL11") && name.equals("glDisableClientState")) {
                                    super.visitMethodInsn(opcode, owner, name, desc, false);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, SpACoreGLTransformer.STATE_MANAGER, "resetColor", "()V", false);
                                    SpACoreGLTransformer.changed = true;
                                    SpACorePlugin.LOG.debug("Injected a resetColor call");
                                } else {
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                }
                            }

                            @Override // Compatability
                            @SuppressWarnings("deprecation")
                            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                                if (this.flag && opcode == Opcodes.INVOKESTATIC && owner.equals("org/lwjgl/opengl/GL11") && name.equals("glDisableClientState")) {
                                    super.visitMethodInsn(opcode, owner, name, desc);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, SpACoreGLTransformer.STATE_MANAGER, "resetColor", "()V", false);
                                    SpACoreGLTransformer.changed = true;
                                    SpACorePlugin.LOG.debug("Injected a resetColor call");
                                } else {
                                    super.visitMethodInsn(opcode, owner, name, desc);
                                }
                            }
                        };
                    }
                    return visitor;
                }
            };

            reader.accept(visitor, 0);
            if (SpACoreGLTransformer.changed) {
                if (SpACorePlugin.config.stateManager) {
                    SpACorePlugin.LOG.debug("Inserted GLState calls to " + transformedName);
                } else {
                    SpACorePlugin.LOG.debug("Removed GLState calls from " + transformedName);
                }
                byte[] result = writer.toByteArray();

                if (SpACorePlugin.config.stateManagerDebug) {
                    if (name.indexOf('.') != -1) {
                        String folderName = name.substring(0, name.lastIndexOf('.')).replace('.', File.separatorChar);
                        File folder = new File(this.saveFolder, folderName);
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                    }

                    File output = new File(this.saveFolder, name.replace('.', File.separatorChar) + ".class");

                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(output);
                        out.write(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }

                return result;
            }
        }
        return original;
    }

    private static class Replacement {

        public String owner, name, replacement, descriptor;

        public Replacement(String owner, String name, String replacement, String descriptor) {
            this.owner = owner;
            this.name = name;
            this.replacement = replacement;
            this.descriptor = descriptor;
        }
    }
}
