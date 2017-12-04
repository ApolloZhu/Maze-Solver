package io.github.apollozhu.mazesolver.view;

import io.github.apollozhu.mazesolver.model.MazeBlock;
import io.github.apollozhu.mazesolver.solver.MazeSolver;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author ApolloZhu, Pd. 1
 */
public class MazeCanvas extends SnapshotablePanel
        implements MazeSolver.MSEventListener {
    public static final BlockPainter START_PAINTER = (g, r, c, x, y, w, h) -> {
        g.setColor(Color.red);
        g.drawOval(x + w / 5, y + h / 5,
                w * 3 / 5, h * 3 / 5);
    };
    public static final BlockPainter TARGET_PAINTER = (g, r, c, x, y, w, h) -> {
        g.setColor(Color.red);
        g.drawLine(x + w / 5, y + h / 5,
                x + w * 4 / 5, y + h * 4 / 5);
        g.drawLine(x + w * 4 / 5, y + h / 5,
                x + w / 5, y + h * 4 / 5);
    };
    private static final Color DIFF_COLOR_NEW = new Color(102, 204, 255);
    private static final Color COMMON_COLOR_FOUND = new Color(29, 135, 17);
    private static final Color COMMON_COLOR_NORMAL = Color.blue;
    private static final Color COMMON_COLOR_FAILED = Color.orange;
    List<BlockPainter> painters = new LinkedList<>();
    private MazeBlock[][] map;
    private final BlockPainter wallPainter = dependentPainter(() -> Color.black,
            (r, c, i, j) -> (i != r || j != c) && isWall(i, j));
    private Path[][] paths;
    private Color diffColor;
    private final BlockPainter diffPainter = (graphics, r, c, x, y, w, h) -> {
        Graphics2D g = (Graphics2D) graphics;
        Path path = paths[r][c];
        if (path == null) return; // In between trials
        int headX = x + w / 2 + path.direction.dy() * w;
        int headY = y + h / 2 + path.direction.dx() * h;
        Stroke stroke = g.getStroke();
        g.setStroke(new BasicStroke(w / 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(diffColor);
        g.drawLine(x + w / 2, y + h / 2, headX, headY);
        int dX = w / 5;
        int dY = h / 5;
        switch (path.direction) {
            case UP:
                g.drawLine(headX, headY, headX + dX, headY + dY);
                g.drawLine(headX, headY, headX - dX, headY + dY);
                break;
            case DOWN:
                g.drawLine(headX, headY, headX + dX, headY - dY);
                g.drawLine(headX, headY, headX - dX, headY - dY);
                break;
            case LEFT:
                g.drawLine(headX, headY, headX + dX, headY + dY);
                g.drawLine(headX, headY, headX + dX, headY - dY);
                break;
            case RIGHT:
                g.drawLine(headX, headY, headX - dX, headY + dY);
                g.drawLine(headX, headY, headX - dX, headY - dY);
                break;
        }
        g.setStroke(stroke);
    };
    private Color commonColor = COMMON_COLOR_NORMAL;
    private final BlockPainter multiPathPainter = dependentPainter(() -> commonColor,
            (r, c, i, j) -> (i == r || j == c) && isPath(i, j));
    private Path diff = null;
    private MazeBlock.Location start, end;
    private final BlockPainter painter = (graphics, r, c, x, y, w, h) -> {
        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.gray);
        g.drawRect(x, y, w, h);

        Stroke stroke = g.getStroke();
        g.setStroke(new BasicStroke(w / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (start != null && r == start.getR() && c == start.getC())
            START_PAINTER.paintBlock(g, r, c, x, y, w, h);
        if (end != null && r == end.getR() && c == end.getC()) {
            TARGET_PAINTER.paintBlock(g, r, c, x, y, w, h);
            g.setStroke(stroke);
            return;
        }

        switch (map[r][c]) {
            case WALL:
                wallPainter.paintBlock(g, r, c, x, y, w, h);
                break;
            case VISITED:
                g.setColor(COMMON_COLOR_FAILED);
                g.drawLine(x + w / 2, y + h / 5,
                        x + w * 4 / 5, y + h * 4 / 5);
                g.drawLine(x + w * 4 / 5, y + h * 4 / 5,
                        x + w / 5, y + h * 4 / 5);
                g.drawLine(x + w / 5, y + h * 4 / 5,
                        x + w / 2, y + h / 5);
                break;
            case PATH:
                g.setStroke(new BasicStroke(w / 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                multiPathPainter.paintBlock(g, r, c, x, y, w, h);
                break;
            default:
                break;
        }
        if (diff != null && diff.r == r && diff.c == c)
            diffPainter.paintBlock(g, r, c, x, y, w, h);
        g.setStroke(stroke);
    };

    public MazeCanvas(MazeBlock[][] map) {
        setMap(map);
    }

    private static BlockPainter dependentPainter(Supplier<Color> color, DiPredicate condition) {
        return (graphics, r, c, x, y, w, h) -> {
            Graphics2D g = (Graphics2D) graphics;
            g.setColor(color.get());
            int centerX = x + w / 2;
            int centerY = y + h / 2;
            boolean hasNeighbor = false;
            for (int i = r - 1, k = 0; i <= r + 1; i++)
                for (int j = c - 1; j <= c + 1; j++, k++)
                    if (condition.test(r, c, i, j)) {
                        hasNeighbor = true;
                        int vX = k % 3, vY = k / 3;
                        int lX = vX == 0 ? x : vX == 1 ? centerX : x + w;
                        int lY = vY == 0 ? y : vY == 1 ? centerY : y + h;
                        g.drawLine(lX, lY, centerX, centerY);
                    }
            if (hasNeighbor) return;
            g.drawLine(x, centerY, centerX, y);
            g.drawLine(centerX, y, x + w, centerY);
            g.drawLine(x + w, centerY, centerX, y + h);
            g.drawLine(centerX, y + h, x, centerY);
        };
    }

    public boolean isWall(int r, int c) {
        return get(r, c) == MazeBlock.WALL;
    }

    public boolean isPath(int r, int c) {
        return get(r, c) == MazeBlock.PATH;
    }

    public MazeBlock get(int r, int c) {
        try {
            return map[r][c];
        } catch (Exception e) {
            return MazeBlock.WALL;
        }
    }

    public void resetMap(MazeBlock[][] map) {
        this.map = map;
        reset();
        repaint();
    }

    public void setMap(MazeBlock[][] map) {
        this.map = map;
        repaint();
    }

    public void addPainter(BlockPainter painter) {
        painters.add(painter);
    }

    public void removePainter(BlockPainter painter) {
        painters.remove(painter);
    }

    private int getSide() {
        if (map == null || map.length == 0 || map[0].length == 0) return 0;
        return Math.min(getWidth() / map[0].length, getHeight() / map.length);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map == null || map.length == 0 || map[0].length == 0) return;
        int side = getSide();
        int xOffset = (getWidth() - side * map[0].length) / 2;
        int yOffset = (getHeight() - side * map.length) / 2;
        for (int j = 0; j < map[0].length; j++)
            for (int i = 0; i < map.length; i++) {
                int r = i, c = j;
                int x = xOffset + side * j;
                int y = yOffset + side * i;
                painter.paintBlock(g, r, c, x, y, side, side);
                painters.forEach(l -> l.paintBlock(g, r, c, x, y, side, side));
            }
    }

    public MazeBlock.Location getLoc(int x, int y) {
        if (map == null || map.length == 0 || map[0].length == 0) return null;
        int side = Math.min(getWidth() / map[0].length,
                getHeight() / map.length);
        int xOffset = (getWidth() - side * map[0].length) / 2;
        int yOffset = (getHeight() - side * map.length) / 2;
        int r = (y - yOffset) / side;
        int c = (x - xOffset) / side;
        if (r < 0 || c < 0 || r >= map.length || c >= map[0].length) return null;
        return new MazeBlock.Location(r, c);
    }

    public void setStart(MazeBlock.Location start) {
        this.start = start;
        repaint();
    }

    public void setTarget(MazeBlock.Location target) {
        this.end = target;
        repaint();
    }

    protected void reset() {
        commonColor = COMMON_COLOR_NORMAL;
        this.paths = new Path[map.length][map[0].length];
    }

    @Override
    public void started(int r, int c, int tR, int tC, MazeBlock[][] map) {
        setStart(new MazeBlock.Location(r, c));
        setTarget(new MazeBlock.Location(tR, tC));
        resetMap(map);
    }

    @Override
    public void tryout(int r, int c, MazeSolver.Direction direction, Object path, MazeBlock[][] map) {
        diffColor = DIFF_COLOR_NEW;
        paths[r][c] = diff = new Path(r, c, direction);
        setMap(map);
    }

    @Override
    public void found(int tR, int tC, Object path, MazeBlock[][] map) {
        commonColor = COMMON_COLOR_FOUND;
        diff = null;
        setMap(map);
    }

    @Override
    public void failed(int r, int c, Object path, MazeBlock[][] map) {
        diffColor = COMMON_COLOR_FAILED;
        diff = paths[r][c];
        paths[r][c] = null;
        setMap(map);
    }

    @Override
    public void ended(boolean hasPath, MazeBlock[][] map) {
        commonColor = hasPath ? COMMON_COLOR_FOUND : COMMON_COLOR_FAILED;
        diff = null;
        setMap(map);
    }

    private interface DiPredicate {
        boolean test(int r, int c, int i, int j);
    }

    private static class Path {
        private int r, c;
        private MazeSolver.Direction direction;

        Path(int r, int c, MazeSolver.Direction direction) {
            this.r = r;
            this.c = c;
            this.direction = direction;
        }
    }
}
