package io.github.apollozhu.mazesolver.view;

import io.github.apollozhu.mazesolver.controller.TopDialog;
import io.github.apollozhu.mazesolver.utilities.Safely;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

/**
 * @author ApolloZhu, Pd. 1
 */
public class SnapshotablePanel extends JPanel {
    public boolean saveSnapshot() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Image (*.png)", "png"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Save snapshot");
        if (chooser.showSaveDialog(TopDialog.getDialog()) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = chooser.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".png")) path += ".png";
                File file = Paths.get(path).toFile();
                ImageIO.write(toImage(), "png", file);
                JOptionPane.showMessageDialog(TopDialog.getDialog(),
                        "Snapshot saved to " + path,
                        "Saved!", JOptionPane.INFORMATION_MESSAGE);
                Safely.execute(() -> Desktop.getDesktop().browseFileDirectory(file));
                return true;
            } catch (Throwable t) {
                String message = t.getLocalizedMessage();
                if (message == null || message.isEmpty())
                    message = "Something went wrong when saving the snapshot.";
                JOptionPane.showMessageDialog(TopDialog.getDialog(), message,
                        "Failed!", JOptionPane.ERROR_MESSAGE);
            }
        } else JOptionPane.showMessageDialog(TopDialog.getDialog(),
                "You didn't choose a file to save the snapshot as.",
                "Cancelled!", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    protected BufferedImage toImage() {
        int w = getWidth();
        int h = getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.createGraphics();
        paint(graphics);
        return image;
    }
}
