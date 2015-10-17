package net.specialattack.forge.core.client.gui.deprecated.layout;

import java.util.LinkedHashMap;
import java.util.Map;
import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import net.specialattack.util.MathHelper;

public class FlowSGLayoutManager extends SGLayoutManager {

    private final Map<SGComponent, Boolean> components = new LinkedHashMap<SGComponent, Boolean>();
    private FlowDirection direction;
    private FlowLayout layout;

    public FlowSGLayoutManager(FlowDirection direction, FlowLayout layout) {
        if (direction == null) {
            throw new IllegalArgumentException("The flow direction cannot be null");
        }
        if (layout == null) {
            throw new IllegalArgumentException("The flow layout cannot be null");
        }
        this.direction = direction;
        this.layout = layout;
    }

    @Override
    public void performLayout(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot lay out null!");
        }

        int totalWidth = component.getWidth(SizeContext.INNER);
        int totalHeight = component.getHeight(SizeContext.INNER);
        int limitWidth = component.getWidthLimit();
        int limitHeight = component.getHeightLimit();

        int lastPos = 0;
        Map<SGComponent, Location> delayed = new LinkedHashMap<SGComponent, Location>(this.components.size());
        for (Map.Entry<SGComponent, Boolean> entry : this.components.entrySet()) {
            SGComponent current = entry.getKey();
            Region predicted = current.predictSize();
            int height;
            int width;
            if (current.forcePreferredSize() || predicted == null) {
                height = current.getPreferredHeight();
                width = current.getPreferredWidth();
            } else {
                height = MathHelper.max(current.getPreferredHeight(), predicted.getHeight());
                width = MathHelper.max(current.getPreferredWidth(), predicted.getWidth());
            }
            if (height > limitHeight)
                height = limitHeight;
            if (width > limitWidth)
                width = limitWidth;

            if (entry.getValue()) {
                if (this.direction == FlowDirection.HORIZONTAL) {
                    delayed.put(entry.getKey(), new Location(lastPos, width));
                    lastPos += width;
                    limitWidth -= width;
                } else {
                    delayed.put(entry.getKey(), new Location(lastPos, height));
                    lastPos += height;
                    limitHeight -= height;
                }
            } else {
                if (this.direction == FlowDirection.HORIZONTAL) {
                    this.limitComponent(current, width, height);
                    this.positionComponent(current, lastPos, this.layout.decide(totalHeight, height), width, height);
                    lastPos += width;
                    limitWidth -= width;
                } else {
                    this.limitComponent(current, width, height);
                    this.positionComponent(current, this.layout.decide(totalWidth, width), lastPos, width, height);
                    lastPos += height;
                    limitHeight -= height;
                }
            }
        }
        for (Map.Entry<SGComponent, Location> entry : delayed.entrySet()) {
            Location location = entry.getValue();
            if (this.direction == FlowDirection.HORIZONTAL) {
                this.limitComponent(entry.getKey(), location.top, totalHeight);
                this.positionComponent(entry.getKey(), location.left, 0, location.top, totalHeight);
            } else {
                this.limitComponent(entry.getKey(), totalWidth, location.top);
                this.positionComponent(entry.getKey(), 0, location.left, totalWidth, location.top);
            }
        }
    }

    @Override
    public void addComponent(SGComponent component, Object param) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }
        if (param != null && !(param instanceof Boolean)) {
            throw new IllegalArgumentException("The layout argument has to be a boolean value");
        }
        if (param == null) {
            param = Boolean.FALSE;
        }

        this.addComponent(component, (Boolean) param);
    }

    private void addComponent(SGComponent component, Boolean flow) {
        if (this.components.containsKey(component)) {
            throw new IllegalArgumentException("Cannot add the same component twice");
        }

        this.components.put(component, flow);
    }

    @Override
    public void removeComponent(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        this.components.remove(component);
    }

    @Override
    public Region predictSize(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        int preferredWidth = component.getPreferredWidth();
        int preferredHeight = component.getPreferredHeight();
        if (component.forcePreferredSize()) {
            return new Region(0, 0, preferredWidth, preferredHeight);
        }
        int limitWidth = component.getWidthLimit();
        int limitHeight = component.getHeightLimit();

        int width = 0;
        int height = 0;

        for (SGComponent current : this.components.keySet()) { // We only look at the preferred sizes, flowing to fill doesn't matter here
            Region predicted = current.predictSize();
            int currentHeight;
            int currentWidth;
            if (predicted == null) {
                currentHeight = current.getPreferredHeight();
                currentWidth = current.getPreferredWidth();
            } else {
                currentHeight = MathHelper.max(current.getPreferredHeight(), predicted.getHeight());
                currentWidth = MathHelper.max(current.getPreferredWidth(), predicted.getWidth());
            }

            if (this.direction == FlowDirection.HORIZONTAL) {
                width += currentWidth;
                height = MathHelper.max(currentHeight, height);
            } else {
                height += currentHeight;
                width = MathHelper.max(currentWidth, width);
            }
        }
        int borders = (component.getBorderWidth() + component.getOutlineWidth()) * 2;
        width = MathHelper.max(preferredWidth, width);
        height = MathHelper.max(preferredHeight, height);
        return new Region(0, 0, MathHelper.min(width, limitWidth), MathHelper.min(height, limitHeight));
    }

}
