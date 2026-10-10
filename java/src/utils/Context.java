package utils;


import java.util.BitSet;
import java.util.Map;

public class Context {
    private Map<Integer, BitSet> objs;
    private Map<Integer, BitSet> attrs;
    private Map<Integer, BitSet> objs_n;
    private Map<Integer, BitSet> attrs_n;
    private Map<Integer,BitSet> attrs_max;
    private Map<Integer,BitSet> attrs_min;
    private Map<Integer,BitSet> attrs_incomplete;
    private Map<Integer,BitSet> objs_max;
    private Map<Integer,BitSet> objs_min;

    private int objs_size;
    private int attrs_size;

    public Map<Integer, BitSet> getObjs() {
        return objs;
    }

    public void setObjs(Map<Integer, BitSet> objs) {
        this.objs = objs;
    }

    public Map<Integer, BitSet> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<Integer, BitSet> attrs) {
        this.attrs = attrs;
    }

    public Map<Integer, BitSet> getObjs_n() {
        return objs_n;
    }

    public void setObjs_n(Map<Integer, BitSet> objs_n) {
        this.objs_n = objs_n;
    }

    public Map<Integer, BitSet> getAttrs_n() {
        return attrs_n;
    }

    public void setAttrs_n(Map<Integer, BitSet> attrs_n) {
        this.attrs_n = attrs_n;
    }

    public int getObjs_size() {
        return objs_size;
    }

    public void setObjs_size(int objs_size) {
        this.objs_size = objs_size;
    }

    public int getAttrs_size() {
        return attrs_size;
    }

    public void setAttrs_size(int attrs_size) {
        this.attrs_size = attrs_size;
    }

    public Context() {
    }

    public Map<Integer, BitSet> getAttrs_max() {
        return attrs_max;
    }

    public void setAttrs_max(Map<Integer, BitSet> attrs_max) {
        this.attrs_max = attrs_max;
    }

    public Map<Integer, BitSet> getAttrs_min() {
        return attrs_min;
    }

    public void setAttrs_min(Map<Integer, BitSet> attrs_min) {
        this.attrs_min = attrs_min;
    }

    public Map<Integer, BitSet> getObjs_max() {
        return objs_max;
    }

    public void setObjs_max(Map<Integer, BitSet> objs_max) {
        this.objs_max = objs_max;
    }

    public Map<Integer, BitSet> getObjs_min() {
        return objs_min;
    }

    public void setObjs_min(Map<Integer, BitSet> objs_min) {
        this.objs_min = objs_min;
    }

    public Map<Integer, BitSet> getAttrs_incomplete() {
        return attrs_incomplete;
    }

    public void setAttrs_incomplete(Map<Integer, BitSet> attrs_incomplete) {
        this.attrs_incomplete = attrs_incomplete;
    }
}



