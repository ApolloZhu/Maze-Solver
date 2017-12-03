package io.github.apollozhu;

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
        frame.setIconImage(imageNamed("Icon.png"));
        try {
            Class NSApplication = Class.forName("com.apple.eawt.Application");
            Method sharedApplication = NSApplication.getMethod("getApplication");
            Object shared = sharedApplication.invoke(NSApplication);
            Method setApplicationIconImage = NSApplication.getMethod("setDockIconImage", Image.class);
            setApplicationIconImage.invoke(shared, imageNamed("Icon-Mac.png"));
        } catch (Exception e) {
        }
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new MazePanel());
        frame.setTitle("Maze Solver - Zhiyu Zhu, Period 1");
        frame.setVisible(true);
    }

    private static Image imageNamed(String name) {
        try {
            return new ImageIcon(MazeGUI.class.getClassLoader().getResource(name)).getImage();
        } catch (Exception e) {
            return null;
        }
    }
}
