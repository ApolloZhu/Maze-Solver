package io.github.apollozhu.mazesolver.utilities;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * @author ApolloZhu, Pd. 1
 */
public enum Resources {
    ;

    public static Image getIcon() {
        return imageNamed(isMacOS() ? "Icon-Mac.png" : "Icon.png");
    }

    public static boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static String getAppVersion() {
        return "1.0.2";
    }

    public static Image imageNamed(String name) {
        try {
            URL url = Resources.class.getClassLoader().getResource(name);
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }
}
