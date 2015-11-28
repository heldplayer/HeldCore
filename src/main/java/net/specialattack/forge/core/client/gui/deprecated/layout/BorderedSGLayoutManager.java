package net.specialattack.forge.core.client.gui.deprecated.layout;

import net.specialattack.forge.core.client.gui.deprecated.SizeContext;
import net.specialattack.forge.core.client.gui.deprecated.element.SGComponent;
import net.specialattack.util.math.MathHelper;

/**
 * Allows the use of a layout with 5 positions that can be used.
 * Each position can hold 1 component, if you need more,
 * you will need to use a panel and add each component to it like you need it
 */
public class BorderedSGLayoutManager extends SGLayoutManager {

    private SGComponent[] components = new SGComponent[5];

    @Override
    public void performLayout(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Cannot lay out null!");
        }
        int limitWidth = component.getWidthLimit();
        int limitHeight = component.getHeightLimit();
        int availableWidth = component.getWidth(SizeContext.BORDER);
        int availableHeight = component.getHeight(SizeContext.BORDER);
        int totalHeight = MathHelper.min(limitHeight, availableHeight), totalWidth = MathHelper.min(limitWidth, availableWidth);
        if (availableWidth <= 0 || availableHeight <= 0) {
            this.positionComponent(this.components[0], 0, 0, 0, 0);
            this.positionComponent(this.components[1], 0, 0, 0, 0);
            this.positionComponent(this.components[2], 0, 0, 0, 0);
            this.positionComponent(this.components[3], 0, 0, 0, 0);
            this.positionComponent(this.components[4], 0, 0, 0, 0);
            return;
        }
        boolean[] delayed = new boolean[4];
        int[][] matrix = new int[5][4];
        if (this.components[0] != null) {
            Region predicted = this.components[0].predictSize();
            int height;
            if (predicted == null) {
                height = this.components[0].getPreferredHeight();
            } else {
                height = MathHelper.max(this.components[0].getPreferredHeight(), predicted.getHeight());
            }

            if (height > 0) {
                height = MathHelper.min(height, availableHeight);
                matrix[0] = new int[] { 0, 0, availableWidth, height };
                availableHeight -= height;
            } else {
                delayed[0] = true;
            }
        } else {
            matrix[0] = new int[] { 0, 0, availableWidth, 0 };
        }
        if (this.components[1] != null) {
            Region predicted = this.components[1].predictSize();
            int height;
            if (predicted == null) {
                height = this.components[1].getPreferredHeight();
            } else {
                height = MathHelper.max(this.components[1].getPreferredHeight(), predicted.getHeight());
            }

            if (height > 0) {
                height = MathHelper.min(height, availableHeight);
                matrix[1] = new int[] { 0, totalHeight - height, availableWidth, totalHeight };
                availableHeight -= height;
            } else {
                delayed[1] = true;
            }
        } else {
            matrix[1] = new int[] { 0, totalHeight, availableWidth, totalHeight };
        }
        if (this.components[2] != null) {
            Region predicted = this.components[2].predictSize();
            int width;
            if (predicted == null) {
                width = this.components[2].getPreferredWidth();
            } else {
                width = MathHelper.max(this.components[2].getPreferredWidth(), predicted.getWidth());
            }

            if (width > 0) {
                width = MathHelper.min(width, availableWidth);
                matrix[2] = new int[] { 0, matrix[0][3], width, totalHeight + matrix[1][1] - matrix[1][3] };
                availableWidth -= width;
            } else {
                delayed[2] = true;
            }
        } else {
            matrix[2] = new int[] { 0, matrix[0][3], 0, totalHeight + matrix[1][1] - matrix[1][3] };
        }
        if (this.components[3] != null) {
            Region predicted = this.components[3].predictSize();
            int width;
            if (predicted == null) {
                width = this.components[3].getPreferredWidth();
            } else {
                width = MathHelper.max(this.components[3].getPreferredWidth(), predicted.getWidth());
            }

            if (width > 0) {
                width = MathHelper.min(width, availableWidth);
                matrix[3] = new int[] { totalWidth - width, matrix[0][3], totalWidth, totalHeight + matrix[1][1] - matrix[1][3] };
                availableWidth -= width;
            } else {
                delayed[3] = true;
            }
        } else {
            matrix[3] = new int[] { totalWidth, matrix[0][3], totalWidth, totalHeight + matrix[1][1] - matrix[1][3] };
        }
        if (this.components[4] != null) {
            if (availableWidth > 0 && availableHeight > 0) {
                int left = matrix[2][2];
                int top = matrix[0][3];
                int right = matrix[3][0];
                int bottom = matrix[1][1];
                if (delayed[0] || delayed[1]) {
                    int height = this.components[4].getPreferredHeight();

                    if (height <= 0) {
                        Region predicted = this.components[4].predictSize();
                        height = predicted.getHeight();
                    }

                    if (height > 0) {
                        if (delayed[0] && delayed[1]) {
                            int sharedHeight = (availableHeight - height) / 2;
                            top = matrix[0][3] = sharedHeight;
                            bottom = matrix[3][0] = totalHeight - sharedHeight;
                        } else if (delayed[0]) {
                            top = matrix[0][3] = availableHeight - height;
                        } else {
                            bottom = matrix[3][0] = availableHeight - height;
                        }
                        delayed[0] = delayed[1] = false;
                    }
                }
                if (delayed[2] || delayed[3]) {
                    int width = this.components[4].getPreferredWidth();

                    if (width <= 0) {
                        Region predicted = this.components[4].predictSize();
                        width = predicted.getWidth();
                    }

                    if (width > 0) {
                        if (delayed[2] && delayed[3]) {
                            int sharedWidth = (availableWidth - width) / 2;
                            left = matrix[2][2] = sharedWidth;
                            right = matrix[1][1] = totalHeight - sharedWidth;
                        } else if (delayed[2]) {
                            left = matrix[2][2] = availableWidth - width;
                        } else {
                            right = matrix[1][1] = availableWidth - width;
                        }
                        delayed[0] = delayed[1] = false;
                    }
                }
                matrix[4] = new int[] { left, top, right, bottom };
            }
        }
        if (delayed[0] || delayed[1]) {
            int sharedHeight = availableHeight / 3;
            matrix[4][1] = matrix[0][3] = sharedHeight;
            matrix[4][3] = matrix[3][0] = totalHeight - sharedHeight;
        }
        if (delayed[2] || delayed[3]) {
            int sharedWidth = availableWidth / 3;
            matrix[4][0] = matrix[2][2] = sharedWidth;
            matrix[4][2] = matrix[1][1] = totalHeight - sharedWidth;
        }
        for (int i = 0; i < 5; i++) {
            this.limitComponent(this.components[i], matrix[i][2] - matrix[i][0], matrix[i][3] - matrix[i][1]);
            this.positionComponent(this.components[i], matrix[i][0], matrix[i][1], matrix[i][2] - matrix[i][0], matrix[i][3] - matrix[i][1]);
        }
    }

    @Override
    public void addComponent(SGComponent component, Object param) {
        if (param == null || !(param instanceof BorderedSGLayoutManager.Border)) {
            throw new IllegalArgumentException("The layout argument has to be an instance of Border");
        }
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        this.addComponent(component, (BorderedSGLayoutManager.Border) param);
    }

    private void addComponent(SGComponent component, BorderedSGLayoutManager.Border param) {
        this.components[(param).ordinal()] = component;
    }

    @Override
    public void removeComponent(SGComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("The component argument cannot be null");
        }

        for (int i = 0; i < this.components.length; i++) {
            if (this.components[i] == component) {
                this.components[i] = null;
            }
        }
    }

    @Override
    public Region predictSize(SGComponent component) {
        if (component.forcePreferredSize()) {
            return new Region(0, 0, component.getPreferredWidth(), component.getPreferredHeight());
        }
        int limitWidth = component.getWidthLimit();
        int limitHeight = component.getHeightLimit();
        int width = 0;
        int height = 0;
        for (BorderedSGLayoutManager.Border border : BorderedSGLayoutManager.Border.values()) {
            SGComponent current = this.components[border.ordinal()];
            if (current != null) {
                Region predicted = current.predictSize();
                if (border == BorderedSGLayoutManager.Border.TOP || border == BorderedSGLayoutManager.Border.BOTTOM || border == BorderedSGLayoutManager.Border.CENTER) {
                    if (predicted == null) {
                        height += current.getPreferredHeight();
                    } else {
                        height += MathHelper.max(current.getPreferredHeight(), predicted.getHeight());
                    }
                }
                if (border == BorderedSGLayoutManager.Border.LEFT || border == BorderedSGLayoutManager.Border.RIGHT || border == BorderedSGLayoutManager.Border.CENTER) {
                    if (predicted == null) {
                        width += current.getPreferredWidth();
                    } else {
                        width += MathHelper.max(current.getPreferredWidth(), predicted.getWidth());
                    }
                }
            }
        }
        return new Region(0, 0, MathHelper.min(width, limitWidth), MathHelper.min(height, limitHeight));
    }

    public enum Border {
        TOP, BOTTOM, LEFT, RIGHT, CENTER
    }

}
