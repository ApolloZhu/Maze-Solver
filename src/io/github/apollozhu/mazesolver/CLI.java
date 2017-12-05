package io.github.apollozhu.mazesolver;

import io.github.apollozhu.mazesolver.model.Maze;
import io.github.apollozhu.mazesolver.model.MazeBlock;
import io.github.apollozhu.mazesolver.model.MazeCoder;
import io.github.apollozhu.mazesolver.solver.MazeSolver;
import io.github.apollozhu.mazesolver.solver.RecursiveMazeSolver;

import java.util.Scanner;

public enum CLI {;
    private static int[][] grid = MazeCoder.encode(Maze.decodeLauMaze());
    private static RecursiveMazeSolver solver = new RecursiveMazeSolver();
    private static RecursiveMazeSolver.MSEventListener listener = new MazeSolver.MSEventListener<String>() {
        @Override
        public void started(int r, int c, int tR, int tC, MazeBlock[][] map) {
        }

        @Override
        public void tryout(int r, int c, MazeSolver.Direction direction, String path, MazeBlock[][] map) {
        }

        @Override
        public void found(int tR, int tC, String path, MazeBlock[][] map) {
            System.out.println(path);
        }

        @Override
        public void failed(int r, int c, String path, MazeBlock[][] map) {
        }

        @Override
        public void ended(boolean hasPath, MazeBlock[][] map) {
            grid = MazeCoder.encode(map);
        }
    };

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Welcome to Maze Solver.\n" +
                "1. Solve Mr. Lau's maze (default)\n" +
                "2. Randomly generates a maze to solve\n" +
                "> ");
        CHECK:
        try {
            if (Integer.parseInt(input.nextLine()
                    .split(" ")[0]) != 2) break CHECK;
            System.out.print("Row: ");
            int r = input.nextInt();
            System.out.print("Column: ");
            int c = input.nextInt();
            System.out.print("Percentage of empty path: ");
            double percentage = input.nextDouble();
            grid = MazeCoder.encode(Maze.generate(r, c, percentage));
        } catch (Exception e) {
        }

        LOOP:
        solver.addEventListener(listener);
        int startX, startY, targetX, targetY;
        do {
            MazeCoder.print(grid);
            System.out.print("Enter current x and y coordinates: ");
            startX = input.nextInt();
            startY = input.nextInt();
            System.out.print("Enter target x and y coordinates: ");
            targetX = input.nextInt();
            targetY = input.nextInt();
        } while (!findAnExit(startX, startY, targetX, targetY)
                && print("Still trapped inside!"));

        System.out.println("Successfully exit the maze!!!");

        // display the path (indicated by 7)
        // that leads to the exit of the maze
        // also display locations tried
        MazeCoder.print(grid);
    }

    private static boolean print(String s) {
        System.out.println(s);
        return true;
    }

    private static boolean findAnExit(int x, int y, int tR, int tC) {
        return solver.start(MazeCoder.decode(grid), x, y, tR, tC);
    }
}

/*
 7 8
 [7,8][7,9][7,10][7,11][7,12]
 Successfully exit the maze!!!

 3 3 3 0 3 3 0 0 0 3 3 3 3 
 3 0 3 3 3 0 3 3 3 3 0 0 3 
 0 0 0 0 3 0 3 0 3 0 1 0 0 
 3 3 3 0 3 3 3 0 3 0 0 1 1 
 3 0 3 0 0 0 0 3 3 3 0 0 1 
 3 0 3 3 3 3 3 3 0 3 3 3 0 
 3 0 0 0 0 0 0 0 0 0 0 0 0 
 3 3 3 3 3 3 3 3 7 7 7 7 7
*/

/*
 0 0
 [0,0][0,1][0,2][1,2][1,3][1,4][2,4][3,4][3,5][3,6][2,6][1,6][1,7][1,8][2,8][3,8][4,8][4,7][5,7][5,6][5,5][5,4][5,3][5,2]
 [4,2][3,2][3,1][3,0][4,0][5,0][6,0][7,0][7,1][7,2][7,3][7,4][7,5][7,6][7,7][7,8][7,9][7,10][7,11][7,12]
 Successfully exit the maze!!!

 7 7 7 0 1 1 0 0 0 1 1 1 1 
 3 0 7 7 7 0 7 7 7 1 0 0 1 
 0 0 0 0 7 0 7 0 7 0 1 0 0 
 7 7 7 0 7 7 7 0 7 0 0 1 1 
 7 0 7 0 0 0 0 7 7 1 0 0 1 
 7 0 7 7 7 7 7 7 7 7 7 7 7
*/

/*
 3 12
 no way out!

 1 1 1 0 1 1 0 0 0 1 1 1 1 
 1 0 1 1 1 0 1 1 1 1 0 0 1 
 0 0 0 0 1 0 1 0 1 0 1 0 0 
 1 1 1 0 1 1 1 0 1 0 0 3 3 
 1 0 1 0 0 0 0 1 1 1 0 0 3 
 1 0 1 1 1 1 1 1 0 1 1 1 0 
 1 0 0 0 0 0 0 0 0 0 0 0 0 
 1 1 1 1 1 1 1 1 1 1 1 1 1
 */
