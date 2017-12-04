package io.github.apollozhu.controller;

import io.github.apollozhu.model.Maze;
import io.github.apollozhu.model.MazeBlock;
import io.github.apollozhu.model.MazeFile;
import io.github.apollozhu.solver.MazeSolver;
import io.github.apollozhu.view.MazeCanvas;
import io.github.apollozhu.view.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * @author ApolloZhu, Pd. 1
 */
public class MazePanel extends PlaybackPanel implements MazeSolver.MSEventListener {
    private static final MazeBlock[][] LAU_MAZE = Maze.decodeLauMaze();

    private static MazeSolver.Type[] types = MazeSolver.Type.values();
    private final JButton pickStartButton, pickEndButton, editWallButton;
    private final JComboBox<String> solverComboBox;
    private final JTextField rowTextField, columnTextField, percentageTextField;
    private final JPanel panel = new JPanel(),
            mapGenerationControlPanel = new JPanel(), controlsPanel = new JPanel();
    private JMenuItem saveImageMenuItem;
    private MazeSolver solver;
    private MazeCanvas canvas;
    private boolean isSelectingStart, isSelectingEnd, isEditingWall;
    private MazeBlock.Location start, end;
    private MazeBlock[][] map;
    private int selectedSolverIndex;
    private double pathPercentage = 0.7;

