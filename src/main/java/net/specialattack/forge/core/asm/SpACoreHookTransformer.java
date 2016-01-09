package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SpACoreHookTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (original == null) {
            return null;
        }
        if (transformedName.equals("net.minecraft.client.Minecraft")) {
            ClassReader reader = new ClassReader(original);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM5, writer) {

                @Override
                @SuppressWarnings("deprecation")
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if ((name.equals("func_71353_a") || name.equals("loadWorld")) && desc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) {
                        return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {

                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    super.visitVarInsn(Opcodes.ALOAD, 1);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientHooks", "clientLoadWorld", "(Lnet/minecraft/client/multiplayer/WorldClient;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }
                    if ((name.equals("func_71411_J") || name.equals("runGameLoop")) && desc.equals("()V")) {
                        return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                                if (this.check(opcode, owner, name, desc)) {
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientHooks", "framebufferRenderPre", "()V", false);
                                    super.visitMethodInsn(opcode, owner, name, desc);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientHooks", "framebufferRenderPost", "()V", false);
                                } else {
                                    super.visitMethodInsn(opcode, owner, name, desc);
                                }
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if (this.check(opcode, owner, name, desc)) {
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientHooks", "framebufferRenderPre", "()V", false);
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientHooks", "framebufferRenderPost", "()V", false);
                                } else {
                                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                                }
                            }

                            private boolean check(int opcode, String owner, String name, String desc) {
                                return opcode == Opcodes.INVOKEVIRTUAL && owner.equals("net/minecraft/client/shader/Framebuffer") && desc.equals("(II)V") && (name.equals("func_147615_c") || name.equals("framebufferRender"));
                            }
                        };
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
