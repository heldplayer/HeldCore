package net.specialattack.forge.core.client.gui.layout;

public class Region {

    public static final Region NULL = new Region(0, 0, 0, 0);

    public final int left, top, width, height;

    public Region(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public Region atZero() {
        return new Region(0, 0, this.width, this.height);
    }

    public Region offset(int left, int top) {
        return new Region(this.left + left, this.top + top, this.width, this.height);
    }

    public boolean intersects(Region other) {
        if (other == null) {
            return false;
        }
        int tw = this.width;
        int th = this.height;
        int rw = other.width;
        int rh = other.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.left;
        int ty = this.top;
        int rx = other.left;
        int ry = other.top;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }

}
