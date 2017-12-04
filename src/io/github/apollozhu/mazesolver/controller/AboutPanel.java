package io.github.apollozhu.mazesolver.controller;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

/**
 * @author ApolloZhu, Pd. 1
 */
public class AboutPanel extends JPanel {
    private final JLabel iconView, name, descrption, version;
    private final JButton github, license;

    private final JPanel infoPanel, copyrightsPanel;

    private AboutPanel(Frame parent) {
        setLayout(new BorderLayout());
        add(infoPanel = new JPanel(), BorderLayout.CENTER);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        infoPanel.setAlignmentX(CENTER_ALIGNMENT);
        Image icon = parent.getIconImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        infoPanel.add(iconView = new JLabel(new ImageIcon(icon), SwingConstants.CENTER));
        iconView.setPreferredSize(new Dimension(128, 128));

        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        infoPanel.add(name = new JLabel("Maze Finder", SwingConstants.CENTER));
        name.setFont(name.getFont().deriveFont(Font.BOLD));

        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));


        infoPanel.add(descrption = new JLabel("<html>Creates and solves mazes,<br />" +
                "With different algorithms.</html>"));
        descrption.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        infoPanel.add(version = new JLabel("Version 1.0.1", SwingConstants.CENTER));

        for (Component comp : infoPanel.getComponents())
            if (comp instanceof JComponent)
                ((JComponent) comp).setAlignmentX(CENTER_ALIGNMENT);

        add(copyrightsPanel = new JPanel(), BorderLayout.SOUTH);
        copyrightsPanel.add(github = new JButton("GitHub: @ApolloZhu/Maze-Solver"));
        github.addActionListener(e -> openURL("https://github.com/ApolloZhu/Maze-Solver"));
        copyrightsPanel.add(license = new JButton("Copyright (c) 2017 Zhiyu Zhu/朱智语"));
        license.addActionListener(e -> openURL("https://github.com/ApolloZhu/Maze-Solver/blob/master/LICENSE"));
        copyrightsPanel.setLayout(new GridLayout(
                copyrightsPanel.getComponentCount(), 1));
    }

    private static void openURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {

        }
    }

    public static void showOn(Frame frame) {
        JDialog dialog = new JDialog(frame, "About Maze Solver", true);
        dialog.setSize(new Dimension(300, 325));
        dialog.setContentPane(new AboutPanel(frame));
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((screenSize.width - dialog.getWidth()) / 2,
                (screenSize.height - dialog.getHeight()) / 2);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
}
