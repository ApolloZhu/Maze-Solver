package io.github.apollozhu.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * r c sR sC tR tC HEX
 */
public enum MazeFile {
    ;

    public static String write(Info info, String directory) {
        StringBuilder sb = new StringBuilder();
        int r = info.map.length;
        int c = info.map[0].length;
        int sR = info.start.getR();
        int sC = info.start.getC();
        int tR = info.end.getR();
        int tC = info.end.getC();
        Logger.getGlobal().log(Level.INFO, "validate");
        if (!isValid(r, c, sR, sC, tR, tC)) return null;
        Logger.getGlobal().log(Level.INFO, "is valid");

        sb.append(r).append('_').append(c).append('_');
        sb.append(sR).append('_').append(sC).append('_');
        sb.append(tR).append('_').append(tC).append(".maze");
        String fileName = sb.toString();
        Logger.getGlobal().log(Level.INFO, fileName);

        try {
            Path path = Paths.get(directory, fileName);
            Logger.getGlobal().log(Level.INFO, path.toString());
            Files.write(path, toByteArray(info.map));
            return path.toAbsolutePath().toString();
        } catch (Throwable e) {
            Logger.getGlobal().log(Level.WARNING, "Failed to save to path", e);
            return null;
        }
    }

    public static Info read(Path file) {
        try {
            int[] comp = Arrays.stream(file.getFileName().toString()
                    .replace(".maze", "").split("_"))
                    .mapToInt(Integer::parseUnsignedInt).toArray();
            if (!isValid(comp)) return null;
            MazeBlock[][] map = fromByteArray(Files.readAllBytes(file), comp[0], comp[1]);
            return new Info(map, new MazeBlock.Location(comp[2], comp[3]),
                    new MazeBlock.Location(comp[4], comp[5]));
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isValid(int... comp) {
        return comp.length >= 6 && comp[0] > 0 && comp[1] > 0 &&
                comp[2] >= 0 && comp[2] < comp[0] &&
                comp[3] >= 0 && comp[3] < comp[1] &&
                comp[4] >= 0 && comp[4] < comp[0] &&
                comp[5] >= 0 && comp[5] < comp[1];
    }

    private static byte[] toByteArray(MazeBlock[][] map) {
        int r = map.length, c = map[0].length;
        BitSet set = new BitSet(r * c);
        for (int i = 0, k = 0; i < r; i++)
            for (int j = 0; j < c; j++, k++)
                if (map[i][j] == MazeBlock.WALL)
                    set.flip(k);
        return set.toByteArray();
    }

    private static MazeBlock[][] fromByteArray(byte[] bytes, int r, int c) {
        BitSet set = BitSet.valueOf(bytes);
        MazeBlock[][] map = new MazeBlock[r][c];
        for (int i = 0, k = 0; i < r; i++)
            for (int j = 0; j < c; j++, k++)
                if (set.get(k)) map[i][j] = MazeBlock.WALL;
                else map[i][j] = MazeBlock.EMPTY;
        return map;
    }

    public static class Info {
        private final MazeBlock[][] map;
        private final MazeBlock.Location start, end;

        public Info(MazeBlock[][] map, MazeBlock.Location start, MazeBlock.Location end) {
            this.map = map.clone();
            this.start = start;
            this.end = end;
            map[start.getR()][start.getC()] = MazeBlock.EMPTY;
            map[end.getR()][end.getC()] = MazeBlock.EMPTY;
        }

        public MazeBlock[][] getMap() {
            return map;
        }

        public MazeBlock.Location getStart() {
            return start;
        }

        public MazeBlock.Location getEnd() {
            return end;
        }
    }
}
