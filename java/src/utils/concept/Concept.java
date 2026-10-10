package utils.concept;

import java.util.BitSet;


public class Concept {
    private BitSet extent;

    private BitSet intent;
    private BitSet none_intent;

    private int id=0;

    public Concept() {
    }

    @Override
    public String toString() {
        return "Concept{" +
                "extent=" + extent +
                ", intent=" + intent +
                ", none_intent=" + none_intent +
                ", id=" + id +
                '}';
    }

    public BitSet getExtent() {
        return extent;
    }

    public void setExtent(BitSet extent) {
        this.extent = extent;
    }

    public BitSet getIntent() {
        return intent;
    }

    public BitSet getNone_intent() {
        return none_intent;
    }

    public void setNone_intent(BitSet none_intent) {
        this.none_intent = none_intent;
    }

    public void setIntent(BitSet intent) {
        this.intent = intent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Concept(BitSet extent, BitSet intent) {
        this.extent = extent;
        this.intent = intent;
    }



    public Concept(BitSet extent, BitSet intent, int id) {
        this.extent = extent;
        this.intent = intent;
        this.id = id;
    }

    public Concept(BitSet extent, BitSet intent, BitSet none_intent, int id) {
        this.extent = extent;
        this.intent = intent;
        this.none_intent = none_intent;
        this.id = id;
    }

    public Concept(BitSet extent, BitSet intent, BitSet none_intent) {
        this.extent = extent;
        this.intent = intent;
        this.none_intent = none_intent;
    }

}
