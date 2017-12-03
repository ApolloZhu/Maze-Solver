package io.github.apollozhu;

import io.github.apollozhu.controller.MazePanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author ApolloZhu, Pd. 1
 */
public class MazeGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new MazePanel());
        frame.setVisible(true);
    }
}