    public MazePanel() {
        add(panel, BorderLayout.NORTH);
        panel.setLayout(new GridLayout(2, 1));
        panel.add(mapGenerationControlPanel);
        mapGenerationControlPanel.setLayout(new SpringLayout());

        mapGenerationControlPanel.add(new JLabel("Row: "));
        mapGenerationControlPanel.add(rowTextField = new JTextField("" + LAU_MAZE.length));
        rowTextField.addActionListener(this::regenerateMap);
        mapGenerationControlPanel.add(new JLabel("Column: "));
        mapGenerationControlPanel.add(
                columnTextField = new JTextField("" + LAU_MAZE[0].length));
        columnTextField.addActionListener(this::regenerateMap);
        mapGenerationControlPanel.add(new JLabel("Path percentage: "));
        mapGenerationControlPanel.add(percentageTextField = new JTextField("" + pathPercentage));
        percentageTextField.addActionListener(this::regenerateMap);
        JButton regenerateButton = new JButton("Re-Generate");
        mapGenerationControlPanel.add(regenerateButton);
        regenerateButton.addActionListener(this::regenerateMap);
        SpringUtilities.makeCompactGrid(mapGenerationControlPanel,
                1, mapGenerationControlPanel.getComponentCount(),
                8, 8, 8, 8);

        panel.add(controlsPanel);
        controlsPanel.setLayout(new SpringLayout());
        controlsPanel.add(pickStartButton = new JButton("Pick Start"));
        pickStartButton.addActionListener(l -> {
            isSelectingEnd = false;
            isEditingWall = false;
            isSelectingStart = true;
        });
        controlsPanel.add(pickEndButton = new JButton("Pick End"));
        pickEndButton.addActionListener(l -> {
            isSelectingStart = false;
            isEditingWall = false;
            isSelectingEnd = true;
        });
        controlsPanel.add(editWallButton = new JButton("Edit Wall"));
        editWallButton.addActionListener(l -> {
            isSelectingStart = false;
            isSelectingEnd = false;
            isEditingWall = true;
        });

        controlsPanel.add(new JLabel("Solver: ", SwingConstants.TRAILING));
        String[] solverTypes = Arrays.stream(types)
                .map(MazeSolver.Type::description).toArray(String[]::new);
        controlsPanel.add(solverComboBox = new JComboBox<>(solverTypes));
        setMazeSolverAtIndex(selectedSolverIndex);
        solverComboBox.addActionListener(ignored ->
                setMazeSolverAtIndex(solverComboBox.getSelectedIndex()));

        SpringUtilities.makeGrid(controlsPanel,
                1, controlsPanel.getComponentCount(),
                8, 8, 8, 8);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MazeCanvas canvas = (MazeCanvas) getCenterComponent();
                int x = e.getX() - canvas.getX();
                int y = e.getY() - canvas.getY();
                MazeBlock.Location location = canvas.getLoc(x, y);
                if (location == null || location.getR() < 0 || location.getC() < 0
                        || location.getR() >= map.length || location.getC() >= map[location.getR()].length) return;
                boolean notWall = !canvas.isWall(location.getR(), location.getC());
                if (isSelectingStart && notWall) {
                    setStart(location);
                    isSelectingStart = false;
                } else if (isSelectingEnd && notWall) {
                    setEnd(location);
                    isSelectingEnd = false;
                } else if (isEditingWall && !location.equals(start) && !location.equals(end)) {
                    Maze.clear(map);
                    canvas.setMap(map);
                    map[location.getR()][location.getC()] = notWall ? MazeBlock.WALL : MazeBlock.EMPTY;
                    canvas.setMap(map);
                } else return;
                clearMap();
            }
        });
        resetStartEnd();

        // MARK: Menu

        JMenuBar menuBar = new JMenuBar();
        Desktop.getDesktop().setDefaultMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem openFileMenuItem = new JMenuItem("Open...");
        fileMenu.add(openFileMenuItem);
        openFileMenuItem.addActionListener(l -> {
            MazeFile.Info info = MazeFile.chooseMaze(this);
            if (info == null) return;
            if (loadMap(info)) JOptionPane.showMessageDialog(this,
                    "Successfully loaded the maze",
                    "Loaded!", JOptionPane.INFORMATION_MESSAGE);
            else JOptionPane.showMessageDialog(this,
                    "Something went wrong when opening the maze.",
                    "Failed!", JOptionPane.ERROR_MESSAGE);
        });

        JMenuItem saveFileMenuItem = new JMenuItem("Save to folder...");
        fileMenu.add(saveFileMenuItem);
        saveFileMenuItem.addActionListener(l ->
                MazeFile.saveMaze(this, MazeFile.Info.init(map, start, end)));

        saveImageMenuItem = new JMenuItem("Save as image...");
        fileMenu.add(saveImageMenuItem);
        saveImageMenuItem.addActionListener(l -> canvas.saveSnapshot());
    }

    protected boolean loadMap(MazeFile.Info info) {
        if (info == null) return false;
        if (!setMap(info.getMap())) return false;
        setStart(info.getStart());
        setEnd(info.getEnd());
        return true;
    }

    @Override
    protected Component getCenterComponent() {
        if (canvas != null) return canvas;
        if (map != null) return canvas = new MazeCanvas(map);
        return canvas = new MazeCanvas(map = LAU_MAZE);
    }

    protected void setMazeSolverAtIndex(int index) {
        index = Math.max(Math.min(types.length, index), 0);
        if (solver != null && selectedSolverIndex == index) return;
        setMazeSolver(types[selectedSolverIndex = index].init());
    }

    protected void setMazeSolver(MazeSolver solver) {
        terminate();
        clearMap();
        if (this.solver != null)
            this.solver.removeEventListener(canvas);
        this.solver = solver;
        for (int i = 0; i < types.length; i++)
            if (types[i].getClass().equals(solver.getClass())) {
                solverComboBox.setSelectedIndex(i);
                break;
            }
        solver.addEventListener(canvas);
    }

    protected void regenerateMap(ActionEvent ignored) {
        int r = map.length, c = map[0].length, newR = r, newC = c;
        double newPercentage = pathPercentage;
        try {
            newR = Integer.parseInt(rowTextField.getText());
            newR = Math.max(Math.min(newR, canvas.getHeight()), 0);
        } catch (Exception e) {
        }
        try {
            newC = Integer.parseInt(columnTextField.getText());
            newC = Math.max(Math.min(newC, canvas.getWidth()), 0);
        } catch (Exception e) {
        }
        try {
            newPercentage = Double.parseDouble(percentageTextField.getText());
            newPercentage = Math.max(Math.min(newPercentage, 1), 0);
        } catch (Exception e) {
        }
        percentageTextField.setText("" + (pathPercentage = newPercentage));
        setMap(Maze.generate(newR, newC, pathPercentage));
    }

    @Override
    protected void start() {
        for (Component comp : mapGenerationControlPanel.getComponents()) comp.setEnabled(false);
        for (Component comp : controlsPanel.getComponents()) comp.setEnabled(false);
        saveImageMenuItem.setEnabled(false);
        isEditingWall = false;
        clearMap();
        solver.addEventListener(this);
        solver.start(map, start.getR(), start.getC(), end.getR(), end.getC());
    }

    @Override
    protected void pause() {
        super.pause();
        saveImageMenuItem.setEnabled(true);
    }

    @Override
    protected void resume() {
        super.resume();
        saveImageMenuItem.setEnabled(false);
    }

    @Override
    protected void terminate() {
        terminate(false);
    }

    protected void terminate(boolean hasPath) {
        for (Component comp : mapGenerationControlPanel.getComponents()) comp.setEnabled(true);
        for (Component comp : controlsPanel.getComponents()) comp.setEnabled(true);
        if (saveImageMenuItem != null) saveImageMenuItem.setEnabled(true);
        if (solver != null) {
            solver.removeEventListener(this);
            solver.stop(hasPath);
        }
        super.terminate();
    }

    protected boolean setMap(MazeBlock[][] newMap) {
        if (newMap == null || newMap.length == 0 || newMap[0].length == 0 ||
                newMap.length > canvas.getHeight() ||
                newMap[0].length > canvas.getWidth()) return false;
        map = newMap.clone();
        rowTextField.setText("" + map.length);
        columnTextField.setText("" + map[0].length);
        canvas.resetMap(map);
        resetStartEnd();
        return true;
    }

    protected void setStart(MazeBlock.Location start) {
        map[start.getR()][start.getC()] = MazeBlock.EMPTY;
        canvas.setStart(this.start = start);
    }

    protected void setEnd(MazeBlock.Location end) {
        map[end.getR()][end.getC()] = MazeBlock.EMPTY;
        canvas.setTarget(this.end = end);
    }

    protected void resetStartEnd() {
        map[0][0] = map[map.length - 1][map[0].length - 1] = MazeBlock.EMPTY;
        setStart(new MazeBlock.Location(0, 0));
        setEnd(new MazeBlock.Location(map.length - 1, map[0].length - 1));
    }

    protected void clearMap() {
        Maze.clear(map);
        canvas.resetMap(map);
    }

    @Override
    public void started(int r, int c, int tR, int tC, MazeBlock[][] map) {
        sleep();
    }

    @Override
    public void tryout(int r, int c, MazeSolver.Direction direction, Object path, MazeBlock[][] map) {
        sleep();
    }

    @Override
    public void found(int tR, int tC, Object path, MazeBlock[][] map) {
        sleep();
    }

    @Override
    public void failed(int r, int c, Object path, MazeBlock[][] map) {
        sleep();
    }

    @Override
    public void ended(boolean hasPath, MazeBlock[][] map) {
        JOptionPane.showMessageDialog(this, hasPath ? "It is doable." : "Can do better.");
        terminate(hasPath);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (map != null && map.length > canvas.getHeight() ||
                map.length != 0 && map[0].length > canvas.getWidth())
            regenerateMap(null);
    }
}
