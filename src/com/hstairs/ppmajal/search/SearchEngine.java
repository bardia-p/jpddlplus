package com.hstairs.ppmajal.search;

import com.hstairs.ppmajal.problem.State;
import com.hstairs.ppmajal.search.searchnodes.SearchNode;
import com.hstairs.ppmajal.search.searchnodes.SimpleSearchNode;

import java.io.PrintStream;
import java.util.LinkedList;

public abstract class SearchEngine {
    protected final float G_DEFAULT = Float.NaN;
    protected int deadEndsDetected;
    protected int nodesExpanded;
    protected int nodesEvaluated;
    protected int duplicatedDetected;
    //Timing information
    protected long totalTime;
    protected long heuristicTime;
    State lastState;
    final boolean helpfulActions;
    private SearchNode searchSpaceHandle;

    protected SearchEngine(boolean helpfulActionsPruning) {
        this.helpfulActions = helpfulActionsPruning;
    }
    public abstract SearchStats getStats();

    protected void zeroCounters() {
        deadEndsDetected = 0;
        nodesExpanded = 0;
        nodesEvaluated = 0;
        duplicatedDetected = 0;
        heuristicTime = 0;
    }

    public void initHandle(SearchNode init){
            searchSpaceHandle = init;//this needs to have an handle on the initial state for saving it into a json file

    }

    public abstract SimpleSearchNode search(SearchProblem p, SearchHeuristic h, PrintStream out);
    public LinkedList extractDecisions(SimpleSearchNode c) {
        LinkedList plan = new LinkedList<>();
        lastState = c.s;
        while (c.transition != null) {
            if (c.transition != null) {//this is an action
                plan.addFirst(c.transition);
            }
            c = c.father;
        }
        return plan;
    }
    Object[] getActionsToSearch(SimpleSearchNode currentNode, SearchProblem problem, SearchHeuristic h) {
        if (helpfulActions && currentNode != null) {
            return ((SearchNode)currentNode).helpfulActions;
        }
        return h.getTransitions(false);
    }

    public SearchNode getSearchSpaceHandle() {
        return searchSpaceHandle;
    }

    public enum TieBreaking {
        LOWERG,
        HIGHERG,
        ARBITRARY
    }

    public class SearchStats {

        private final int nodesExpanded;
        private final int nodesEvaluated;
        private final int deadEnds;
        private final int duplicates;
        private final long searchTime;
        private final long heuristicTime;

        public SearchStats(int nodesExpanded, int nodesEvaluated, int deadEnds, int duplicates, long searchTime, long heuristicTime) {
            this.nodesExpanded = nodesExpanded;
            this.nodesEvaluated = nodesEvaluated;
            this.deadEnds = deadEnds;
            this.duplicates = duplicates;
            this.searchTime = searchTime;
            this.heuristicTime = heuristicTime;
        }

        public int nodesExpanded() {
            return nodesExpanded;
        }

        public int nodesEvaluated() {
            return nodesEvaluated;
        }

        public int deadEnds() {
            return deadEnds;
        }

        public int duplicates() {
            return duplicates;
        }

        public long searchTime() {
            return searchTime;
        }

        public long heuristicTime() {
            return heuristicTime;
        }

        @Override
        public String toString() {
            return "SearchStats{" +
                    "nodesExpanded=" + nodesExpanded +
                    ", nodesEvaluated=" + nodesEvaluated +
                    ", deadEnds=" + deadEnds +
                    ", duplicates=" + duplicates +
                    ", searchTime=" + searchTime +
                    ", heuristicTime=" + heuristicTime +
                    '}';
        }
    }
}
