package utils.item;

import java.util.BitSet;


public class queueItem_InClose {
    private BitSet W;
    private BitSet Z;
    private int j;

    public queueItem_InClose(BitSet w, int j) {
        W = w;
        this.j = j;
    }

    public queueItem_InClose(BitSet w, BitSet z, int j) {
        W = w;
        Z = z;
        this.j = j;
    }

    public BitSet getW() {
        return W;
    }

    public int getJ() {
        return j;
    }

    public BitSet getZ() {
        return Z;
    }
}
