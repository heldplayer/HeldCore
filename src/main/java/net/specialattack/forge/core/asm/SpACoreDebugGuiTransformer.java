package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SpACoreDebugGuiTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (!SpACorePlugin.debugScreen) {
            return original;
        }
        if (original == null) {
            return null;
        }
        if (transformedName.equals("net.minecraftforge.client.GuiIngameForge")) {
            ClassReader reader = new ClassReader(original);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (desc.equals("(II)V")) {
                        if (name.equals("renderHUDText")) {
                            return new DummyMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    MethodVisitor mv = this.mv;
                                    if (mv != null) {
                                    /*
                                     * Replace method with proxy:
                                     *
                                     * public static void renderHUDText(int width, int height) {
                                     *     RenderReplacements.renderHUDText(this.eventParent, width, height);
                                     * }
                                     */
                                        mv.visitCode();
                                        Label l0 = new Label();
                                        mv.visitLabel(l0);
                                        mv.visitLineNumber(-1, l0);
                                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                                        mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "eventParent", "Lnet/minecraftforge/client/event/RenderGameOverlayEvent;");
                                        mv.visitVarInsn(Opcodes.ILOAD, 1);
                                        mv.visitVarInsn(Opcodes.ILOAD, 2);
                                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/RenderReplacements", "renderHUDText", "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent;II)V", false);
                                        mv.visitInsn(Opcodes.RETURN);
                                        Label l2 = new Label();
                                        mv.visitLabel(l2);
                                        mv.visitLocalVariable("this", "Lnet/minecraftforge/client/GuiIngameForge;", null, l0, l2, 0);
                                        mv.visitLocalVariable("width", "I", null, l0, l2, 1);
                                        mv.visitLocalVariable("height", "I", null, l0, l2, 2);
                                        mv.visitMaxs(3, 3);
                                        mv.visitEnd();
                                    }
                                    SpACorePlugin.LOG.debug("Replaced the debug GUI renderer");
                                }
                            };
                        } else if (name.equals("renderCrosshairs")) {
                            return new DummyMethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {
                                @Override
                                public void visitCode() {
                                    super.visitCode();
                                    MethodVisitor mv = this.mv;
                                    if (mv != null) {
                                    /*
                                     * Replace method with proxy:
                                     *
                                     * public static void renderHUDText(int width, int height) {
                                     *     RenderReplacements.renderHUDText(this.eventParent, width, height);
                                     * }
                                     */
                                        mv.visitCode();
                                        Label l0 = new Label();
                                        mv.visitLabel(l0);
                                        mv.visitLineNumber(-1, l0);
                                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                                        mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "eventParent", "Lnet/minecraftforge/client/event/RenderGameOverlayEvent;");
                                        mv.visitVarInsn(Opcodes.ILOAD, 1);
                                        mv.visitVarInsn(Opcodes.ILOAD, 2);
                                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/RenderReplacements", "renderCrosshairs", "(Lnet/minecraftforge/client/event/RenderGameOverlayEvent;II)V", false);
                                        mv.visitInsn(Opcodes.RETURN);
                                        Label l2 = new Label();
                                        mv.visitLabel(l2);
                                        mv.visitLocalVariable("this", "Lnet/minecraftforge/client/GuiIngameForge;", null, l0, l2, 0);
                                        mv.visitLocalVariable("width", "I", null, l0, l2, 1);
                                        mv.visitLocalVariable("height", "I", null, l0, l2, 2);
                                        mv.visitMaxs(3, 3);
                                        mv.visitEnd();
                                    }
                                    SpACorePlugin.LOG.debug("Replaced the crosshair renderer");
                                }
                            };
                        }
                    }
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
            };

            reader.accept(visitor, 0);
            return writer.toByteArray();
        }
        return original;
    }
}
