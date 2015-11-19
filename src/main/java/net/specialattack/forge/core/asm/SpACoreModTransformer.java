package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.specialattack.forge.core.SpACoreMod;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class SpACoreModTransformer implements IClassTransformer {

    private static final String type = Type.getInternalName(SpACoreMod.class);
    private static final String[] methods = new String[] { "preInit", "init", "postInit" };
    private static final Type[] methodTypes = new Type[] { Type.getType("Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;"), Type.getType("Lnet/minecraftforge/fml/common/event/FMLInitializationEvent;"), Type.getType("Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;") };

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (original == null) {
            return null;
        }
        ClassReader reader = new ClassReader(original);

        if (reader.getSuperName().equals(SpACoreModTransformer.type)) {
            ClassNode node = new ClassNode(Opcodes.ASM4);
            reader.accept(node, 0);

            boolean[] methodsPresent = new boolean[SpACoreModTransformer.methods.length];

            for (MethodNode method : node.methods) {
                for (int i = 0; i < SpACoreModTransformer.methods.length; i++) {
                    if (method.name.equals(SpACoreModTransformer.methods[i])) {
                        methodsPresent[i] = true;
                    }
                }
            }

            boolean changed = false;
            for (int i = 0; i < SpACoreModTransformer.methods.length; i++) {
                if (!methodsPresent[i]) {
                    changed = true;
                    Type methodType = SpACoreModTransformer.methodTypes[i];
                    String descriptor = Type.getMethodDescriptor(Type.VOID_TYPE, methodType);
                    /*
                     * Add
                     * @Mod.EventHandler
                     * public void [pre|post|]init(FML[Pre|Post|]InitializationEvent event) {
                     *     super.[pre|post|]init(event);
                     * }
                     */
                    MethodNode method = new MethodNode(Opcodes.ASM4, Opcodes.ACC_PUBLIC, SpACoreModTransformer.methods[i], descriptor, null, null);
                    AnnotationVisitor annotation = method.visitAnnotation("Lnet/minecraftforge/fml/common/Mod$EventHandler;", true);
                    annotation.visitEnd();
                    method.visitCode();
                    Label l0 = new Label();
                    method.visitLabel(l0);
                    method.visitVarInsn(Opcodes.ALOAD, 0);
                    method.visitVarInsn(Opcodes.ALOAD, 1);
                    method.visitMethodInsn(Opcodes.INVOKESPECIAL, "net/specialattack/forge/core/SpACoreMod", SpACoreModTransformer.methods[i], descriptor, false);
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
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                return writer.toByteArray();
            }
        }

        return original;
    }
}
