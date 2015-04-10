package net.specialattack.forge.core.client.gui.element;

public class SGPopout extends SGScrollPane {

    private SGComponent parent;

    public SGPopout(SGComponent parent) {
        this.setScrollbarSize(4);
        this.setCanScroll(false, true);
        this.parent = parent;
    }

    @Override
    public void setParent(IComponentHolder holder) {
    }

    @Override
    public IComponentHolder getParent() {
        return this.parent.getRoot();
    }

}
