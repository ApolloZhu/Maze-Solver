package io.github.apollozhu.model;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum MazeCoder {
    ;
    private static final int WALL = 0;
    private static final int EMPTY = 1;
    private static final int VISITED = 3;
    private static final int PATH = 7;

    public static MazeBlock[][] decode(int[][] intMap) {
        return decode(intMap, WALL, EMPTY, VISITED, PATH);
    }

    public static MazeBlock[][] decode(int[][] intMap, int wall, int empty, int visited, int path) {
        MazeBlock[][] converted = new MazeBlock[intMap.length][intMap[0].length];
        for (int i = 0; i < intMap.length; i++)
            for (int j = 0; j < intMap[i].length; j++)
                if (intMap[i][j] == wall) converted[i][j] = MazeBlock.WALL;
                else if (intMap[i][j] == empty) converted[i][j] = MazeBlock.EMPTY;
                else if (intMap[i][j] == visited) converted[i][j] = MazeBlock.VISITED;
                else if (intMap[i][j] == path) converted[i][j] = MazeBlock.PATH;
        return converted;
    }

    public static int[][] encode(MazeBlock[][] map) {
        return encode(map, WALL, EMPTY, VISITED, PATH);
    }

    public static int[][] encode(MazeBlock[][] map, int wall, int empty, int visited, int path) {
        int[][] converted = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                if (map[i][j] == MazeBlock.WALL) converted[i][j] = wall;
                else if (map[i][j] == MazeBlock.EMPTY) converted[i][j] = empty;
                else if (map[i][j] == MazeBlock.VISITED) converted[i][j] = visited;
                else if (map[i][j] == MazeBlock.PATH) converted[i][j] = path;
        return converted;
    }

    public static void print(int[][] grid) {
        System.out.println(toString(grid));
    }

    public static String toString(int[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++)
                sb.append(grid[i][j]).append(' ');
            sb.append("\n");
        }
        return sb.toString();
    }
}
