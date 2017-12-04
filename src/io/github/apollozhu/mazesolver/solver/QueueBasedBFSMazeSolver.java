package io.github.apollozhu.mazesolver.solver;

import io.github.apollozhu.mazesolver.model.MazeBlock;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author ApolloZhu, Pd. 1
 */
public class QueueBasedBFSMazeSolver extends MazeSolver {
    private Queue<Step> pending;
    private MazeBlock.Location start;

    // Same old thing, greedy algorithm
    protected void pushAllNextStepsFrom(MazeBlock.Location curLocation, /*targeting*/ MazeBlock.Location target) {
        int dR = target.getR() - curLocation.getR();
        int dC = target.getC() - curLocation.getC();
        if (Math.abs(dR) <= Math.abs(dC)) {
            if (dR != 0) pending.add(new Step(curLocation, dR < 0 ? Direction.UP : Direction.DOWN));
            if (dC != 0) pending.add(new Step(curLocation, dC < 0 ? Direction.LEFT : Direction.RIGHT));
            if (dR <= 0) pending.add(new Step(curLocation, Direction.DOWN));
            if (dR >= 0) pending.add(new Step(curLocation, Direction.UP));
            if (dC <= 0) pending.add(new Step(curLocation, Direction.RIGHT));
            if (dC >= 0) pending.add(new Step(curLocation, Direction.LEFT));
        } else {
            if (dC != 0) pending.add(new Step(curLocation, dC < 0 ? Direction.LEFT : Direction.RIGHT));
            if (dR != 0) pending.add(new Step(curLocation, dR < 0 ? Direction.UP : Direction.DOWN));
            if (dC <= 0) pending.add(new Step(curLocation, Direction.RIGHT));
            if (dC >= 0) pending.add(new Step(curLocation, Direction.LEFT));
            if (dR <= 0) pending.add(new Step(curLocation, Direction.DOWN));
            if (dR >= 0) pending.add(new Step(curLocation, Direction.UP));
        }
    }

    @Override
    protected boolean start(int r, int c, int tR, int tC) {
        // Setup
        pending = new LinkedList<>();
        forEachListener(l -> l.started(r, c, tR, tC, getGrid()));
        start = new MazeBlock.Location(r, c);
        final MazeBlock.Location end = new MazeBlock.Location(tR, tC);
        boolean hasPath = false;
        pushAllNextStepsFrom(start, end);
        Step curStep = pending.remove();
        // Mainloop
        while (curStep != null) {
            Step copy = curStep;
            MazeBlock.Location previous = curStep.getStart();
            forEachListener(l -> l.tryout(previous.getR(), previous.getC(),
                    copy.getDirection(), null, getGrid()));
            final MazeBlock.Location curLocation = curStep.getEnd();
            if (curLocation.equals(end)) {
                hasPath = true;
                set(end, MazeBlock.PATH);
                forEachListener(l -> l.found(tR, tC, null, getGrid()));
                break;
            }
            if (get(curLocation) == MazeBlock.EMPTY) {
                set(curLocation, MazeBlock.PATH);
                pushAllNextStepsFrom(curLocation, end);
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
        if (get(r, c) == MazeBlock.PATH
                && isInaccessible(r, c)) fail(r, c);
    }

    private void fail(int r, int c) {
        if (start.getR() == r && start.getC() == c) return;
        set(r, c, MazeBlock.VISITED);
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
        MazeBlock block = get(r, c);
        return block == MazeBlock.PATH || block == MazeBlock.EMPTY;
    }
}
