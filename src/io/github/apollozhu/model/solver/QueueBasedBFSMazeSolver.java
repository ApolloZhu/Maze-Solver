package io.github.apollozhu.model.solver;

import io.github.apollozhu.model.MazeCoder;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author ApolloZhu, Pd. 1
 */
public class QueueBasedBFSMazeSolver extends MazeSolver {
    private Queue<Step> pending;
    private Loc start;

    // Same old thing, greedy algorithm
    protected void pushAllNextStepsFrom(Loc curLoc, /*targeting*/ Loc target) {
        int dR = target.getR() - curLoc.getR();
        int dC = target.getC() - curLoc.getC();
        if (Math.abs(dR) <= Math.abs(dC)) {
            if (dR != 0) pending.add(new Step(curLoc, dR < 0 ? Direction.UP : Direction.DOWN));
            if (dC != 0) pending.add(new Step(curLoc, dC < 0 ? Direction.LEFT : Direction.RIGHT));
            if (dR <= 0) pending.add(new Step(curLoc, Direction.DOWN));
            if (dR >= 0) pending.add(new Step(curLoc, Direction.UP));
            if (dC <= 0) pending.add(new Step(curLoc, Direction.RIGHT));
            if (dC >= 0) pending.add(new Step(curLoc, Direction.LEFT));
        } else {
            if (dC != 0) pending.add(new Step(curLoc, dC < 0 ? Direction.LEFT : Direction.RIGHT));
            if (dR != 0) pending.add(new Step(curLoc, dR < 0 ? Direction.UP : Direction.DOWN));
            if (dC <= 0) pending.add(new Step(curLoc, Direction.RIGHT));
            if (dC >= 0) pending.add(new Step(curLoc, Direction.LEFT));
            if (dR <= 0) pending.add(new Step(curLoc, Direction.DOWN));
            if (dR >= 0) pending.add(new Step(curLoc, Direction.UP));
        }
    }

    @Override
    protected boolean start(int r, int c, int tR, int tC) {
        // Setup
        pending = new LinkedList<>();
        forEachListener(l -> l.started(r, c, tR, tC, getGrid()));
        start = new Loc(r, c);
        final Loc end = new Loc(tR, tC);
        boolean hasPath = false;
        pushAllNextStepsFrom(start, end);
        Step curStep = pending.remove();
        // Mainloop
        while (curStep != null) {
            Step copy = curStep;
            Loc previous = curStep.getStart();
            forEachListener(l -> l.tryout(previous.getR(), previous.getC(),
                    copy.getDirection(), null, getGrid()));
            final Loc curLoc = curStep.getEnd();
            if (curLoc.equals(end)) {
                hasPath = true;
                set(end, MazeCoder.Block.PATH);
                forEachListener(l -> l.found(tR, tC, null, getGrid()));
                break;
            }
            if (get(curLoc) == MazeCoder.Block.EMPTY) {
                set(curLoc, MazeCoder.Block.PATH);
                pushAllNextStepsFrom(curLoc, end);
            } else {
                if (pending.isEmpty()) break;
                Step next = pending.peek();
                if (!next.getStart().equals(previous))
                    failIfNeeded(previous.getR(), previous.getC());
            }
            curStep = pending.isEmpty() ? null : pending.remove();
        }
        // End search
        boolean copy = hasPath;
        forEachListener(l -> l.ended(copy, getGrid()));
        return hasPath;
    }

    private void failIfNeeded(int r, int c) {
        if (get(r, c) == MazeCoder.Block.PATH
                && isInaccessible(r, c)) fail(r, c);
    }

    private void fail(int r, int c) {
        if (start.getR() == r && start.getC() == c) return;
        set(r, c, MazeCoder.Block.VISITED);
        forEachListener(l -> l.failed(r, c, null, getGrid()));
        failIfNeeded(r + 1, c);
        failIfNeeded(r, c + 1);
        failIfNeeded(r - 1, c);
        failIfNeeded(r, c - 1);
    }

    private boolean isInaccessible(int r, int c) {
        int count = 0;
        if (isAccessible(r + 1, c)) count++;
        if (isAccessible(r, c + 1)) count++;
        if (isAccessible(r - 1, c)) count++;
        if (isAccessible(r, c - 1)) count++;
        return count == 1;
    }

    private boolean isAccessible(int r, int c) {
        MazeCoder.Block block = get(r, c);
        return block == MazeCoder.Block.PATH || block == MazeCoder.Block.EMPTY;
    }
}
