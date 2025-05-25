
package com.hstairs.ppmajal.pddl.heuristics.advanced;

import com.hstairs.ppmajal.conditions.Condition;
import com.hstairs.ppmajal.expressions.NumEffect;
import com.hstairs.ppmajal.transition.Transition;
import java.util.Collection;

/**
 *
 * @author enrico
 */
public class CompactPDDLProblem {

    private final Condition[] preconditionFunction;
    private final Collection<Integer>[] propEffectFunction;
    private final Collection<NumEffect>[] numericEffectFunction;
    private final float[] actionCost;
    private final int numActions;
    private final int goal;
    private final Collection<Integer>[] tr2CpTrMap;
    private final int[] cpTr2TrMap;

    public CompactPDDLProblem(
            Condition[] preconditionFunction,
            Collection<Integer>[] propEffectFunction,
            Collection<NumEffect>[] numericEffectFunction,
            float[] actionCost,
            int numActions,
            int goal,
            Collection<Integer>[] tr2CpTrMap,
            int[] cpTr2TrMap) {
        this.preconditionFunction = preconditionFunction;
        this.propEffectFunction = propEffectFunction;
        this.numericEffectFunction = numericEffectFunction;
        this.actionCost = actionCost;
        this.numActions = numActions;
        this.goal = goal;
        this.tr2CpTrMap = tr2CpTrMap;
        this.cpTr2TrMap = cpTr2TrMap;
    }

    public Condition[] preconditionFunction() {
        return preconditionFunction;
    }

    public Collection<Integer>[] propEffectFunction() {
        return propEffectFunction;
    }

    public Collection<NumEffect>[] numericEffectFunction() {
        return numericEffectFunction;
    }

    public float[] actionCost() {
        return actionCost;
    }

    public int numActions() {
        return numActions;
    }

    public int goal() {
        return goal;
    }

    public Collection<Integer>[] tr2CpTrMap() {
        return tr2CpTrMap;
    }

    public int[] cpTr2TrMap() {
        return cpTr2TrMap;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < preconditionFunction.length; i++) {
            ret.append("Transition: ")
               .append(Transition.getTransition(cpTr2TrMap[i]))
               .append("\n");
            ret.append("Precondition in the model: ")
               .append(preconditionFunction[i])
               .append("\n");
            ret.append("Effect in the model: ")
               .append(propEffectFunction[i])
               .append("\n");
            ret.append("Cost: ")
               .append(actionCost[i])
               .append("\n");
        }
        return ret.toString();
    }
}

