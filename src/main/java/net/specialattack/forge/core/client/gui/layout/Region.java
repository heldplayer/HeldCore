package net.specialattack.forge.core.client.gui.layout;

public class Region {

    public static final Region NULL = new Region(0, 0, 0, 0);

    private final int left;
    private final int top;
    private final int width;
    private final int height;

    public Region(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public int getLeft() {
        return this.left;
    }

    public int getTop() {
        return this.top;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Region atZero() {
        return new Region(0, 0, this.getWidth(), this.getHeight());
    }

    public Region offset(int left, int top) {
        return new Region(this.getLeft() + left, this.getTop() + top, this.getWidth(), this.getHeight());
    }

    public Region offset(Location location) {
        return new Region(this.getLeft() + location.left, this.getTop() + location.top, this.getWidth(), this.getHeight());
    }

    public Region expanded(int width, int height) {
        return new Region(this.getLeft(), this.getTop(), this.getWidth() + width, this.getHeight() + height);
    }

    public boolean intersects(Region other) {
        if (other == null) {
            return false;
        }
        int tw = this.getWidth();
        int th = this.getHeight();
        int rw = other.getWidth();
        int rh = other.getHeight();
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.getLeft();
        int ty = this.getTop();
        int rx = other.getLeft();
        int ry = other.getTop();
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
