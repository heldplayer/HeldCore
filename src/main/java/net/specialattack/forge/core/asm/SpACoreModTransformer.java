package net.specialattack.forge.core.asm;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.launchwrapper.IClassTransformer;
import net.specialattack.forge.core.SpACoreMod;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class SpACoreModTransformer implements IClassTransformer {

    private static final String type = Type.getInternalName(SpACoreMod.class);
    private static final String[] methods = new String[] { "preInit", "init", "postInit" };
    private static final Type[] methodTypes = new Type[] { Type.getType(FMLPreInitializationEvent.class), Type.getType(FMLInitializationEvent.class), Type.getType(FMLPostInitializationEvent.class) };

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        ClassReader reader = new ClassReader(original);

        if (reader.getSuperName().equals(type)) {
            ClassNode node = new ClassNode();
            reader.accept(node, 0);

            boolean[] methodsPresent = new boolean[methods.length];

            for (MethodNode method : node.methods) {
                for (int i = 0; i < methods.length; i++) {
                    if (method.name.equals(methods[i])) {
                        methodsPresent[i] = true;
                    }
                }
            }

            boolean changed = false;
            for (int i = 0; i < methods.length; i++) {
                if (!methodsPresent[i]) {
                    changed = true;
                    Type methodType = methodTypes[i];
                    String descriptor = Type.getMethodDescriptor(Type.VOID_TYPE, methodType);
                    MethodNode method = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, methods[i], descriptor, null, null);
                    AnnotationVisitor annotation = method.visitAnnotation("Lcpw/mods/fml/common/Mod$EventHandler;", true);
                    annotation.visitEnd();
                    method.visitCode();
                    Label l0 = new Label();
                    method.visitLabel(l0);
                    method.visitVarInsn(Opcodes.ALOAD, 0);
                    method.visitVarInsn(Opcodes.ALOAD, 1);
                    method.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/specialattack/forge/core/SpACoreMod", methods[i], descriptor);
                    method.visitInsn(Opcodes.RETURN);
                    Label l2 = new Label();
                    method.visitLabel(l2);
                    method.visitLocalVariable("this", Type.getObjectType(node.name).getDescriptor(), null, l0, l2, 0);
                    method.visitLocalVariable("event", methodType.getDescriptor(), null, l0, l2, 1);
                    method.visitMaxs(2, 2);
                    method.visitEnd();
                    node.methods.add(method);
                }
            }

            if (changed) {
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                return writer.toByteArray();
            }
        }

        return original;
    }
}
