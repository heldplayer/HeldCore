
package net.specialattack.util;

import java.util.LinkedList;

public final class VectorPool {

    private static LinkedList<Vector> usedVectors = new LinkedList<Vector>();
    private static LinkedList<Vector> unusedVectors = new LinkedList<Vector>();

    private static LinkedList<Vector[]> usedTinyVectorArrays = new LinkedList<Vector[]>();
    private static LinkedList<Vector[]> unusedTinyVectorArrays = new LinkedList<Vector[]>();

    private static LinkedList<Vector[]> usedSmallVectorArrays = new LinkedList<Vector[]>();
    private static LinkedList<Vector[]> unusedSmallVectorArrays = new LinkedList<Vector[]>();

    private static LinkedList<Vector[]> usedBigVectorArrays = new LinkedList<Vector[]>();
    private static LinkedList<Vector[]> unusedBigVectorArrays = new LinkedList<Vector[]>();

    private static int bigArraySize = 256;

    public static Vector getFreeVector() {
        if (VectorPool.unusedVectors.size() == 0) {
            Vector vector = new Vector();
            VectorPool.usedVectors.add(vector);
            return vector;
        }
        else {
            Vector vector = VectorPool.unusedVectors.remove(0);
            vector.posX = 0.0D;
            vector.posY = 0.0D;
            vector.posZ = 0.0D;
            VectorPool.usedVectors.add(vector);
            return vector;
        }
    }

    public static Vector getFreeVector(double x, double y, double z) {
        if (VectorPool.unusedVectors.size() == 0) {
            Vector vector = new Vector(x, y, z);
            VectorPool.usedVectors.add(vector);
            return vector;
        }
        else {
            Vector vector = VectorPool.unusedVectors.remove(0);
            vector.posX = x;
            vector.posY = y;
            vector.posZ = z;
            VectorPool.usedVectors.add(vector);
            return vector;
        }
    }

    public static Vector[] getFreeVectorArray(int size) {
        LinkedList<Vector[]> used = VectorPool.usedBigVectorArrays;
        LinkedList<Vector[]> unused = VectorPool.unusedBigVectorArrays;
        if (size <= 16) {
            size = 16;
            used = VectorPool.usedTinyVectorArrays;
            unused = VectorPool.unusedTinyVectorArrays;
        }
        else if (size <= 256) {
            size = 256;
            used = VectorPool.usedSmallVectorArrays;
            unused = VectorPool.unusedSmallVectorArrays;
        }
        else {
            if (size <= VectorPool.bigArraySize) {
                size = VectorPool.bigArraySize;
            }
            else {
                VectorPool.bigArraySize = size;
                VectorPool.usedBigVectorArrays.clear();
                VectorPool.unusedBigVectorArrays.clear();
            }
        }

        if (unused.size() == 0) {
            Vector[] vectors = new Vector[size];
            used.add(vectors);
            return vectors;
        }
        else {
            Vector[] vectors = unused.remove(0);
            for (int i = 0; i < vectors.length; i++) {
                vectors[i] = null;
            }
            used.add(vectors);
            return vectors;
        }
    }

    public static void unuseVectors() {
        VectorPool.unusedVectors.clear();
        VectorPool.unusedVectors.addAll(VectorPool.usedVectors);
        VectorPool.usedVectors.clear();

        VectorPool.unusedTinyVectorArrays.clear();
        VectorPool.unusedTinyVectorArrays.addAll(VectorPool.usedTinyVectorArrays);
        VectorPool.usedTinyVectorArrays.clear();

        VectorPool.unusedSmallVectorArrays.clear();
        VectorPool.unusedSmallVectorArrays.addAll(VectorPool.usedSmallVectorArrays);
        VectorPool.usedSmallVectorArrays.clear();

        VectorPool.unusedBigVectorArrays.clear();
        VectorPool.unusedBigVectorArrays.addAll(VectorPool.usedBigVectorArrays);
        VectorPool.usedBigVectorArrays.clear();
    }

}
