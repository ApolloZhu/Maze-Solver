package io.github.apollozhu.mazesolver.model;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum MazeBlock {
    WALL, EMPTY, VISITED, PATH;

    public static class Location {
        private int r, c;

        public Location(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public int getR() {
            return r;
        }

        public int getC() {
            return c;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Location) {
                Location location = (Location) obj;
                return r == location.r && c == location.c;
            }
            return false;
        }

        @Override
        public String toString() {
            return "(" + r + "," + c + ")";
        }
    }
}
