package com.hstairs.ppmajal.PDDLProblem;

import com.hstairs.ppmajal.search.SearchEngine;
import com.hstairs.ppmajal.transition.TransitionGround;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.math.BigDecimal;
import java.util.LinkedList;

import java.math.BigDecimal;
import java.util.LinkedList;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class PDDLSolution {

    private final LinkedList<ImmutablePair<BigDecimal, TransitionGround>> rawPlan;
    private final PDDLState lastState;
    private final SearchEngine.SearchStats stats;
    private final float gValueAtTheEnd;

    public PDDLSolution(LinkedList<ImmutablePair<BigDecimal, TransitionGround>> rawPlan,
                        PDDLState lastState,
                        SearchEngine.SearchStats stats,
                        float gValueAtTheEnd) {
        this.rawPlan = rawPlan;
        this.lastState = lastState;
        this.stats = stats;
        this.gValueAtTheEnd = gValueAtTheEnd;
    }

    public LinkedList<ImmutablePair<BigDecimal, TransitionGround>> rawPlan() {
        return rawPlan;
    }

    public PDDLState lastState() {
        return lastState;
    }

    public SearchEngine.SearchStats stats() {
        return stats;
    }

    public float gValueAtTheEnd() {
        return gValueAtTheEnd;
    }
}
