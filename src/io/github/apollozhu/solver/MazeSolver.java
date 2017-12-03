package io.github.apollozhu.solver;

import io.github.apollozhu.model.MazeBlock;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.function.Consumer;

/**
 * @author ApolloZhu, Pd. 1
 */
public abstract class MazeSolver {

    private final EventListenerList list = new EventListenerList();
    private MazeBlock[][] grid;

    public final boolean start(MazeBlock[][] input,
                               int r, int c, int tR, int tC) {
        setGrid(input);
        if (get(r, c) == MazeBlock.WALL || get(tR, tC) == MazeBlock.WALL) return false;
        set(r, c, MazeBlock.EMPTY);
        set(tR, tC, MazeBlock.EMPTY);
        return start(r, c, tR, tC);
    }

    protected abstract boolean start(int r, int c, int tR, int tC);

    public void stop() {
        stop(false);
    }

    public void stop(boolean hasPath) {
        forEachListener(l -> l.ended(hasPath, grid));
    }

    public MazeBlock[][] getGrid() {
        return grid;
    }

    public void setGrid(MazeBlock[][] grid) {
        this.grid = grid;
    }

    protected MazeBlock get(MazeBlock.Location location) {
        return get(location.getR(), location.getC());
    }

    protected MazeBlock get(int x, int y) {
        try {
            return grid[x][y];
        } catch (Exception e) {
            return MazeBlock.WALL;
        }
    }

    protected void set(MazeBlock.Location location, MazeBlock block) {
        set(location.getR(), location.getC(), block);
    }

    protected void set(int x, int y, MazeBlock block) {
        try {
            grid[x][y] = block;
        } catch (Exception e) {
        }
    }

    public void addEventListener(MSEventListener l) {
        list.add(MSEventListener.class, l);
    }

    public void removeEventListener(MSEventListener l) {
        list.remove(MSEventListener.class, l);
    }

    protected void forEachListener(Consumer<MSEventListener> consumer) {
        for (MSEventListener l : list.getListeners(MSEventListener.class))
            consumer.accept(l);
    }

    public enum Type {
        RECURSIVE, STACK, DFS, BFS;

        Class associatedClass() {
            switch (this) {
                case RECURSIVE:
                    return RecursiveMazeSolver.class;
                case STACK:
                    return StackBasedMazeSolver.class;
                case DFS:
                    return StackBasedDFSMazeSolver.class;
                case BFS:
                    return QueueBasedBFSMazeSolver.class;
            }
            throw new EnumConstantNotPresentException(Type.class, name());
        }

        public MazeSolver init() {
            switch (this) {
                case RECURSIVE:
                    return new RecursiveMazeSolver();
                case STACK:
                    return new StackBasedMazeSolver();
                case DFS:
                    return new StackBasedDFSMazeSolver();
                case BFS:
                    return new QueueBasedBFSMazeSolver();
            }
            throw new EnumConstantNotPresentException(Type.class, name());
        }

        public String description() {
            switch (this) {
                case RECURSIVE:
                    return "Recursive";
                case STACK:
                    return "Recursive - Stack Rewrite";
                case DFS:
                    return "DFS - Stack";
                case BFS:
                    return "BFS - Queue";
            }
            throw new EnumConstantNotPresentException(Type.class, name());
        }
    }

    public enum Direction {
        UP, RIGHT, DOWN, LEFT, NONE;

        public MazeBlock.Location reverse(int r, int c) {
            return new MazeBlock.Location(r - dx(), c - dy());
        }

        public MazeBlock.Location forward(MazeBlock.Location location) {
            return forward(location.getR(), location.getC());
        }

        public MazeBlock.Location forward(int r, int c) {
            return new MazeBlock.Location(r + dx(), c + dy());
        }

        public int dx() {
            switch (this) {
                case DOWN:
                    return 1;
                case UP:
                    return -1;
            }
            return 0;
        }

        public int dy() {
            switch (this) {
                case RIGHT:
                    return 1;
                case LEFT:
                    return -1;
            }
            return 0;
        }
    }

    public interface MSEventListener<PathType> extends EventListener {
        void started(int r, int c, int tR, int tC, MazeBlock[][] map);

        void tryout(int r, int c, MazeSolver.Direction direction, PathType path, MazeBlock[][] map);

        void found(int tR, int tC, PathType path, MazeBlock[][] map);

        void failed(int r, int c, PathType path, MazeBlock[][] map);

        void ended(boolean hasPath, MazeBlock[][] map);
    }

    public static class Step {
        private final MazeBlock.Location start;
        private final Direction direction;
        protected int pass;
        private MazeBlock.Location end;

        public Step(MazeBlock.Location start, Direction direction) {
            this.start = start;
            this.direction = direction == null
                    ? Direction.NONE : direction;
        }

        public MazeBlock.Location getStart() {
            return start;
        }

        public Direction getDirection() {
            return direction;
        }

        public MazeBlock.Location getEnd() {
            if (end != null) return end;
            return end = direction.forward(start);
        }

        public void nextStepFailed() {
            pass++;
        }

        @Override
        public String toString() {
            return "#" + pass + start + " " + direction;
        }
    }
}
