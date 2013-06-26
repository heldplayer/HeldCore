
package me.heldplayer.util.HeldCore;

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
        if (unusedVectors.size() == 0) {
            Vector vector = new Vector();
            usedVectors.add(vector);
            return vector;
        }
        else {
            Vector vector = unusedVectors.remove(0);
            vector.posX = 0.0D;
            vector.posY = 0.0D;
            vector.posZ = 0.0D;
            usedVectors.add(vector);
            return vector;
        }
    }

    public static Vector getFreeVector(double x, double y, double z) {
        if (unusedVectors.size() == 0) {
            Vector vector = new Vector(x, y, z);
            usedVectors.add(vector);
            return vector;
        }
        else {
            Vector vector = unusedVectors.remove(0);
            vector.posX = x;
            vector.posY = y;
            vector.posZ = z;
            usedVectors.add(vector);
            return vector;
        }
    }

    public static Vector[] getFreeVectorArray(int size) {
        LinkedList<Vector[]> used = usedBigVectorArrays;
        LinkedList<Vector[]> unused = unusedBigVectorArrays;
        if (size <= 16) {
            size = 16;
            used = usedTinyVectorArrays;
            unused = unusedTinyVectorArrays;
        }
        else if (size <= 256) {
            size = 256;
            used = usedSmallVectorArrays;
            unused = unusedSmallVectorArrays;
        }
        else {
            if (size <= bigArraySize) {
                size = bigArraySize;
            }
            else {
                bigArraySize = size;
                usedBigVectorArrays.clear();
                unusedBigVectorArrays.clear();
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
        unusedVectors.clear();
        unusedVectors.addAll(usedVectors);
        usedVectors.clear();

        unusedTinyVectorArrays.clear();
        unusedTinyVectorArrays.addAll(usedTinyVectorArrays);
        usedTinyVectorArrays.clear();

        unusedSmallVectorArrays.clear();
        unusedSmallVectorArrays.addAll(usedSmallVectorArrays);
        usedSmallVectorArrays.clear();

        unusedBigVectorArrays.clear();
        unusedBigVectorArrays.addAll(usedBigVectorArrays);
        usedBigVectorArrays.clear();
    }

}
