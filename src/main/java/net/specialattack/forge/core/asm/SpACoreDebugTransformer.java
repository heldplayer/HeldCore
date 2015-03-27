package net.specialattack.forge.core.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class SpACoreDebugTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] original) {
        if (!SpACorePlugin.debug || !transformedName.startsWith("net.minecraft.")) {
            return original;
        }
        ClassReader reader = new ClassReader(original);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        node.access = fixAccess(node.access);
        for (FieldNode fieldNode : node.fields) {
            if (fieldNode.name.startsWith("__")) {
                continue;
            }
            fieldNode.access = fixAccess(fieldNode.access);
        }
        for (MethodNode methodNode : node.methods) {
            if (methodNode.name.startsWith("__")) {
                continue;
            }
            methodNode.access = fixAccess(methodNode.access);
        }

        node.accept(writer);
        return writer.toByteArray();
    }

    private static int fixAccess(int start) {
        return (start & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
    }

}
