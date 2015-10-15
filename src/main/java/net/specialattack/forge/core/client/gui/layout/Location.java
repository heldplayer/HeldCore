package net.specialattack.forge.core.client.gui.layout;

public class Location {

    public static final Location ZERO = new Location(0, 0);

    public final int left, top;

    public Location(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public Location invert() {
        return new Location(-this.left, -this.top);
    }

    @Override
    public String toString() {
        return "Location{" +
                "left=" + this.left +
                ", top=" + this.top +
                '}';
    }
}
