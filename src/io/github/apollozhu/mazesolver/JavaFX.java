package io.github.apollozhu.mazesolver;/**
 * @author ApolloZhu, Pd. 1
 */

import io.github.apollozhu.mazesolver.controller.AboutPanel;
import io.github.apollozhu.mazesolver.controller.MazePanel;
import io.github.apollozhu.mazesolver.utilities.Resources;
import io.github.apollozhu.mazesolver.utilities.Safely;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class JavaFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Desktop desktop = Desktop.getDesktop();
        Safely.execute(() -> desktop.setAboutHandler(AboutPanel::display));
        final SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            MazePanel panel = new MazePanel();

            Resources.trySetMacOSDockIcon();
            Safely.execute(() -> desktop.requestForeground(true));
            Safely.execute(() -> {
                desktop.setDefaultMenuBar(panel.getMenuBar());
                AboutPanel.display();
            });
            swingNode.setContent(panel);
        });
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(swingNode);
        AnchorPane.setTopAnchor(swingNode, 0.);
        AnchorPane.setBottomAnchor(swingNode, 0.);
        AnchorPane.setLeftAnchor(swingNode, 0.);
        AnchorPane.setRightAnchor(swingNode, 0.);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setTitle("Maze Solver - Zhiyu Zhu, Period 1");
        primaryStage.getIcons().add(new javafx.scene.image.Image(Resources.getIconName()));
        primaryStage.setScene(new Scene(pane, size.getWidth(), size.getHeight()));
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
}
