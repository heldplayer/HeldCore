
package me.heldplayer.util.HeldCore.client;

import me.heldplayer.util.HeldCore.MathHelper;
import me.heldplayer.util.HeldCore.Vector;
import me.heldplayer.util.HeldCore.VectorPool;

/**
 * A helper class used for rendering in 3D-space
 * 
 * @author heldplayer
 * 
 */
public final class RenderHelper {

    public static Vector[] getBezierPlanePoints(Vector[][] points, int pointCount) {
        int actualLength = 0;
        for (int i = 0; i < points[0].length; i++) {
            if (points[0][i] == null) {
                break;
            }
            actualLength++;
        }
        Vector[][] list = new Vector[pointCount + 1][];
        for (int i = 0; i < list.length; i++) {
            list[i] = VectorPool.getFreeVectorArray(actualLength);
        }

        for (int j = 0; j <= pointCount; j++) {
            for (int i = 0; i < points.length; i++) {
                list[j][i] = MathHelper.bezier(points[i], (double) j / (double) pointCount);
            }
        }

        Vector[] result = VectorPool.getFreeVectorArray(list.length * (pointCount + 1));

        for (int j = 0; j <= pointCount; j++) {
            for (int i = 0; i < list.length; i++) {
                result[i + j * (pointCount + 1)] = MathHelper.bezier(list[i], (double) j / (double) pointCount);
            }
        }

        return result;
    }

    public static void renderSphere() {

    }

}
