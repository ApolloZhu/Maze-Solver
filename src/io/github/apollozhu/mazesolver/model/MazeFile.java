package io.github.apollozhu.mazesolver.model;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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

    public static boolean saveMaze(Component parent, Info info) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save maze document in...");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = MazeFile.write(info, chooser.getSelectedFile().getPath());
            if (path == null) JOptionPane.showMessageDialog(parent,
                    "Something went wrong when saving the maze.",
                    "Failed!", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(parent,
                        "Maze saved to " + path,
                        "Saved!", JOptionPane.INFORMATION_MESSAGE);
                Desktop.getDesktop().browseFileDirectory(Paths.get(path).toFile());
                return true;
            }
        } else JOptionPane.showMessageDialog(parent,
                "You didn't choose a directory to save the maze.",
                "Cancelled!", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    public static String write(Info info, String directory) {
        if (info == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(info.map.length).append('_');
        sb.append(info.map[0].length).append('_');
        sb.append(info.start.getR()).append('_');
        sb.append(info.start.getC()).append('_');
        sb.append(info.end.getR()).append('_');
        sb.append(info.end.getC()).append(".maze");
        String fileName = sb.toString();
        Logger.getGlobal().log(Level.INFO, fileName);

        try {
            Path path = Paths.get(directory, fileName);
            Logger.getGlobal().log(Level.INFO, path.toString());
            Files.write(path, toByteArray(info.map));
            return path.toAbsolutePath().toString();
        } catch (Throwable e) {
            Logger.getGlobal().log(Level.WARNING, "Failed to save maze", e);
            return null;
        }
    }

    public static Info chooseMaze(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open maze document");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("Maze (*.maze)", "maze"));
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
            return MazeFile.read(Paths.get(chooser.getSelectedFile().getAbsolutePath()));
        JOptionPane.showMessageDialog(parent,
                "You didn't choose a maze.",
                "Cancelled!", JOptionPane.WARNING_MESSAGE);
        return null;
    }

    public static Info read(Path file) {
        try {
            int[] comp = Arrays.stream(file.getFileName().toString()
                    .replace(".maze", "").split("_"))
                    .mapToInt(Integer::parseUnsignedInt).toArray();
            if (comp.length < 6) return null;
            MazeBlock[][] map = fromByteArray(Files.readAllBytes(file), comp[0], comp[1]);
            return Info.init(map, new MazeBlock.Location(comp[2], comp[3]),
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
        private MazeBlock[][] map;
        private MazeBlock.Location start, end;

        public static Info init(MazeBlock[][] map, MazeBlock.Location start, MazeBlock.Location end) {
            int r = map.length;
            int c = map[0].length;
            int sR = start.getR();
            int sC = start.getC();
            int tR = end.getR();
            int tC = end.getC();
            Logger.getGlobal().log(Level.INFO, "validate");
            if (!isValid(r, c, sR, sC, tR, tC)) return null;
            Logger.getGlobal().log(Level.INFO, "is valid");
            Info info = new Info();
            info.map = map.clone();
            info.start = start;
            info.end = end;
            map[start.getR()][start.getC()] = MazeBlock.EMPTY;
            map[end.getR()][end.getC()] = MazeBlock.EMPTY;
            return info;
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
