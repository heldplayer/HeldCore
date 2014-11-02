package net.specialattack.forge.core.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum Positioning {
    CENTER {
        @Override
        public int position(int pos, int size, int parentSize) {
            return (parentSize - size) / 2;
        }
    },
    CENTER_OFFSET {
        @Override
        public int position(int pos, int size, int parentSize) {
            return (parentSize - size) / 2 + pos;
        }
    },
    MIN {
        @Override
        public int position(int pos, int size, int parentSize) {
            return 0;
        }
    },
    MIN_OFFSET {
        @Override
        public int position(int pos, int size, int parentSize) {
            return pos;
        }
    },
    MAX {
        @Override
        public int position(int pos, int size, int parentSize) {
            return parentSize - size;
        }
    },
    MAX_OFFSET {
        @Override
        public int position(int pos, int size, int parentSize) {
            return parentSize - size - pos;
        }
    };

    public abstract int position(int pos, int size, int parentSize);

}
