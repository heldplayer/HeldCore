package net.specialattack.forge.core.client.gui.deprecated.layout;

public enum FlowLayout {
    MIN {
        @Override
        public int decide(int total, int portion) {
            return 0;
        }
    }, CENTER {
        @Override
        public int decide(int total, int portion) {
            return (total - portion) / 2;
        }
    }, MAX {
        @Override
        public int decide(int total, int portion) {
            return total - portion;
        }
    };

    public abstract int decide(int total, int portion);

}
