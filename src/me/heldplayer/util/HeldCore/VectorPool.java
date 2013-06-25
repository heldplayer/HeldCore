
package me.heldplayer.util.HeldCore;

import java.util.ArrayList;

public final class VectorPool {

    private static ArrayList<Vector> usedVectors = new ArrayList<Vector>();
    private static ArrayList<Vector> unusedVectors = new ArrayList<Vector>();

    public static Vector getFreeVector() {
        if (unusedVectors.size() == 0) {
            Vector face = new Vector();
            usedVectors.add(face);
            return face;
        }
        else {
            Vector face = unusedVectors.remove(0);
            usedVectors.add(face);
            return face;
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

    public static void unuseVectors() {
        unusedVectors.clear();
        unusedVectors.addAll(usedVectors);
        usedVectors.clear();
    }

}
