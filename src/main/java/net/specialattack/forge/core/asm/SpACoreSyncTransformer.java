package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SpACoreSyncTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (!SpACorePlugin.config.loggerTransformer) {
            return original;
        }
        if (original == null) {
            return null;
        }
        if (transformedName.equals("net.minecraft.client.Minecraft")) {
            ClassReader reader = new ClassReader(original);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if ((name.equals("func_71353_a") || name.equals("loadWorld")) && desc.equals("(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V")) {
                        return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {

                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == Opcodes.RETURN) {
                                    super.visitVarInsn(Opcodes.ALOAD, 1);
                                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "net/specialattack/forge/core/client/ClientProxy", "clientLoadWorld", "(Lnet/minecraft/client/multiplayer/WorldClient;)V", false);
                                }
                                super.visitInsn(opcode);
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
