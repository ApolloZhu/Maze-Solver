package io.github.apollozhu.mazesolver;

import io.github.apollozhu.mazesolver.controller.AboutPanel;
import io.github.apollozhu.mazesolver.controller.MazePanel;
import io.github.apollozhu.mazesolver.utilities.Resources;
import io.github.apollozhu.mazesolver.utilities.Safely;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum GUI {
    ;
    public static final JFrame frame = new JFrame();

    public static void main(String[] args) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(size);
        frame.setVisible(true);
        Desktop desktop = Desktop.getDesktop();
        Safely.execute(() -> desktop.setAboutHandler(AboutPanel::display));
        SwingUtilities.invokeLater(() -> {
            customizeFrame(frame);
            MazePanel panel = new MazePanel();
            frame.setContentPane(panel);
            frame.toFront();
            frame.requestFocus();
            Safely.execute(() -> desktop.requestForeground(true));
            customizeApp(frame);
            frame.setVisible(true);
            Safely.execute(() -> {
                desktop.setDefaultMenuBar(panel.getMenuBar());
                AboutPanel.display();
            });
        });
    }

    private static void customizeFrame(JFrame frame) {
        frame.setTitle("Maze Solver - Zhiyu Zhu, Period 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Resources.getIcon());
        Safely.execute(() -> {
            // FIXME: Will be deprecated by Java 9+
            // TODO: http://openjdk.java.net/jeps/272
            Class NSApplication = Class.forName("com.apple.eawt.Application");
            Method sharedApplication = NSApplication.getMethod("getApplication");
            Object shared = sharedApplication.invoke(NSApplication);
            Method setApplicationIconImage = NSApplication.getMethod("setDockIconImage", Image.class);
            setApplicationIconImage.invoke(shared, Resources.imageNamed("Icon-Mac.png"));
        });
    }

    private static void customizeApp(JFrame frame) {
        // Add more menus to menu bar
        // JMenuBar: File, Edit, ... Help
        // desktop.setPreferencesHandler(e -> {});
        // desktop.moveToTrash(File)
        // desktop.setOpenFileHandler();
        // desktop.setOpenURIHandler();
        // desktop.openHelpViewer();
    }
}
