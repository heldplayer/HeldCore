package net.specialattack.forge.core.client.gui.layout;

import net.specialattack.forge.core.client.gui.element.SGComponent;

public abstract class SGLayoutManager {

    /**
     * Performs the layout of all child components of the parameter component.
     *
     * @param component
     *         The parent component.
     */
    public abstract void performLayout(SGComponent component);

    public abstract void addComponent(SGComponent component, Object param);

    public abstract void removeComponent(SGComponent component);

    /**
     * Attempt to predict the size of the component that want to layout.
     *
     * @return A location representing the predicted size, or null if it is not possible to predict.
     */
    public abstract Region predictSize(SGComponent component);

    protected final void positionComponent(SGComponent component, int left, int top, int width, int height) {
        if (component != null) {
            component.setDimensions(left, top, width, height);
        }
    }

    protected final void limitComponent(SGComponent component, int width, int height) {
        if (component != null) {
            component.setSizeRestrictions(width, height);
        }
    }

}
