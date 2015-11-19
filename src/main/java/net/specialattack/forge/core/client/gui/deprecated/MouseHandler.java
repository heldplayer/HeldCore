package net.specialattack.forge.core.client.gui.deprecated;

public abstract class MouseHandler {

    /**
     * Called when the element is clicked
     *
     * @param mouseX
     *         The X position of the mouse
     * @param mouseY
     *         The Y position of the mouse
     * @param button
     *         The mouse button
     *
     * @return Return true to cancel the default behaviour of the element. (true == handled)
     */
    public boolean onClick(int mouseX, int mouseY, int button) {
        return false;
    }

    public void onMouseDown(int mouseX, int mouseY, int button) {
    }

    public void onMouseUp(int mouseX, int mouseY, int button) {
    }

    public void onMouseDrag(int fromX, int fromY, int toX, int toY, int button, long pressTime) {
    }

}
