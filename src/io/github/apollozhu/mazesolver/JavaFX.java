package io.github.apollozhu.mazesolver;
/**
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;

public class JavaFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        final Desktop desktop = Desktop.getDesktop();
        Safely.execute(() -> desktop.setAboutHandler(AboutPanel::display));
        final BorderPane root = new BorderPane();
        final SwingNode swingNode = new SwingNode(), menuNode = new SwingNode();
        final AnchorPane pane = new AnchorPane(), menuPane = new AnchorPane();
        SwingUtilities.invokeLater(() -> {
            MazePanel panel = new MazePanel();
            JMenuBar bar = panel.getMenuBar();
            if (Resources.isMacOS()) {
                Safely.execute(() -> desktop.setDefaultMenuBar(bar));
                Resources.trySetMacOSDockIcon();
            } else menuNode.setContent(bar);
            Safely.execute(() -> desktop.requestForeground(true));
            swingNode.setContent(panel);
            AboutPanel.display();
            SwingUtilities.updateComponentTreeUI(panel);
            SwingUtilities.updateComponentTreeUI(bar);
        });
        root.setTop(menuPane);
        menuPane.getChildren().add(menuNode);
        AnchorPane.setTopAnchor(menuNode, 0.);
        AnchorPane.setBottomAnchor(menuNode, 0.);
        AnchorPane.setLeftAnchor(menuNode, 0.);
        AnchorPane.setRightAnchor(menuNode, 0.);

        root.setCenter(pane);
        pane.getChildren().add(swingNode);
        AnchorPane.setTopAnchor(swingNode, 0.);
        AnchorPane.setBottomAnchor(swingNode, 0.);
        AnchorPane.setLeftAnchor(swingNode, 0.);
        AnchorPane.setRightAnchor(swingNode, 0.);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        primaryStage.setTitle("Maze Solver - Zhiyu Zhu, Period 1");
        primaryStage.getIcons().add(new Image(Resources.getIconName()));
        Scene scene = new Scene(root, size.getWidth(), size.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }
}
