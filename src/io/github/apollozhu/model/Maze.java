package io.github.apollozhu.model;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum Maze {
    ;

    private static final int[][] grid = {
            {1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1},
            {1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1},
            {0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0},
            {1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 0, 1, 1},
            {1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    public static MazeBlock[][] decodeLauMaze() {
        return MazeCoder.decode(grid);
    }

    public static void clear(MazeBlock[][] map) {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                map[i][j] = map[i][j] == MazeBlock.WALL ? MazeBlock.WALL : MazeBlock.EMPTY;
    }

    public static MazeBlock[][] generate(int r, int c, double emptyPossibility) {
        emptyPossibility = Math.max(Math.min(1, emptyPossibility), 0);
        MazeBlock[][] map = new MazeBlock[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                map[i][j] = Math.random() < emptyPossibility ? MazeBlock.EMPTY : MazeBlock.WALL;
        return map;
    }
}
