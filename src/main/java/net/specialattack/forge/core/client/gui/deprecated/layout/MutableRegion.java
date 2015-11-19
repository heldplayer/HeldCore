package net.specialattack.forge.core.client.gui.deprecated.layout;

public class MutableRegion extends Region {

    private int left;
    private int top;
    private int width;
    private int height;

    public MutableRegion(int left, int top, int width, int height) {
        super(left, top, width, height);
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getLeft() {
        return this.left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    @Override
    public int getTop() {
        return this.top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
