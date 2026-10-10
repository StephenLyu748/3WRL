package utils.concept;

import java.util.BitSet;
import java.util.Objects;


public class Concept_SE_ISI {
    private BitSet extent;
    private BitSet intent_max;
    private BitSet intent_min;

    private int id=0;

    public Concept_SE_ISI() {
    }

    @Override
    public String toString() {
        return "Concept{" +
                "extent=" + extent +
                ", intent_min=" + intent_min +
                ", intent_max=" + intent_max +
                ", id=" + id +
                '}';
    }

    public BitSet getExtent() {
        return extent;
    }

    public void setExtent(BitSet extent) {
        this.extent = extent;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public BitSet getIntent_max() {
        return intent_max;
    }

    public void setIntent_max(BitSet intent_max) {
        this.intent_max = intent_max;
    }

    public BitSet getIntent_min() {
        return intent_min;
    }

    public void setIntent_min(BitSet intent_min) {
        this.intent_min = intent_min;
    }

    public Concept_SE_ISI(BitSet extent, BitSet intent_min, BitSet intent_max, int id) {
        this.extent = extent;
        this.intent_min = intent_min;
        this.intent_max = intent_max;
        this.id = id;
    }

    public Concept_SE_ISI(BitSet extent, BitSet intent_min, BitSet intent_max) {
        this.extent = extent;
        this.intent_min = intent_min;
        this.intent_max = intent_max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concept_SE_ISI)) return false;
        Concept_SE_ISI that = (Concept_SE_ISI) o;
        return Objects.equals(extent, that.extent) && Objects.equals(intent_max, that.intent_max) && Objects.equals(intent_min, that.intent_min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extent, intent_max, intent_min);
    }
}
