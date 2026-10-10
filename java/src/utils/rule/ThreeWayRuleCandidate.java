package utils.rule;

import java.util.BitSet;

public class ThreeWayRuleCandidate {
    private final BitSet conditionExtent;
    private final BitSet conditionOriginalExtent;
    private final BitSet lowerIntent;
    private final BitSet upperIntent;
    private final BitSet decisionExtent;
    private final BitSet decisionOriginalExtent;
    private final BitSet decisionIntent;

    public ThreeWayRuleCandidate(
            BitSet conditionExtent,
            BitSet conditionOriginalExtent,
            BitSet lowerIntent,
            BitSet upperIntent,
            BitSet decisionExtent,
            BitSet decisionOriginalExtent,
            BitSet decisionIntent
    ) {
        this.conditionExtent = (BitSet) conditionExtent.clone();
        this.conditionOriginalExtent = (BitSet) conditionOriginalExtent.clone();
        this.lowerIntent = (BitSet) lowerIntent.clone();
        this.upperIntent = (BitSet) upperIntent.clone();
        this.decisionExtent = (BitSet) decisionExtent.clone();
        this.decisionOriginalExtent = (BitSet) decisionOriginalExtent.clone();
        this.decisionIntent = (BitSet) decisionIntent.clone();
    }

    public BitSet getConditionExtent() {
        return (BitSet) conditionExtent.clone();
    }

    public BitSet getConditionOriginalExtent() {
        return (BitSet) conditionOriginalExtent.clone();
    }

    public BitSet getLowerIntent() {
        return (BitSet) lowerIntent.clone();
    }

    public BitSet getUpperIntent() {
        return (BitSet) upperIntent.clone();
    }

    public BitSet getDecisionExtent() {
        return (BitSet) decisionExtent.clone();
    }

    public BitSet getDecisionOriginalExtent() {
        return (BitSet) decisionOriginalExtent.clone();
    }

    public BitSet getDecisionIntent() {
        return (BitSet) decisionIntent.clone();
    }

    @Override
    public String toString() {
        return "[B_low=" + lowerIntent +
                ", B_high=" + upperIntent +
                "] -> C=" + decisionIntent +
                "; X_original=" + conditionOriginalExtent +
                ", Y_original=" + decisionOriginalExtent +
                ", X_internal=" + conditionExtent +
                ", Y_internal=" + decisionExtent +
                ", X_subseteq_Y=true";
    }
}
