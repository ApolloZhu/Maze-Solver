package io.github.apollozhu;

import io.github.apollozhu.controller.AboutPanel;
import io.github.apollozhu.controller.MazePanel;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

/**
 * @author ApolloZhu, Pd. 1
 */
public class MazeGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setContentPane(new MazePanel());
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
        Desktop.getDesktop().requestForeground(true);
        customizeFrame(frame);
        customizeApp(frame);
    }

    private static void customizeFrame(JFrame frame) {
        frame.setTitle("Maze Solver - Zhiyu Zhu, Period 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(imageNamed("Icon.png"));
        try {
            // FIXME: Will be deprecated by Java 9+
            // TODO: http://openjdk.java.net/jeps/272
            Class NSApplication = Class.forName("com.apple.eawt.Application");
            Method sharedApplication = NSApplication.getMethod("getApplication");
            Object shared = sharedApplication.invoke(NSApplication);
            Method setApplicationIconImage = NSApplication.getMethod("setDockIconImage", Image.class);
            setApplicationIconImage.invoke(shared, imageNamed("Icon-Mac.png"));
            frame.setIconImage(imageNamed("Icon-Mac.png"));
        } catch (Exception e) {
        }
    }

    private static void customizeApp(JFrame frame) {
        Desktop desktop = Desktop.getDesktop();
        // Add more menus to menubar
        // JMenuBar: File, Edit, ... Help
        // desktop.setDefaultMenuBar(JMenuBar);
        // desktop.setPreferencesHandler(e -> {});
        // desktop.browseFileDirectory​(File);
        // desktop.moveToTrash(File)
        // desktop.setOpenFileHandler();
        // desktop.setOpenURIHandler();
        // desktop.openHelpViewer();
        desktop.setAboutHandler(e -> AboutPanel.showOn(frame));
    }

    private static Image imageNamed(String name) {
        try {
            return new ImageIcon(MazeGUI.class.getClassLoader().getResource(name)).getImage();
        } catch (Exception e) {
            return null;
        }
    }
}
