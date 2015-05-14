package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

public class SpACoreLoggerTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (!SpACorePlugin.loggerTransformer) {
            return original;
        }
        if (original == null) {
            return null;
        }
        if (transformedName.equals("net.minecraft.client.renderer.texture.TextureMap")) {
            ClassReader reader = new ClassReader(original);
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {

                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("func_110571_b") || name.equals("loadTextureAtlas")) {
                        return new MethodVisitor(Opcodes.ASM4, super.visitMethod(access, name, desc, signature, exceptions)) {

                            private boolean checking = false;

                            @Override
                            public void visitLineNumber(int line, Label start) {
                                if (line == 179) {
                                    checking = true;
                                }
                                super.visitLineNumber(line, start);
                            }

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                                if (checking && opcode == Opcodes.INVOKEINTERFACE && owner.equals("org/apache/logging/log4j/Logger") && desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(Throwable.class)))) {
                                    super.visitInsn(Opcodes.POP);
                                    desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class));
                                    checking = false;
                                    SpACorePlugin.LOG.debug("Removed stacktraces from missing textures.");
                                }
                                super.visitMethodInsn(opcode, owner, name, desc, itf);
                            }


                            @Override
                            @SuppressWarnings("deprecation")
                            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                                if (checking && opcode == Opcodes.INVOKEINTERFACE && owner.equals("org/apache/logging/log4j/Logger") && desc.equals(Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class), Type.getType(Throwable.class)))) {
                                    super.visitInsn(Opcodes.POP);
                                    desc = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class));
                                    checking = false;
                                    SpACorePlugin.LOG.debug("Removed stacktraces from missing textures.");
                                }
                                super.visitMethodInsn(opcode, owner, name, desc);
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
