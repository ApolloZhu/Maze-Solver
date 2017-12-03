package io.github.apollozhu.solver;

import io.github.apollozhu.model.MazeBlock;

import java.util.Stack;

/**
 * @author ApolloZhu, Pd. 1
 */
public class StackBasedDFSMazeSolver extends MazeSolver {
    private Stack<Step> pending, path;

    // Same old thing, greedy algorithm
    protected void pushAllNextStepsFrom(MazeBlock.Location curLocation, /*targeting*/ MazeBlock.Location target) {
        int dX = target.getR() - curLocation.getR();
        int dY = target.getC() - curLocation.getC();
        if (Math.abs(dX) <= Math.abs(dY)) {
            if (dY >= 0) pending.push(new Step(curLocation, Direction.LEFT));
            if (dY <= 0) pending.push(new Step(curLocation, Direction.RIGHT));
            if (dX >= 0) pending.push(new Step(curLocation, Direction.UP));
            if (dX <= 0) pending.push(new Step(curLocation, Direction.DOWN));
            if (dY != 0) pending.push(new Step(curLocation, dY < 0 ? Direction.LEFT : Direction.RIGHT));
            if (dX != 0) pending.push(new Step(curLocation, dX < 0 ? Direction.UP : Direction.DOWN));
        } else {
            if (dX >= 0) pending.push(new Step(curLocation, Direction.UP));
            if (dX <= 0) pending.push(new Step(curLocation, Direction.DOWN));
            if (dY >= 0) pending.push(new Step(curLocation, Direction.LEFT));
            if (dY <= 0) pending.push(new Step(curLocation, Direction.RIGHT));
            if (dX != 0) pending.push(new Step(curLocation, dX < 0 ? Direction.UP : Direction.DOWN));
            if (dY != 0) pending.push(new Step(curLocation, dY < 0 ? Direction.LEFT : Direction.RIGHT));
        }
    }

    @Override
    protected boolean start(int r, int c, int tR, int tC) {
        // Setup
        pending = new Stack<>();
        path = new Stack<>();
        forEachListener(l -> l.started(r, c, tR, tC, getGrid()));
        final MazeBlock.Location start = new MazeBlock.Location(r, c), end = new MazeBlock.Location(tR, tC);
        boolean hasPath = false;
        Step curStep = new Step(start, Direction.NONE);
        // Mainloop
        while (curStep != null) {
            Step copy = curStep;
            forEachListener(l -> l.tryout(copy.getStart().getR(), copy.getStart().getC(),
                    copy.getDirection(), path, getGrid()));
            final MazeBlock.Location curLocation = curStep.getEnd();
            if (curLocation.equals(end)) {
                hasPath = true;
                set(tR, tC, MazeBlock.PATH);
                forEachListener(l -> l.found(tR, tC, path, getGrid()));
                break;
            }
            int curR = curLocation.getR(), curC = curLocation.getC();
            if (get(curR, curC) == MazeBlock.EMPTY) {
                path.push(curStep);
                set(curLocation, MazeBlock.PATH);
                pushAllNextStepsFrom(curLocation, end);
            } else while (!pending.isEmpty() && path.peek().getDirection() != Direction.NONE
                    && !path.peek().getEnd().equals(pending.peek().getStart())) {
                Step step = path.pop();
                set(step.getEnd(), MazeBlock.VISITED);
                forEachListener(l -> l.failed(step.getEnd().getR(), step.getEnd().getC(), path, getGrid()));
            }
            curStep = pending.isEmpty() ? null : pending.pop();
        }
        // End search
        boolean copy = hasPath;
        forEachListener(l -> l.ended(copy, getGrid()));
        return hasPath;
    }
}
