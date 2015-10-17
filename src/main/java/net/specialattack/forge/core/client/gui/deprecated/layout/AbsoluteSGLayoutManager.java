package net.specialattack.forge.core.client.gui.deprecated.layout;

import java.util.HashMap;
import java.util.Map;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import net.specialattack.util.MathHelper;

/**
 * Allows placing of elements at a fixed position and a fixed size.
 * NOTE: only use this if you know for sure the container element will not change sizes,
 * or some elements might get clipped out.
 */
public class AbsoluteSGLayoutManager extends SGLayoutManager {

    private Map<SGComponent, Region> locations = new HashMap<SGComponent, Region>();

    @Override
    public void performLayout(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot lay out null!");
        }

        for (Map.Entry<SGComponent, Region> entry : this.locations.entrySet()) {
            Region region = entry.getValue();
            SGComponent comp = entry.getKey();
            if (region != null) {
                int width = region.getWidth();
                int height = region.getHeight();
                int borders = (comp.getBorderWidth() + comp.getOutlineWidth()) * 2;
                if (width <= 0) {
                    width = comp.getPreferredWidth() + borders;
                }
                if (height <= 0) {
                    height = comp.getPreferredHeight() + borders;
                }
                this.limitComponent(comp, width, height);
                this.positionComponent(comp, region.getLeft(), region.getTop(), width, height);
            } else {
                this.limitComponent(comp, 0, 0);
                this.positionComponent(comp, 0, 0, 0, 0);
            }
        }
        /*
        Region region = locations.get(component);
        if (region != null) {
            int width = region.width;
            int height = region.height;
            int borders = (component.getBorderWidth() + component.getOutlineWidth()) * 2;
            if (width <= 0) {
                width = component.getPreferredWidth() + borders;
            }
            if (height <= 0) {
                height = component.getPreferredHeight() + borders;
            }
            this.limitComponent(component, width, height);
            this.positionComponent(component, region.left, region.top, width, height);
        } else {
            this.limitComponent(component, 0, 0);
            this.positionComponent(component, 0, 0, 0, 0);
        }
        */
    }

    @Override
    public void addComponent(SGComponent component, Object param) {
        if (param != null && !(param instanceof Region)) {
            throw new IllegalArgumentException("The layout argument has to be an instance of Location");
        }
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        this.addComponent(component, (Region) param);
    }

    private void addComponent(SGComponent component, Region param) {
        this.locations.put(component, param);
    }

    @Override
    public void removeComponent(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        this.locations.remove(component);
    }

    @Override
    public Region predictSize(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        int width = component.getPreferredWidth();
        int height = component.getPreferredHeight();
        if (component.forcePreferredSize()) {
            return new Region(0, 0, width, height);
        }
        int limitWidth = component.getWidthLimit();
        int limitHeight = component.getHeightLimit();

        for (SGComponent current : component.getChildren()) {
            Region predicted = current.predictSize();
            Region stored = this.locations.get(current);
            if (stored != null) {
                width = MathHelper.max(width, stored.getLeft() + stored.getWidth());
                height = MathHelper.max(height, stored.getTop() + stored.getHeight());
            } else if (predicted != null) {
                width = MathHelper.max(width, predicted.getLeft() + predicted.getWidth());
                height = MathHelper.max(height, predicted.getTop() + predicted.getHeight());
            }
        }
        return new Region(0, 0, MathHelper.min(width, limitWidth), MathHelper.min(height, limitHeight));
    }
}
