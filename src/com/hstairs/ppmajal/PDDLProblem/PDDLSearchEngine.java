/*
 * Copyright (C) 2018 enrico.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.hstairs.ppmajal.PDDLProblem;

import com.hstairs.ppmajal.problem.State;
import com.hstairs.ppmajal.search.old.OldSearchEngineMethods;
import com.hstairs.ppmajal.search.SearchHeuristic;
import com.hstairs.ppmajal.search.searchnodes.SearchNode;
import com.hstairs.ppmajal.search.searchnodes.SimpleSearchNode;
import com.hstairs.ppmajal.transition.Transition;
import com.hstairs.ppmajal.transition.TransitionGround;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutablePair;


/**
 * @author enrico
 */
public class PDDLSearchEngine extends OldSearchEngineMethods {

    protected Collection<TransitionGround> reachableProcesses;
    protected Collection<TransitionGround> reachableEvents;
        public BigDecimal executionDelta;
    public BigDecimal planningDelta;
    
    //This is a hack!
    private HashMap<Float,TransitionGround> triggeredEvents;

    
    public PDDLSearchEngine(PDDLProblem problem, SearchHeuristic h) {
        this(System.out,problem,h);
    }
    public PDDLSearchEngine(PrintStream out, PDDLProblem problem, SearchHeuristic h) {
        super(out,h, problem);
        executionDelta = problem.executionDelta;
        planningDelta = problem.planningDelta;
        if (!problem.isReadyForSearch()){
            throw new RuntimeException("PDDL Problem is not ready for search yet. Bailing out");
        }
    }
    @Override
    protected void printInfo(PrintStream out) {
            //out.println("Reachable actions and processes: |A U P U E|:" + TransitionGround.totNumberOfTransitions);
    }


    @Override
    public LinkedList<ImmutablePair<BigDecimal,TransitionGround>> extractPlan (SimpleSearchNode input) {

        final LinkedList<ImmutablePair<BigDecimal,TransitionGround>> plan = new LinkedList<>();
        lastState = input.s;
        if (!(input instanceof SearchNode)) {
            SimpleSearchNode temp = input;
            while (temp.transition != null) {
                Double time = null;
                plan.addFirst(ImmutablePair.of(BigDecimal.ZERO,(TransitionGround)temp.transition));
                temp = temp.father;
            }
            return plan;
        }
        
        SearchNode c = (SearchNode) input;
        if (((PDDLProblem)problem).getProcessesSet().isEmpty()) {
            while ((c.transition != null || c.waitingPoints > 0 )) {
                BigDecimal time = null;
                if (c.father != null && c.father.s instanceof PDDLState) {
                    time = ((PDDLState) c.father.s).time;
                }
                if (c.transition != null) {//this is an action
                    if (c.transition instanceof ImmutablePair) {
                        final ImmutablePair<TransitionGround, Integer> t = (ImmutablePair<TransitionGround, Integer>) c.transition;
                        for (int i = 0; i < t.right; i++) {
                            plan.addFirst(ImmutablePair.of(time, t.left));
                        }
                        System.out.println("JUMP for " + t.left + ":" + t.right);
                    } else {
                        plan.addFirst(ImmutablePair.of(time, (TransitionGround) c.transition));
                    }
                }
                c = (SearchNode) c.father;

            }
        }else {
           
            triggeredEvents = new HashMap();
            System.out.println("Extracting plan with execution delta: " + executionDelta);
            BigDecimal time = ((PDDLState) c.s).time;
            TransitionGround waiting = TransitionGround.waitingAction();
            State current = null;
            while (c != null ) {
                if (c.transition != null) {
                    // This is an action
                    plan.addFirst(ImmutablePair.of(((PDDLState) c.s).time, (TransitionGround) c.transition));
                } else { //This is when I am waiting
                    for (int i = 0 ; i < c.waitingPoints; i++ ){
                        time = time.subtract(executionDelta);
                        plan.addFirst(ImmutablePair.of(time, waiting));                       
                    }
                }
                current = c.s;
                c = (SearchNode) c.father;
            }
            final LinkedList<ImmutablePair<BigDecimal,TransitionGround>> finalPlan = new LinkedList<>();
            BigDecimal currentTime = new BigDecimal(0);
            for (org.apache.commons.lang3.tuple.Pair<BigDecimal, TransitionGround> ele : plan) {
                TransitionGround right = (TransitionGround) ele.getRight();
                if (right.getSemantics().equals(Transition.Semantics.PROCESS)) {
                    ArrayList<TransitionGround> sponteneousTransitions = new ArrayList();
                    final ImmutablePair<State, Integer> stateCollectionPair
                            = ((PDDLProblem)problem).simulation(current, executionDelta, 
                                    executionDelta, false, null,sponteneousTransitions);
                    if (stateCollectionPair == null) {
                        throw new RuntimeException("This can't be possible");
                    } else {
                        if (sponteneousTransitions.isEmpty()){
                            System.out.println("something fishy just happened");
                        }
                        for (var v: sponteneousTransitions){
                            finalPlan.add(ImmutablePair.
                                    of(currentTime, v));
                            if (v.getSemantics().equals(Transition.Semantics.PROCESS)){
                                currentTime = currentTime.add(executionDelta);
                            }
                        }
                    }
                    current = stateCollectionPair.getLeft();
                }else{
                    if (ele.getRight() != null && right.getSemantics().equals(Transition.Semantics.ACTION)) {
                        current.apply(right, current.clone());
                        finalPlan.add(ImmutablePair.
                                of(currentTime, right));
                    }else{
                        throw new RuntimeException("We can't have something different from actions or processes");
                    }
                }
            }
            
            return finalPlan;
        }

        return plan;
    }

}
