package net.specialattack.forge.core.client.shader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public final class RenderMatrix {

    public final double[][] matrix = new double[4][4];

    public RenderMatrix() {
    }

    public RenderMatrix clone(RenderMatrix other) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.matrix[i], 0, this.matrix[i], 0, 4);
        }
        return this;
    }

    public RenderMatrix multiply(RenderMatrix other) {
        RenderMatrix temp = new RenderMatrix().clone(this);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.matrix[i][j] = 0.0D;
                for (int k = 0; k < 4; k++) {
                    this.matrix[i][j] += temp.matrix[i][k] * other.matrix[k][j];
                }
            }
        }
        return this;
    }

    public RenderMatrix nill() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.matrix[i][j] = 0.0D;
            }
        }
        return this;
    }

    public static RenderMatrix createIdentity() {
        return new RenderMatrix().identity();
    }

    public RenderMatrix identity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.matrix[i][j] = i == j ? 1.0D : 0.0D;
            }
        }
        return this;
    }

    public static RenderMatrix createTranslation(Vec3 translation) {
        RenderMatrix result = new RenderMatrix().identity();
        result.matrix[3][0] = translation.xCoord;
        result.matrix[3][1] = translation.yCoord;
        result.matrix[3][2] = translation.zCoord;
        return result;
    }

    public RenderMatrix translate(Vec3 translation) {
        RenderMatrix other = RenderMatrix.createTranslation(translation);
        return this.multiply(other);
    }

    public static RenderMatrix createScale(Vec3 scale) {
        RenderMatrix result = new RenderMatrix();
        result.matrix[0][0] = scale.xCoord;
        result.matrix[1][1] = scale.yCoord;
        result.matrix[2][2] = scale.zCoord;
        result.matrix[3][3] = 1.0D;
        return result;
    }

    public RenderMatrix scale(Vec3 scale) {
        RenderMatrix other = RenderMatrix.createScale(scale);
        return this.multiply(other);
    }

    public static RenderMatrix createRotation(Vec3 rotation) {
        return createRotationX(rotation.xCoord).rotateY(rotation.yCoord).rotateZ(rotation.zCoord);
    }

    public RenderMatrix rotate(Vec3 rotation) {
        return this.multiply(RenderMatrix.createRotation(rotation));
    }

    public static RenderMatrix createRotationX(double angle) {
        RenderMatrix result = new RenderMatrix().identity();
        result.matrix[1][1] = result.matrix[2][2] = Math.cos(angle * 180.0D / Math.PI);
        result.matrix[1][2] = Math.sin(angle * 180.0D / Math.PI);
        result.matrix[2][1] = -result.matrix[2][3];
        return result;
    }

    public RenderMatrix rotateX(double angle) {
        return this.multiply(createRotationX(angle));
    }

    public static RenderMatrix createRotationY(double angle) {
        RenderMatrix result = new RenderMatrix().identity();
        result.matrix[0][0] = result.matrix[2][2] = Math.cos(angle * 180.0D / Math.PI);
        result.matrix[2][0] = Math.sin(angle * 180.0D / Math.PI);
        result.matrix[0][2] = -result.matrix[2][0];
        return result;
    }

    public RenderMatrix rotateY(double angle) {
        return this.multiply(createRotationY(angle));
    }

    public static RenderMatrix createRotationZ(double angle) {
        RenderMatrix result = new RenderMatrix().identity();
        result.matrix[0][0] = result.matrix[1][1] = Math.cos(angle * 180.0D / Math.PI);
        result.matrix[0][1] = Math.sin(angle * 180.0D / Math.PI);
        result.matrix[1][0] = -result.matrix[0][1];
        return result;
    }

    public RenderMatrix rotateZ(double angle) {
        return this.multiply(createRotationZ(angle));
    }

    public Vec3 getTransformed(Vec3 input) {
        double[] pos = new double[4];
        for (int i = 0; i < 4; i++) {
            pos[i] += input.xCoord * this.matrix[0][i];
            pos[i] += input.yCoord * this.matrix[1][i];
            pos[i] += input.zCoord * this.matrix[2][i];
            pos[i] += this.matrix[3][i];
        }
        return Vec3.createVectorHelper(pos[0] / pos[3], pos[1] / pos[3], pos[2] / pos[3]);
    }

}
